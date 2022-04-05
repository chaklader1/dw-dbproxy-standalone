package com.drivewealth.service;


import com.amazonaws.http.HttpResponse;
import com.amazonaws.http.SdkHttpMetadata;
import com.amazonaws.services.dynamodbv2.model.*;
import com.amazonaws.services.dynamodbv2.util.TableUtils;
import com.drivewealth.dbproxy.entity.Account;
import com.drivewealth.dbproxy.entity.User;
import com.drivewealth.setup.MockedTestSetup;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Tag;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;


import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.drivewealth.utils.TestUtils.extractEntityIdWithDynamoDBResponse;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;


@EnableAutoConfiguration
public class DynamoDbPersistenceManagerMockedTest extends MockedTestSetup {



    private final String userEntityID = "userID";
    private final String accountEntityID = "id";

    private static final String USER_TABLE = "User";
    private static final String ACCOUNT_TABLE = "Account";


    @Test
    @Tag("User")
    @Tag("Setup")
    @DisplayName("CREATE USER TABLE: We are testing if the User table is created properly")
    public void test_userTable_isCreated() throws InterruptedException {

        LOGGER.info("We are testing if the User table is created properly");

        CreateTableRequest tableRequest = dynamoDBMapper.generateCreateTableRequest(User.class)
            .withProvisionedThroughput(new ProvisionedThroughput(1L, 1L));

        final boolean tableIfNotExists = TableUtils.createTableIfNotExists(dynamoDBClient, tableRequest);

        assertTrue(tableIfNotExists);
        assertEquals(tableRequest.getTableName(), USER_TABLE);
    }

    @Test
    @Tag("Account")
    @Tag("Setup")
    @DisplayName("CREATE ACCOUNT TABLE: We are testing if the Account table is created properly")
    public void test_accountTable_isCreated() throws InterruptedException {

        LOGGER.info("We are testing if the Account table is created properly");

        CreateTableRequest tableRequest = dynamoDBMapper.generateCreateTableRequest(Account.class)
            .withProvisionedThroughput(new ProvisionedThroughput(1L, 1L));

        final boolean tableIfNotExists = TableUtils.createTableIfNotExists(dynamoDBClient, tableRequest);

        assertTrue(tableIfNotExists);
        assertEquals(tableRequest.getTableName(), ACCOUNT_TABLE);
    }



    // ===========
    // User table
    // ===========

    @Test
    @Tag("User")
    @Tag("Sync")
    @Tag("Mocked")
    @DisplayName("PUT User DynamoDB SYNC mode: test PUT the user to the DynamoDB in the User table")
    public void test_dynamoDB_putItemSync_user_return_result() throws JsonProcessingException {

        final HttpResponse httpResponse = new HttpResponse(null, null);
        httpResponse.setStatusCode(200);
        SdkHttpMetadata sdkHttpMetadata = SdkHttpMetadata.from(httpResponse);

        final PutItemResult putItemResultMocked = new PutItemResult();
        putItemResultMocked.setSdkHttpMetadata(sdkHttpMetadata);

        Mockito.when(dynamoDBClient.putItem(any(PutItemRequest.class))).thenReturn(putItemResultMocked);

        final PutItemRequest putItemRequest = new PutItemRequest();
        final PutItemResult putItemResult = persistenceManager.putDynamodbSync(putItemRequest);

        final int httpStatusCode = putItemResult.getSdkHttpMetadata().getHttpStatusCode();

        assertNotNull(putItemResult);
        assertEquals(200, httpStatusCode);

        Mockito.verify(dynamoDBClient, times(1)).putItem(any(PutItemRequest.class));
    }

