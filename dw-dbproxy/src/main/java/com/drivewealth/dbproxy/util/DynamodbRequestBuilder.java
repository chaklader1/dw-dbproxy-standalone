package com.drivewealth.dbproxy.util;

import com.amazonaws.services.dynamodbv2.model.*;
import com.drivewealth.dbproxy.entity.Account;
import com.drivewealth.dbproxy.entity.GrpcPocDTO;
import com.drivewealth.dbproxy.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static com.drivewealth.dbproxy.util.Parameters.*;


@Component
public class DynamodbRequestBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(DynamodbRequestBuilder.class);


    @Autowired
    private ObjectMapper objectMapper;

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public PutItemRequest createPutItemRequest(String requestBody, String tableName) throws IOException {

        PutItemRequest putItemRequest = new PutItemRequest();


        Map<String, AttributeValue> map = new HashMap<>();

        switch (tableName) {

            case "GrpcPoc":{

                GrpcPocDTO grpcPoc = objectMapper.readValue(requestBody, GrpcPocDTO.class);

                final String id = UUID.randomUUID().toString();
                map.put("id", new AttributeValue(id));

                if (grpcPoc.getName() != null && !grpcPoc.getName().equalsIgnoreCase("")) {
                    map.put("name", new AttributeValue(grpcPoc.getName()));
                }
                if (grpcPoc.getContact() != null && !grpcPoc.getContact().equalsIgnoreCase("")) {
                    map.put("contact", new AttributeValue(grpcPoc.getContact()));
                }
                if (grpcPoc.getCreatedAt() != null && !grpcPoc.getCreatedAt().equalsIgnoreCase("")) {
                    map.put("createdAt", new AttributeValue(grpcPoc.getCreatedAt()));
                }

                break;
            }
            case USER_TABLE:
            {
                User user = objectMapper.readValue(requestBody, User.class);

                final String userId = UUID.randomUUID().toString();
                LOGGER.info("The user ID provided during save user in the database " + userId);
                map.put("userID", new AttributeValue(userId));


                if (!String.valueOf(user.getUserType()).equalsIgnoreCase("null") && !String.valueOf(user.getUserType()).equalsIgnoreCase("")) {
                    map.put("userType", new AttributeValue(String.valueOf(user.getUserType())));
                }
                if (user.getDisplayName() != null && !user.getDisplayName().equalsIgnoreCase("")) {
                    map.put("displayName", new AttributeValue(user.getDisplayName()));
                }
                if (user.getAvatarUrl() != null && !user.getAvatarUrl().equalsIgnoreCase("")) {
                    map.put("avatarUrl", new AttributeValue(user.getAvatarUrl()));
                }
                if (user.getDob() != null && !user.getDob().equalsIgnoreCase("")) {
                    map.put("dob", new AttributeValue(user.getDob()));
                }
                if (user.getEmailAddress1() != null && !user.getEmailAddress1().equalsIgnoreCase("")) {
                    map.put("emailAddress1", new AttributeValue(user.getEmailAddress1()));
                }
                if (user.getEmailAddress2() != null && !user.getEmailAddress2().equalsIgnoreCase("")) {
                    map.put("emailAddress2", new AttributeValue(user.getEmailAddress2()));
                }
                if (user.getFirstName() != null && !user.getFirstName().equalsIgnoreCase("")) {
                    map.put("firstName", new AttributeValue(user.getFirstName()));
                }
                if (user.getGender() != null && !user.getGender().equalsIgnoreCase("")) {
                    map.put("gender", new AttributeValue(user.getGender()));
                }
                if (user.getIdNo() != null && !user.getIdNo().equalsIgnoreCase("")) {
                    map.put("idNo", new AttributeValue(user.getIdNo()));
                }
                if (user.getIdType() != null && !user.getIdType().equalsIgnoreCase("")) {
                    map.put("idType", new AttributeValue(user.getIdType()));
                }
                if (user.getJointFirstName() != null && !user.getJointFirstName().equalsIgnoreCase("")) {
                    map.put("jointFirstName", new AttributeValue(user.getJointFirstName()));
                }
                if (user.getJointGender() != null && !user.getJointGender().equalsIgnoreCase("")) {
                    map.put("jointGender", new AttributeValue(user.getJointGender()));
                }
                if (user.getJointLastName() != null && !user.getJointLastName().equalsIgnoreCase("")) {
                    map.put("jointLastName", new AttributeValue(user.getJointLastName()));
                }
                if (user.getJointMaritalStatus() != null && !user.getJointMaritalStatus().equalsIgnoreCase("")) {
                    map.put("jointMaritalStatus", new AttributeValue(user.getJointMaritalStatus()));
                }
                if (user.getJointZipPostalCode() != null && !user.getJointZipPostalCode().equalsIgnoreCase("")) {
                    map.put("jointZipPostalCode", new AttributeValue(user.getJointZipPostalCode()));
                }


                map.put("commissionID", new AttributeValue(UUID.randomUUID().toString()));
                if (user.getCommissionGroupID() != null && !user.getCommissionGroupID().equalsIgnoreCase("")) {
                    map.put("commissionGroupID", new AttributeValue(user.getCommissionGroupID()));
                }
                if (user.getCommissionRate() != null && !user.getCommissionRate().toString().equalsIgnoreCase("")) {
                    map.put("commissionRate", new AttributeValue(String.valueOf(user.getCommissionRate())));
                }


                map.put("agreementID", new AttributeValue(UUID.randomUUID().toString()));
                if (!String.valueOf(user.getAckCustomerAgreement()).equalsIgnoreCase("null") && !String.valueOf(user.getAckCustomerAgreement()).equalsIgnoreCase("")) {
                    map.put("ackCustomerAgreement", new AttributeValue(String.valueOf(user.getAckCustomerAgreement())));
                }
                if (user.getAckDataSharing() != null && !user.getAckDataSharing().toString().equalsIgnoreCase("")) {
                    map.put("ackDataSharing", new AttributeValue(String.valueOf(user.getAckDataSharing())));
                }
                if (user.getAckJointSignedBy() != null && !user.getAckJointSignedBy().equalsIgnoreCase("")) {
                    map.put("ackJointSignedBy", new AttributeValue(user.getAckJointSignedBy()));
                }
                if (user.getAckJointSignedWhen() != null && !user.getAckJointSignedWhen().equalsIgnoreCase("")) {
                    map.put("ackJointSignedWhen", new AttributeValue(user.getAckJointSignedWhen()));
                }
                if (user.getAckMarginAgreementWhen() != null && !user.getAckMarginAgreementWhen().equalsIgnoreCase("")) {
                    map.put("ackMarginAgreementWhen", new AttributeValue(user.getAckMarginAgreementWhen()));
                }
                if (user.getAckSignedWhen() != null && !user.getAckSignedWhen().equalsIgnoreCase("")) {
                    map.put("ackSignedWhen", new AttributeValue(user.getAckSignedWhen()));
                }


                map.put("addressID", new AttributeValue(UUID.randomUUID().toString()));
                if (user.getAddressLine1() != null && !user.getAddressLine1().equalsIgnoreCase("")) {
                    map.put("addressLine1", new AttributeValue(user.getAddressLine1()));
                }
                if (user.getAddressLine2() != null && !user.getAddressLine2().equalsIgnoreCase("")) {
                    map.put("addressLine2", new AttributeValue(user.getAddressLine2()));
                }
                if (user.getCity() != null && !user.getCity().equalsIgnoreCase("")) {
                    map.put("city", new AttributeValue(user.getCity()));
                }
                if (user.getCountryID() != null && !user.getCountryID().equalsIgnoreCase("")) {
                    map.put("countryID", new AttributeValue(user.getCountryID()));
                }
                if (user.getCreatedWhen() != null && !user.getCreatedWhen().equalsIgnoreCase("")) {
                    map.put("createdWhen", new AttributeValue(user.getCreatedWhen()));
                }
                if (user.getEmployerCity() != null && !user.getEmployerCity().equalsIgnoreCase("")) {
                    map.put("employerCity", new AttributeValue(user.getEmployerCity()));
                }
                if (user.getEmployerCompany() != null && !user.getEmployerCompany().equalsIgnoreCase("")) {
                    map.put("employerCompany", new AttributeValue(user.getEmployerCompany()));
                }
                if (user.getEmployerCountryID() != null && !user.getEmployerCountryID().equalsIgnoreCase("")) {
                    map.put("employerCountryID", new AttributeValue(user.getEmployerCountryID()));
                }
                if (user.getAddressLine1() != null && !user.getAddressLine1().equalsIgnoreCase("")) {
                    map.put("jointAddressLine1", new AttributeValue(user.getAddressLine1()));
                }
                if (user.getAddressLine2() != null && !user.getAddressLine2().equalsIgnoreCase("")) {
                    map.put("jointAddressLine2", new AttributeValue(user.getAddressLine2()));
                }
                if (user.getJointCitizenship() != null && !user.getJointCitizenship().equalsIgnoreCase("")) {
                    map.put("jointCitizenship", new AttributeValue(user.getJointCitizenship()));
                }
                if (user.getJointCity() != null && !user.getJointCity().equalsIgnoreCase("")) {
                    map.put("jointCity", new AttributeValue(user.getJointCity()));
                }
                if (user.getJointCountryID() != null && !user.getJointCountryID().equalsIgnoreCase("")) {
                    map.put("jointCountryID", new AttributeValue(user.getJointCountryID()));
                }
                if (user.getZipPostalCode() != null && !user.getZipPostalCode().equalsIgnoreCase("")) {
                    map.put("zipPostalCode", new AttributeValue(user.getZipPostalCode()));
                }

                break;
            }

            /*
             * for account table "id" is the partition key (primary key) and "accountId" is merelt for another column
             * */
            case ACCOUNT_TABLE: {
                Account account = objectMapper.readValue(requestBody, Account.class);

                final String id = UUID.randomUUID().toString();
                LOGGER.info("The account ID provided during save account in the database " + id);
                map.put("id", new AttributeValue(id));

                if (account.getAccountID() != null && !account.getAccountID().equalsIgnoreCase("")) {
                    map.put("accountID", new AttributeValue(account.getAccountID()));
                }
                if (account.getAccountNo() != null && !account.getAccountNo().equalsIgnoreCase("")) {
                    map.put("accountNo", (new AttributeValue()).withS(account.getAccountNo()));
                }
                if (account.getCommissionID() != null && !account.getCommissionID().equalsIgnoreCase("")) {
                    map.put("commissionID", new AttributeValue(account.getCommissionID()));
                }
                if (account.getCurrencyID() != null && !account.getCurrencyID().equalsIgnoreCase("")) {
                    map.put("currencyID", new AttributeValue(account.getCurrencyID()));
                }
                if (!String.valueOf(account.getAccountType()).equalsIgnoreCase("null") && !String.valueOf(account.getAccountType()).equalsIgnoreCase("")) {
                    map.put("accountType", (new AttributeValue()).withN(String.valueOf(account.getAccountType())));
                }
                if (account.getCash() != null && !account.getCash().toString().equalsIgnoreCase("")) {
                    map.put("cash", (new AttributeValue()).withN(String.valueOf(account.getCash())));
                }
                if (account.getDisableSubscriptions() != null && !account.getDisableSubscriptions().toString().equalsIgnoreCase("")) {
                    map.put("disableSubscriptions", (new AttributeValue()).withBOOL(account.getDisableSubscriptions()));
                }

                break;
            }

            default:
            {
                throw new UnsupportedEncodingException("The table name provided doesn't exist in the database");
            }
        }

        putItemRequest.setItem(map);

        putItemRequest.setTableName(tableName);
        putItemRequest.setReturnConsumedCapacity(ReturnConsumedCapacity.TOTAL);
        putItemRequest.setReturnValues(ReturnValue.ALL_OLD);

        return putItemRequest;
    }

    private boolean isValidUUID(String uuidString) {
        try {
            UUID uuid = UUID.fromString(uuidString);
            return true;
        } catch (IllegalArgumentException exception) {
            LOGGER.error("The provided UUID is not valid : "+ uuidString);
        }

        return false;
    }

    public GetItemRequest createGetItemRequest(String tableName, String requestBody) throws UnsupportedEncodingException {

        GetItemRequest request = new GetItemRequest();

        JSONObject requestBodyJsonObject = new JSONObject(requestBody);
        Map<String, AttributeValue> keysMap = new HashMap<>();

        if (isRequestHasKey(requestBodyJsonObject, "userID")) {
            String userId = requestBodyJsonObject.getString("userID");
            keysMap.put("userID", new AttributeValue(userId));
        }
        else if (isRequestHasKey(requestBodyJsonObject, "id")) {
            String id = requestBodyJsonObject.getString("id");
            keysMap.put("id", new AttributeValue(id));
        }
        else {
            throw new UnsupportedEncodingException("The request body doesn't have the key to acquire the entity");
        }

        request.setKey(keysMap);
        request.setTableName(tableName);
        request.setReturnConsumedCapacity(ReturnConsumedCapacity.TOTAL);
        request.setConsistentRead(true);

        return request;
    }

    public DeleteItemRequest createDeleteItemRequest(String tableName, String requestBody) throws UnsupportedEncodingException {

        DeleteItemRequest deleteItemRequest = new DeleteItemRequest();

        JSONObject requestBodyJsonObject = new JSONObject(requestBody);
        Map<String, AttributeValue> keysMap = new HashMap<>();

        if (isRequestHasKey(requestBodyJsonObject, "userID")) {
            String userId = requestBodyJsonObject.getString("userID");
            keysMap.put("userID", new AttributeValue(userId));
        }
        else if (isRequestHasKey(requestBodyJsonObject, "id")) {
            // id and accountId are different
            String id = requestBodyJsonObject.getString("id");
            keysMap.put("id", new AttributeValue(id));
        }
        else {
            throw new UnsupportedEncodingException("The requestbody doesn't have the key to delete the entity");
        }

        deleteItemRequest.setKey(keysMap);
        deleteItemRequest.setTableName(tableName);
        deleteItemRequest.setReturnConsumedCapacity(ReturnConsumedCapacity.TOTAL);
        deleteItemRequest.setReturnValues(ReturnValue.ALL_OLD);

        return deleteItemRequest;
    }

    public UpdateItemRequest createUpdateItemRequest(String tableName, String requestBody) throws IOException {

        UpdateItemRequest updateItemRequest = new UpdateItemRequest();

        Map<String, AttributeValueUpdate> map = new HashMap<>();
        Map<String, AttributeValue> keysMap = new HashMap<>();

        switch (tableName) {

            case USER_TABLE:
            {

                User user = objectMapper.readValue(requestBody, User.class);
                LOGGER.info("We proceed to update info for the user with ID : " + user.getUserID());

                keysMap.put("userID", new AttributeValue(user.getUserID()));

                if (!String.valueOf(user.getUserType()).equalsIgnoreCase("null") && !String.valueOf(user.getUserType()).equalsIgnoreCase("")) {
                    map.put("userType", new AttributeValueUpdate(new AttributeValue(String.valueOf(user.getUserType())), AttributeAction.PUT));
                }
                if (user.getDisplayName() != null && !user.getDisplayName().equalsIgnoreCase("")) {
                    map.put("displayName", new AttributeValueUpdate(new AttributeValue(user.getDisplayName()), AttributeAction.PUT));
                }
                if (user.getAvatarUrl() != null && !user.getAvatarUrl().equalsIgnoreCase("")) {
                    map.put("avatarUrl", new AttributeValueUpdate(new AttributeValue(user.getAvatarUrl()), AttributeAction.PUT));
                }
                if (user.getDob() != null && !user.getDob().equalsIgnoreCase("")) {
                    map.put("dob", new AttributeValueUpdate(new AttributeValue(user.getDob()), AttributeAction.PUT));
                }
                if (user.getEmailAddress1() != null && !user.getEmailAddress1().equalsIgnoreCase("")) {
                    map.put("emailAddress1", new AttributeValueUpdate(new AttributeValue(user.getEmailAddress1()), AttributeAction.PUT));
                }
                if (user.getEmailAddress2() != null && !user.getEmailAddress2().equalsIgnoreCase("")) {
                    map.put("emailAddress2", new AttributeValueUpdate(new AttributeValue(user.getEmailAddress2()), AttributeAction.PUT));
                }
                if (user.getFirstName() != null && !user.getFirstName().equalsIgnoreCase("")) {
                    map.put("firstName", new AttributeValueUpdate(new AttributeValue(user.getFirstName()), AttributeAction.PUT));
                }
                if (user.getGender() != null && !user.getGender().equalsIgnoreCase("")) {
                    map.put("gender", new AttributeValueUpdate(new AttributeValue(user.getGender()), AttributeAction.PUT));
                }
                if (user.getIdNo() != null && !user.getIdNo().equalsIgnoreCase("")) {
                    map.put("idNo", new AttributeValueUpdate(new AttributeValue(user.getIdNo()), AttributeAction.PUT));
                }
                if (user.getIdType() != null && !user.getIdType().equalsIgnoreCase("")) {
                    map.put("idType", new AttributeValueUpdate(new AttributeValue(user.getIdType()), AttributeAction.PUT));
                }
                if (user.getJointFirstName() != null && !user.getJointFirstName().equalsIgnoreCase("")) {
                    map.put("jointFirstName", new AttributeValueUpdate(new AttributeValue(user.getJointFirstName()), AttributeAction.PUT));
                }
                if (user.getJointGender() != null && !user.getJointGender().equalsIgnoreCase("")) {
                    map.put("jointGender", new AttributeValueUpdate(new AttributeValue(user.getJointGender()), AttributeAction.PUT));
                }
                if (user.getJointLastName() != null && !user.getJointLastName().equalsIgnoreCase("")) {
                    map.put("jointLastName", new AttributeValueUpdate(new AttributeValue(user.getJointLastName()), AttributeAction.PUT));
                }
                if (user.getJointMaritalStatus() != null && !user.getJointMaritalStatus().equalsIgnoreCase("")) {
                    map.put("jointMaritalStatus", new AttributeValueUpdate(new AttributeValue(user.getJointMaritalStatus()), AttributeAction.PUT));
                }
                if (user.getJointZipPostalCode() != null && !user.getJointZipPostalCode().equalsIgnoreCase("")) {
                    map.put("jointZipPostalCode", new AttributeValueUpdate(new AttributeValue(user.getJointZipPostalCode()), AttributeAction.PUT));
                }



                final String userCommissionID = user.getCommissionID() != null && !user.getCommissionID().equalsIgnoreCase("") && isValidUUID(user.getCommissionID())? user.getCommissionID() : UUID.randomUUID().toString();
                map.put("commissionID", new AttributeValueUpdate(new AttributeValue(userCommissionID), AttributeAction.PUT));
                if (user.getCommissionGroupID() != null && !user.getCommissionGroupID().equalsIgnoreCase("")) {
                    map.put("commissionGroupID", new AttributeValueUpdate(new AttributeValue(user.getCommissionGroupID()), AttributeAction.PUT));
                }
                if (user.getCommissionRate() != null && !user.getCommissionRate().toString().equalsIgnoreCase("")) {
                    map.put("commissionRate", new AttributeValueUpdate(new AttributeValue(String.valueOf(user.getCommissionRate())), AttributeAction.PUT));
                }



                final String userAgreementID = user.getAgreementID() != null && !user.getAgreementID().equalsIgnoreCase("") && isValidUUID(user.getAgreementID())? user.getAgreementID() : UUID.randomUUID().toString();
                map.put("agreementID", new AttributeValueUpdate(new AttributeValue(userAgreementID), AttributeAction.PUT));
                if (!String.valueOf(user.getAckCustomerAgreement()).equalsIgnoreCase("null") && !String.valueOf(user.getAckCustomerAgreement()).equalsIgnoreCase("")) {
                    map.put("ackCustomerAgreement", new AttributeValueUpdate(new AttributeValue(String.valueOf(user.getAckCustomerAgreement())), AttributeAction.PUT));
                }
                if (user.getAckDataSharing() != null && !user.getAckDataSharing().toString().equalsIgnoreCase("")) {
                    map.put("ackDataSharing", new AttributeValueUpdate(new AttributeValue(String.valueOf(user.getAckDataSharing())), AttributeAction.PUT));
                }
                if (user.getAckJointSignedBy() != null && !user.getAckJointSignedBy().equalsIgnoreCase("")) {
                    map.put("ackJointSignedBy", new AttributeValueUpdate(new AttributeValue(user.getAckJointSignedBy()), AttributeAction.PUT));
                }
                if (user.getAckJointSignedWhen() != null && !user.getAckJointSignedWhen().equalsIgnoreCase("")) {
                    map.put("ackJointSignedWhen", new AttributeValueUpdate(new AttributeValue(user.getAckJointSignedWhen()), AttributeAction.PUT));
                }
                if (user.getAckMarginAgreementWhen() != null && !user.getAckMarginAgreementWhen().equalsIgnoreCase("")) {
                    map.put("ackMarginAgreementWhen", new AttributeValueUpdate(new AttributeValue(user.getAckMarginAgreementWhen()), AttributeAction.PUT));
                }
                if (user.getAckSignedWhen() != null && !user.getAckSignedWhen().equalsIgnoreCase("")) {
                    map.put("ackSignedWhen", new AttributeValueUpdate(new AttributeValue(user.getAckSignedWhen()), AttributeAction.PUT));
                }



                final String userAddressID = user.getAddressID() != null && !user.getAddressID().equalsIgnoreCase("") && isValidUUID(user.getAddressID())? user.getAddressID() : UUID.randomUUID().toString();
                map.put("addressID", new AttributeValueUpdate(new AttributeValue(userAddressID), AttributeAction.PUT));
                if (user.getAddressLine1() != null && !user.getAddressLine1().equalsIgnoreCase("")) {
                    map.put("addressLine1", new AttributeValueUpdate(new AttributeValue(user.getAddressLine1()), AttributeAction.PUT));
                }
                if (user.getAddressLine2() != null && !user.getAddressLine2().equalsIgnoreCase("")) {
                    map.put("addressLine2", new AttributeValueUpdate(new AttributeValue(user.getAddressLine2()), AttributeAction.PUT));
                }
                if (user.getCity() != null && !user.getCity().equalsIgnoreCase("")) {
                    map.put("city", new AttributeValueUpdate(new AttributeValue(user.getCity()), AttributeAction.PUT));
                }
                if (user.getCountryID() != null && !user.getCountryID().equalsIgnoreCase("")) {
                    map.put("countryID", new AttributeValueUpdate(new AttributeValue(user.getCountryID()), AttributeAction.PUT));
                }
                if (user.getCreatedWhen() != null && !user.getCreatedWhen().equalsIgnoreCase("")) {
                    map.put("createdWhen", new AttributeValueUpdate(new AttributeValue(user.getCreatedWhen()), AttributeAction.PUT));
                }
                if (user.getEmployerCity() != null && !user.getEmployerCity().equalsIgnoreCase("")) {
                    map.put("employerCity", new AttributeValueUpdate(new AttributeValue(user.getEmployerCity()), AttributeAction.PUT));
                }
                if (user.getEmployerCompany() != null && !user.getEmployerCompany().equalsIgnoreCase("")) {
                    map.put("employerCompany", new AttributeValueUpdate(new AttributeValue(user.getEmployerCompany()), AttributeAction.PUT));
                }
                if (user.getEmployerCountryID() != null && !user.getEmployerCountryID().equalsIgnoreCase("")) {
                    map.put("employerCountryID", new AttributeValueUpdate(new AttributeValue(user.getEmployerCountryID()), AttributeAction.PUT));
                }
                if (user.getAddressLine1() != null && !user.getAddressLine1().equalsIgnoreCase("")) {
                    map.put("jointAddressLine1", new AttributeValueUpdate(new AttributeValue(user.getAddressLine1()), AttributeAction.PUT));
                }
                if (user.getAddressLine2() != null && !user.getAddressLine2().equalsIgnoreCase("")) {
                    map.put("jointAddressLine2", new AttributeValueUpdate(new AttributeValue(user.getAddressLine2()), AttributeAction.PUT));
                }
                if (user.getJointCitizenship() != null && !user.getJointCitizenship().equalsIgnoreCase("")) {
                    map.put("jointCitizenship", new AttributeValueUpdate(new AttributeValue(user.getJointCitizenship()), AttributeAction.PUT));
                }
                if (user.getJointCity() != null && !user.getJointCity().equalsIgnoreCase("")) {
                    map.put("jointCity", new AttributeValueUpdate(new AttributeValue(user.getJointCity()), AttributeAction.PUT));
                }
                if (user.getJointCountryID() != null && !user.getJointCountryID().equalsIgnoreCase("")) {
                    map.put("jointCountryID", new AttributeValueUpdate(new AttributeValue(user.getJointCountryID()), AttributeAction.PUT));
                }
                if (user.getZipPostalCode() != null && !user.getZipPostalCode().equalsIgnoreCase("")) {
                    map.put("zipPostalCode", new AttributeValueUpdate(new AttributeValue(user.getZipPostalCode()), AttributeAction.PUT));
                }

                break;
            }

            case ACCOUNT_TABLE:
            {
                Account account = objectMapper.readValue(requestBody, Account.class);
                LOGGER.info("We proceed to update info for the account with ID : " + account.getAccountID());

                keysMap.put("id", new AttributeValue(account.getId()));


                if (account.getAccountID() != null && !account.getAccountID().equalsIgnoreCase("")) {
                    map.put("accountID", new AttributeValueUpdate(new AttributeValue(account.getAccountID()), AttributeAction.PUT));
                }
                if (account.getAccountNo() != null && !account.getAccountNo().equalsIgnoreCase("")) {
                    map.put("accountNo", new AttributeValueUpdate(new AttributeValue(account.getAccountNo()), AttributeAction.PUT));
                }
                if (account.getCommissionID() != null && !account.getCommissionID().equalsIgnoreCase("")) {
                    map.put("commissionID", new AttributeValueUpdate(new AttributeValue(account.getCommissionID()), AttributeAction.PUT));
                }
                if (account.getCurrencyID() != null && !account.getCurrencyID().equalsIgnoreCase("")) {
                    map.put("currencyID", new AttributeValueUpdate(new AttributeValue(account.getCurrencyID()), AttributeAction.PUT));
                }
                if (account.getAccountType() != 0) {
                    map.put("accountType", new AttributeValueUpdate(new AttributeValue(String.valueOf(account.getAccountType())), AttributeAction.PUT));
                }
                if (!Objects.equals(account.getCash(), new BigDecimal("0.0"))) {
                    map.put("cash", new AttributeValueUpdate(new AttributeValue(String.valueOf(account.getCash())), AttributeAction.PUT));
                }
                if (account.getDisableSubscriptions() != null && !account.getDisableSubscriptions().toString().equalsIgnoreCase("")) {
                    map.put("disableSubscriptions", new AttributeValueUpdate(new AttributeValue(String.valueOf(account.getDisableSubscriptions())), AttributeAction.PUT));
                }

                map.put("disableSubscriptions", new AttributeValueUpdate(new AttributeValue(String.valueOf(account.getDisableSubscriptions())), AttributeAction.PUT));
                break;
            }

            default:
            {
                throw new UnsupportedEncodingException("WE dont have this table in the database and hence, the update request can't be created.");
            }
        }

        updateItemRequest.setKey(keysMap);
        updateItemRequest.setAttributeUpdates(map);

        updateItemRequest.setTableName(tableName);
        updateItemRequest.setReturnConsumedCapacity(ReturnConsumedCapacity.TOTAL);
        updateItemRequest.setReturnValues(ReturnValue.UPDATED_OLD);


        return updateItemRequest;
    }

    public boolean isRequestHasKey(JSONObject requestBodyJsonObject, String key) {
        final boolean isProvidedKeyNull = requestBodyJsonObject.has(key) && !requestBodyJsonObject.getString(key).equalsIgnoreCase("null");
        return isProvidedKeyNull;
    }


}
