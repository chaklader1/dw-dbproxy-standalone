package com.drivewealth.dbproxy.util;

import com.drivewealth.dbproxy.entity.Account;
import com.drivewealth.dbproxy.entity.User;
import com.drivewealth.dbproxy.mapper.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.drivewealth.dbproxy.util.Parameters.*;


/*
 * this class will convert the CockroachDB responses to match with the DynamoDB
 * */
public class DynamodbResponseBuilder {


    private static final Logger LOGGER = LoggerFactory.getLogger(DynamodbResponseBuilder.class);


    public static Map<String, String> createResponse(Message cockroachResponseMessage, String tableName, String action) throws IOException {


        Map<String, String> cockroachDbPresistenceResponseMap = new LinkedHashMap<>();

        final String DYNAMODB_RESPONSE_REQUEST_ID = createRequestId();

        JSONObject jsonObject = createBaseMetaResponse(tableName, DYNAMODB_RESPONSE_REQUEST_ID);

        switch (action) {

            case PUTITEM:
            {

//                if(!tableName.equalsIgnoreCase("GrpcPoc"))
//                {
//                    Descriptors.FieldDescriptor fieldDescriptor = cockroachResponseMessage.getDescriptorForType().findFieldByName("response");
//                    Object value = cockroachResponseMessage.getField(fieldDescriptor);
//
//                    if (value == null) {
//                        LOGGER.info("We don't receive correct response from CockroachDB for put item operation");
//                        return new LinkedHashMap<>();
//                    }
//                }

                jsonObject.put("attributes", JSONObject.NULL);
                jsonObject.put("itemCollectionMetrics", JSONObject.NULL);

                break;
            }

            case GETITEM:
            {
                final String cockroachResponseJsonStr = JsonFormat.printer().print(cockroachResponseMessage);
                final Map<String, JSONObject> attributeValueMap = createEntityKeyAndAttributeValueMap(tableName, cockroachResponseJsonStr);

                jsonObject.put("item", attributeValueMap);
                break;
            }
            case DELETEITEM:
            {
                final String cockroachResponseJsonStr = JsonFormat.printer().print(cockroachResponseMessage);
                final Map<String, JSONObject> attributeValueMap = createEntityKeyAndAttributeValueMap(tableName, cockroachResponseJsonStr);
                jsonObject.put("attributes", attributeValueMap);

                jsonObject.put("itemCollectionMetrics", JSONObject.NULL);

                break;
            }
            case UPDATEITEM:
            {
                final String cockroachResponseJsonStr = JsonFormat.printer().print(cockroachResponseMessage);
                final Map<String, JSONObject> attributeValueMap = createEntityKeyAndAttributeValueMap(tableName, cockroachResponseJsonStr);
                jsonObject.put("attributes", attributeValueMap);

                jsonObject.put("itemCollectionMetrics", JSONObject.NULL);

                break;
            }
        }


        final String response = jsonObject.toString();

        cockroachDbPresistenceResponseMap.put(X_AMZN_REQUESTID, DYNAMODB_RESPONSE_REQUEST_ID);
        cockroachDbPresistenceResponseMap.put(X_AMZN_RESPONSE, response);
        cockroachDbPresistenceResponseMap.put("DBType", "CockroachDB");

        return cockroachDbPresistenceResponseMap;
    }



