package com.drivewealth.dbproxy.service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.*;
import com.drivewealth.dbproxy.mapper.GRPCMapper;
import com.drivewealth.dbproxy.util.ProtoUtil;
import com.google.protobuf.Message;
import io.grpc.ManagedChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Component
public class PersistenceManager {


    public static final Logger LOGGER = LoggerFactory.getLogger(DBProxyService.class);

    public final static int NUMBER_OF_THREADS_EXECUTOR_SERVICE = 4;

    @Value("${auth.token}")
    private String authorizationBearerToken;


    @Autowired
    private AmazonDynamoDB amazonDynamoDBClient;

    public void setAmazonDynamoDBClient(AmazonDynamoDB amazonDynamoDBClient) {
        this.amazonDynamoDBClient = amazonDynamoDBClient;
    }


    // DYNAMODB OPERATIONS

    // For DynamoDB Sync
    public PutItemResult putDynamodbSync(PutItemRequest putItemRequest) {
        return amazonDynamoDBClient.putItem(putItemRequest);
    }

    public GetItemResult getDynamodbSync(GetItemRequest getItemRequest) {
        return amazonDynamoDBClient.getItem(getItemRequest);
    }

    public DeleteItemResult deleteDynamodbSync(DeleteItemRequest deleteItemRequest) {
        return amazonDynamoDBClient.deleteItem(deleteItemRequest);
    }

    public UpdateItemResult updateDynamodbSync(UpdateItemRequest updateItemRequest) {
        return amazonDynamoDBClient.updateItem(updateItemRequest);
    }

    // For DynamoDB Async
    public CompletableFuture<PutItemResult> putDynamodbAsync(PutItemRequest request) {

        ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS_EXECUTOR_SERVICE);
        CompletableFuture<PutItemResult> deleteItemResultCompletableFuture = CompletableFuture.supplyAsync(() -> amazonDynamoDBClient.putItem(request), executorService);

