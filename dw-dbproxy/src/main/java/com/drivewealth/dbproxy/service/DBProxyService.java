package com.drivewealth.dbproxy.service;


import com.amazonaws.services.dynamodbv2.model.*;
import com.drivewealth.cr.grpc.server.ProtoService;
import com.drivewealth.dbproxy.config.CockroachDbConfig;
import com.drivewealth.dbproxy.config.DBProxyConfig;
import com.drivewealth.dbproxy.mapper.*;
import com.drivewealth.dbproxy.util.DynamodbRequestBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.Message;
import io.grpc.ManagedChannel;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Supplier;

import static com.drivewealth.dbproxy.util.DynamodbResponseBuilder.createResponse;
import static com.drivewealth.dbproxy.util.Parameters.*;



@Service
public class DBProxyService {


    private static final Logger LOGGER = LoggerFactory.getLogger(DBProxyService.class);


    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PersistenceManager persistenceManager;

    @Autowired
    private DynamodbRequestBuilder dynamoDbRequestBuilder;

    @Autowired
    private Supplier<ManagedChannel> sCustomManagedChannel;


    public Map<String, String> processRequest(final String requestBody, final Map<String, String> headers, final Map<String, String> configs) throws IOException, URISyntaxException, ExecutionException, InterruptedException, TimeoutException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {


        // ACTIVE_PASSIVE
        final String MODE_TYPE = configs.get("MODE_TYPE");
        // SYNCHRONOUS
        final String SYNCHRONICITY_TYPE = configs.get("SYNCHRONICITY_TYPE");
        ;


        final String OPERATION_CONDITION = MODE_TYPE + "_" + "AND" + "_" + SYNCHRONICITY_TYPE;

        final String action = headers.get("ACTION_NAME");
        final String tableName = headers.get("TABLE_NAME");
        final String AMZN_REQUESTID = headers.containsKey("RequestId") ? headers.get("RequestId") : String.valueOf(UUID.randomUUID());


        Map<String, String> responseMap = new HashMap<>();

        responseMap.put(X_AMZN_REQUESTID, AMZN_REQUESTID);
        responseMap.put(X_AMZN_RESPONSE, "{}");
        responseMap.put(DB_TYPE, "DynamoDB");
//        ManagedChannel customManagedChannel = (new CockroachDbConfig()).getManagedChannel();


        switch (action) {

            case PUTITEM: {

                RequestMapper requestMapper = (new ServiceMapper()).getServices(action, tableName);

                PutItemRequest putItemRequest = dynamoDbRequestBuilder.createPutItemRequest(requestBody, tableName);

                long startDTime = System.currentTimeMillis();
                LOGGER.info("putItemRequest ...\t" + putItemRequest);

                switch (MODE_TYPE) {

                    case IS_BOTH_DATABASE: {

                        PutItemResult result;

                        switch (SYNCHRONICITY_TYPE) {

                            case IS_SYNCHRONOUS: {
                                result = persistenceManager.putDynamodbSync(putItemRequest);
                                break;
                            }

                            case IS_ASYNCHRONOUS: {
                                CompletableFuture<PutItemResult> putItemResultCompletableFuture = persistenceManager.putDynamodbAsync(putItemRequest);
                                result = putItemResultCompletableFuture.get(3000, TimeUnit.MILLISECONDS);

                                break;
                            }

                            default: {
                                LOGGER.info("The Service type is not supported - we only support SYNCHRONOUS and ASYNCHRONOUS service type at the moment");
                                return responseMap;
                            }
                        }

                        if (result == null) {

                            LOGGER.info("The save operation in the DynamoDB is not sucessful and hence, will not proceed to commit to the CockrachDB ...");
                            LOGGER.info("The request is failed for the request Id " + AMZN_REQUESTID);
                            LOGGER.info("The request payload is " + requestBody);
                            LOGGER.info("The operation condition is " + OPERATION_CONDITION);

                            return responseMap;
                        }

                        LOGGER.info("Result ...\t" + result);

                        long dDuration = System.currentTimeMillis() - startDTime;
                        LOGGER.info("ClientRepositroy - saveUser took " + dDuration + " milliseconds to run");

                        if (result.getAttributes() != null) {
                            result.getAttributes().forEach((key, value) -> System.out.println(key + " " + value));
                        }

                        // DynamoDB operation
                        String putItemResult = objectMapper.writeValueAsString(result);
                        LOGGER.info("DynamoDB Put Item Result \n" + putItemResult);

                        if (result.getSdkResponseMetadata() != null) {
                            String requestId = result.getSdkResponseMetadata().getRequestId();
                            responseMap.put(X_AMZN_REQUESTID, requestId);
                        }

                        responseMap.put(X_AMZN_RESPONSE, putItemResult);
                        responseMap.put(DB_TYPE, "DynamoDB");


                        // cockroachDb operation
                        List<GRPCMapper> lGRPCMapper = requestMapper.getGRPCMapper();

                        if (lGRPCMapper == null || lGRPCMapper.isEmpty()) {
                            LOGGER.info("The parameters are not provided...");
                            return new LinkedHashMap<>();
                        }

                        List<Message> saveMessageList = null;

                        JSONObject putItemRequestJsonObj = new JSONObject(putItemRequest);
                        String putItemRequestRequestJsonStr = putItemRequestJsonObj.toString();

                        switch (SYNCHRONICITY_TYPE) {

                            case IS_SYNCHRONOUS: {
                                final ManagedChannel customManagedChannel = sCustomManagedChannel.get();
                                while (RETRY_COUNT <= 3) {

                                    saveMessageList = persistenceManager.saveCockroachDBSyncGrpc(lGRPCMapper, putItemRequestRequestJsonStr, customManagedChannel);

                                    if (saveMessageList != null) {
                                        LOGGER.info("successfully save to the CockroachDB synchronously ....");
                                        RETRY_COUNT = 1;
                                        break;
                                    }

                                    Thread.sleep(1000);
                                    RETRY_COUNT++;
                                }

                                ProtoService.shutdownManagedChannel(customManagedChannel);
                                break;
                            }

                            case IS_ASYNCHRONOUS: {
                                final ManagedChannel customManagedChannel = sCustomManagedChannel.get();
                                while (RETRY_COUNT <= 3) {

                                    CompletableFuture<List<Message>> messageCompletableFuture = persistenceManager.saveCockroachdbAsyncGrpc(lGRPCMapper, putItemRequestRequestJsonStr, customManagedChannel);
                                    saveMessageList = messageCompletableFuture.get(3000, TimeUnit.MILLISECONDS);

                                    if (saveMessageList != null) {
                                        LOGGER.info("successfully save to the CockroachDB asynchronously ....");
                                        RETRY_COUNT = 1;
                                        break;
                                    }

                                    Thread.sleep(1000);
                                    RETRY_COUNT++;
                                }

                                ProtoService.shutdownManagedChannel(customManagedChannel);
                                break;
                            }

                            default: {
                                LOGGER.info("The Service type is not supported - we only support SYNCHRONOUS and ASYNCHRONOUS service type at the moment");
                                return responseMap;
                            }
                        }

                        if (saveMessageList == null || saveMessageList.isEmpty()) {
                            LOGGER.info("failed to save in the CockroachDB");
                            return responseMap;
                        }

                        final Message saveMessageCockroachDB = saveMessageList.get(0);

                        final Map<String, String> cockroachDbPresistenceResponseMap = createResponse(saveMessageCockroachDB, tableName, action);

                        LOGGER.info("The request ID from the CockroachDB response :" + cockroachDbPresistenceResponseMap.get(X_AMZN_REQUESTID));
                        LOGGER.info("The response body from the cockroachDB :" + cockroachDbPresistenceResponseMap.get(X_AMZN_RESPONSE));

                        break;
                    }

                    case IS_ONLY_DYNAMODB: {

                        PutItemResult result;

                        switch (SYNCHRONICITY_TYPE) {
                            case IS_SYNCHRONOUS: {
                                result = persistenceManager.putDynamodbSync(putItemRequest);
                                break;
                            }
                            case IS_ASYNCHRONOUS: {
                                CompletableFuture<PutItemResult> putItemResultCompletableFuture = persistenceManager.putDynamodbAsync(putItemRequest);
                                result = putItemResultCompletableFuture.get(3000, TimeUnit.MILLISECONDS);

                                break;
                            }
                            default: {
                                LOGGER.info("The Service type is not supported - we only support SYNCHRONOUS and ASYNCHRONOUS service type at the moment");
                                return responseMap;
                            }
                        }

                        if (result == null) {

                            LOGGER.info("The save operation in the DynamoDB is not sucessful and hence, will not proceed to commit to the CockrachDB ...");
                            LOGGER.info("The request is failed for the request Id " + AMZN_REQUESTID);
                            LOGGER.info("The request payload is " + requestBody);
                            LOGGER.info("The operation condition is " + OPERATION_CONDITION);

                            return responseMap;
                        }

                        if (result.getAttributes() != null) {
                            result.getAttributes().forEach((key, value) -> System.out.println(key + " " + value));
                        }

                        String putItemResult = objectMapper.writeValueAsString(result);
                        System.out.println("DynamoDB Put Item Result \n" + putItemResult);

                        if (result.getSdkResponseMetadata() != null) {
                            String requestId = result.getSdkResponseMetadata().getRequestId();
                            responseMap.put(X_AMZN_REQUESTID, requestId);
                        }

                        responseMap.put(X_AMZN_RESPONSE, putItemResult);
                        responseMap.put(DB_TYPE, "DynamoDB");

                        break;
                    }

                    case IS_ONLY_COCKROACHDB: {
                        List<GRPCMapper> lGRPCMapper = requestMapper.getGRPCMapper();

                        if (lGRPCMapper == null || lGRPCMapper.isEmpty()) {
                            LOGGER.info("The parameters are not provided...");
                            return responseMap;
                        }

                        List<Message> saveMessageCockroachDbList = null;

                        JSONObject putItemRequestJsonObj = new JSONObject(putItemRequest);
                        String putItemRequestRequestJsonStr = putItemRequestJsonObj.toString();

                        switch (SYNCHRONICITY_TYPE) {

                            case IS_SYNCHRONOUS: {
                                final ManagedChannel customManagedChannel = sCustomManagedChannel.get();
                                saveMessageCockroachDbList = persistenceManager.saveCockroachDBSyncGrpc(lGRPCMapper, putItemRequestRequestJsonStr, customManagedChannel);

                                ProtoService.shutdownManagedChannel(customManagedChannel);
                                break;
                            }

                            case IS_ASYNCHRONOUS: {
                                final ManagedChannel customManagedChannel = sCustomManagedChannel.get();
                                CompletableFuture<List<Message>> messageCompletableFuture = persistenceManager.saveCockroachdbAsyncGrpc(lGRPCMapper, putItemRequestRequestJsonStr, customManagedChannel);
                                saveMessageCockroachDbList = messageCompletableFuture.get(5000, TimeUnit.MILLISECONDS);

                                ProtoService.shutdownManagedChannel(customManagedChannel);
                                break;
                            }

                            default: {
                                LOGGER.info("The Service type is not supported - we only support SYNCHRONOUS and ASYNCHRONOUS service type at the moment");
                                return responseMap;
                            }
                        }

                        if (saveMessageCockroachDbList == null || saveMessageCockroachDbList.isEmpty()) {
                            LOGGER.info("failed to save in the CockroachDB");
                            return responseMap;
                        }

                        Message saveMessage = saveMessageCockroachDbList.get(0);

                        Map<String, String> saveMessageMap = createResponse(saveMessage, tableName, action);
                        return saveMessageMap;
                    }

                    default: {
                        LOGGER.info("We dont support this condition at the moment ....");
                        throw new UnsupportedEncodingException("This operation is not supported in the PUTITEM case");
                    }
                }

                return responseMap;
            }


            case GETITEM: {

                RequestMapper getRequestMapper = (new ServiceMapper()).getServices(action, tableName);

                GetItemRequest getItemRequest = dynamoDbRequestBuilder.createGetItemRequest(tableName, requestBody);

                long startDTime = System.currentTimeMillis();

                switch (MODE_TYPE) {

                    case IS_BOTH_DATABASE: {
                        GetItemResult result = null;

                        switch (SYNCHRONICITY_TYPE) {

                            case IS_SYNCHRONOUS: {
                                result = persistenceManager.getDynamodbSync(getItemRequest);
                                break;
                            }

                            case IS_ASYNCHRONOUS: {

                                CompletableFuture<GetItemResult> dynamodbAsync = persistenceManager.getDynamodbAsync(getItemRequest);
                                result = dynamodbAsync.get(3000, TimeUnit.MILLISECONDS);
                                break;

                            }

                            default: {
                                LOGGER.info("The Service type is not supported - we only support SYNCHRONOUS and ASYNCHRONOUS service type at the moment");
                                return responseMap;
                            }
                        }

                        if (result == null) {

                            LOGGER.info("The save operation in the DynamoDB is not successful and hence, will not proceed to commit to the CockroachDB ...");
                            LOGGER.info("The getItemRequest is failed for the getItemRequest Id " + AMZN_REQUESTID);
                            LOGGER.info("The getItemRequest payload is " + requestBody);
                            LOGGER.info("The operation condition is " + OPERATION_CONDITION);

                            return responseMap;
                        }

                        long dDuration = System.currentTimeMillis() - startDTime;
                        System.out.println("Client Repository - GetItemResult took " + dDuration + " milliseconds to run");

                        if (result.getItem() != null) {
                            result.getItem().forEach((key, value) -> System.out.println(key + " " + value.toString()));
                        }

                        String getItemResult = objectMapper.writeValueAsString(result);
                        System.out.println("DynamoDB Put Item Result \n" + getItemResult);

                        if (result.getSdkResponseMetadata() != null) {
                            String requestId = result.getSdkResponseMetadata().getRequestId();
                            responseMap.put(X_AMZN_REQUESTID, requestId);
                        }

                        responseMap.put(X_AMZN_RESPONSE, getItemResult);
                        responseMap.put(DB_TYPE, "DynamoDB");


                        // for CockroachDB
                        JSONObject getItemRequestJsonObj = new JSONObject(getItemRequest);
                        String getItemRequestRequestJsonStr = getItemRequestJsonObj.toString();

                        List<GRPCMapper> glGRPCMapper = getRequestMapper.getGRPCMapper();

                        if (glGRPCMapper == null || glGRPCMapper.isEmpty()) {
                            LOGGER.info("The supporting parameters are not provided ");
                            return new LinkedHashMap<>();
                        }

                        Message getMessageCockroachDB = null;

                        switch (SYNCHRONICITY_TYPE) {
                            case IS_SYNCHRONOUS: {
                                final ManagedChannel customManagedChannel = sCustomManagedChannel.get();

                                while (RETRY_COUNT <= 3) {

                                    getMessageCockroachDB = persistenceManager.getCockroachDBItemSyncGrpc(glGRPCMapper, getItemRequestRequestJsonStr, customManagedChannel);

                                    if (getMessageCockroachDB != null) {
                                        LOGGER.info("successfully save to the CockroachDB synchronously ....");
                                        RETRY_COUNT = 1;
                                        break;
                                    }

                                    Thread.sleep(1000);
                                    RETRY_COUNT++;
                                }

                                ProtoService.shutdownManagedChannel(customManagedChannel);
                                break;
                            }

                            case IS_ASYNCHRONOUS: {
                                final ManagedChannel customManagedChannel = sCustomManagedChannel.get();

                                while (RETRY_COUNT <= 3) {

                                    CompletableFuture<Message> cockroachdbAsync = persistenceManager.getCockroachdbAsyncGrpc(glGRPCMapper, getItemRequestRequestJsonStr, customManagedChannel);
                                    getMessageCockroachDB = cockroachdbAsync.get(3000, TimeUnit.MILLISECONDS);

                                    if (getMessageCockroachDB != null) {
                                        LOGGER.info("successfully save to the CockroachDB synchronously ....");
                                        RETRY_COUNT = 1;
                                        break;
                                    }

                                    Thread.sleep(1000);
                                    RETRY_COUNT++;
                                }

                                ProtoService.shutdownManagedChannel(customManagedChannel);
                                break;
                            }

                            default: {
                                LOGGER.info("The service type is not supported ...");
                                return responseMap;
                            }
                        }

                        if (getMessageCockroachDB == null) {

                            LOGGER.info("We are unable to get the data from the CockroachDB ...");
                            LOGGER.info("The getItemRequest id is : " + AMZN_REQUESTID);
                            LOGGER.info("The getItemRequest payload is : " + requestBody);
                            LOGGER.info("The operation condition is : " + OPERATION_CONDITION);

                            return new LinkedHashMap<>();
                        }

                        Map<String, String> cockroachDbPresistenceResponseMap = createResponse(getMessageCockroachDB, tableName, action);

                        LOGGER.info("The request ID from the ockroachDB response :" + cockroachDbPresistenceResponseMap.get(X_AMZN_REQUESTID));
                        LOGGER.info("The response body from the cockroachDB :" + cockroachDbPresistenceResponseMap.get(X_AMZN_RESPONSE));

                        break;
                    }

                    case IS_ONLY_DYNAMODB: {

                        GetItemResult result = null;

                        switch (SYNCHRONICITY_TYPE) {

                            case IS_SYNCHRONOUS: {
                                result = persistenceManager.getDynamodbSync(getItemRequest);
                                break;
                            }

                            case IS_ASYNCHRONOUS: {
                                CompletableFuture<GetItemResult> dynamodbAsync = persistenceManager.getDynamodbAsync(getItemRequest);
                                result = dynamodbAsync.get(3000, TimeUnit.MILLISECONDS);

                                break;
                            }

                            default: {
                                LOGGER.info("The service type is not supported!");
                                break;
                            }
                        }

                        if (result == null) {

                            LOGGER.info("The save operation in the DynamoDB is not successful and hence, will not proceed to commit to the CockrachDB ...");
                            LOGGER.info("The getItemRequest is failed for the getItemRequest Id " + AMZN_REQUESTID);
                            LOGGER.info("The getItemRequest payload is " + requestBody);
                            LOGGER.info("The operation condition is " + OPERATION_CONDITION);

                            return responseMap;
                        }

                        long dDuration = System.currentTimeMillis() - startDTime;
                        System.out.println("ClientRepositroy - GetItemResult took " + dDuration + " milliseconds to run");

                        if (result.getItem() != null) {
                            result.getItem().forEach((key, value) -> System.out.println(key + " " + value.toString()));
                        }

                        String getItemResult = objectMapper.writeValueAsString(result);
                        System.out.println("DynamoDB Put Item Result \n" + getItemResult);

                        if (result.getSdkResponseMetadata() != null) {
                            String requestId = result.getSdkResponseMetadata().getRequestId();
                            responseMap.put(X_AMZN_REQUESTID, requestId);
                        }

                        responseMap.put(X_AMZN_RESPONSE, getItemResult);
                        responseMap.put(DB_TYPE, "DynamoDB");

                        return responseMap;
                    }

                    case IS_ONLY_COCKROACHDB: {

                        JSONObject getItemRequestJsonObj = new JSONObject(getItemRequest);
                        String getItemRequestRequestJsonStr = getItemRequestJsonObj.toString();

                        List<GRPCMapper> glGRPCMapper = getRequestMapper.getGRPCMapper();

                        if (glGRPCMapper == null || glGRPCMapper.isEmpty()) {
                            LOGGER.info("The supporting parameters are not provided ");
                            return new LinkedHashMap<>();
                        }

                        Message getMessageCockroachDB = null;

                        switch (SYNCHRONICITY_TYPE) {
                            case IS_SYNCHRONOUS: {
                                final ManagedChannel customManagedChannel = sCustomManagedChannel.get();
                                getMessageCockroachDB = persistenceManager.getCockroachDBItemSyncGrpc(glGRPCMapper, getItemRequestRequestJsonStr, customManagedChannel);

                                ProtoService.shutdownManagedChannel(customManagedChannel);
                                break;
                            }

                            case IS_ASYNCHRONOUS: {
                                final ManagedChannel customManagedChannel = sCustomManagedChannel.get();
                                CompletableFuture<Message> cockroachdbAsync = persistenceManager.getCockroachdbAsyncGrpc(glGRPCMapper, getItemRequestRequestJsonStr, customManagedChannel);
                                getMessageCockroachDB = cockroachdbAsync.get(3000, TimeUnit.MILLISECONDS);

                                ProtoService.shutdownManagedChannel(customManagedChannel);
                                break;
                            }

                            default: {
                                LOGGER.info("The service type is not supported ...");
                                return responseMap;
                            }
                        }

                        if (getMessageCockroachDB == null) {

                            LOGGER.info("We are unable to get the data from the CockroachDB ...");
                            LOGGER.info("The getItemRequest id is : " + AMZN_REQUESTID);
                            LOGGER.info("The getItemRequest payload is : " + requestBody);
                            LOGGER.info("The operation condition is : " + OPERATION_CONDITION);

                            return responseMap;
                        }

                        Map<String, String> getMessageMap = createResponse(getMessageCockroachDB, tableName, action);
                        return getMessageMap;
                    }

                    default: {
                        LOGGER.info("We dont support this condition at the moment ....");
                        throw new UnsupportedEncodingException("WE don't support this mode of operation in the Get item...");
                    }
                }

                return responseMap;

            }

            case DELETEITEM: {

                RequestMapper requestMapper = (new ServiceMapper()).getServices(action, tableName);
                DeleteItemRequest deleteItemRequest = dynamoDbRequestBuilder.createDeleteItemRequest(tableName, requestBody);

                RequestMapper getRequestMapper = (new ServiceMapper()).getServices(GETITEM, tableName);
                final GetItemRequest getItemRequest = dynamoDbRequestBuilder.createGetItemRequest(tableName, requestBody);

                long startDTime = System.currentTimeMillis();

                switch (MODE_TYPE) {

                    case IS_BOTH_DATABASE: {
                        DeleteItemResult result = null;

                        switch (SYNCHRONICITY_TYPE) {
                            case IS_SYNCHRONOUS: {
                                result = persistenceManager.deleteDynamodbSync(deleteItemRequest);
                                break;
                            }

                            case IS_ASYNCHRONOUS: {
                                CompletableFuture<DeleteItemResult> deleteItemResultCompletableFuture = persistenceManager.deleteDynamodbAsync(deleteItemRequest);
                                result = deleteItemResultCompletableFuture.get(3000, TimeUnit.MILLISECONDS);

                                break;
                            }

                            default: {
                                LOGGER.info("The Service type is not supported - we only support SYNCHRONOUS and ASYNCHRONOUS service type at the moment");
                                return responseMap;
                            }
                        }

                        if (result == null) {

                            LOGGER.info("The save operation in the DynamoDB is not sucessful and hence, will not proceed to commit to the CockrachDB ...");
                            LOGGER.info("The deleteItemRequest is failed for the deleteItemRequest Id " + AMZN_REQUESTID);
                            LOGGER.info("The deleteItemRequest payload is " + requestBody);
                            LOGGER.info("The operation condition is " + OPERATION_CONDITION);

                            return responseMap;
                        }

                        long dDuration = System.currentTimeMillis() - startDTime;
                        System.out.println("ClientRepositroy - GetItemResult took " + dDuration + " milliseconds to run");

                        if (result.getAttributes() != null) {
                            result.getAttributes().forEach((key, value) -> System.out.println(key + " " + value.toString()));
                        }

                        String deleteItemResult = objectMapper.writeValueAsString(result);
                        System.out.println("DynamoDB Deelete Item Result \n" + deleteItemResult);

                        if (result.getSdkResponseMetadata() != null) {
                            String requestId = result.getSdkResponseMetadata().getRequestId();
                            responseMap.put(X_AMZN_REQUESTID, requestId);
                        }

                        responseMap.put(X_AMZN_RESPONSE, deleteItemResult);
                        responseMap.put(DB_TYPE, "DynamoDB");


                        // CockroachDB
                        JSONObject deleteItemRequestJsonObject = new JSONObject(deleteItemRequest);
                        String requestBodyForDeleteItem = deleteItemRequestJsonObject.toString();

                        List<GRPCMapper> deleteGRPCMapper = requestMapper.getGRPCMapper();

                        if (deleteGRPCMapper == null || deleteGRPCMapper.isEmpty()) {
                            LOGGER.info("The required parameters are not provided ..");
                            return responseMap;
                        }

                        JSONObject getItemRequestJsonObject = new JSONObject(getItemRequest);
                        String requestBodyForGetItem = getItemRequestJsonObject.toString();

                        final List<GRPCMapper> getGrpcMapper = getRequestMapper.getGRPCMapper();

                        if (getGrpcMapper == null || getGrpcMapper.isEmpty()) {
                            LOGGER.info("The required parameters are not provided ..");
                            return responseMap;
                        }

                        List<Message> deleteMessages = new ArrayList<>();
                        Message getMessage = null;

                        switch (SYNCHRONICITY_TYPE) {

                            case IS_SYNCHRONOUS: {
                                final ManagedChannel customManagedChannel = sCustomManagedChannel.get();
                                while (RETRY_COUNT <= 3) {

                                    getMessage = persistenceManager.getCockroachDBItemSyncGrpc(getGrpcMapper, requestBodyForGetItem, customManagedChannel);
                                    deleteMessages = persistenceManager.deleteCockroachDBItemSyncGrpc(deleteGRPCMapper, requestBodyForDeleteItem, customManagedChannel);

                                    if (deleteMessages != null && getMessage != null) {
                                        LOGGER.info("successfully delete to the CockroachDB synchronously ....");
                                        RETRY_COUNT = 1;
                                        break;
                                    }

                                    Thread.sleep(1000);
                                    RETRY_COUNT++;
                                }

                                ProtoService.shutdownManagedChannel(customManagedChannel);
                                break;
                            }

                            case IS_ASYNCHRONOUS: {
                                long ASYNC_TIMEOUT = System.currentTimeMillis();
                                final ManagedChannel customManagedChannel = sCustomManagedChannel.get();

                                    out:
                                while (RETRY_COUNT <= 3) {

                                    final CompletableFuture<Message> cockroachdbAsyncGrpc = persistenceManager.getCockroachdbAsyncGrpc(getGrpcMapper, requestBodyForGetItem, customManagedChannel);
                                    getMessage = cockroachdbAsyncGrpc.get(3000, TimeUnit.MILLISECONDS);

                                    while (getMessage != null || System.currentTimeMillis() > ASYNC_TIMEOUT + TIMEOUT_LIMIT * 1000) {

                                        if (System.currentTimeMillis() > ASYNC_TIMEOUT + TIMEOUT_LIMIT * 1000) {
                                            LOGGER.info("We can't process the async request successfully due to timeout and returing an empty response");
                                            break;
                                        }

                                        CompletableFuture<List<Message>> messageCompletableFuture = persistenceManager.deleteCockroachdbAsyncGrpc(deleteGRPCMapper, requestBodyForDeleteItem, customManagedChannel);
                                        deleteMessages = messageCompletableFuture.get(3000, TimeUnit.MILLISECONDS);

                                        if (deleteMessages != null) {
                                            LOGGER.info("Successfully save to the CockroachDB Asynchronously.");
                                            RETRY_COUNT = 1;
                                            break out;
                                        }
                                    }

                                    Thread.sleep(1000);

                                    ASYNC_TIMEOUT = System.currentTimeMillis() + TIMEOUT_LIMIT * 1000;
                                    RETRY_COUNT++;
                                }

                                ProtoService.shutdownManagedChannel(customManagedChannel);
                                break;
                            }

                            default: {
                                LOGGER.info("The Service type is not supported - we only support SYNCHRONOUS and ASYNCHRONOUS service type at the moment");
                                return responseMap;
                            }
                        }

                        if (deleteMessages == null || getMessage == null || deleteMessages.isEmpty()) {

                            LOGGER.info("We are unable to process data to the CockroachDB ...");
                            LOGGER.info("The deleteItemRequest id is : " + AMZN_REQUESTID);
                            LOGGER.info("The deleteItemRequest payload is : " + requestBody);
                            LOGGER.info("The operation condition is : " + OPERATION_CONDITION);

                            return responseMap;
                        }

                        final Map<String, String> cockroachDbPresistenceResponseMap = createResponse(getMessage, tableName, action);

                        LOGGER.info("The request ID from the cockroachDB response :" + cockroachDbPresistenceResponseMap.get(X_AMZN_REQUESTID));
                        LOGGER.info("The response body from the cockroachDB :" + cockroachDbPresistenceResponseMap.get(X_AMZN_RESPONSE));

                        return responseMap;
                    }

                    case IS_ONLY_DYNAMODB: {

                        DeleteItemResult result = null;

                        switch (SYNCHRONICITY_TYPE) {

                            case IS_SYNCHRONOUS: {
                                result = persistenceManager.deleteDynamodbSync(deleteItemRequest);
                                break;
                            }

                            case IS_ASYNCHRONOUS: {
                                CompletableFuture<DeleteItemResult> deleteItemResultCompletableFuture = persistenceManager.deleteDynamodbAsync(deleteItemRequest);
                                result = deleteItemResultCompletableFuture.get(3000, TimeUnit.MILLISECONDS);

                                break;
                            }

                            default: {
                                LOGGER.info("The Service type is not supported - we only support SYNCHRONOUS and ASYNCHRONOUS service type at the moment");
                                return responseMap;
                            }
                        }

                        if (result == null) {

                            LOGGER.info("The save operation in the DynamoDB is not successful and hence, will not proceed to commit to the CockrachDB ...");
                            LOGGER.info("The deleteItemRequest is failed for the deleteItemRequest Id " + AMZN_REQUESTID);
                            LOGGER.info("The deleteItemRequest payload is " + requestBody);
                            LOGGER.info("The operation condition is " + OPERATION_CONDITION);

                            return responseMap;
                        }

                        long dDuration = System.currentTimeMillis() - startDTime;
                        System.out.println("ClientRepositroy - GetItemResult took " + dDuration + " milliseconds to run");

                        if (result.getAttributes() != null) {
                            result.getAttributes().forEach((key, value) -> System.out.println(key + " " + value.toString()));
                        }

                        String deleteItemResult = objectMapper.writeValueAsString(result);
                        System.out.println("DynamoDB Deelete Item Result \n" + deleteItemResult);

                        if (result.getSdkResponseMetadata() != null) {
                            String requestId = result.getSdkResponseMetadata().getRequestId();
                            responseMap.put(X_AMZN_REQUESTID, requestId);
                        }

                        responseMap.put(X_AMZN_RESPONSE, deleteItemResult);
                        responseMap.put(DB_TYPE, "DynamoDB");

                        return responseMap;
                    }

                    case IS_ONLY_COCKROACHDB: {

                        JSONObject deleteItemRequestJsonObject = new JSONObject(deleteItemRequest);
                        String requestBodyForDeleteItem = deleteItemRequestJsonObject.toString();

                        List<GRPCMapper> deleteGrpcMapper = requestMapper.getGRPCMapper();

                        if (deleteGrpcMapper == null || deleteGrpcMapper.isEmpty()) {
                            LOGGER.info("The required parameters are not provided ..");
                            return responseMap;
                        }

                        JSONObject getItemRequestJsonObject = new JSONObject(getItemRequest);
                        String requestBodyForGetItem = getItemRequestJsonObject.toString();

                        final List<GRPCMapper> getGrpcMapper = getRequestMapper.getGRPCMapper();
                        if (getGrpcMapper == null || getGrpcMapper.isEmpty()) {
                            LOGGER.info("The required parameters are not provided ..");
                            return responseMap;
                        }

                        List<Message> deleteCockroachDBItemMessages = new ArrayList<>();
                        Message getMessage = null;

                        switch (SYNCHRONICITY_TYPE) {

                            case IS_SYNCHRONOUS: {
                                final ManagedChannel customManagedChannel = sCustomManagedChannel.get();
                                getMessage = persistenceManager.getCockroachDBItemSyncGrpc(getGrpcMapper, requestBodyForGetItem, customManagedChannel);

                                deleteCockroachDBItemMessages = persistenceManager.deleteCockroachDBItemSyncGrpc(deleteGrpcMapper, requestBodyForDeleteItem, customManagedChannel);

                                ProtoService.shutdownManagedChannel(customManagedChannel);
                                break;
                            }

                            case IS_ASYNCHRONOUS: {
                                long ASYNC_TIMEOUT = System.currentTimeMillis();
                                final ManagedChannel customManagedChannel = sCustomManagedChannel.get();

                                final CompletableFuture<Message> getCockroachdbAsyncGrpc = persistenceManager.getCockroachdbAsyncGrpc(getGrpcMapper, requestBodyForGetItem, customManagedChannel);
                                getMessage = getCockroachdbAsyncGrpc.get(3000, TimeUnit.MILLISECONDS);

                                while (getMessage != null || System.currentTimeMillis() > ASYNC_TIMEOUT + TIMEOUT_LIMIT * 1000) {

                                    if (System.currentTimeMillis() > ASYNC_TIMEOUT + TIMEOUT_LIMIT * 1000) {
                                        LOGGER.info("We can't process the async request successfully due to timeout and returing an empty response");
                                        return responseMap;
                                    }

                                    CompletableFuture<List<Message>> messageCompletableFuture = persistenceManager.deleteCockroachdbAsyncGrpc(deleteGrpcMapper, requestBodyForDeleteItem, customManagedChannel);
                                    deleteCockroachDBItemMessages = messageCompletableFuture.get(3000, TimeUnit.MILLISECONDS);

                                    if (deleteCockroachDBItemMessages != null) {
                                        break;
                                    }
                                }

                                ProtoService.shutdownManagedChannel(customManagedChannel);
                                break;
                            }

                            default: {
                                LOGGER.info("The Service type is not supported - we only support SYNCHRONOUS and ASYNCHRONOUS service type at the moment");
                                return responseMap;
                            }
                        }

                        if (deleteCockroachDBItemMessages == null || getMessage == null || deleteCockroachDBItemMessages.isEmpty()) {

                            LOGGER.info("We are unable to process data to the CockroachDB ...");
                            LOGGER.info("The deleteItemRequest id is : " + AMZN_REQUESTID);
                            LOGGER.info("The deleteItemRequest payload is : " + requestBody);
                            LOGGER.info("The operation condition is : " + OPERATION_CONDITION);

                            return responseMap;
                        }

                        final Map<String, String> cockroachDbPresistenceResponseMap = createResponse(getMessage, tableName, action);

                        LOGGER.info("The request ID from the ockroachDB response :" + cockroachDbPresistenceResponseMap.get(X_AMZN_REQUESTID));
                        LOGGER.info("The response body from the cockroachDB  \n\n:" + cockroachDbPresistenceResponseMap.get(X_AMZN_RESPONSE) + "\n\n");

                        return cockroachDbPresistenceResponseMap;
                    }

                    default: {
                        LOGGER.info("We dont support this condition at the moment ....");
                        break;
                    }
                }

                return responseMap;
            }


            case UPDATEITEM: {

                RequestMapper requestMapper = (new ServiceMapper()).getServices(action, tableName);
                UpdateItemRequest updateItemRequest = dynamoDbRequestBuilder.createUpdateItemRequest(tableName, requestBody);

                RequestMapper requestMapperForGetItem = (new ServiceMapper()).getServices(GETITEM, tableName);

                GetItemRequest getItemRequest = dynamoDbRequestBuilder.createGetItemRequest(tableName, requestBody);

                long startDTime = System.currentTimeMillis();
                System.out.println("updateItemRequest ...\t" + updateItemRequest);

                switch (MODE_TYPE) {

                    case IS_BOTH_DATABASE: {

                        UpdateItemResult result;

                        switch (SYNCHRONICITY_TYPE) {

                            case IS_SYNCHRONOUS: {
                                result = persistenceManager.updateDynamodbSync(updateItemRequest);
                                break;
                            }

                            case IS_ASYNCHRONOUS: {
                                CompletableFuture<UpdateItemResult> updateItemResultCompletableFuture = persistenceManager.updateDynamodbAsync(updateItemRequest);
                                result = updateItemResultCompletableFuture.get(3000, TimeUnit.MILLISECONDS);

                                break;
                            }

                            default: {
                                LOGGER.info("The Service type is not supported - we only support SYNCHRONOUS and ASYNCHRONOUS service type at the moment");
                                return responseMap;
                            }
                        }

                        if (result == null) {

                            LOGGER.info("The request is failed for the request Id " + AMZN_REQUESTID);
                            LOGGER.info("The request payload is " + requestBody);
                            LOGGER.info("The operation condition is " + OPERATION_CONDITION);

                            return responseMap;
                        }

                        long dDuration = System.currentTimeMillis() - startDTime;
                        LOGGER.info("ClientRepositroy - GetItemResult took " + dDuration + " milliseconds to run");

                        String updateItemResult = objectMapper.writeValueAsString(result);
                        LOGGER.info("DynamoDB Update Item Result \n" + updateItemResult);

                        if (result.getSdkResponseMetadata() != null) {
                            String requestId = result.getSdkResponseMetadata().getRequestId();
                            responseMap.put(X_AMZN_REQUESTID, requestId);
                        }

                        responseMap.put(X_AMZN_RESPONSE, updateItemResult);
                        responseMap.put(DB_TYPE, "DynamoDB");


                        // for CockroachDB
                        JSONObject updateItemRequestJsonObject = new JSONObject(updateItemRequest);
                        String updateItemRequestJsonRequestBody = updateItemRequestJsonObject.toString();

                        List<GRPCMapper> updateGRPCMapper = requestMapper.getGRPCMapper();

                        if (updateGRPCMapper == null || updateGRPCMapper.isEmpty()) {
                            LOGGER.info("The parameters are not supported ...");
                            return responseMap;
                        }

                        JSONObject getItemRequestJsonObject = new JSONObject(getItemRequest);
                        String getItemRequestJsonRequestBody = getItemRequestJsonObject.toString();

                        final List<GRPCMapper> getGRPCMapper = requestMapperForGetItem.getGRPCMapper();

                        if (getGRPCMapper == null || getGRPCMapper.isEmpty()) {
                            LOGGER.info("The parameters are not supported ...");
                            return responseMap;
                        }

                        List<Message> cockroachUpdateMessages = new ArrayList<>();
                        Message getMessage = null;

                        // for update item
                        switch (SYNCHRONICITY_TYPE) {

                            case IS_SYNCHRONOUS: {
                                final ManagedChannel customManagedChannel = sCustomManagedChannel.get();
                                while (RETRY_COUNT <= 3) {

                                    cockroachUpdateMessages = persistenceManager.updateCockroachDBItemSyncGrpc(updateGRPCMapper, updateItemRequestJsonRequestBody, customManagedChannel);
                                    getMessage = persistenceManager.getCockroachDBItemSyncGrpc(getGRPCMapper, getItemRequestJsonRequestBody, customManagedChannel);

                                    if (cockroachUpdateMessages != null && getMessage != null) {
                                        LOGGER.info("successfully update the item to the CockroachDB synchronously ....");
                                        RETRY_COUNT = 1;
                                        break;
                                    }

                                    Thread.sleep(1000);
                                    RETRY_COUNT++;
                                }

                                ProtoService.shutdownManagedChannel(customManagedChannel);
                                break;
                            }

                            case IS_ASYNCHRONOUS: {

                                long ASYNC_TIMEOUT = System.currentTimeMillis() + TIMEOUT_LIMIT * 1000;
                                final ManagedChannel customManagedChannel = sCustomManagedChannel.get();

                                    out:
                                while (RETRY_COUNT <= 3) {

                                    CompletableFuture<List<Message>> updateMessageCompletableFuture = persistenceManager.updateCockroachdbAsyncGrpc(updateGRPCMapper, updateItemRequestJsonRequestBody, customManagedChannel);
                                    cockroachUpdateMessages = updateMessageCompletableFuture.get(3000, TimeUnit.MILLISECONDS);

                                    while (cockroachUpdateMessages != null || System.currentTimeMillis() > ASYNC_TIMEOUT) {

                                        if (System.currentTimeMillis() > ASYNC_TIMEOUT) {

                                            LOGGER.info("Timeout for the request is reached...");
                                            break;
                                        }

                                        final CompletableFuture<Message> cockroachdbAsyncGrpc = persistenceManager.getCockroachdbAsyncGrpc(getGRPCMapper, getItemRequestJsonRequestBody, customManagedChannel);
                                        getMessage = cockroachdbAsyncGrpc.get(3000, TimeUnit.MILLISECONDS);

                                        if (getMessage != null) {
                                            LOGGER.info("successfully update the item to the CockroachDB asynchronously and retrieve the get message");
                                            RETRY_COUNT = 1;
                                            break out;
                                        }
                                    }

                                    Thread.sleep(1000);

                                    ASYNC_TIMEOUT = System.currentTimeMillis() + TIMEOUT_LIMIT * 1000;
                                    RETRY_COUNT++;
                                }

                                ProtoService.shutdownManagedChannel(customManagedChannel);
                                break;
                            }

                            default: {
                                LOGGER.info("The Service type is not supported - we only support SYNCHRONOUS and ASYNCHRONOUS service type at the moment");
                                return responseMap;
                            }
                        }

                        if (cockroachUpdateMessages == null || getMessage == null || cockroachUpdateMessages.isEmpty()) {

                            LOGGER.info("We are unable to process data to the CockroachDB ...");
                            LOGGER.info("The request id is : " + AMZN_REQUESTID);
                            LOGGER.info("The request payload is : " + requestBody);
                            LOGGER.info("The operation condition is : " + OPERATION_CONDITION);

                            return responseMap;
                        }

                        Map<String, String> cockroachDbPresistenceResponseMap = createResponse(getMessage, tableName, action);

                        LOGGER.info("The request ID from the ockroachDB response :" + cockroachDbPresistenceResponseMap.get(X_AMZN_REQUESTID));
                        LOGGER.info("The response body from the cockroachDB :" + cockroachDbPresistenceResponseMap.get(X_AMZN_RESPONSE));

                        break;
                    }

                    case IS_ONLY_DYNAMODB: {
                        UpdateItemResult result = null;

                        switch (SYNCHRONICITY_TYPE) {

                            case IS_SYNCHRONOUS: {
                                result = persistenceManager.updateDynamodbSync(updateItemRequest);
                                break;
                            }

                            case IS_ASYNCHRONOUS: {
                                CompletableFuture<UpdateItemResult> updateItemResultCompletableFuture = persistenceManager.updateDynamodbAsync(updateItemRequest);
                                result = updateItemResultCompletableFuture.get(3000, TimeUnit.MILLISECONDS);

                                break;
                            }

                            default: {
                                LOGGER.info("The Service type is not supported - we only support SYNCHRONOUS and ASYNCHRONOUS service type at the moment");
                                return responseMap;
                            }
                        }

                        if (result == null) {

                            LOGGER.info("The request is failed for the request Id " + AMZN_REQUESTID);
                            LOGGER.info("The request payload is " + requestBody);
                            LOGGER.info("The operation condition is " + OPERATION_CONDITION);

                            return responseMap;
                        }

                        long dDuration = System.currentTimeMillis() - startDTime;
                        LOGGER.info("Client Repository - GetItemResult took " + dDuration + " milliseconds to run");

                        String updateItemResult = objectMapper.writeValueAsString(result);
                        LOGGER.info("DynamoDB Update Item Result \n" + updateItemResult);

                        if (result.getSdkResponseMetadata() != null) {
                            String requestId = result.getSdkResponseMetadata().getRequestId();
                            responseMap.put(X_AMZN_REQUESTID, requestId);
                        }

                        responseMap.put(X_AMZN_RESPONSE, updateItemResult);
                        responseMap.put(DB_TYPE, "DynamoDB");

                        break;
                    }

                    case IS_ONLY_COCKROACHDB: {

                        JSONObject updateItemRequestJsonObject = new JSONObject(updateItemRequest);
                        String updateItemRequestJsonRequestBody = updateItemRequestJsonObject.toString();

                        List<GRPCMapper> updateGRPCMapper = requestMapper.getGRPCMapper();

                        if (updateGRPCMapper == null || updateGRPCMapper.isEmpty()) {
                            LOGGER.info("The parameters are not supported ...");
                            return responseMap;
                        }

                        JSONObject getItemRequestJsonObject = new JSONObject(getItemRequest);
                        String getItemRequestJsonRequestBody = getItemRequestJsonObject.toString();

                        final List<GRPCMapper> getGRPCMapper = requestMapperForGetItem.getGRPCMapper();

                        if (getGRPCMapper == null || getGRPCMapper.isEmpty()) {
                            LOGGER.info("The parameters are not supported ...");
                            return responseMap;
                        }

                        List<Message> cockroachDbPresistenceMessages = new ArrayList<>();
                        Message getMessage = null;

                        // for update item
                        switch (SYNCHRONICITY_TYPE) {

                            case IS_SYNCHRONOUS: {
                                final ManagedChannel customManagedChannel = sCustomManagedChannel.get();

                                cockroachDbPresistenceMessages = persistenceManager.updateCockroachDBItemSyncGrpc(updateGRPCMapper, updateItemRequestJsonRequestBody, customManagedChannel);
                                getMessage = persistenceManager.getCockroachDBItemSyncGrpc(getGRPCMapper, getItemRequestJsonRequestBody, customManagedChannel);

                                ProtoService.shutdownManagedChannel(customManagedChannel);
                                break;
                            }

                            case IS_ASYNCHRONOUS: {
                                final long ASYNC_TIMEOUT = System.currentTimeMillis() + TIMEOUT_LIMIT * 1000;
                                final ManagedChannel customManagedChannel = sCustomManagedChannel.get();

                                CompletableFuture<List<Message>> messageCompletableFuture = persistenceManager.updateCockroachdbAsyncGrpc(updateGRPCMapper, updateItemRequestJsonRequestBody, customManagedChannel);
                                cockroachDbPresistenceMessages = messageCompletableFuture.get();

                                while (cockroachDbPresistenceMessages != null || System.currentTimeMillis() > ASYNC_TIMEOUT) {

                                    if (System.currentTimeMillis() > ASYNC_TIMEOUT) {

                                        LOGGER.info("We can't process the async request successfully due to ASYNC_TIMEOUT and returning an empty response");
                                        return responseMap;
                                    }

                                    final CompletableFuture<Message> cockroachdbAsyncGrpc = persistenceManager.getCockroachdbAsyncGrpc(getGRPCMapper, getItemRequestJsonRequestBody, customManagedChannel);
                                    getMessage = cockroachdbAsyncGrpc.get(3000, TimeUnit.MILLISECONDS);

                                    if (getMessage != null) {

                                        LOGGER.info("WE received the get message correctly...");
                                        break;
                                    }
                                }

                                ProtoService.shutdownManagedChannel(customManagedChannel);
                                break;
                            }

                            default: {
                                LOGGER.info("The Service type is not supported - we only support SYNCHRONOUS and ASYNCHRONOUS service type at the moment");
                                return responseMap;
                            }
                        }

                        if (cockroachDbPresistenceMessages == null || getMessage == null || cockroachDbPresistenceMessages.isEmpty()) {

                            LOGGER.info("We are unable to process data to the CockroachDB ...");
                            LOGGER.info("The request id is : " + AMZN_REQUESTID);
                            LOGGER.info("The request payload is : " + requestBody);
                            LOGGER.info("The operation condition is : " + OPERATION_CONDITION);

                            return responseMap;
                        }

                        Map<String, String> getResponseMap = createResponse(getMessage, tableName, action);
                        return getResponseMap;
                    }

                    default: {
                        LOGGER.info("We dont support this condition at the moment ....");

                        break;
                    }
                }

                return responseMap;
            }

            default: {
                LOGGER.info("We don't support this kind of operation currently");
                return responseMap;
            }

        }

    }

}

