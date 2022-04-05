package com.drivewealth.server;


import com.drivewealth.cr.grpc.server.CommissionRequest;
import com.drivewealth.cr.grpc.server.CommissionResponse;
import com.drivewealth.cr.grpc.server.CommissionServiceGrpc;
import com.drivewealth.dbproxy.util.ProtoUtil;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.drivewealth.utils.TestUtils.readFileFromResourcesFolder;



public class CommissionServiceImpl extends CommissionServiceGrpc.CommissionServiceImplBase {


    private static final Logger LOGGER = LoggerFactory.getLogger(CommissionServiceImpl.class);


    public void saveCommission(CommissionRequest commissionRequest, StreamObserver<CommissionResponse> responseObserver) {

        try {
            LOGGER.info("CommissionServiceImpl saveCommission Started");

            long startDTime = System.currentTimeMillis();
            long dDuration = System.currentTimeMillis() - startDTime;

            LOGGER.info("CommissionServiceImpl - CommissionRequest - saveCommission took " + dDuration + " milliseconds to run");

            CommissionResponse commissionResponse = CommissionResponse.newBuilder()
                .setResponse(1)
                .build();

            responseObserver.onNext(commissionResponse);
            responseObserver.onCompleted();

            long duration = System.currentTimeMillis() - startDTime;

            LOGGER.info("CommissionServiceImpl - saveCommission - took " + duration + " milliseconds to run");
            LOGGER.info("CommissionServiceImpl saveCommission End");

        } catch (Exception e) {

            e.printStackTrace();
            System.out.printf("Save ERROR: { cause => %s, message => %s }\n", e.getCause(), e.getMessage());
        }

    }

    public void getCommission(CommissionRequest commissionRequest, StreamObserver<CommissionRequest> responseObserver) {

        try {
            LOGGER.info(" CommissionServiceImpl Get Commission Started");
            LOGGER.info(" CommissionServiceImpl Get Commission commissionRequest\t" + commissionRequest.getUserID());
            LOGGER.info(" CommissionServiceImpl Get Commission commissionRequest\t" + commissionRequest.getCommissionID());

            long startDTime = System.currentTimeMillis();

            long dDuration = System.currentTimeMillis() - startDTime;

            LOGGER.info(" UserServiceImpl -  getCommission - Get User took " + dDuration + " milliseconds to run");

            String commissionResponseJSON = readFileFromResourcesFolder("responses/commission.json");
            CommissionRequest protoCommissionRequest = ProtoUtil.toProto(commissionResponseJSON, CommissionRequest.getDefaultInstance());

            responseObserver.onNext(protoCommissionRequest);
            responseObserver.onCompleted();

            long duration = System.currentTimeMillis() - startDTime;

            LOGGER.info(" UserServiceImpl - Get User - took " + duration + " milliseconds to run");
            LOGGER.info(" UserServiceImpl Get User End");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void deleteCommission(CommissionRequest commissionRequest, StreamObserver<CommissionResponse> responseObserver) {

        try {
            LOGGER.info("CommissionServiceImpl deleteCommission Started");

            long startDTime = System.currentTimeMillis();
            long dDuration = System.currentTimeMillis() - startDTime;

            LOGGER.info("CommissionServiceImpl - CommissionRequest - deleteCommission took " + dDuration + " milliseconds to run");

            CommissionResponse commissionResponse = CommissionResponse.newBuilder()
                .setResponse(1)
                .build();

            responseObserver.onNext(commissionResponse);
            responseObserver.onCompleted();

            long duration = System.currentTimeMillis() - startDTime;

            LOGGER.info("CommissionServiceImpl - deleteCommission - took " + duration + " milliseconds to run");
            LOGGER.info("CommissionServiceImpl deleteCommission End");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void updateCommission(CommissionRequest commissionRequest, StreamObserver<CommissionResponse> responseObserver) {

        try {
            LOGGER.info("CommissionServiceImpl updateCommission Started");

            long startDTime = System.currentTimeMillis();
            long dDuration = System.currentTimeMillis() - startDTime;

            LOGGER.info("CommissionServiceImpl - CommissionRequest - updateCommission took " + dDuration + " milliseconds to run");

            CommissionResponse commissionResponse = CommissionResponse.newBuilder()
                .setResponse(1)
                .build();

            responseObserver.onNext(commissionResponse);
            responseObserver.onCompleted();

            long duration = System.currentTimeMillis() - startDTime;

            LOGGER.info("CommissionServiceImpl - updateCommission - took " + duration + " milliseconds to run");
            LOGGER.info("CommissionServiceImpl updateCommission End");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


//    public void findAll(StreamObserver<List<CommissionResponses>> responseObserver) {
//
//        try {
//            LOGGER.info(" CommissionServiceImpl Fina Commission Started");
//
//            long startDTime = System.currentTimeMillis();
//            List<CommissionResponses> lCommissionResponses = commissionRepository.findAll();
//            long dDuration = System.currentTimeMillis() - startDTime;
//
//            LOGGER.info(" CommissionServiceImpl -  Find Commission took " + dDuration + " milliseconds to run");
//
//            responseObserver.onNext(lCommissionResponses);
//            responseObserver.onCompleted();
//
//            long duration = System.currentTimeMillis() - startDTime;
//
//            LOGGER.info(" CommissionServiceImpl - Find Commission - took " + duration + " milliseconds to run");
//            LOGGER.info(" CommissionServiceImpl Find Commission End");
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