    @Test
    @Tag("User")
    @Tag("Sync")
    @Tag("Mocked")
    @DisplayName("GET User DynamoDB SYNC mode: test GET the user to the DynamoDB in the User table")
    public void test_dynamoDB_getItemSync_user_return_result() throws JsonProcessingException {

        final HttpResponse httpResponse = new HttpResponse(null, null);
        httpResponse.setStatusCode(200);
        SdkHttpMetadata sdkHttpMetadata = SdkHttpMetadata.from(httpResponse);

        Map<String, com.amazonaws.services.dynamodbv2.model.AttributeValue> userItem = new HashMap<>(3);
        final String userIdUUID = UUID.randomUUID().toString();
        userItem.put("userID", new com.amazonaws.services.dynamodbv2.model.AttributeValue().withS(userIdUUID));

        final GetItemResult getItemResultMocked = new GetItemResult();
        getItemResultMocked.setItem(userItem);
        getItemResultMocked.setSdkHttpMetadata(sdkHttpMetadata);

        final GetItemRequest getItemRequest = new GetItemRequest();

        Mockito.when(dynamoDBClient.getItem(any(GetItemRequest.class))).thenReturn(getItemResultMocked);

        final GetItemResult getItemResult = persistenceManager.getDynamodbSync(getItemRequest);

        final String getItemResultStr = objectMapper.writeValueAsString(getItemResult);
        final String userIDString = extractEntityIdWithDynamoDBResponse(getItemResultStr, "item", userEntityID);

        final int httpStatusCode = getItemResult.getSdkHttpMetadata().getHttpStatusCode();

        assertNotNull(getItemResult);
        assertEquals(userIdUUID, userIDString);
        assertEquals(200, httpStatusCode);

        Mockito.verify(dynamoDBClient, times(1)).getItem(any(GetItemRequest.class));
    }

    @Test
    @Tag("User")
    @Tag("Sync")
    @Tag("Mocked")
    @DisplayName("UPDATE User DynamoDB SYNC mode: test UPDATE the user to the DynamoDB in the User table")
    public void test_dynamoDB_updateItemSync_user_return_result() throws JsonProcessingException {

        final HttpResponse httpResponse = new HttpResponse(null, null);
        httpResponse.setStatusCode(200);
        SdkHttpMetadata sdkHttpMetadata = SdkHttpMetadata.from(httpResponse);

        Map<String, com.amazonaws.services.dynamodbv2.model.AttributeValue> userItem = new HashMap<>(3);
        final String userIdUUID = UUID.randomUUID().toString();
        userItem.put("userID", new com.amazonaws.services.dynamodbv2.model.AttributeValue().withS(userIdUUID));

        final UpdateItemResult updateItemResultMocked = new UpdateItemResult();
        updateItemResultMocked.setAttributes(userItem);
        updateItemResultMocked.setSdkHttpMetadata(sdkHttpMetadata);

        final UpdateItemRequest updateItemRequest = new UpdateItemRequest();

        Mockito.when(dynamoDBClient.updateItem(any(UpdateItemRequest.class))).thenReturn(updateItemResultMocked);

        final UpdateItemResult updateItemResult = persistenceManager.updateDynamodbSync(updateItemRequest);

        final String updateItemResultStr = objectMapper.writeValueAsString(updateItemResult);
        final String userIDString = extractEntityIdWithDynamoDBResponse(updateItemResultStr, "attributes", userEntityID);

        final int httpStatusCode = updateItemResult.getSdkHttpMetadata().getHttpStatusCode();

        assertNotNull(updateItemResult);
        assertEquals(userIdUUID, userIDString);
        assertEquals(200, httpStatusCode);

        Mockito.verify(dynamoDBClient, times(1)).updateItem(any(UpdateItemRequest.class));
    }

