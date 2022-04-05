package com.drivewealth.cr.grpc.server;

import io.grpc.ManagedChannel;
import io.grpc.Metadata;
import io.grpc.stub.MetadataUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AgreementProtoService {


    private static final Logger LOGGER = LoggerFactory.getLogger(AgreementProtoService.class);

    private AgreementServiceGrpc.AgreementServiceBlockingStub stub;


    public AgreementProtoService(ManagedChannel managedChannel, String token) {

        this.stub = AgreementServiceGrpc.newBlockingStub(managedChannel);

        Metadata header = new Metadata();
        Metadata.Key<String> key = Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER);
        header.put(key, token);

        this.stub = this.stub.withInterceptors(MetadataUtils.newAttachHeadersInterceptor(header));
        LOGGER.info("Added the Authorization token to the gRPC client header");
    }


    public  AgreementResponse saveAgreement(AgreementRequest agreementRequest) {

        final AgreementResponse agreementResponse = stub.saveAgreement(agreementRequest);
        return agreementResponse;
    }

    public  AgreementResponse deleteAgreement(AgreementRequest agreementRequest) {

        final AgreementResponse agreementResponse = stub.deleteAgreement(agreementRequest);
        return agreementResponse;
    }

    public  AgreementResponse updateAgreement(AgreementRequest agreementRequest) {

        final AgreementResponse agreementResponse = stub.updateAgreement(agreementRequest);
        return agreementResponse;
    }

    public  AgreementRequest getAgreement(AgreementRequest agreementRequest) {

        final AgreementRequest agreement = stub.getAgreement(agreementRequest);
        return agreement;
    }

}
