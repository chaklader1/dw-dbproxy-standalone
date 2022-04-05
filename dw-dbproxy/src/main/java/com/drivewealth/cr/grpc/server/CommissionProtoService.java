package com.drivewealth.cr.grpc.server;


import io.grpc.ManagedChannel;
import io.grpc.Metadata;
import io.grpc.stub.MetadataUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class CommissionProtoService {


    private static final Logger LOGGER = LoggerFactory.getLogger(AgreementProtoService.class);


    private  CommissionServiceGrpc.CommissionServiceBlockingStub stub;


    public CommissionProtoService(ManagedChannel managedChannel, String token) {

        this.stub = CommissionServiceGrpc.newBlockingStub(managedChannel);

        Metadata header = new Metadata();
        Metadata.Key<String> key = Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER);
        header.put(key, token);

        this.stub = this.stub.withInterceptors(MetadataUtils.newAttachHeadersInterceptor(header));
        LOGGER.info("Added the Authorization token to the gRPC client header");
    }

    public CommissionResponse saveCommission(CommissionRequest commissionRequest) {

        final CommissionResponse commissionResponse = stub.saveCommission(commissionRequest);
        return commissionResponse;
    }

    public CommissionResponse deleteCommission(CommissionRequest commissionRequest) {

        final CommissionResponse commissionResponse = stub.deleteCommission(commissionRequest);
        return commissionResponse;
    }

    public  CommissionResponse updateCommission(CommissionRequest commissionRequest) {

        final CommissionResponse commissionResponse = stub.updateCommission(commissionRequest);
        return commissionResponse;
    }

    public  CommissionRequest getCommission(CommissionRequest commissionRequest) {

        final CommissionRequest commission = stub.getCommission(commissionRequest);
        return commission;
    }

}