    @Test
    @Tag("User")
    @Tag("Sync")
    @Tag("Mocked")
    @DisplayName("DELETE User DynamoDB SYNC mode: test UPDATE the user to the DynamoDB in the User table")
    public void test_dynamoDB_deleteItemSync_user_return_result() throws JsonProcessingException {

        final HttpResponse httpResponse = new HttpResponse(null, null);
        httpResponse.setStatusCode(200);
        SdkHttpMetadata sdkHttpMetadata = SdkHttpMetadata.from(httpResponse);

        Map<String, com.amazonaws.services.dynamodbv2.model.AttributeValue> userItem = new HashMap<>(3);
        final String userIdUUID = UUID.randomUUID().toString();
        userItem.put("userID", new com.amazonaws.services.dynamodbv2.model.AttributeValue().withS(userIdUUID));

        final DeleteItemResult deleteItemResultMocked = new DeleteItemResult();
        deleteItemResultMocked.setAttributes(userItem);
        deleteItemResultMocked.setSdkHttpMetadata(sdkHttpMetadata);

        final DeleteItemRequest deleteItemRequest = new DeleteItemRequest();

        Mockito.when(dynamoDBClient.deleteItem(any(DeleteItemRequest.class))).thenReturn(deleteItemResultMocked);

        final DeleteItemResult deleteItemResult = persistenceManager.deleteDynamodbSync(deleteItemRequest);

        final String deleteItemResultStr = objectMapper.writeValueAsString(deleteItemResult);
        final String userIDString = extractEntityIdWithDynamoDBResponse(deleteItemResultStr, "attributes", userEntityID);

        final int httpStatusCode = deleteItemResult.getSdkHttpMetadata().getHttpStatusCode();

        assertNotNull(deleteItemResult);

        assertEquals(userIdUUID, userIDString);
        assertEquals(200, httpStatusCode);

        Mockito.verify(dynamoDBClient, times(1)).deleteItem(any(DeleteItemRequest.class));
    }


    @Test
    @Tag("User")
    @Tag("Async")
    @Tag("Mocked")
    @DisplayName("PUT User DynamoDB SYNC mode: test PUT the user to the DynamoDB in the User table")
    public void test_dynamoDB_putItemAsync_user_return_result() throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {

        final HttpResponse httpResponse = new HttpResponse(null, null);
        httpResponse.setStatusCode(200);
        SdkHttpMetadata sdkHttpMetadata = SdkHttpMetadata.from(httpResponse);

        final PutItemResult putItemResultMocked = new PutItemResult();
        putItemResultMocked.setSdkHttpMetadata(sdkHttpMetadata);

        Mockito.when(dynamoDBClient.putItem(any(PutItemRequest.class))).thenReturn(putItemResultMocked);

        final PutItemRequest putItemRequest = new PutItemRequest();
        final CompletableFuture<PutItemResult> putItemResultCompletableFuture = persistenceManager.putDynamodbAsync(putItemRequest);
        final PutItemResult putItemResult = putItemResultCompletableFuture.get(3000, TimeUnit.MILLISECONDS);

        final int httpStatusCode = putItemResult.getSdkHttpMetadata().getHttpStatusCode();

        assertNotNull(putItemResult);
        assertEquals(200, httpStatusCode);

        Mockito.verify(dynamoDBClient, times(1)).putItem(any(PutItemRequest.class));
    }

    @Test
    @Tag("User")
    @Tag("Async")
    @Tag("Mocked")
    @DisplayName("GET User DynamoDB SYNC mode: test GET the user to the DynamoDB in the User table")
    public void test_dynamoDB_getItemAsync_user_return_result() throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {

        final HttpResponse httpResponse = new HttpResponse(null, null);
        httpResponse.setStatusCode(200);
        SdkHttpMetadata sdkHttpMetadata = SdkHttpMetadata.from(httpResponse);

        Map<String, com.amazonaws.services.dynamodbv2.model.AttributeValue> userItem = new HashMap<>(3);
        final String userIdUUID = UUID.randomUUID().toString();
        userItem.put("userID", new com.amazonaws.services.dynamodbv2.model.AttributeValue().withS(userIdUUID));

        final GetItemResult getItemResultMocked = new GetItemResult();
        getItemResultMocked.setItem(userItem);
        getItemResultMocked.setSdkHttpMetadata(sdkHttpMetadata);

        final GetItemRequest getItemRequest = new GetItemRequest();

        Mockito.when(dynamoDBClient.getItem(any(GetItemRequest.class))).thenReturn(getItemResultMocked);

        final CompletableFuture<GetItemResult> dynamodbAsync = persistenceManager.getDynamodbAsync(getItemRequest);
        final GetItemResult getItemResult = dynamodbAsync.get(3000, TimeUnit.MILLISECONDS);

        final String getItemResultStr = objectMapper.writeValueAsString(getItemResult);
        final String userIDString = extractEntityIdWithDynamoDBResponse(getItemResultStr, "item", userEntityID);

        final int httpStatusCode = getItemResult.getSdkHttpMetadata().getHttpStatusCode();

        assertNotNull(getItemResult);
        assertEquals(userIdUUID, userIDString);
        assertEquals(200, httpStatusCode);

        Mockito.verify(dynamoDBClient, times(1)).getItem(any(GetItemRequest.class));
    }

