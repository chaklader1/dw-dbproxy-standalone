package com.drivewealth.cr.grpc.server;

import com.dw.acatsdomain.grpc.GrpcPocServiceGrpc;
import com.dw.acatsdomain.grpc.GrpcPocServiceOuterClass;
import io.grpc.ManagedChannel;
import io.grpc.Metadata;
import io.grpc.stub.MetadataUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class GrpcProtoService {


    private static final Logger LOGGER = LoggerFactory.getLogger(GrpcProtoService.class);

    private GrpcPocServiceGrpc.GrpcPocServiceBlockingStub stub;



    public GrpcProtoService(ManagedChannel managedChannel, String token) {

        this.stub = GrpcPocServiceGrpc.newBlockingStub(managedChannel);

        Metadata header = new Metadata();
        Metadata.Key<String> key = Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER);
        header.put(key, token);

        this.stub = this.stub.withInterceptors(MetadataUtils.newAttachHeadersInterceptor(header));

        LOGGER.info("Added the Authorization token to the gRPC client header");
    }


    public GrpcPocServiceOuterClass.GrpcPocDTO add(GrpcPocServiceOuterClass.GrpcPocDTO grpcPoc) {

        final GrpcPocServiceOuterClass.GrpcPocDTO grpcPocDTORequest = stub.add(grpcPoc);
        return grpcPocDTORequest;
    }
}
