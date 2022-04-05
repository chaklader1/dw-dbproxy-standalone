package com.drivewealth.cr.grpc.server;


import io.grpc.ManagedChannel;
import io.grpc.Metadata;
import io.grpc.stub.MetadataUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AccountProtoService {


    private static final Logger LOGGER = LoggerFactory.getLogger(AccountProtoService.class);

    private  AccountServiceGrpc.AccountServiceBlockingStub stub;


    public AccountProtoService(ManagedChannel managedChannel, String token) {

        this.stub = AccountServiceGrpc.newBlockingStub(managedChannel);

        Metadata header = new Metadata();
        Metadata.Key<String> key = Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER);
        header.put(key, token);

        this.stub = this.stub.withInterceptors(MetadataUtils.newAttachHeadersInterceptor(header));
        LOGGER.info("Added the Authorization token to the gRPC client header");
    }

    public  AccountResponse saveAccount(AccountRequest accountRequest) {

//        final AccountRequest accountRequest = AccountRequest.newBuilder()
//            .setId(accountRequest.getId())
//            .setAccountID(accountRequest.getAccountID())
//            .setAccountNo(accountRequest.getAccountNo())
//            .setCommissionID(accountRequest.getCommissionID())
//            .setCurrencyID(accountRequest.getCurrencyID())
//            .setAccountType(accountRequest.getAccountType())
//            .setCash(accountRequest.getCash().doubleValue())
//            .setDisableSubscriptions(accountRequest.getDisableSubscriptions())
//            .build();

        final AccountResponse accountResponse = stub.saveAccount(accountRequest);
        return accountResponse;
    }

    public  AccountResponse deleteAccount(AccountRequest accountRequest) {

        final AccountResponse accountResponse = stub.deleteAccount(accountRequest);
        return accountResponse;
    }

    public  AccountResponse updateAccount(AccountRequest accountRequest) {

        final AccountResponse accountResponse = stub.updateAccount(accountRequest);
        return accountResponse;
    }

    public  AccountRequest getAccount(AccountRequest accountRequest) {

        final AccountRequest request = stub.getAccount(accountRequest);
        return request;
    }
}