    @Test
    @Tag("User")
    @Tag("Async")
    @Tag("Mocked")
    @DisplayName("UPDATE User DynamoDB SYNC mode: test UPDATE the user to the DynamoDB in the User table")
    public void test_dynamoDB_updateItemAsync_user_return_result() throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {

        final HttpResponse httpResponse = new HttpResponse(null, null);
        httpResponse.setStatusCode(200);
        SdkHttpMetadata sdkHttpMetadata = SdkHttpMetadata.from(httpResponse);

        Map<String, com.amazonaws.services.dynamodbv2.model.AttributeValue> userItem = new HashMap<>(3);
        final String userIdUUID = UUID.randomUUID().toString();
        userItem.put("userID", new com.amazonaws.services.dynamodbv2.model.AttributeValue().withS(userIdUUID));

        final UpdateItemResult updateItemResultMocked = new UpdateItemResult();
        updateItemResultMocked.setAttributes(userItem);
        updateItemResultMocked.setSdkHttpMetadata(sdkHttpMetadata);

        final UpdateItemRequest updateItemRequest = new UpdateItemRequest();

        Mockito.when(dynamoDBClient.updateItem(any(UpdateItemRequest.class))).thenReturn(updateItemResultMocked);

        final CompletableFuture<UpdateItemResult> updateItemResultCompletableFuture = persistenceManager.updateDynamodbAsync(updateItemRequest);
        final UpdateItemResult updateItemResult = updateItemResultCompletableFuture.get(3000, TimeUnit.MILLISECONDS);

        final String updateItemResultStr = objectMapper.writeValueAsString(updateItemResult);
        final String userIDString = extractEntityIdWithDynamoDBResponse(updateItemResultStr, "attributes", userEntityID);

        final int httpStatusCode = updateItemResult.getSdkHttpMetadata().getHttpStatusCode();

        assertNotNull(updateItemResult);
        assertEquals(userIdUUID, userIDString);
        assertEquals(200, httpStatusCode);

        Mockito.verify(dynamoDBClient, times(1)).updateItem(any(UpdateItemRequest.class));
    }

