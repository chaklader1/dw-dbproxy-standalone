package com.drivewealth.server;

import com.drivewealth.cr.grpc.server.AccountRequest;
import com.drivewealth.cr.grpc.server.AccountResponse;
import com.drivewealth.cr.grpc.server.AccountServiceGrpc;
import com.drivewealth.dbproxy.util.ProtoUtil;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.drivewealth.utils.TestUtils.readFileFromResourcesFolder;


public class AccountServiceImpl extends AccountServiceGrpc.AccountServiceImplBase {


    private static final Logger LOGGER = LoggerFactory.getLogger(AccountServiceImpl.class);


    public void saveAccount(AccountRequest accountRequest, StreamObserver<AccountResponse> responseObserver) {

        try {
            LOGGER.info("AccountServiceImpl save Account Started");

            long startDTime = System.currentTimeMillis();
            long dDuration = System.currentTimeMillis() - startDTime;

            LOGGER.info(" AccountServiceImpl -  AccountRequest - save Account took " + dDuration + " milliseconds to run");
            AccountResponse accountResponse = AccountResponse.newBuilder()
                .setResponse(1)
                .build();

            responseObserver.onNext(accountResponse);
            responseObserver.onCompleted();

            long duration = System.currentTimeMillis() - startDTime;

            LOGGER.info(" AccountServiceImpl - save Account - took " + duration + " milliseconds to run");
            LOGGER.info(" AccountServiceImpl save Account End");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getAccount(AccountRequest accountRequest, StreamObserver<AccountRequest> responseObserver) {

        try {

            final String id = accountRequest.getId();

            LOGGER.info(" AccountServiceImpl Get Account Started");
            LOGGER.info(" AccountServiceImpl Get Account accountRequest\t" + id);

            long startDTime = System.currentTimeMillis();

            long dDuration = System.currentTimeMillis() - startDTime;

            LOGGER.info(" AccountServiceImpl -  AccountRequest - Get Account took " + dDuration + " milliseconds to run");

            String accountResponseJSON = readFileFromResourcesFolder("responses/account.json");
            AccountRequest protoAccountRequest = ProtoUtil.toProto(accountResponseJSON, AccountRequest.getDefaultInstance());

            responseObserver.onNext(protoAccountRequest);
            responseObserver.onCompleted();

            long duration = System.currentTimeMillis() - startDTime;

            LOGGER.info(" UserServiceImpl - Get User - took " + duration + " milliseconds to run");
            LOGGER.info(" UserServiceImpl Get User End");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void updateAccount(AccountRequest accountRequest, StreamObserver<AccountResponse> responseObserver) {

        try {
            LOGGER.info(" AccountServiceImpl Update Account Started");
            LOGGER.info(" AccountServiceImpl Update Account getID" + accountRequest.getId());
            LOGGER.info(" AccountServiceImpl Update Account getAccountID" + accountRequest.getAccountID());

            long startDTime = System.currentTimeMillis();
            long dDuration = System.currentTimeMillis() - startDTime;

            LOGGER.info(" AccountServiceImpl -  AccountRequest - Update Account took " + dDuration + " milliseconds to run");

            AccountResponse userResponse = AccountResponse.newBuilder()
                .setResponse(1)
                .build();
            responseObserver.onNext(userResponse);
            responseObserver.onCompleted();

            long duration = System.currentTimeMillis() - startDTime;

            LOGGER.info(" AccountServiceImpl - Update Account - took " + duration + " milliseconds to run");
            LOGGER.info(" AccountServiceImpl Update Account End");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteAccount(AccountRequest accountRequest, StreamObserver<AccountResponse> responseObserver) {

        try {
            LOGGER.info("AccountServiceImpl delete Account Started");

            long startDTime = System.currentTimeMillis();
            long dDuration = System.currentTimeMillis() - startDTime;

            LOGGER.info(" AccountServiceImpl -  AccountRequest - delete Account took " + dDuration + " milliseconds to run");

            AccountResponse accountResponse = AccountResponse.newBuilder()
                .setResponse(1)
                .build();

            responseObserver.onNext(accountResponse);
            responseObserver.onCompleted();

            long duration = System.currentTimeMillis() - startDTime;

            LOGGER.info(" AccountServiceImpl - delete Account - took " + duration + " milliseconds to run");
            LOGGER.info(" AccountServiceImpl delete Account End");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}

