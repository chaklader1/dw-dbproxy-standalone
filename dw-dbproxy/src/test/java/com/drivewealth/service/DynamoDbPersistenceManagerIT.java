package com.drivewealth.service;


import com.amazonaws.services.dynamodbv2.model.*;
import com.drivewealth.setup.IntegrationTestSetup;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

import java.io.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.Order;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import static com.drivewealth.utils.TestUtils.readFileFromResourcesFolder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


/*
* TODO: write tests for the gRPC service
* TODO: write tests for the negative scenarios and exceptions
* TODO: write tests with argument matchers and answers, nested tests
* TODO: write the test suite
* TODO: define the slow and fast tests in the pom.xml file
*
* */
/*
 *
 * we can also use docker image to run the DynamoDB local server
 *
 *   docker run -p 8000:8000 amazon/dynamodb-local
 *
 * Here is an answer that discuss it: <https://stackoverflow.com/questions/26901613/easier-dynamodb-local-testing>
 * */
//@EnableAutoConfiguration
public class DynamoDbPersistenceManagerIT extends IntegrationTestSetup {


    // TODO: can I create the DynamoDB tables without using the Dynamo client????

    public static final Logger LOGGER = LoggerFactory.getLogger(DynamoDbPersistenceManagerIT.class);


    private final String userEntityID = "userID";
    private final String accountEntityID = "id";

    private static final String USER_TABLE = "User";
    private static final String ACCOUNT_TABLE = "Account";

    private static String userStr = null;
    private static String accountStr = null;


    @PostConstruct
    private void postConstruct() {

        userStr = readFileFromResourcesFolder("responses/user.json");
        accountStr = readFileFromResourcesFolder("responses/account.json");
    }

    @PreDestroy
    public void preDestroy() {

        userStr = StringUtils.EMPTY;
        accountStr = StringUtils.EMPTY;
    }



    // ===========
    // for user
    // ===========
    @Test
    @Tag("User")
    @Tag("Sync")
    @Tag("IT")
    @Order(1)
    @DisplayName("PUT User DynamoDB SYNC mode: test SAVE the user to the DynamoDB in the User table")
    public void test1() throws IOException, InterruptedException {

        LOGGER.info("The is is order 1");

        final PutItemRequest putItemRequest = dynamodbRequestBuilder.createPutItemRequest(userStr, USER_TABLE);

        final PutItemResult putItemResult = persistenceManager.putDynamodbSync(putItemRequest);
        final int httpStatusCode = putItemResult.getSdkHttpMetadata().getHttpStatusCode();

        assertNotNull(putItemResult);
        assertEquals(200, httpStatusCode);
    }

    @Test
    @Tag("User")
    @Tag("Sync")
    @Tag("IT")
    @Order(2)
    @DisplayName("GET User DynamoDB SYNC mode: test GET the user to the DynamoDB in the User table")
    public void test2() throws IOException, InterruptedException {

        LOGGER.info("The is is order 2");

        final String userID = getEntityIDWithTable(USER_TABLE, userEntityID);
        LOGGER.info("User ID from the User table extracted : "+ userID);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(userEntityID, userID);

        final String getUserStr = jsonObject.toString();
        LOGGER.info("User GET string : "+ getUserStr);


        final GetItemRequest getItemRequest = dynamodbRequestBuilder.createGetItemRequest( USER_TABLE, getUserStr);

        final GetItemResult getItemResult = persistenceManager.getDynamodbSync(getItemRequest);
        final int httpStatusCode = getItemResult.getSdkHttpMetadata().getHttpStatusCode();

        assertNotNull(getItemResult);
        assertEquals(200, httpStatusCode);
    }