    @Test
    @Tag("User")
    @Tag("Async")
    @Tag("Mocked")
    @DisplayName("DELETE User DynamoDB SYNC mode: test UPDATE the user to the DynamoDB in the User table")
    public void test_dynamoDB_deleteItemAsync_user_return_result() throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {

        final HttpResponse httpResponse = new HttpResponse(null, null);
        httpResponse.setStatusCode(200);
        SdkHttpMetadata sdkHttpMetadata = SdkHttpMetadata.from(httpResponse);

        Map<String, com.amazonaws.services.dynamodbv2.model.AttributeValue> userItem = new HashMap<>(3);
        final String userIdUUID = UUID.randomUUID().toString();
        userItem.put("userID", new com.amazonaws.services.dynamodbv2.model.AttributeValue().withS(userIdUUID));

        final DeleteItemResult deleteItemResultMocked = new DeleteItemResult();
        deleteItemResultMocked.setAttributes(userItem);
        deleteItemResultMocked.setSdkHttpMetadata(sdkHttpMetadata);

        final DeleteItemRequest deleteItemRequest = new DeleteItemRequest();

        Mockito.when(dynamoDBClient.deleteItem(any(DeleteItemRequest.class))).thenReturn(deleteItemResultMocked);

        final CompletableFuture<DeleteItemResult> deleteItemResultCompletableFuture = persistenceManager.deleteDynamodbAsync(deleteItemRequest);
        final DeleteItemResult deleteItemResult = deleteItemResultCompletableFuture.get(3000, TimeUnit.MILLISECONDS);

        final String deleteItemResultStr = objectMapper.writeValueAsString(deleteItemResult);
        final String userIDString = extractEntityIdWithDynamoDBResponse(deleteItemResultStr, "attributes", userEntityID);

        final int httpStatusCode = deleteItemResult.getSdkHttpMetadata().getHttpStatusCode();

        assertNotNull(deleteItemResult);

        assertEquals(userIdUUID, userIDString);
        assertEquals(200, httpStatusCode);

        Mockito.verify(dynamoDBClient, times(1)).deleteItem(any(DeleteItemRequest.class));
    }


    //==============
    // Account Table
    //==============

    @Test
    @Tag("Account")
    @Tag("Sync")
    @Tag("Mocked")
    @DisplayName("PUT Account DynamoDB SYNC mode: test PUT the account to the DynamoDB in the Account table")
    public void test_dynamoDB_putItemSync_account_return_result() throws JsonProcessingException {

        final HttpResponse httpResponse = new HttpResponse(null, null);
        httpResponse.setStatusCode(200);
        SdkHttpMetadata sdkHttpMetadata = SdkHttpMetadata.from(httpResponse);

        final PutItemResult putItemResultMocked = new PutItemResult();
        putItemResultMocked.setSdkHttpMetadata(sdkHttpMetadata);

        Mockito.when(dynamoDBClient.putItem(any(PutItemRequest.class))).thenReturn(putItemResultMocked);

        final PutItemRequest putItemRequest = new PutItemRequest();
        final PutItemResult putItemResult = persistenceManager.putDynamodbSync(putItemRequest);

        final int httpStatusCode = putItemResult.getSdkHttpMetadata().getHttpStatusCode();

        assertNotNull(putItemResult);
        assertEquals(200, httpStatusCode);

        Mockito.verify(dynamoDBClient, times(1)).putItem(any(PutItemRequest.class));
    }

    @Test
    @Tag("Account")
    @Tag("Sync")
    @Tag("Mocked")
    @DisplayName("GET Account DynamoDB SYNC mode: test GET the Account to the DynamoDB in the Account table")
    public void test_dynamoDB_getItemSync_account_return_result() throws JsonProcessingException {

        final HttpResponse httpResponse = new HttpResponse(null, null);
        httpResponse.setStatusCode(200);
        SdkHttpMetadata sdkHttpMetadata = SdkHttpMetadata.from(httpResponse);

        Map<String, com.amazonaws.services.dynamodbv2.model.AttributeValue> accountItem = new HashMap<>(3);
        final String accountIdUUID = UUID.randomUUID().toString();
        accountItem.put("id", new com.amazonaws.services.dynamodbv2.model.AttributeValue().withS(accountIdUUID));

        final GetItemResult getItemResultMocked = new GetItemResult();
        getItemResultMocked.setItem(accountItem);
        getItemResultMocked.setSdkHttpMetadata(sdkHttpMetadata);

        final GetItemRequest getItemRequest = new GetItemRequest();

        Mockito.when(dynamoDBClient.getItem(any(GetItemRequest.class))).thenReturn(getItemResultMocked);

        final GetItemResult getItemResult = persistenceManager.getDynamodbSync(getItemRequest);

        final String getItemResultStr = objectMapper.writeValueAsString(getItemResult);
        final String accountIDString = extractEntityIdWithDynamoDBResponse(getItemResultStr, "item", accountEntityID);

        final int httpStatusCode = getItemResult.getSdkHttpMetadata().getHttpStatusCode();

        assertNotNull(getItemResult);
        assertEquals(accountIdUUID, accountIDString);
        assertEquals(200, httpStatusCode);

        Mockito.verify(dynamoDBClient, times(1)).getItem(any(GetItemRequest.class));
    }