    /*
    * use it as the @JsonProperty annotation is the Jackson is not working, we need to change it
    * */
    public static JSONObject createBaseMetaResponse(String tableName, String requestId) {

        JSONObject responseJsonObj = new JSONObject();


        JSONObject sdkResponseMetadataJsonObj = new JSONObject();
        sdkResponseMetadataJsonObj.put("requestId", requestId);

        JSONObject sdkHttpMetadataJsonObj = new JSONObject();
        JSONObject httpHeaders = new JSONObject();
        httpHeaders.put("Connection", "keep-alive");
        httpHeaders.put("Content-Length", "371");
        httpHeaders.put("Content-Type", X_AWS_JSON);
        httpHeaders.put("Date", getFormattedDateTime());
        httpHeaders.put("Server", "Server");
        httpHeaders.put("x-amz-crc32", "1780032442");
        httpHeaders.put("x-amzn-RequestId", requestId);
        sdkHttpMetadataJsonObj.put("httpHeaders", httpHeaders);

        JSONObject allHttpHeaders = new JSONObject();
        allHttpHeaders.put("Connection", Collections.singletonList("keep-alive"));
        allHttpHeaders.put("Content-Length", Collections.singletonList("371"));
        allHttpHeaders.put("Content-Type", Collections.singletonList(X_AWS_JSON));
        allHttpHeaders.put("Date", Collections.singletonList(getFormattedDateTime()));
        allHttpHeaders.put("Server", Collections.singletonList("Server"));
        allHttpHeaders.put("x-amz-crc32", Collections.singletonList("1780032442"));
        allHttpHeaders.put("x-amzn-RequestId", Collections.singletonList(requestId));
        sdkHttpMetadataJsonObj.put("allHttpHeaders", allHttpHeaders);
        sdkHttpMetadataJsonObj.put("httpStatusCode", 200);


        JSONObject consumedCapacityJsonObj = new JSONObject();
        consumedCapacityJsonObj.put("tableName", tableName);
        consumedCapacityJsonObj.put("capacityUnits", 1.0);
        consumedCapacityJsonObj.put("readCapacityUnits", JSONObject.NULL);
        consumedCapacityJsonObj.put("writeCapacityUnits", JSONObject.NULL);
        consumedCapacityJsonObj.put("table", tableName);
        consumedCapacityJsonObj.put("localSecondaryIndexes", JSONObject.NULL);
        consumedCapacityJsonObj.put("globalSecondaryIndexes", JSONObject.NULL);


        responseJsonObj.put("sdkResponseMetadata", sdkResponseMetadataJsonObj);
        responseJsonObj.put("sdkHttpMetadata", sdkHttpMetadataJsonObj);
        responseJsonObj.put("consumedCapacity", consumedCapacityJsonObj);


        return responseJsonObj;
    }


    // TODO: JsonProperty isn't overriding the default name jackson gets from the getter
    public static Response createDummyMetaResponse(String tableName, String requestId) {

        Response dummyMetaResponseObj = new Response();

        final SdkResponseMetadata sdkResponseMetadataObj = new SdkResponseMetadata();
        sdkResponseMetadataObj.setRequestId(requestId);


        SdkHttpMetadata sdkHttpMetadata = new SdkHttpMetadata();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setXAmznRequestId(requestId);
        httpHeaders.setXAmzCrc32("1780032442");
        httpHeaders.setServer1("Server");
        httpHeaders.setDate(getFormattedDateTime());
        httpHeaders.setContentType(X_AWS_JSON);
        httpHeaders.setContentLength("371");
        sdkHttpMetadata.setHttpHeaders(httpHeaders);

        AllHttpHeaders allHttpHeaders = new AllHttpHeaders();
        allHttpHeaders.setLServer(Collections.singletonList("Server"));
        allHttpHeaders.setXAmznRequestId(Collections.singletonList(requestId));
        allHttpHeaders.setXAmzCrc32(Collections.singletonList("1780032442"));
        allHttpHeaders.setContentLength(Collections.singletonList("371"));


        allHttpHeaders.setDate(Collections.singletonList(getFormattedDateTime()));
        allHttpHeaders.setContentType(Collections.singletonList("application/x-amz-json-1.0"));
        sdkHttpMetadata.setAllHttpHeaders(allHttpHeaders);

        sdkHttpMetadata.setHttpStatusCode(200);

        ConsumedCapacity consumedCapacity = new ConsumedCapacity();
        consumedCapacity.setCapacityUnits(1.0);
        consumedCapacity.setTableName(tableName);
        consumedCapacity.setTable(null);
        consumedCapacity.setReadCapacityUnits(1);
        consumedCapacity.setWriteCapacityUnits(2);
        consumedCapacity.setLocalSecondaryIndexes(null);
        consumedCapacity.setGlobalSecondaryIndexes(null);

        dummyMetaResponseObj.setSdkResponseMetadata(sdkResponseMetadataObj);
        dummyMetaResponseObj.setSdkHttpMetadata(sdkHttpMetadata);
        dummyMetaResponseObj.setConsumedCapacity(consumedCapacity);


        return dummyMetaResponseObj;
    }

