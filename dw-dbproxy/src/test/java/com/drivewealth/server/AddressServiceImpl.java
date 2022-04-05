package com.drivewealth.server;


import com.drivewealth.cr.grpc.server.AddressRequest;
import com.drivewealth.cr.grpc.server.AddressResponse;
import com.drivewealth.cr.grpc.server.AddressServiceGrpc;
import com.drivewealth.dbproxy.util.ProtoUtil;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.drivewealth.utils.TestUtils.readFileFromResourcesFolder;


public class AddressServiceImpl extends AddressServiceGrpc.AddressServiceImplBase {


    private static final Logger LOGGER = LoggerFactory.getLogger(AddressServiceImpl.class);


    public void saveAddress(AddressRequest addressRequest, StreamObserver<AddressResponse> responseObserver) {

        try {
            LOGGER.info(" AddressServiceImpl save Address Started");

            long startDTime = System.currentTimeMillis();
            long dDuration = System.currentTimeMillis() - startDTime;

            LOGGER.info(" AddressServiceImpl -  AddressRequest - save Address took " + dDuration + " milliseconds to run");

            AddressResponse addressResponse = AddressResponse.newBuilder()
                .setResponse(1)
                .build();

            responseObserver.onNext(addressResponse);
            responseObserver.onCompleted();

            long duration = System.currentTimeMillis() - startDTime;

            LOGGER.info(" AddressServiceImpl - save Address - took " + duration + " milliseconds to run");
            LOGGER.info(" AddressServiceImpl save Address End");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getAddress(AddressRequest addressRequest, StreamObserver<AddressRequest> responseObserver) {

        try {
            LOGGER.info(" AddressServiceImpl Get Address Started");
            LOGGER.info(" AddressServiceImpl Get Address addressRequest\t" + addressRequest.getUserID());
            LOGGER.info(" AddressServiceImpl Get Address addressRequest\t" + addressRequest.getAddressID());

            long startDTime = System.currentTimeMillis();

            long dDuration = System.currentTimeMillis() - startDTime;

            LOGGER.info("AddressServiceImpl -  getAddress - Get Address took " + dDuration + " milliseconds to run");

            String userResponseJSON = readFileFromResourcesFolder("responses/address.json");
            AddressRequest protoAddressRequest = ProtoUtil.toProto(userResponseJSON, AddressRequest.getDefaultInstance());

            responseObserver.onNext(protoAddressRequest);
            responseObserver.onCompleted();

            long duration = System.currentTimeMillis() - startDTime;

            LOGGER.info(" AddressServiceImpl - Get User - took " + duration + " milliseconds to run");
            LOGGER.info("AddressServiceImpl Get User End");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void deleteAddress(AddressRequest addressRequest, StreamObserver<AddressResponse> responseObserver) {

        try {
            LOGGER.info("AddressServiceImpl deleteAddress Started");

            long startDTime = System.currentTimeMillis();
            long dDuration = System.currentTimeMillis() - startDTime;

            LOGGER.info("AddressServiceImpl - AddressRequest - deleteAddress took " + dDuration + " milliseconds to run");

            AddressResponse addressResponse = AddressResponse.newBuilder()
                .setResponse(1)
                .build();

            responseObserver.onNext(addressResponse);
            responseObserver.onCompleted();

            long duration = System.currentTimeMillis() - startDTime;

            LOGGER.info("AddressServiceImpl - deleteAddress - took " + duration + " milliseconds to run");
            LOGGER.info("AddressServiceImpl deleteAddress End");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void updateAddress(AddressRequest addressRequest, StreamObserver<AddressResponse> responseObserver) {

        try {
            LOGGER.info("AddressServiceImpl updateAddress Started");

            long startDTime = System.currentTimeMillis();
            long dDuration = System.currentTimeMillis() - startDTime;

            LOGGER.info("AddressServiceImpl - AddressRequest - updateAddress took " + dDuration + " milliseconds to run");

            AddressResponse addressResponse = AddressResponse.newBuilder()
                .setResponse(1)
                .build();

            responseObserver.onNext(addressResponse);
            responseObserver.onCompleted();

            long duration = System.currentTimeMillis() - startDTime;

            LOGGER.info("AddressServiceImpl - updateAddress - took " + duration + " milliseconds to run");
            LOGGER.info("AddressServiceImpl updateAddress End");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