    @Test
    @Tag("Account")
    @Tag("Sync")
    @Tag("Mocked")
    @DisplayName("UPDATE Account DynamoDB SYNC mode: test UPDATE the Account to the DynamoDB in the Account table")
    public void test_dynamoDB_updateItemSync_account_return_result() throws JsonProcessingException {

        final HttpResponse httpResponse = new HttpResponse(null, null);
        httpResponse.setStatusCode(200);
        SdkHttpMetadata sdkHttpMetadata = SdkHttpMetadata.from(httpResponse);

        Map<String, com.amazonaws.services.dynamodbv2.model.AttributeValue> accountItem = new HashMap<>(3);
        final String accountIdUUID = UUID.randomUUID().toString();
        accountItem.put("id", new com.amazonaws.services.dynamodbv2.model.AttributeValue().withS(accountIdUUID));

        final UpdateItemResult updateItemResultMocked = new UpdateItemResult();
        updateItemResultMocked.setAttributes(accountItem);
        updateItemResultMocked.setSdkHttpMetadata(sdkHttpMetadata);

        final UpdateItemRequest updateItemRequest = new UpdateItemRequest();

        Mockito.when(dynamoDBClient.updateItem(any(UpdateItemRequest.class))).thenReturn(updateItemResultMocked);

        final UpdateItemResult updateItemResult = persistenceManager.updateDynamodbSync(updateItemRequest);

        final String updateItemResultStr = objectMapper.writeValueAsString(updateItemResult);
        final String accountIDString = extractEntityIdWithDynamoDBResponse(updateItemResultStr, "attributes", accountEntityID);

        final int httpStatusCode = updateItemResult.getSdkHttpMetadata().getHttpStatusCode();

        assertNotNull(updateItemResult);
        assertEquals(accountIdUUID, accountIDString);
        assertEquals(200, httpStatusCode);

        Mockito.verify(dynamoDBClient, times(1)).updateItem(any(UpdateItemRequest.class));
    }

    @Test
    @Tag("Account")
    @Tag("Sync")
    @Tag("Mocked")
    @DisplayName("DELETE Account DynamoDB SYNC mode: test UPDATE the Account to the DynamoDB in the Account table")
    public void test_dynamoDB_deleteItemSync_account_return_result() throws JsonProcessingException {

        final HttpResponse httpResponse = new HttpResponse(null, null);
        httpResponse.setStatusCode(200);
        SdkHttpMetadata sdkHttpMetadata = SdkHttpMetadata.from(httpResponse);

        Map<String, com.amazonaws.services.dynamodbv2.model.AttributeValue> userItem = new HashMap<>(3);
        final String accountIdUUID = UUID.randomUUID().toString();
        userItem.put("id", new com.amazonaws.services.dynamodbv2.model.AttributeValue().withS(accountIdUUID));

        final DeleteItemResult deleteItemResultMocked = new DeleteItemResult();
        deleteItemResultMocked.setAttributes(userItem);
        deleteItemResultMocked.setSdkHttpMetadata(sdkHttpMetadata);

        final DeleteItemRequest deleteItemRequest = new DeleteItemRequest();

        Mockito.when(dynamoDBClient.deleteItem(any(DeleteItemRequest.class))).thenReturn(deleteItemResultMocked);

        final DeleteItemResult deleteItemResult = persistenceManager.deleteDynamodbSync(deleteItemRequest);

        final String deleteItemResultStr = objectMapper.writeValueAsString(deleteItemResult);
        final String accountIDString = extractEntityIdWithDynamoDBResponse(deleteItemResultStr, "attributes", accountEntityID);

        final int httpStatusCode = deleteItemResult.getSdkHttpMetadata().getHttpStatusCode();

        assertNotNull(deleteItemResult);
        assertEquals(accountIdUUID, accountIDString);
        assertEquals(200, httpStatusCode);

        Mockito.verify(dynamoDBClient, times(1)).deleteItem(any(DeleteItemRequest.class));
    }


