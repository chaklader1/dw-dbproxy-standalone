package com.drivewealth.setup;


import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.drivewealth.dbproxy.config.DynamoDBConfiguration;
import com.drivewealth.dbproxy.service.PersistenceManager;
import com.drivewealth.dbproxy.util.DynamodbRequestBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;


@ExtendWith({MockitoExtension.class, SpringExtension.class})
@WebAppConfiguration
@SpringBootTest(classes = {DynamoDBConfiguration.class, DynamodbRequestBuilder.class})
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MockedTestSetup {


    public static final Logger LOGGER = LoggerFactory.getLogger(MockedTestSetup.class);


    @Mock
    public AmazonDynamoDB dynamoDBClient;

    @InjectMocks
    public DynamoDBMapper dynamoDBMapper;

    @InjectMocks
    public PersistenceManager persistenceManager;


    @Autowired
    public DynamodbRequestBuilder dynamodbRequestBuilder;

    @Autowired
    public ObjectMapper objectMapper;


    @BeforeAll
    public static void setupClass() throws Exception {
        LOGGER.info("This is running before ALL (@BeforeClass) to complete the setup");
    }

    @AfterAll
    public static void tearDownClass() throws Exception {
        LOGGER.info("This is running after ALL (@AfterClass) to tear down everything");
    }

    @BeforeEach
    public void setup() throws Exception {
        LOGGER.info("We are making the setup for every test to make it run");
    }

    @AfterEach
    public void tearDown() throws Exception {
        LOGGER.info("We are teardown the setup after every test so the next test will run smoothly.");
    }
}