    @Test
    @Tag("User")
    @Tag("Sync")
    @Order(3)
    @Tag("IT")
    @DisplayName("UPDATE User DynamoDB SYNC mode: test UPDATE the user to the DynamoDB in the User table")
    public void test3() throws InterruptedException, IOException {

        LOGGER.info("This is the test 3");

        final String userID = getEntityIDWithTable(USER_TABLE, userEntityID);
        LOGGER.info("User ID from the User table extracted : "+ userID);

        JSONObject jsonObject = new JSONObject(userStr);
        jsonObject.put("userID", userID);

        final String updateUserStr = jsonObject.toString();

        LOGGER.info("User GET string : "+ updateUserStr);


        final UpdateItemRequest updateItemRequest = dynamodbRequestBuilder.createUpdateItemRequest(USER_TABLE, updateUserStr);

        final UpdateItemResult updateItemResult = persistenceManager.updateDynamodbSync(updateItemRequest);
        final int httpStatusCode = updateItemResult.getSdkHttpMetadata().getHttpStatusCode();

        assertNotNull(updateItemResult);
        assertEquals(200, httpStatusCode);
    }

    @Test
    @Tag("User")
    @Tag("Sync")
    @Tag("IT")
    @Order(4)
    @DisplayName("DELETE User DynamoDB SYNC mode: test DELETE the user to the DynamoDB in the User table")
    public void test4() throws UnsupportedEncodingException, InterruptedException {

        LOGGER.info("This is the test 4");

        final String userID = getEntityIDWithTable(USER_TABLE, userEntityID);
        LOGGER.info("User ID from the User table extracted : "+ userID);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(userEntityID, userID);

        final String deleteUserStr = jsonObject.toString();
        LOGGER.info("User GET string : "+ deleteUserStr);


        final DeleteItemRequest deleteItemRequest = dynamodbRequestBuilder.createDeleteItemRequest(USER_TABLE, deleteUserStr);

        final DeleteItemResult deleteItemResult = persistenceManager.deleteDynamodbSync(deleteItemRequest);
        final int httpStatusCode = deleteItemResult.getSdkHttpMetadata().getHttpStatusCode();

        assertNotNull(deleteItemResult);
        assertEquals(200, httpStatusCode);
    }

    @Test
    @Tag("User")
    @Tag("Async")
    @Tag("IT")
    @Order(5)
    @DisplayName("PUT User DynamoDB ASYNC mode: test SAVE the user to the DynamoDB in the User table")
    public void test5() throws IOException, InterruptedException, ExecutionException, TimeoutException {

        LOGGER.info("The is is order 5");

        final PutItemRequest putItemRequest = dynamodbRequestBuilder.createPutItemRequest(userStr, USER_TABLE);

        final CompletableFuture<PutItemResult> putItemResultCompletableFuture = persistenceManager.putDynamodbAsync(putItemRequest);
        final PutItemResult putItemResult = putItemResultCompletableFuture.get(3000, TimeUnit.MILLISECONDS);

        final int httpStatusCode = putItemResult.getSdkHttpMetadata().getHttpStatusCode();

        assertNotNull(putItemResult);
        assertEquals(200, httpStatusCode);
    }

    @Test
    @Tag("User")
    @Tag("Async")
    @Tag("IT")
    @Order(6)
    @DisplayName("GET User DynamoDB ASYNC mode: User DynamoDB SYNC mode: test GET the user to the DynamoDB in the User table")
    public void test6() throws IOException, InterruptedException, ExecutionException, TimeoutException {

        LOGGER.info("The is is order 6");

        final String userID = getEntityIDWithTable(USER_TABLE, userEntityID);
        LOGGER.info("User ID from the User table extracted : "+ userID);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(userEntityID, userID);

        final String getUserStr = jsonObject.toString();
        LOGGER.info("User GET string : "+ getUserStr);


        final GetItemRequest getItemRequest = dynamodbRequestBuilder.createGetItemRequest( USER_TABLE, getUserStr);

        final CompletableFuture<GetItemResult> dynamodbAsync = persistenceManager.getDynamodbAsync(getItemRequest);
        final GetItemResult getItemResult = dynamodbAsync.get(3000, TimeUnit.MILLISECONDS);
        final int httpStatusCode = getItemResult.getSdkHttpMetadata().getHttpStatusCode();

        assertNotNull(getItemResult);
        assertEquals(200, httpStatusCode);
    }

