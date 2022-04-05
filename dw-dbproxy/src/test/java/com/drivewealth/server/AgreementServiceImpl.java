package com.drivewealth.server;

import com.drivewealth.cr.grpc.server.AgreementRequest;
import com.drivewealth.cr.grpc.server.AgreementResponse;
import com.drivewealth.cr.grpc.server.AgreementServiceGrpc;
import com.drivewealth.dbproxy.util.ProtoUtil;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.drivewealth.utils.TestUtils.readFileFromResourcesFolder;


public class AgreementServiceImpl extends AgreementServiceGrpc.AgreementServiceImplBase {


    private static final Logger LOGGER = LoggerFactory.getLogger(AgreementServiceImpl.class);


    public void saveAgreement(AgreementRequest agreementRequest, StreamObserver<AgreementResponse> responseObserver) {

        try {
            LOGGER.info(" AgreementServiceImpl save Agreement Started");

            long startDTime = System.currentTimeMillis();
            long dDuration = System.currentTimeMillis() - startDTime;

            LOGGER.info(" AgreementServiceImpl -  AgreementRequest - save Agreement took " + dDuration + " milliseconds to run");

            AgreementResponse agreementResponse = AgreementResponse.newBuilder()
                .setResponse(1)
                .build();

            responseObserver.onNext(agreementResponse);
            responseObserver.onCompleted();

            long duration = System.currentTimeMillis() - startDTime;

            LOGGER.info(" AgreementServiceImpl - save Agreement - took " + duration + " milliseconds to run");
            LOGGER.info(" AgreementServiceImpl save Agreement End");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getAgreement(AgreementRequest agreementRequest, StreamObserver<AgreementRequest> responseObserver) {

        try {

            LOGGER.info(" AgreementServiceImpl Get Agreement Started");
            LOGGER.info(" AgreementServiceImpl Get Agreement agreementRequest\t" + agreementRequest.getUserID());
            LOGGER.info(" AgreementServiceImpl Get Agreement agreementRequest\t" + agreementRequest.getAgreementID());

            long startDTime = System.currentTimeMillis();
            long dDuration = System.currentTimeMillis() - startDTime;

            LOGGER.info("AgreementServiceImpl -  getAgreement - Get Agreement took " + dDuration + " milliseconds to run");

            String userResponseJSON = readFileFromResourcesFolder("responses/agreement.json");
            AgreementRequest protoAgreementRequest = ProtoUtil.toProto(userResponseJSON, AgreementRequest.getDefaultInstance());

            responseObserver.onNext(protoAgreementRequest);
            responseObserver.onCompleted();

            long duration = System.currentTimeMillis() - startDTime;

            LOGGER.info(" AgreementServiceImpl - Get User - took " + duration + " milliseconds to run");
            LOGGER.info("AgreementServiceImpl Get User End");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void deleteAgreement(AgreementRequest agreementRequest, StreamObserver<AgreementResponse> responseObserver) {

        try {
            LOGGER.info("AgreementServiceImpl deleteAgreement Started");

            long startDTime = System.currentTimeMillis();
            long dDuration = System.currentTimeMillis() - startDTime;

            LOGGER.info("AgreementServiceImpl - AgreementRequest - deleteAgreement took " + dDuration + " milliseconds to run");

            AgreementResponse agreementResponse = AgreementResponse.newBuilder()
                .setResponse(1)
                .build();

            responseObserver.onNext(agreementResponse);
            responseObserver.onCompleted();

            long duration = System.currentTimeMillis() - startDTime;

            LOGGER.info("AgreementServiceImpl - deleteAgreement - took " + duration + " milliseconds to run");
            LOGGER.info("AgreementServiceImpl deleteAgreement End");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void updateAgreement(AgreementRequest agreementRequest, StreamObserver<AgreementResponse> responseObserver) {

        try {
            LOGGER.info("AgreementServiceImpl updateAgreement Started");

            long startDTime = System.currentTimeMillis();
            long dDuration = System.currentTimeMillis() - startDTime;

            LOGGER.info("AgreementServiceImpl - AgreementRequest - updateAgreement took " + dDuration + " milliseconds to run");

            AgreementResponse agreementResponse = AgreementResponse.newBuilder()
                .setResponse(1)
                .build();

            responseObserver.onNext(agreementResponse);
            responseObserver.onCompleted();

            long duration = System.currentTimeMillis() - startDTime;

            LOGGER.info("AgreementServiceImpl - updateAgreement - took " + duration + " milliseconds to run");
            LOGGER.info("AgreementServiceImpl updateAgreement End");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}

