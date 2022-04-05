package com.drivewealth.dbproxy.util;


import org.springframework.beans.factory.annotation.Value;



public class Parameters {


    public static int RETRY_COUNT = 1;

    public static final String PUTITEM = "PutItem";
    public static final String GETITEM = "GetItem";
    public static final String DELETEITEM = "DeleteItem";
    public static final String UPDATEITEM = "UpdateItem";


    public static final  String IS_BOTH_DATABASE = "ACTIVE_ACTIVE";
    public static final  String IS_ONLY_DYNAMODB = "ACTIVE_PASSIVE";
    public static final  String IS_ONLY_COCKROACHDB = "PASSIVE_ACTIVE";

    public static final  String IS_SYNCHRONOUS = "SYNCHRONOUS";
    public static final  String IS_ASYNCHRONOUS = "ASYNCHRONOUS";


    public static final String X_AMZN_REQUESTID = "x-amzn-RequestId";
    public static final String X_AMZN_RESPONSE = "x-amzn-Response";
    public static final String X_AWS_JSON = "application/x-amz-json-1.0";
    public static final String X_AMZN_TARGET = "x-amz-target";
    public static final String AMZ_SDK_INVOCATION_ID = "amz-sdk-invocation-id";
    public static final String DB_TYPE = "DBType";

    public static final int REQUEST_ID_LENGTH = 52;


    public static final String USER_TABLE = "User";
    public static final String COMMISSION_TABLE = "Commission";
    public static final String ADDRESS_TABLE = "Address";
    public static final String AGREEMENT_TABLE = "Agreement";
    public static final String ACCOUNT_TABLE = "Account";
    public static final String GRPCPOC_TABLE = "GrpcPoc";



    public static final String DYNAMODB_RESPONSE_DATETIME_FORMAT = "EEE, dd MMM yyyy hh:mm:ss z";
    public static final String TIMEZONE = "GMT";

    public static final int TIMEOUT_LIMIT = 35;


    public static final int TIMEOUT_SHUTDOWN = 5;
    public static final int TIMEOUT_SHUTDOWN_NOW = 3;

}
