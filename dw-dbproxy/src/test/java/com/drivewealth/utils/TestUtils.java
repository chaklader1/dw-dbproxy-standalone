package com.drivewealth.utils;


import com.drivewealth.service.DynamoDbPersistenceManagerIT;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;


public class TestUtils {


    public static String extractEntityIdWithDynamoDBResponse(String getUserJSON, String attrName, String id) {

        JSONObject jsonObject = new JSONObject(getUserJSON);

        final JSONObject jsonItem = jsonObject.getJSONObject(attrName);

        final JSONObject userID = jsonItem.getJSONObject(id);
        final String userIDString = userID.getString("s");

        return userIDString;
    }

    public static String readFileFromResourcesFolder(String fileName) {
        InputStream inputStream = DynamoDbPersistenceManagerIT.class.getClassLoader().getResourceAsStream(fileName);

        final StringBuilder stringBuilder = new StringBuilder();
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName(StandardCharsets.UTF_8.name()));

        try (Reader reader = new BufferedReader(inputStreamReader)) {
            int c = 0;
            while ((c = reader.read()) != -1) {
                stringBuilder.append((char) c);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        final String str = stringBuilder.toString();
        return str;
    }

}