    @Test
    @Tag("Account")
    @Tag("Async")
    @Tag("Mocked")
    @DisplayName("PUT Account DynamoDB SYNC mode: test PUT the Account to the DynamoDB in the Account table")
    public void test_dynamoDB_putItemAsync_account_return_result() throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {

        final HttpResponse httpResponse = new HttpResponse(null, null);
        httpResponse.setStatusCode(200);
        SdkHttpMetadata sdkHttpMetadata = SdkHttpMetadata.from(httpResponse);

        final PutItemResult putItemResultMocked = new PutItemResult();
        putItemResultMocked.setSdkHttpMetadata(sdkHttpMetadata);

        Mockito.when(dynamoDBClient.putItem(any(PutItemRequest.class))).thenReturn(putItemResultMocked);

        final PutItemRequest putItemRequest = new PutItemRequest();
        final CompletableFuture<PutItemResult> putItemResultCompletableFuture = persistenceManager.putDynamodbAsync(putItemRequest);
        final PutItemResult putItemResult = putItemResultCompletableFuture.get(3000, TimeUnit.MILLISECONDS);

        final int httpStatusCode = putItemResult.getSdkHttpMetadata().getHttpStatusCode();

        assertNotNull(putItemResult);
        assertEquals(200, httpStatusCode);

        Mockito.verify(dynamoDBClient, times(1)).putItem(any(PutItemRequest.class));
    }

    @Test
    @Tag("Account")
    @Tag("Async")
    @Tag("Mocked")
    @DisplayName("GET Account DynamoDB SYNC mode: test GET the Account to the DynamoDB in the Account table")
    public void test_dynamoDB_getItemAsync_account_return_result() throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {

        final HttpResponse httpResponse = new HttpResponse(null, null);
        httpResponse.setStatusCode(200);
        SdkHttpMetadata sdkHttpMetadata = SdkHttpMetadata.from(httpResponse);

        Map<String, com.amazonaws.services.dynamodbv2.model.AttributeValue> userItem = new HashMap<>(3);
        final String accountIdUUID = UUID.randomUUID().toString();
        userItem.put("id", new com.amazonaws.services.dynamodbv2.model.AttributeValue().withS(accountIdUUID));

        final GetItemResult getItemResultMocked = new GetItemResult();
        getItemResultMocked.setItem(userItem);
        getItemResultMocked.setSdkHttpMetadata(sdkHttpMetadata);

        final GetItemRequest getItemRequest = new GetItemRequest();

        Mockito.when(dynamoDBClient.getItem(any(GetItemRequest.class))).thenReturn(getItemResultMocked);

        final CompletableFuture<GetItemResult> dynamodbAsync = persistenceManager.getDynamodbAsync(getItemRequest);
        final GetItemResult getItemResult = dynamodbAsync.get(3000, TimeUnit.MILLISECONDS);

        final String getItemResultStr = objectMapper.writeValueAsString(getItemResult);
        final String accountIDString = extractEntityIdWithDynamoDBResponse(getItemResultStr, "item", accountEntityID);

        final int httpStatusCode = getItemResult.getSdkHttpMetadata().getHttpStatusCode();

        assertNotNull(getItemResult);
        assertEquals(accountIdUUID, accountIDString);
        assertEquals(200, httpStatusCode);

        Mockito.verify(dynamoDBClient, times(1)).getItem(any(GetItemRequest.class));
    }