    @Test
    @Tag("User")
    @Tag("Async")
    @Tag("IT")
    @Order(7)
    @DisplayName("UPDATE User DynamoDB ASYNC mode: User DynamoDB SYNC mode: test UPDATE the user to the DynamoDB in the User table")
    public void test7() throws InterruptedException, IOException, ExecutionException, TimeoutException {

        LOGGER.info("This is the test 7");

        final String userID = getEntityIDWithTable(USER_TABLE, userEntityID);
        LOGGER.info("User ID from the User table extracted : "+ userID);

        JSONObject jsonObject = new JSONObject(userStr);
        jsonObject.put("userID", userID);

        final String updateUserStr = jsonObject.toString();

        LOGGER.info("User GET string : "+ updateUserStr);


        final UpdateItemRequest updateItemRequest = dynamodbRequestBuilder.createUpdateItemRequest(USER_TABLE, updateUserStr);

        final CompletableFuture<UpdateItemResult> updateItemResultCompletableFuture = persistenceManager.updateDynamodbAsync(updateItemRequest);
        final UpdateItemResult updateItemResult = updateItemResultCompletableFuture.get(3000, TimeUnit.MILLISECONDS);
        final int httpStatusCode = updateItemResult.getSdkHttpMetadata().getHttpStatusCode();

        assertNotNull(updateItemResult);
        assertEquals(200, httpStatusCode);
    }

    @Test
    @Tag("User")
    @Tag("Async")
    @Tag("IT")
    @Order(8)
    @DisplayName("DELETE User DynamoDB ASYNC mode: User DynamoDB SYNC mode: test DELETE the user to the DynamoDB in the User table")
    public void test8() throws UnsupportedEncodingException, InterruptedException, ExecutionException, TimeoutException {

        LOGGER.info("This is the test 8");

        final String userID = getEntityIDWithTable(USER_TABLE, userEntityID);
        LOGGER.info("User ID from the User table extracted : "+ userID);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(userEntityID, userID);

        final String deleteUserStr = jsonObject.toString();
        LOGGER.info("User GET string : "+ deleteUserStr);


        final DeleteItemRequest deleteItemRequest = dynamodbRequestBuilder.createDeleteItemRequest(USER_TABLE, deleteUserStr);

        final CompletableFuture<DeleteItemResult> deleteItemResultCompletableFuture = persistenceManager.deleteDynamodbAsync(deleteItemRequest);
        final DeleteItemResult deleteItemResult = deleteItemResultCompletableFuture.get(3000, TimeUnit.MILLISECONDS);
        final int httpStatusCode = deleteItemResult.getSdkHttpMetadata().getHttpStatusCode();

        assertNotNull(deleteItemResult);
        assertEquals(200, httpStatusCode);
    }


    // ===========
    // for account
    // ===========

    @Test
    @Tag("Account")
    @Tag("Sync")
    @Tag("IT")
    @Order(9)
    @DisplayName("PUT Account DynamoDB SYNC mode: test SAVE the account to the DynamoDB in the Account table")
    public void test1_() throws IOException, InterruptedException {

        LOGGER.info("The is is order 1_");

        final PutItemRequest putItemRequest = dynamodbRequestBuilder.createPutItemRequest(accountStr, ACCOUNT_TABLE);

        final PutItemResult putItemResult = persistenceManager.putDynamodbSync(putItemRequest);
        final int httpStatusCode = putItemResult.getSdkHttpMetadata().getHttpStatusCode();

        assertNotNull(putItemResult);
        assertEquals(200, httpStatusCode);
    }