        return deleteItemResultCompletableFuture.thenApply(res -> {

            if (res == null) {
                LOGGER.info("The save operation in the async mode in the DynamoDB is not successful ");
            }

            return res;
        });
    }

    public CompletableFuture<GetItemResult> getDynamodbAsync(GetItemRequest request) {

        ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS_EXECUTOR_SERVICE);
        CompletableFuture<GetItemResult> deleteItemResultCompletableFuture = CompletableFuture.supplyAsync(() -> amazonDynamoDBClient.getItem(request), executorService);

        return deleteItemResultCompletableFuture.thenApply(res -> {

            if (res == null) {

                LOGGER.info("The get operation in the async mode in the DynamoDB is not successfull ");
            }

            return res;
        });
    }

    public CompletableFuture<DeleteItemResult> deleteDynamodbAsync(DeleteItemRequest request) {

        ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS_EXECUTOR_SERVICE);
        CompletableFuture<DeleteItemResult> deleteItemResultCompletableFuture = CompletableFuture.supplyAsync(() -> amazonDynamoDBClient.deleteItem(request), executorService);

        return deleteItemResultCompletableFuture.thenApply(res -> {

            if (res == null) {
                LOGGER.info("The delete operation in the async mode in the DynamoDB is not successfull ");
            }
            return res;
        });
    }

    public CompletableFuture<UpdateItemResult> updateDynamodbAsync(UpdateItemRequest request) {

        ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS_EXECUTOR_SERVICE);
        CompletableFuture<UpdateItemResult> updateItemResultCompletableFuture = CompletableFuture.supplyAsync(() -> amazonDynamoDBClient.updateItem(request), executorService);

        return updateItemResultCompletableFuture.thenApply(res -> {

            if (res == null) {
                LOGGER.info("The update operation in the async mode in the DynamoDB is not successfull ");
            }
            return res;
        });
    }


    // COCKROACHDB OPERATIONS
    // CockroachDB Sync
    public <T extends Message> List<T> saveCockroachDBSyncGrpc(List<GRPCMapper> lGRPCMapper, String requestBody, ManagedChannel managedChannel) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, IOException {

        List<T> saveCockroachDbMessageList = new ArrayList<>();

        for (GRPCMapper grpcMapper : lGRPCMapper) {
            LOGGER.info("CR GRPCMapper.getClassName ={}", grpcMapper.getClassName());

            final boolean isGrpcPocProvidedProto = grpcMapper.getProtoType().equalsIgnoreCase("GrpcPoc");
            Class<?> className = isGrpcPocProvidedProto ? com.dw.acatsdomain.grpc.GrpcPocServiceOuterClass.GrpcPocDTO.class: Class.forName(grpcMapper.getClassName());

            Class<?> implementedClassType = Class.forName(grpcMapper.getImplementedClass());

            Method method = implementedClassType.getDeclaredMethod(grpcMapper.getServiceName(), className);

            final Class<?> aClass = Class.forName(grpcMapper.getImplementedClass());
            final Constructor<?> constructor = aClass.getConstructor(ManagedChannel.class, String.class);

            final Object updateKeyInstance = ProtoUtil.getInstance(requestBody, grpcMapper.getProtoType());
            final Object obj = constructor.newInstance(managedChannel, authorizationBearerToken);

            final T t = (T) method.invoke(obj, updateKeyInstance);

            LOGGER.info("CR GRPCMapper MethodName ={}", method.getName());
            LOGGER.info("CR GRPCMapper  = {} row(s) Inserted", t.getAllFields().toString());

            saveCockroachDbMessageList.add(t);
        }

        return saveCockroachDbMessageList;
    }

    public <T extends Message> T getCockroachDBItemSyncGrpc(List<GRPCMapper> lGRPCMapper, String requestBody, ManagedChannel managedChannel) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, IOException {
        T t = null;
        for (GRPCMapper grpcMapper : lGRPCMapper) {
            LOGGER.info("CR GRPCMapper.getClassName ={}", grpcMapper.getClassName());

            Class<?> className = Class.forName(grpcMapper.getClassName());
            Class<?> implementedClassType = Class.forName(grpcMapper.getImplementedClass());
            Method userMethod = implementedClassType.getDeclaredMethod(grpcMapper.getServiceName(), className);

            LOGGER.info("CR GRPCMapper MethodName ={}", userMethod.getName());

            final Class<?> aClass = Class.forName(grpcMapper.getImplementedClass());
            final Constructor<?> constructor = aClass.getConstructor(ManagedChannel.class, String.class);

            final Object obj = constructor.newInstance(managedChannel, authorizationBearerToken);
            final Object getInstance = ProtoUtil.getKeyInstance(requestBody, grpcMapper.getProtoType());

            t = (T) userMethod.invoke(obj, getInstance);
        }

        return t;
    }

    public <T extends Message> List<T> deleteCockroachDBItemSyncGrpc(List<GRPCMapper> lGRPCMapper, String requestBody, ManagedChannel managedChannel) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, IOException {
        List<T> tList = new ArrayList<>();

        for (GRPCMapper grpcMapper : lGRPCMapper) {

            LOGGER.info("CR GRPCMapper.getClassName ={}", grpcMapper.getClassName());

            Class<?> className = Class.forName(grpcMapper.getClassName());
            Class<?> implementedClassType = Class.forName(grpcMapper.getImplementedClass());
            Method userMethod = implementedClassType.getDeclaredMethod(grpcMapper.getServiceName(), className);

            LOGGER.info("CR GRPCMapper MethodName ={}", userMethod.getName());

            final Class<?> aClass = Class.forName(grpcMapper.getImplementedClass());
            final Constructor<?> constructor = aClass.getConstructor(ManagedChannel.class, String.class);

            final Object obj = constructor.newInstance(managedChannel, authorizationBearerToken);
            final Object keyInstance = ProtoUtil.getKeyInstance(requestBody, grpcMapper.getProtoType());

            final T t = (T) userMethod.invoke(obj, keyInstance);

            tList.add(t);
        }

        return tList;
    }

    public <T extends Message> List<T> updateCockroachDBItemSyncGrpc(List<GRPCMapper> lGRPCMapper, String requestBody, ManagedChannel managedChannel) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, IOException {

        List<T> tList = new ArrayList<>();
        for (GRPCMapper grpcMapper : lGRPCMapper) {
            LOGGER.info("CR GRPCMapper.getClassName ={}", grpcMapper.getClassName());

            Class<?> className = Class.forName(grpcMapper.getClassName());
            Class<?> implementedClassType = Class.forName(grpcMapper.getImplementedClass());
            Method userMethod = implementedClassType.getDeclaredMethod(grpcMapper.getServiceName(), className);

            LOGGER.info("CR GRPCMapper MethodName ={}", userMethod.getName());

            final Class<?> aClass = Class.forName(grpcMapper.getImplementedClass());
            final Constructor<?> constructor = aClass.getConstructor(ManagedChannel.class, String.class);

            final Object obj = constructor.newInstance(managedChannel, authorizationBearerToken);
            final Object updateKeyInstance = ProtoUtil.updateKeyInstance(requestBody, grpcMapper.getProtoType());

            final T t = (T) userMethod.invoke(obj, updateKeyInstance);
            tList.add(t);
        }

        return tList;
    }

    // CockroachDB async
    public CompletableFuture<List<Message>> saveCockroachdbAsyncGrpc(List<GRPCMapper> lGRPCMapper, String requestBody, ManagedChannel managedChannel) {

        ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS_EXECUTOR_SERVICE);
        CompletableFuture<List<Message>> saveItemResultCompletableFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return saveCockroachDBSyncGrpc(lGRPCMapper, requestBody, managedChannel);
            } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException | IOException e) {
                e.printStackTrace();
            }
            return null;
        }, executorService);

        return saveItemResultCompletableFuture.thenApply(result -> {

            if (result == null) {
                LOGGER.info("The save operation in the async mode in the CockroachDB is not successful ");
            }
            return result;
        });
    }

    public CompletableFuture<Message> getCockroachdbAsyncGrpc(List<GRPCMapper> lGRPCMapper, String requestBody, ManagedChannel managedChannel) {

        ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS_EXECUTOR_SERVICE);
        CompletableFuture<Message> updateItemResultCompletableFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return getCockroachDBItemSyncGrpc(lGRPCMapper, requestBody, managedChannel);
            } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException | IOException e) {
                e.printStackTrace();
            }
            return null;
        }, executorService);

        return updateItemResultCompletableFuture.thenApply(result -> {

            if (result == null) {
                LOGGER.info("The get operation in the async mode in the CockroachDB is not successful ");
            }
            return result;
        });
    }

    public CompletableFuture<List<Message>> deleteCockroachdbAsyncGrpc(List<GRPCMapper> lGRPCMapper, String requestBody, ManagedChannel managedChannel) {

        ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS_EXECUTOR_SERVICE);
        CompletableFuture<List<Message>> updateItemResultCompletableFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return deleteCockroachDBItemSyncGrpc(lGRPCMapper, requestBody, managedChannel);
            } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException | IOException e) {
                e.printStackTrace();
            }
            return null;
        }, executorService);

        return updateItemResultCompletableFuture.thenApply(result -> {

            if (result == null) {
                LOGGER.info("The delete operation in the async mode in the CockroachDB is not successful ");
            }
            return result;
        });
    }

    public CompletableFuture<List<Message>> updateCockroachdbAsyncGrpc(List<GRPCMapper> lGRPCMapper, String requestBody, ManagedChannel managedChannel) {

        ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS_EXECUTOR_SERVICE);
        CompletableFuture<List<Message>> updateItemResultCompletableFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return updateCockroachDBItemSyncGrpc(lGRPCMapper, requestBody, managedChannel);
            } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException | IOException e) {
                e.printStackTrace();
            }
            return null;
        }, executorService);

        return updateItemResultCompletableFuture.whenCompleteAsync((result, err) -> {

            if (result == null) {
                LOGGER.info("The update operation in the async mode in the CockroachDB is not successful ");
                err.printStackTrace();
            }
        });
    }

}