    @Test
    @Tag("Account")
    @Tag("Async")
    @Tag("Mocked")
    @DisplayName("UPDATE Account DynamoDB SYNC mode: test UPDATE the Account to the DynamoDB in the Account table")
    public void test_dynamoDB_updateItemAsync_account_return_result() throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {

        final HttpResponse httpResponse = new HttpResponse(null, null);
        httpResponse.setStatusCode(200);
        SdkHttpMetadata sdkHttpMetadata = SdkHttpMetadata.from(httpResponse);

        Map<String, com.amazonaws.services.dynamodbv2.model.AttributeValue> accountItem = new HashMap<>(3);
        final String accountIdUUID = UUID.randomUUID().toString();
        accountItem.put("id", new com.amazonaws.services.dynamodbv2.model.AttributeValue().withS(accountIdUUID));

        final UpdateItemResult updateItemResultMocked = new UpdateItemResult();
        updateItemResultMocked.setAttributes(accountItem);
        updateItemResultMocked.setSdkHttpMetadata(sdkHttpMetadata);

        final UpdateItemRequest updateItemRequest = new UpdateItemRequest();

        Mockito.when(dynamoDBClient.updateItem(any(UpdateItemRequest.class))).thenReturn(updateItemResultMocked);

        final CompletableFuture<UpdateItemResult> updateItemResultCompletableFuture = persistenceManager.updateDynamodbAsync(updateItemRequest);
        final UpdateItemResult updateItemResult = updateItemResultCompletableFuture.get(3000, TimeUnit.MILLISECONDS);

        final String updateItemResultStr = objectMapper.writeValueAsString(updateItemResult);
        final String accountIDString = extractEntityIdWithDynamoDBResponse(updateItemResultStr, "attributes", accountEntityID);

        final int httpStatusCode = updateItemResult.getSdkHttpMetadata().getHttpStatusCode();

        assertNotNull(updateItemResult);
        assertEquals(accountIdUUID, accountIDString);
        assertEquals(200, httpStatusCode);

        Mockito.verify(dynamoDBClient, times(1)).updateItem(any(UpdateItemRequest.class));
    }

    @Test
    @Tag("Account")
    @Tag("Async")
    @Tag("Mocked")
    @DisplayName("DELETE Account DynamoDB SYNC mode: test UPDATE the Account to the DynamoDB in the Account table")
    public void test_dynamoDB_deleteItemAsync_account_return_result() throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {

        final HttpResponse httpResponse = new HttpResponse(null, null);
        httpResponse.setStatusCode(200);
        SdkHttpMetadata sdkHttpMetadata = SdkHttpMetadata.from(httpResponse);

        Map<String, com.amazonaws.services.dynamodbv2.model.AttributeValue> accountItem = new HashMap<>(3);
        final String accountIdUUID = UUID.randomUUID().toString();
        accountItem.put("id", new com.amazonaws.services.dynamodbv2.model.AttributeValue().withS(accountIdUUID));

        final DeleteItemResult deleteItemResultMocked = new DeleteItemResult();
        deleteItemResultMocked.setAttributes(accountItem);
        deleteItemResultMocked.setSdkHttpMetadata(sdkHttpMetadata);

        final DeleteItemRequest deleteItemRequest = new DeleteItemRequest();

        Mockito.when(dynamoDBClient.deleteItem(any(DeleteItemRequest.class))).thenReturn(deleteItemResultMocked);

        final CompletableFuture<DeleteItemResult> deleteItemResultCompletableFuture = persistenceManager.deleteDynamodbAsync(deleteItemRequest);
        final DeleteItemResult deleteItemResult = deleteItemResultCompletableFuture.get(3000, TimeUnit.MILLISECONDS);

        final String deleteItemResultStr = objectMapper.writeValueAsString(deleteItemResult);
        final String accountIDString = extractEntityIdWithDynamoDBResponse(deleteItemResultStr, "attributes", accountEntityID);

        final int httpStatusCode = deleteItemResult.getSdkHttpMetadata().getHttpStatusCode();

        assertNotNull(deleteItemResult);
        assertEquals(accountIdUUID, accountIDString);
        assertEquals(200, httpStatusCode);

        Mockito.verify(dynamoDBClient, times(1)).deleteItem(any(DeleteItemRequest.class));
    }

}
