package com.drivewealth.setup;


import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.local.main.ServerRunner;
import com.amazonaws.services.dynamodbv2.local.server.DynamoDBProxyServer;
import com.amazonaws.services.dynamodbv2.model.*;
import com.amazonaws.services.dynamodbv2.util.TableUtils;
import com.drivewealth.dbproxy.config.DynamoDBConfiguration;
import com.drivewealth.dbproxy.entity.Account;
import com.drivewealth.dbproxy.entity.User;
import com.drivewealth.dbproxy.service.PersistenceManager;
import com.drivewealth.dbproxy.util.DynamodbRequestBuilder;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


/*
 * this class will set up for the integration tests, run the AWS local server and will terminate it
 * */
@ExtendWith({MockitoExtension.class, SpringExtension.class})
@WebAppConfiguration
@SpringBootTest(classes = {DynamoDBConfiguration.class, DynamodbRequestBuilder.class})
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class IntegrationTestSetup {


    public static final Logger LOGGER = LoggerFactory.getLogger(IntegrationTestSetup.class);


    public static AmazonDynamoDB amazonDynamoDBClient;

    public static DynamoDBProxyServer server;
    public static DynamoDBMapper dynamoDBMapper;

    public static PersistenceManager persistenceManager;


    @Autowired
    public DynamodbRequestBuilder dynamodbRequestBuilder;


    @BeforeAll
    public static void setupClass() throws Exception {
        LOGGER.info("This is running before ALL (@BeforeClass) to complete the setup");

        startLocalServer();
        setupForRemoteAwsServer();

        List<Class> classes = Arrays.asList(User.class, Account.class);
        createDynamoDbTables(classes);
    }

    @AfterAll
    public static void tearDownClass() throws Exception {
        LOGGER.info("This is running after ALL (@AfterClass) to tear down everything");

        destroyAllTables();
        stopLocalServer();
    }

    @BeforeEach
    public void setup() throws Exception {
        LOGGER.info("We are making the setup for every test to make it run");
    }

    @AfterEach
    public void tearDown() throws Exception {
        LOGGER.info("We are teardown the setup after every test so the next test will run smoothly.");
    }


    private static void createDynamoDbTables(List<Class> classes) throws InterruptedException {

        for (Class aClass : classes) {

            CreateTableRequest tableRequest = dynamoDBMapper.generateCreateTableRequest(aClass)
                .withProvisionedThroughput(new ProvisionedThroughput(1L, 1L));

            final boolean tableIfNotExists = TableUtils.createTableIfNotExists(amazonDynamoDBClient, tableRequest);
            if (tableIfNotExists) {
                LOGGER.info("Created table {}", tableRequest.getTableName());
            }

            TableUtils.waitUntilActive(amazonDynamoDBClient, tableRequest.getTableName());
            LOGGER.info("Table {} is active", tableRequest.getTableName());
        }
    }

    private static void setupForRemoteAwsServer() {

        amazonDynamoDBClient = createAmazonDynamoDB();

        persistenceManager = new PersistenceManager();
        dynamoDBMapper = new DynamoDBMapper(amazonDynamoDBClient);

        persistenceManager.setAmazonDynamoDBClient(amazonDynamoDBClient);
    }

    private static void destroyAllTables() {

        final ListTablesResult listAllTablesResult = amazonDynamoDBClient.listTables();
        final List<String> tableNames = listAllTablesResult.getTableNames();

        LOGGER.info("\n");
        LOGGER.info("We will delete the tables : " + StringUtils.join(tableNames, " | "));
        LOGGER.info("\n");

        for (String tableName : tableNames) {

            amazonDynamoDBClient.deleteTable(tableName);
            LOGGER.info("Deleted the table wih name : " + tableName);
        }

        LOGGER.info("Tables are deleted successfully");
    }

    private static void startLocalServer() throws Exception {

        System.setProperty("sqlite4java.library.path", "native-libs");
        String port = "8000";
        server = ServerRunner.createServerFromCommandLineArgs(
            new String[]{"-inMemory", "-port", port});

        server.start();
    }

    private static void stopLocalServer() throws Exception {
        server.stop();
    }

    private static AmazonDynamoDB createAmazonDynamoDB() {

        LOGGER.info("Wea re creating the DynamoDB client for using with local server");

        return AmazonDynamoDBClientBuilder
            .standard()
            .withEndpointConfiguration(
                new AwsClientBuilder.EndpointConfiguration("http://localhost:8000/", "us-east-2"))
            .withCredentials(new AWSStaticCredentialsProvider(
                new BasicAWSCredentials("", "")))
            .build();
    }

    public String getEntityIDWithTable(String tableName, String entityIdAttr) {

        Map<String, AttributeValue> lastKeyEvaluated = null;

        do {
            ScanRequest scanRequest = new ScanRequest()
                .withTableName(tableName)
                .withLimit(10)
                .withExclusiveStartKey(lastKeyEvaluated);

            ScanResult result = amazonDynamoDBClient.scan(scanRequest);
            final List<Map<String, AttributeValue>> items = result.getItems();


            final AttributeValue userIdAttr = items.get(0).get(entityIdAttr);
            if (userIdAttr != null) {

                final String userID = userIdAttr.getS();
                return userID;
            }

            lastKeyEvaluated = result.getLastEvaluatedKey();
        } while (lastKeyEvaluated != null);

        return "";
    }

}
