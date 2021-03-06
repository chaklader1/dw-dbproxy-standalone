package com.drivewealth.dbproxy.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAutoGeneratedKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamoDBTable(tableName = "User")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {
    @DynamoDBHashKey
    @DynamoDBAutoGeneratedKey
    @Getter
    @Setter
    @JsonProperty("userID")
    private String userID;

    @DynamoDBAttribute
    @Getter
    @Setter
    @JsonProperty("userType")
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int userType;

    @DynamoDBAttribute
    @Getter
    @Setter
    @JsonProperty("displayName")
    private String displayName;

    @DynamoDBAttribute
    @Getter
    @Setter
    @JsonProperty("avatarUrl")
    private String avatarUrl;

    @DynamoDBAttribute
    @Getter
    @Setter
    @JsonProperty("dob")
    private String dob;

    @DynamoDBAttribute
    @Getter
    @Setter
    @JsonProperty("emailAddress1")
    private String emailAddress1;

    @DynamoDBAttribute
    @Getter
    @Setter
    @JsonProperty("emailAddress2")
    private String emailAddress2;

    @DynamoDBAttribute
    @Getter
    @Setter
    @JsonProperty("firstName")
    private String firstName;

    @DynamoDBAttribute
    @Getter
    @Setter
    @JsonProperty("gender")
    private String gender;

    @DynamoDBAttribute
    @Getter
    @Setter
    @JsonProperty("idNo")
    private String idNo;

    @DynamoDBAttribute
    @Getter
    @Setter
    @JsonProperty("idType")
    private String idType;

    @DynamoDBAttribute
    @Getter
    @Setter
    @JsonProperty("jointFirstName")
    private String jointFirstName;

    @DynamoDBAttribute
    @Getter
    @Setter
    @JsonProperty("jointGender")
    private String jointGender;

    @DynamoDBAttribute
    @Getter
    @Setter
    @JsonProperty("jointLastName")
    private String jointLastName;

    @DynamoDBAttribute
    @Getter
    @Setter
    @JsonProperty("jointMaritalStatus")
    private String jointMaritalStatus;

    @DynamoDBAttribute
    @Getter
    @Setter
    @JsonProperty("jointZipPostalCode")
    private String jointZipPostalCode;

    @DynamoDBAttribute
    @Getter
    @Setter
    @JsonProperty("commissionID")
    private String commissionID;

    @DynamoDBAttribute
    @Getter
    @Setter
    @JsonProperty("commissionGroupID")
    private String commissionGroupID;

    @DynamoDBAttribute
    @Getter
    @Setter
    @JsonProperty("commissionRate")
    private BigDecimal commissionRate;

    @DynamoDBAttribute
    @Getter
    @Setter
    @JsonProperty("agreementID")
    private String agreementID;

    @DynamoDBAttribute
    @Getter
    @Setter
    @JsonProperty("ackCustomerAgreement")
    private int ackCustomerAgreement;

    @DynamoDBAttribute
    @Getter
    @Setter
    @JsonProperty("ackDataSharing")
    private Boolean ackDataSharing;

    @DynamoDBAttribute
    @Getter
    @Setter
    @JsonProperty("ackJointSignedBy")
    private String ackJointSignedBy;

    @DynamoDBAttribute
    @Getter
    @Setter
    @JsonProperty("ackJointSignedWhen")
    private String ackJointSignedWhen;

    @DynamoDBAttribute
    @Getter
    @Setter
    @JsonProperty("ackMarginAgreementWhen")
    private String ackMarginAgreementWhen;

    @DynamoDBAttribute
    @Getter
    @Setter
    @JsonProperty("ackSignedWhen")
    private String ackSignedWhen;


    @DynamoDBAttribute
    @Getter
    @Setter
    @JsonProperty("addressID")
    private String addressID;

    @DynamoDBAttribute
    @Getter
    @Setter
    @JsonProperty("addressLine1")
    private String addressLine1;

    @DynamoDBAttribute
    @Getter
    @Setter
    @JsonProperty("addressLine2")
    private String addressLine2;

    @DynamoDBAttribute
    @Getter
    @Setter
    @JsonProperty("city")
    private String city;

    @DynamoDBAttribute
    @Getter
    @Setter
    @JsonProperty("countryID")
    private String countryID;

    @DynamoDBAttribute
    @Getter
    @Setter
    @JsonProperty("createdWhen")
    private String createdWhen;

    @DynamoDBAttribute
    @Getter
    @Setter
    @JsonProperty("employerCity")
    private String employerCity;

    @DynamoDBAttribute
    @Getter
    @Setter
    @JsonProperty("employerCompany")
    private String employerCompany;

    @DynamoDBAttribute
    @Getter
    @Setter
    @JsonProperty("employerCountryID")
    private String employerCountryID;

    @DynamoDBAttribute
    @Getter
    @Setter
    @JsonProperty("jointAddressLine1")
    private String jointAddressLine1;

    @DynamoDBAttribute
    @Getter
    @Setter
    @JsonProperty("jointAddressLine2")
    private String jointAddressLine2;

    @DynamoDBAttribute
    @Getter
    @Setter
    @JsonProperty("jointCitizenship")
    private String jointCitizenship;

    @DynamoDBAttribute
    @Getter
    @Setter
    @JsonProperty("jointCity")
    private String jointCity;

    @DynamoDBAttribute
    @Getter
    @Setter
    @JsonProperty("jointCountryID")
    private String jointCountryID;

    @DynamoDBAttribute
    @Getter
    @Setter
    @JsonProperty("zipPostalCode")
    private String zipPostalCode;


}
