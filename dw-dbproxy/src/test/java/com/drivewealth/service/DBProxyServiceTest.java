package com.drivewealth.service;


import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.PutItemResult;
import com.drivewealth.setup.IntegrationTestSetup;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;

import static com.drivewealth.utils.TestUtils.readFileFromResourcesFolder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

//@EnableAutoConfiguration
public class DBProxyServiceTest extends IntegrationTestSetup {


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

    @Test
    public void test1() throws IOException, InterruptedException {

        int a = 200;
        assertEquals(200, a);
    }
}
