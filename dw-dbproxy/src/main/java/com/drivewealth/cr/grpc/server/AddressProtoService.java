package com.drivewealth.cr.grpc.server;


import io.grpc.ManagedChannel;
import io.grpc.Metadata;
import io.grpc.stub.MetadataUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AddressProtoService {


    private static final Logger LOGGER = LoggerFactory.getLogger(AddressProtoService.class);

    private  AddressServiceGrpc.AddressServiceBlockingStub stub;


    public AddressProtoService(ManagedChannel managedChannel, String token) {

        this.stub = AddressServiceGrpc.newBlockingStub(managedChannel);

        Metadata header = new Metadata();
        Metadata.Key<String> key = Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER);
        header.put(key, token);

        this.stub = this.stub.withInterceptors(MetadataUtils.newAttachHeadersInterceptor(header));
        LOGGER.info("Added the Authorization token to the gRPC client header");
    }


    public AddressResponse saveAddress(AddressRequest addressRequest) throws InterruptedException {

        final AddressResponse addressResponse = stub.saveAddress(addressRequest);
        return addressResponse;
    }


    public AddressResponse deleteAddress(AddressRequest addressRequest) throws InterruptedException {

        final AddressResponse addressResponse = stub.deleteAddress(addressRequest);
        return addressResponse;
    }

    public  AddressResponse updateAddress(AddressRequest addressRequest) throws InterruptedException {

        final AddressResponse addressResponse = stub.updateAddress(addressRequest);
        return addressResponse;
    }

    public  AddressRequest getAddress(AddressRequest addressRequest) throws InterruptedException {

        final AddressRequest request = stub.getAddress(addressRequest);
        return request;
    }

}