    private static String createRequestId() {
        return RandomStringUtils.randomAlphanumeric(REQUEST_ID_LENGTH).toUpperCase();
    }

    /*
     * This is will return dattime in the format - [Fri, 07 Jan 2022 04:25:32 GMT]
     * */
    private static String getFormattedDateTime() {

        final Date currentTime = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat(DYNAMODB_RESPONSE_DATETIME_FORMAT);

        sdf.setTimeZone(TimeZone.getTimeZone(TIMEZONE));
        final String formattedDateTime = sdf.format(currentTime);

        return formattedDateTime;
    }


    public static Map<String,  JSONObject> createEntityKeyAndAttributeValueMap(String tableName, String responseBody) throws IOException {

        Map<String, JSONObject> entityKeyAndAttributeValueMap = new LinkedHashMap<>();

        ObjectMapper objectMapper = new ObjectMapper();

        switch (tableName) {

            case USER_TABLE: {

                User user = objectMapper.readValue(responseBody, User.class);

                if (String.valueOf(user.getUserID()).equalsIgnoreCase("null")|| String.valueOf(user.getUserID()).equalsIgnoreCase("")) {
                    throw new RuntimeException("The entity is missing the primary key to extract the data");
                }

                final String userId = user.getUserID();
                LOGGER.info("We are generating the response for the user with ID : " + userId);

                JSONObject attributeValuesJSON1 = getAttributeValuesJSON("s", (new com.drivewealth.dbproxy.mapper.AttributeValue()).withS(String.valueOf(user.getUserID())));
                entityKeyAndAttributeValueMap.put("userID", attributeValuesJSON1);


                if (!String.valueOf(user.getUserType()).equalsIgnoreCase("null") && !String.valueOf(user.getUserType()).equalsIgnoreCase("")) {
                    JSONObject attributeValuesJSON = getAttributeValuesJSON("n", (new com.drivewealth.dbproxy.mapper.AttributeValue()).withN(String.valueOf(user.getUserType())));
                    entityKeyAndAttributeValueMap.put("userType", attributeValuesJSON);
                }
                if (user.getDisplayName() != null && !user.getDisplayName().equalsIgnoreCase("")) {
                    JSONObject attributeValuesJSON = getAttributeValuesJSON("s", (new com.drivewealth.dbproxy.mapper.AttributeValue()).withS(user.getDisplayName()));
                    entityKeyAndAttributeValueMap.put("displayName", attributeValuesJSON);
                }
                if (user.getAvatarUrl() != null && !user.getAvatarUrl().equalsIgnoreCase("")) {
                    JSONObject attributeValuesJSON = getAttributeValuesJSON("s", (new com.drivewealth.dbproxy.mapper.AttributeValue()).withS(user.getAvatarUrl()));
                    entityKeyAndAttributeValueMap.put("avatarUrl", attributeValuesJSON);
                }



                if (user.getDob() != null && !user.getDob().equalsIgnoreCase("")) {
                    JSONObject attributeValuesJSON = getAttributeValuesJSON("s", (new com.drivewealth.dbproxy.mapper.AttributeValue()).withS(user.getDob()));
                    entityKeyAndAttributeValueMap.put("dob", attributeValuesJSON);
                }
                if (user.getEmailAddress1() != null && !user.getEmailAddress1().equalsIgnoreCase("")) {
                    JSONObject attributeValuesJSON = getAttributeValuesJSON("s", (new com.drivewealth.dbproxy.mapper.AttributeValue()).withS(user.getEmailAddress1()));
                    entityKeyAndAttributeValueMap.put("emailAddress1", attributeValuesJSON);
                }
                if (user.getEmailAddress2() != null && !user.getEmailAddress2().equalsIgnoreCase("")) {
                    JSONObject attributeValuesJSON = getAttributeValuesJSON("s", (new com.drivewealth.dbproxy.mapper.AttributeValue()).withS(user.getEmailAddress2()));
                    entityKeyAndAttributeValueMap.put("emailAddress2", attributeValuesJSON);
                }
                if (user.getFirstName() != null && !user.getFirstName().equalsIgnoreCase("")) {
                    JSONObject attributeValuesJSON = getAttributeValuesJSON("s", (new com.drivewealth.dbproxy.mapper.AttributeValue()).withS(user.getFirstName()));
                    entityKeyAndAttributeValueMap.put("firstName", attributeValuesJSON);
                }
                if (user.getGender() != null && !user.getGender().equalsIgnoreCase("")) {
                    JSONObject attributeValuesJSON = getAttributeValuesJSON("s", (new com.drivewealth.dbproxy.mapper.AttributeValue()).withS(user.getGender()));
                    entityKeyAndAttributeValueMap.put("gender", attributeValuesJSON);
                }
                if (user.getIdNo() != null && !user.getIdNo().equalsIgnoreCase("")) {
                    JSONObject attributeValuesJSON = getAttributeValuesJSON("s", (new com.drivewealth.dbproxy.mapper.AttributeValue()).withS(user.getIdNo()));
                    entityKeyAndAttributeValueMap.put("idNo", attributeValuesJSON);
                }
                if (user.getIdType() != null && !user.getIdType().equalsIgnoreCase("")) {
                    JSONObject attributeValuesJSON = getAttributeValuesJSON("s", (new com.drivewealth.dbproxy.mapper.AttributeValue()).withS(user.getIdType()));
                    entityKeyAndAttributeValueMap.put("idType", attributeValuesJSON);
                }
                if (user.getJointFirstName() != null && !user.getJointFirstName().equalsIgnoreCase("")) {
                    JSONObject attributeValuesJSON = getAttributeValuesJSON("s", (new com.drivewealth.dbproxy.mapper.AttributeValue()).withS(user.getJointFirstName()));
                    entityKeyAndAttributeValueMap.put("jointFirstName", attributeValuesJSON);
                }
                if (user.getJointGender() != null && !user.getJointGender().equalsIgnoreCase("")) {
                    JSONObject attributeValuesJSON = getAttributeValuesJSON("s", (new com.drivewealth.dbproxy.mapper.AttributeValue()).withS(user.getJointGender()));
                    entityKeyAndAttributeValueMap.put("jointGender", attributeValuesJSON);
                }
                if (user.getJointLastName() != null && !user.getJointLastName().equalsIgnoreCase("")) {
                    JSONObject attributeValuesJSON = getAttributeValuesJSON("s", (new com.drivewealth.dbproxy.mapper.AttributeValue()).withS(user.getJointLastName()));
                    entityKeyAndAttributeValueMap.put("jointLastName", attributeValuesJSON);
                }
                if (user.getJointMaritalStatus() != null && !user.getJointMaritalStatus().equalsIgnoreCase("")) {
                    JSONObject attributeValuesJSON = getAttributeValuesJSON("s", (new com.drivewealth.dbproxy.mapper.AttributeValue()).withS(user.getJointMaritalStatus()));
                    entityKeyAndAttributeValueMap.put("jointMaritalStatus", attributeValuesJSON);
                }
                if (user.getJointZipPostalCode() != null && !user.getJointZipPostalCode().equalsIgnoreCase("")) {
                    JSONObject attributeValuesJSON = getAttributeValuesJSON("s", (new com.drivewealth.dbproxy.mapper.AttributeValue()).withS(user.getJointZipPostalCode()));
                    entityKeyAndAttributeValueMap.put("jointZipPostalCode", attributeValuesJSON);
                }




                if (user.getCommissionID() != null && !user.getCommissionID().equalsIgnoreCase("")) {
                    JSONObject attributeValuesJSON = getAttributeValuesJSON("s", (new com.drivewealth.dbproxy.mapper.AttributeValue()).withS(user.getCommissionID()));
                    entityKeyAndAttributeValueMap.put("commissionID", attributeValuesJSON);
                }
                if (user.getCommissionGroupID() != null && !user.getCommissionGroupID().equalsIgnoreCase("")) {
                    JSONObject attributeValuesJSON = getAttributeValuesJSON("s", (new com.drivewealth.dbproxy.mapper.AttributeValue()).withS(user.getCommissionGroupID()));
                    entityKeyAndAttributeValueMap.put("commissionGroupID", attributeValuesJSON);
                }
                if (user.getCommissionRate() != null && !user.getCommissionRate().toString().equalsIgnoreCase("")) {
                    JSONObject attributeValuesJSON = getAttributeValuesJSON("n", (new com.drivewealth.dbproxy.mapper.AttributeValue()).withN(String.valueOf(user.getCommissionRate())));
                    entityKeyAndAttributeValueMap.put("commissionRate", attributeValuesJSON);
                }



                if (user.getAgreementID() != null && !user.getAgreementID().equalsIgnoreCase("")) {
                    JSONObject attributeValuesJSON = getAttributeValuesJSON("s", (new com.drivewealth.dbproxy.mapper.AttributeValue()).withS(user.getAgreementID()));
                    entityKeyAndAttributeValueMap.put("agreementID", attributeValuesJSON);
                }
                if (!String.valueOf(user.getAckCustomerAgreement()).equalsIgnoreCase("null") && !String.valueOf(user.getAckCustomerAgreement()).equalsIgnoreCase("")) {
                    JSONObject attributeValuesJSON = getAttributeValuesJSON("n", (new com.drivewealth.dbproxy.mapper.AttributeValue()).withN(String.valueOf(user.getAckCustomerAgreement())));
                    entityKeyAndAttributeValueMap.put("ackCustomerAgreement", attributeValuesJSON);
                }
                if (user.getAckDataSharing() != null && !user.getAckDataSharing().toString().equalsIgnoreCase("")) {
                    JSONObject attributeValuesJSON = getAttributeValuesJSON("bool", (new com.drivewealth.dbproxy.mapper.AttributeValue()).withBOOL(user.getAckDataSharing()));
                    entityKeyAndAttributeValueMap.put("ackDataSharing", attributeValuesJSON);
                }
                if (user.getAckJointSignedBy() != null && !user.getAckJointSignedBy().equalsIgnoreCase("")) {
                    JSONObject attributeValuesJSON = getAttributeValuesJSON("s", (new com.drivewealth.dbproxy.mapper.AttributeValue()).withS(user.getAckJointSignedBy()));
                    entityKeyAndAttributeValueMap.put("ackJointSignedBy", attributeValuesJSON);
                }
                if (user.getAckJointSignedWhen() != null && !user.getAckJointSignedWhen().equalsIgnoreCase("")) {
                    JSONObject attributeValuesJSON = getAttributeValuesJSON("s", (new com.drivewealth.dbproxy.mapper.AttributeValue()).withS(user.getAckJointSignedWhen()));
                    entityKeyAndAttributeValueMap.put("ackJointSignedWhen", attributeValuesJSON);
                }
                if (user.getAckMarginAgreementWhen() != null && !user.getAckMarginAgreementWhen().equalsIgnoreCase("")) {
                    JSONObject attributeValuesJSON = getAttributeValuesJSON("s", (new com.drivewealth.dbproxy.mapper.AttributeValue()).withS(user.getAckMarginAgreementWhen()));
                    entityKeyAndAttributeValueMap.put("ackMarginAgreementWhen", attributeValuesJSON);
                }
                if (user.getAckSignedWhen() != null && !user.getAckSignedWhen().equalsIgnoreCase("")) {
                    JSONObject attributeValuesJSON = getAttributeValuesJSON("s", (new com.drivewealth.dbproxy.mapper.AttributeValue()).withS(user.getAckSignedWhen()));
                    entityKeyAndAttributeValueMap.put("ackSignedWhen", attributeValuesJSON);
                }
                if (user.getAddressID() != null && !user.getAddressID().equalsIgnoreCase("")) {
                    JSONObject attributeValuesJSON = getAttributeValuesJSON("s", new com.drivewealth.dbproxy.mapper.AttributeValue(user.getAddressID()));
                    entityKeyAndAttributeValueMap.put("addressID", attributeValuesJSON);
                }
                if (user.getAddressLine1() != null && !user.getAddressLine1().equalsIgnoreCase("")) {
                    JSONObject attributeValuesJSON = getAttributeValuesJSON("s", (new com.drivewealth.dbproxy.mapper.AttributeValue()).withS(user.getAddressLine1()));
                    entityKeyAndAttributeValueMap.put("addressLine1", attributeValuesJSON);
                }
                if (user.getAddressLine2() != null && !user.getAddressLine2().equalsIgnoreCase("")) {
                    JSONObject attributeValuesJSON = getAttributeValuesJSON("s", (new com.drivewealth.dbproxy.mapper.AttributeValue()).withS(user.getAddressLine2()));
                    entityKeyAndAttributeValueMap.put("addressLine2", attributeValuesJSON);
                }
                if (user.getCity() != null && !user.getCity().equalsIgnoreCase("")) {
                    JSONObject attributeValuesJSON = getAttributeValuesJSON("s", (new com.drivewealth.dbproxy.mapper.AttributeValue()).withS(user.getCity()));
                    entityKeyAndAttributeValueMap.put("city", attributeValuesJSON);
                }
                if (user.getCountryID() != null && !user.getCountryID().equalsIgnoreCase("")) {
                    JSONObject attributeValuesJSON = getAttributeValuesJSON("s", (new com.drivewealth.dbproxy.mapper.AttributeValue()).withS(user.getCountryID()));
                    entityKeyAndAttributeValueMap.put("countryID", attributeValuesJSON);
                }
                if (user.getCreatedWhen() != null && !user.getCreatedWhen().equalsIgnoreCase("")) {
                    JSONObject attributeValuesJSON = getAttributeValuesJSON("s", (new com.drivewealth.dbproxy.mapper.AttributeValue()).withS(user.getCreatedWhen()));
                    entityKeyAndAttributeValueMap.put("createdWhen", attributeValuesJSON);
                }
                if (user.getEmployerCity() != null && !user.getEmployerCity().equalsIgnoreCase("")) {
                    JSONObject attributeValuesJSON = getAttributeValuesJSON("s", (new com.drivewealth.dbproxy.mapper.AttributeValue()).withS(user.getEmployerCity()));
                    entityKeyAndAttributeValueMap.put("employerCity", attributeValuesJSON);
                }
                if (user.getEmployerCompany() != null && !user.getEmployerCompany().equalsIgnoreCase("")) {
                    JSONObject attributeValuesJSON = getAttributeValuesJSON("s", (new com.drivewealth.dbproxy.mapper.AttributeValue()).withS(user.getEmployerCompany()));
                    entityKeyAndAttributeValueMap.put("employerCompany", attributeValuesJSON);
                }
                if (user.getEmployerCountryID() != null && !user.getEmployerCountryID().equalsIgnoreCase("")) {
                    JSONObject attributeValuesJSON = getAttributeValuesJSON("s", (new com.drivewealth.dbproxy.mapper.AttributeValue()).withS(user.getEmployerCountryID()));
                    entityKeyAndAttributeValueMap.put("employerCountryID", attributeValuesJSON);
                }
                if (user.getAddressLine1() != null && !user.getAddressLine1().equalsIgnoreCase("")) {
                    JSONObject attributeValuesJSON = getAttributeValuesJSON("s", (new com.drivewealth.dbproxy.mapper.AttributeValue()).withS(user.getAddressLine1()));
                    entityKeyAndAttributeValueMap.put("jointAddressLine1", attributeValuesJSON);
                }
                if (user.getAddressLine2() != null && !user.getAddressLine2().equalsIgnoreCase("")) {
                    JSONObject attributeValuesJSON = getAttributeValuesJSON("s", (new com.drivewealth.dbproxy.mapper.AttributeValue()).withS(user.getAddressLine2()));
                    entityKeyAndAttributeValueMap.put("jointAddressLine2", attributeValuesJSON);
                }
                if (user.getJointCitizenship() != null && !user.getJointCitizenship().equalsIgnoreCase("")) {
                    JSONObject attributeValuesJSON = getAttributeValuesJSON("s", (new com.drivewealth.dbproxy.mapper.AttributeValue()).withS(user.getJointCitizenship()));
                    entityKeyAndAttributeValueMap.put("jointCitizenship", attributeValuesJSON);
                }
                if (user.getJointCity() != null && !user.getJointCity().equalsIgnoreCase("")) {
                    JSONObject attributeValuesJSON = getAttributeValuesJSON("s", (new com.drivewealth.dbproxy.mapper.AttributeValue()).withS(user.getJointCity()));
                    entityKeyAndAttributeValueMap.put("jointCity", attributeValuesJSON);
                }
                if (user.getJointCountryID() != null && !user.getJointCountryID().equalsIgnoreCase("")) {
                    JSONObject attributeValuesJSON = getAttributeValuesJSON("s", (new com.drivewealth.dbproxy.mapper.AttributeValue()).withS(user.getJointCountryID()));
                    entityKeyAndAttributeValueMap.put("jointCountryID", attributeValuesJSON);
                }
                if (user.getZipPostalCode() != null && !user.getZipPostalCode().equalsIgnoreCase("")) {
                    JSONObject attributeValuesJSON = getAttributeValuesJSON("s", (new com.drivewealth.dbproxy.mapper.AttributeValue()).withS(user.getZipPostalCode()));
                    entityKeyAndAttributeValueMap.put("zipPostalCode", attributeValuesJSON);
                }

                break;
            }

            case ACCOUNT_TABLE:
            {

                Account account = objectMapper.readValue(responseBody, Account.class);

                if (account.getId() == null || account.getId().equalsIgnoreCase("")) {
                    throw new RuntimeException("The account id is not exist and hence, can't process the response");
                }

                final String id = account.getId();
                LOGGER.info("The account ID provided during save account in the database " + id);

                JSONObject idAttributeJSON = getAttributeValuesJSON("s", (new com.drivewealth.dbproxy.mapper.AttributeValue()).withS(id));
                entityKeyAndAttributeValueMap.put("id", idAttributeJSON);

                if (account.getAccountID() != null && !account.getAccountID().equalsIgnoreCase("")) {
                    JSONObject attributeValuesJSON = getAttributeValuesJSON("s", (new com.drivewealth.dbproxy.mapper.AttributeValue()).withS(account.getAccountID()));
                    entityKeyAndAttributeValueMap.put("accountID", attributeValuesJSON);
                }
                if (account.getAccountNo() != null && !account.getAccountNo().equalsIgnoreCase("")) {
                    JSONObject attributeValuesJSON = getAttributeValuesJSON("s", (new com.drivewealth.dbproxy.mapper.AttributeValue()).withS(account.getAccountNo()));
                    entityKeyAndAttributeValueMap.put("accountNo", attributeValuesJSON);
                }
                if (account.getCommissionID() != null && !account.getCommissionID().equalsIgnoreCase("")) {
                    JSONObject attributeValuesJSON = getAttributeValuesJSON("s", (new com.drivewealth.dbproxy.mapper.AttributeValue()).withS(account.getCommissionID()));
                    entityKeyAndAttributeValueMap.put("commissionID", attributeValuesJSON);
                }
                if (account.getCurrencyID() != null && !account.getCurrencyID().equalsIgnoreCase("")) {
                    JSONObject attributeValuesJSON = getAttributeValuesJSON("s", (new com.drivewealth.dbproxy.mapper.AttributeValue()).withS(account.getCurrencyID()));
                    entityKeyAndAttributeValueMap.put("currencyID", attributeValuesJSON);
                }
                if (!String.valueOf(account.getAccountType()).equalsIgnoreCase("null") && !String.valueOf(account.getAccountType()).equalsIgnoreCase("")) {
                    JSONObject attributeValuesJSON = getAttributeValuesJSON("n", (new com.drivewealth.dbproxy.mapper.AttributeValue()).withN(String.valueOf(account.getAccountType())));
                    entityKeyAndAttributeValueMap.put("accountType", attributeValuesJSON);
                }
                if (account.getCash() != null && !account.getCash().toString().equalsIgnoreCase("")) {
                    JSONObject attributeValuesJSON = getAttributeValuesJSON("n", (new com.drivewealth.dbproxy.mapper.AttributeValue()).withN(String.valueOf(account.getCash())));
                    entityKeyAndAttributeValueMap.put("accountID", attributeValuesJSON);
                }
                if (account.getDisableSubscriptions() != null && !account.getDisableSubscriptions().toString().equalsIgnoreCase("")) {
                    JSONObject attributeValuesJSON = getAttributeValuesJSON("bool", (new com.drivewealth.dbproxy.mapper.AttributeValue()).withBOOL(account.getDisableSubscriptions()));
                    entityKeyAndAttributeValueMap.put("disableSubscriptions", attributeValuesJSON);
                }

                break;
            }

            default: {
                LOGGER.info("This entity type is not supported...");
                throw new UnsupportedEncodingException("This type of entity is not supported and we dont have a table in the CockroachDB");
            }

        }

        return entityKeyAndAttributeValueMap;
    }


