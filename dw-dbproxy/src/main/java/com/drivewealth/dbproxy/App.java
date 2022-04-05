package com.drivewealth.dbproxy;

import com.drivewealth.dbproxy.config.DBProxyConfig;
import com.drivewealth.dbproxy.service.DBProxyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.drivewealth.dbproxy.util.Parameters.X_AMZN_RESPONSE;


@SpringBootApplication
public class App implements CommandLineRunner {


    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    @Autowired
    private DBProxyConfig dbProxyConfig;

    @Autowired
    private DBProxyService dbProxyService;

    public static void main(String[] args) {

        SpringApplication.run(App.class, args);
    }


    // "{"userID": "0cec5ff0-a75e-419b-9af8-ae7cc0d5aacf"}" GetItem User
    @Override
    public void run(String... args) throws Exception {


        if (args[0] == null || args[1] == null || args[2] == null ||
                args[0].isEmpty() || args[1].isEmpty() || args[2].isEmpty()) {

            throw new IllegalArgumentException("The request body, action name (e.g. PutItem, GetItem etc) and table name (e.g. User, Account etc) needs to be provided");
        }

        final String requestBody = args[0];
        final String actionName = args[1];
        final String tableName = args[2];



        Map<String, String> headers = new HashMap<>();
        // "GetItem",
        headers.put("ACTION_NAME", actionName);
        // "User"
        headers.put("TABLE_NAME", tableName);

        /*
         * if service and mode type provided from the CLI as arguments, then use that.
         * Otherwise, read from the application.yml file
         * */
        final boolean isServiceAndModeTypeProvided = (args.length == 5 && !args[3].isEmpty() && !args[4].isEmpty());


        final String modeType = isServiceAndModeTypeProvided ? args[3] : dbProxyConfig.getDbServiceMode();

        final String synchronosityType = isServiceAndModeTypeProvided ? args[3] : dbProxyConfig.getDbServiceType();


        Map<String, String> configs = new HashMap<>();
        // ACTIVE_PASSIVE
        configs.put("MODE_TYPE", modeType);
        // SYNCHRONOUS
        configs.put("SYNCHRONICITY_TYPE", synchronosityType);


        final Map<String, String> responseMap = dbProxyService.processRequest(requestBody, headers, configs);
        String response = responseMap.get(X_AMZN_RESPONSE);
    }
}