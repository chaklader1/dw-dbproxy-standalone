package com.drivewealth.dbproxy.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAutoGeneratedKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAutoGeneratedKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;


@lombok.ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
@DynamoDBTable(tableName = "Account")
public class Account {

    @Getter
    @Setter
    @JsonProperty("id")
    @DynamoDBHashKey
    @DynamoDBAutoGeneratedKey
    private String id;

    @Getter
    @Setter
    @JsonProperty("accountID")
    @DynamoDBAttribute
    private String accountID;

    @Getter
    @Setter
    @JsonProperty("accountNo")
    @DynamoDBAttribute
    private String accountNo;

    @Getter
    @Setter
    @JsonProperty("commissionID")
    @DynamoDBAttribute
    private String commissionID;

    @Getter
    @Setter
    @JsonProperty("currencyID")
    @DynamoDBAttribute
    private String currencyID;

    @Getter
    @Setter
    @JsonProperty("accountType")
    @JsonInclude(Include.NON_DEFAULT)
    @DynamoDBAttribute
    private int accountType;

    @Getter
    @Setter
    @JsonProperty("cash")
    @DynamoDBAttribute
    private BigDecimal cash;

    @Getter
    @Setter
    @JsonProperty("disableSubscriptions")
    @DynamoDBAttribute
    private Boolean disableSubscriptions;
}
