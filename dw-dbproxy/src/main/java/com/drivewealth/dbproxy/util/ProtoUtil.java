package com.drivewealth.dbproxy.util;


import com.amazonaws.services.dynamodbv2.document.ItemUtils;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.AttributeValueUpdate;
import com.drivewealth.dbproxy.model.ItemRequest;
import com.drivewealth.dbproxy.model.KeyRequest;
import com.drivewealth.dbproxy.model.UpdateItemKeyRequest;
import com.dw.acatsdomain.grpc.GrpcPocServiceOuterClass;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.util.JsonFormat;
import com.drivewealth.cr.grpc.server.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static com.drivewealth.dbproxy.util.Parameters.*;


@Component
public class ProtoUtil {


    private static final Logger LOGGER = LoggerFactory.getLogger(ProtoUtil.class);




    public static Object getInstance(String requestBody, String type) throws IOException {

        String itemJSON = getItemRequest(requestBody);

        return resolveGetInstance(type, itemJSON);
    }

    public static Object getKeyInstance(String requestBody, String type) throws IOException {

        String itemJSON = getKeyMetaData(requestBody);

        if (type.equalsIgnoreCase("User")) {
            UserRequest userRequest = toProto(itemJSON, UserRequest.getDefaultInstance());
            return userRequest;
        }

        return resolveGetInstance(type, itemJSON);
    }

    public static Object updateKeyInstance(String requestBody, String type) throws IOException {

        String itemJSON = ItemUtils.toItem(getUpdateAttributeValue(requestBody)).toJSON();

        if (type.equalsIgnoreCase("User")) {
            UserRequest userRequest = toProto(itemJSON, UserRequest.getDefaultInstance());
            return userRequest;
        }

        return resolveGetInstance(type, itemJSON);
    }

    private static Object resolveGetInstance(String type, String itemJSON) throws InvalidProtocolBufferException, UnsupportedEncodingException {

        switch (type) {

            case USER_TABLE:
            {
                User user = toProto(itemJSON, User.getDefaultInstance());
                return user;
            }
            case COMMISSION_TABLE:
            {
                CommissionRequest commissionRequest = toProto(itemJSON, CommissionRequest.getDefaultInstance());
                return commissionRequest;
            }
            case ADDRESS_TABLE:
            {
                AddressRequest addressRequest = toProto(itemJSON, AddressRequest.getDefaultInstance());
                return addressRequest;
            }
            case AGREEMENT_TABLE:
            {
                AgreementRequest agreementRequest = toProto(itemJSON, AgreementRequest.getDefaultInstance());
                return agreementRequest;
            }
            case ACCOUNT_TABLE:
            {
                AccountRequest accountRequest = toProto(itemJSON, AccountRequest.getDefaultInstance());
                return accountRequest;
            }
            case GRPCPOC_TABLE:
            {
                final GrpcPocServiceOuterClass.GrpcPocDTO grpcPocDTO = toProto(itemJSON, GrpcPocServiceOuterClass.GrpcPocDTO.getDefaultInstance());
                return grpcPocDTO;
            }

            default:
            {
                throw new UnsupportedEncodingException("This entity type is not supported in the DB Proxy");
            }
        }
    }



    public static <T extends Message> String toJson(T obj) {

        try {
            return JsonFormat.printer().print(obj);
        } catch (Exception e) {
            throw new RuntimeException("Error converting Proto to json", e);
        }
    }

    public static <T extends MessageOrBuilder> T toProto(String protoJsonStr, T message) throws InvalidProtocolBufferException {

        Message.Builder builder = message.getDefaultInstanceForType().toBuilder();
        JsonFormat.parser().ignoringUnknownFields().merge(protoJsonStr, builder);

        T out = (T) builder.build();
        return out;
    }



    public static String getTableName(String requestBody) throws IOException {

        ObjectMapper mapper = new ObjectMapper();

        mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        ObjectReader itemObjectReader = mapper.reader(ItemRequest.class);
        ItemRequest putItemRequest = itemObjectReader.readValue(requestBody);

        String tableName = putItemRequest.getTableName();

        return tableName;
    }

    // commissionID, addressID and agreementID
    public static String getItemRequest(final String requestBody) throws IOException {

        ObjectMapper mapper = new ObjectMapper();

        mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        ObjectReader itemObjectReader = mapper.reader(ItemRequest.class);
        ItemRequest putItemRequest = itemObjectReader.readValue(requestBody);

        String response = ItemUtils.toItem(putItemRequest.getItem()).toJSON();
        return response;
    }

    public static String getKeyMetaData(final String requestBody) throws IOException {

        ObjectMapper mapper = new ObjectMapper();

        mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        ObjectReader keyObjectReader = mapper.reader(KeyRequest.class);
        KeyRequest keyRequest = keyObjectReader.readValue(requestBody);

        String response = ItemUtils.toItem(keyRequest.getKey()).toJSON();

        return response;
    }

    public static UpdateItemKeyRequest getUpdateItemRequest(final String requestBody) throws IOException {

        ObjectMapper mapper = new ObjectMapper();

        mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        ObjectReader itemObjectReader = mapper.reader(UpdateItemKeyRequest.class);
        UpdateItemKeyRequest updateItemRequest = itemObjectReader.readValue(requestBody);

        return updateItemRequest;
    }

    private  static Map<String, AttributeValue> getUpdateAttributeValue(String requestBody) throws IOException {

        UpdateItemKeyRequest updateItemRequest = getUpdateItemRequest(requestBody);
        Map<String, AttributeValue> mapAttributeValue = new HashMap<>();
        Map<String, AttributeValue> keyMapAttributeValueUpdate = updateItemRequest.getKey();

        if (keyMapAttributeValueUpdate != null) {

            for (Map.Entry<String, AttributeValue> entry : keyMapAttributeValueUpdate.entrySet()) {
                mapAttributeValue.put(entry.getKey(), entry.getValue());
            }
        }

        Map<String, AttributeValueUpdate> mapAttributeValueUpdate = updateItemRequest.getAttributeUpdates();

        if (mapAttributeValueUpdate != null) {
            for (Map.Entry<String, AttributeValueUpdate> entry : mapAttributeValueUpdate.entrySet()) {
                mapAttributeValue.put(entry.getKey(), entry.getValue().getValue());
            }
        }

        return mapAttributeValue;
    }

}