    @Test
    @Tag("Account")
    @Tag("Sync")
    @Tag("IT")
    @Order(10)
    @DisplayName("GET Account DynamoDB SYNC mode: test GET the account to the DynamoDB in the Account table")
    public void test2_() throws IOException, InterruptedException {

        LOGGER.info("The is is order 2_");
        final String accountID = getEntityIDWithTable(ACCOUNT_TABLE, accountEntityID);
        LOGGER.info("Account ID from the Account table extracted : "+ accountID);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(accountEntityID, accountID);

        final String getAccountStr = jsonObject.toString();
        LOGGER.info("Account GET string : "+ getAccountStr);


        final GetItemRequest getItemRequest = dynamodbRequestBuilder.createGetItemRequest( ACCOUNT_TABLE, getAccountStr);

        final GetItemResult getItemResult = persistenceManager.getDynamodbSync(getItemRequest);
        final int httpStatusCode = getItemResult.getSdkHttpMetadata().getHttpStatusCode();

        assertNotNull(getItemResult);
        assertEquals(200, httpStatusCode);
    }

    @Test
    @Tag("Account")
    @Tag("Sync")
    @Tag("IT")
    @Order(11)
    @DisplayName("UPDATE Account DynamoDB SYNC mode: test UPDATE the account to the DynamoDB in the Account table")
    public void test3_() throws IOException, InterruptedException {

        LOGGER.info("The is is order 3_");

        final String accountID = getEntityIDWithTable(ACCOUNT_TABLE, accountEntityID);
        LOGGER.info("Account ID from the Account table extracted : "+ accountID);

        JSONObject jsonObject = new JSONObject(accountStr);
        jsonObject.put("id", accountID);

        final String updateAccountStr = jsonObject.toString();
        LOGGER.info("Account GET string : "+ updateAccountStr);


        final UpdateItemRequest updateItemRequest = dynamodbRequestBuilder.createUpdateItemRequest(ACCOUNT_TABLE, updateAccountStr);

        final UpdateItemResult updateItemResult = persistenceManager.updateDynamodbSync(updateItemRequest);
        final int httpStatusCode = updateItemResult.getSdkHttpMetadata().getHttpStatusCode();

        assertNotNull(updateItemResult);
        assertEquals(200, httpStatusCode);
    }

    @Test
    @Tag("Account")
    @Tag("Sync")
    @Tag("IT")
    @Order(12)
    @DisplayName("DELETE Account DynamoDB SYNC mode: test DELETE the account to the DynamoDB in the Account table")
    public void test4_() throws IOException, InterruptedException {

        LOGGER.info("The is is order 4_");

        final String accountID = getEntityIDWithTable(ACCOUNT_TABLE, accountEntityID);
        LOGGER.info("Account ID from the Account table extracted : "+ accountID);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(accountEntityID, accountID);

        final String deleteAccountStr = jsonObject.toString();
        LOGGER.info("Account GET string : "+ deleteAccountStr);


        final DeleteItemRequest deleteItemRequest = dynamodbRequestBuilder.createDeleteItemRequest(ACCOUNT_TABLE, deleteAccountStr);

        final DeleteItemResult deleteItemResult = persistenceManager.deleteDynamodbSync(deleteItemRequest);
        final int httpStatusCode = deleteItemResult.getSdkHttpMetadata().getHttpStatusCode();

        assertNotNull(deleteItemResult);
        assertEquals(200, httpStatusCode);
    }

    @Test
    @Tag("Account")
    @Tag("Async")
    @Tag("IT")
    @Order(13)
    @DisplayName("PUT Account DynamoDB ASYNC mode: test SAVE the account to the DynamoDB in the Account table")
    public void test5_() throws IOException, InterruptedException, ExecutionException, TimeoutException {

        LOGGER.info("The is is order 5_");

        final PutItemRequest putItemRequest = dynamodbRequestBuilder.createPutItemRequest(accountStr, ACCOUNT_TABLE);

        final CompletableFuture<PutItemResult> putItemResultCompletableFuture = persistenceManager.putDynamodbAsync(putItemRequest);
        final PutItemResult putItemResult = putItemResultCompletableFuture.get(3000, TimeUnit.MILLISECONDS);

        final int httpStatusCode = putItemResult.getSdkHttpMetadata().getHttpStatusCode();

        assertNotNull(putItemResult);
        assertEquals(200, httpStatusCode);
    }