    /*
    * This will create a JSON object like below to match the DynamoDB response:
    * */
    private static JSONObject getAttributeValuesJSON(String dataType, AttributeValue attributeValue) {

        JSONObject attributeValuesJSON = new JSONObject();

        String[] arr = {
            "s",
            "n",
            "b",
            "m",
            "l",
            "ss",
            "ns",
            "bs",
            "null",
            "bool"
        };

        for (String key : arr) {
            attributeValuesJSON.put(key, JSONObject.NULL);
        }

        switch (dataType) {
            case "s":
            {
                attributeValuesJSON.put("s", attributeValue.getS());
                break;
            }
            case "n":
            {
                attributeValuesJSON.put("n", attributeValue.getN());
                break;
            }
            case "b":
            {
                attributeValuesJSON.put("b", attributeValue.getB());
                break;
            }
            case "ss":
            {
                attributeValuesJSON.put("ss", attributeValue.getSS());
                break;
            }
            case "ns":
            {
                attributeValuesJSON.put("ns", attributeValue.getNS());
                break;
            }
            case "bs":
            {
                attributeValuesJSON.put("bs", attributeValue.getBS());
                break;
            }
            case "m":
            {
                attributeValuesJSON.put("m", attributeValue.getM());
                break;
            }
            case "l":
            {
                attributeValuesJSON.put("l", attributeValue.getL());
                break;
            }
            case "null":
            {
                attributeValuesJSON.put("null", attributeValue.getNULL());
                break;
            }
            case "bool":
            {
                attributeValuesJSON.put("bool", attributeValue.getBOOL());
                break;
            }
            default:
            {
                throw new UnsupportedOperationException("This data type is not suported in the DyanmoDB");
            }
        }

        return attributeValuesJSON;
    }

}