    @Test
    @Tag("Account")
    @Tag("Async")
    @Tag("IT")
    @Order(14)
    @DisplayName("GET Account DynamoDB ASYNC mode: test GET the account to the DynamoDB in the Account table")
    public void test6_() throws IOException, InterruptedException, ExecutionException, TimeoutException {

        LOGGER.info("The is is order 6_");

        final String accountID = getEntityIDWithTable(ACCOUNT_TABLE, accountEntityID);
        LOGGER.info("Account ID from the Account table extracted : "+ accountID);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(accountEntityID, accountID);

        final String getAccountStr = jsonObject.toString();
        LOGGER.info("Account GET string : "+ getAccountStr);


        final GetItemRequest getItemRequest = dynamodbRequestBuilder.createGetItemRequest( ACCOUNT_TABLE, getAccountStr);

        final CompletableFuture<GetItemResult> dynamodbAsync = persistenceManager.getDynamodbAsync(getItemRequest);
        final GetItemResult getItemResult = dynamodbAsync.get(3000, TimeUnit.MILLISECONDS);

        final int httpStatusCode = getItemResult.getSdkHttpMetadata().getHttpStatusCode();

        assertNotNull(getItemResult);
        assertEquals(200, httpStatusCode);
    }

    @Test
    @Tag("Account")
    @Tag("Async")
    @Tag("IT")
    @Order(15)
    @DisplayName("UPDATE Account DynamoDB ASYNC mode: test UPDATE the account to the DynamoDB in the Account table")
    public void test7_() throws IOException, InterruptedException, ExecutionException, TimeoutException {

        LOGGER.info("The is is order 7_");

        final String accountID = getEntityIDWithTable(ACCOUNT_TABLE, accountEntityID);
        LOGGER.info("Account ID from the Account table extracted : "+ accountID);

        JSONObject jsonObject = new JSONObject(accountStr);
        jsonObject.put("id", accountID);

        final String updateAccountStr = jsonObject.toString();

        LOGGER.info("Account GET string : "+ updateAccountStr);


        final UpdateItemRequest updateItemRequest = dynamodbRequestBuilder.createUpdateItemRequest(ACCOUNT_TABLE, updateAccountStr);

        final CompletableFuture<UpdateItemResult> updateItemResultCompletableFuture = persistenceManager.updateDynamodbAsync(updateItemRequest);
        final UpdateItemResult updateItemResult = updateItemResultCompletableFuture.get(3000, TimeUnit.MILLISECONDS);

        final int httpStatusCode = updateItemResult.getSdkHttpMetadata().getHttpStatusCode();

        assertNotNull(updateItemResult);
        assertEquals(200, httpStatusCode);
    }

    @Test
    @Tag("Account")
    @Tag("Async")
    @Tag("IT")
    @Order(16)
    @DisplayName("DELETE Account DynamoDB ASYNC mode: test DELETE the account to the DynamoDB in the Account table")
    public void test8_() throws IOException, InterruptedException, ExecutionException, TimeoutException {

        LOGGER.info("The is is order 8_");

        final String accountID = getEntityIDWithTable(ACCOUNT_TABLE, accountEntityID);
        LOGGER.info("Account ID from the Account table extracted : "+ accountID);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(accountEntityID, accountID);

        final String deleteAccountStr = jsonObject.toString();
        LOGGER.info("Account GET string : "+ deleteAccountStr);


        final DeleteItemRequest deleteItemRequest = dynamodbRequestBuilder.createDeleteItemRequest(ACCOUNT_TABLE, deleteAccountStr);

        final CompletableFuture<DeleteItemResult> deleteItemResultCompletableFuture = persistenceManager.deleteDynamodbAsync(deleteItemRequest);
        final DeleteItemResult deleteItemResult = deleteItemResultCompletableFuture.get(3000, TimeUnit.MILLISECONDS);

        final int httpStatusCode = deleteItemResult.getSdkHttpMetadata().getHttpStatusCode();

        assertNotNull(deleteItemResult);
        assertEquals(200, httpStatusCode);
    }

}