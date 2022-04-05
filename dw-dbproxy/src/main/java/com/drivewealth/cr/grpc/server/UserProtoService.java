package com.drivewealth.cr.grpc.server;


import io.grpc.ManagedChannel;
import io.grpc.Metadata;
import io.grpc.stub.MetadataUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
public class UserProtoService {


    private static final Logger LOGGER = LoggerFactory.getLogger(UserProtoService.class);

    private  UserServiceGrpc.UserServiceBlockingStub stub;



    public UserProtoService(ManagedChannel managedChannel, String token) {

        this.stub = UserServiceGrpc.newBlockingStub(managedChannel);

        Metadata header = new Metadata();
        Metadata.Key<String> key = Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER);
        header.put(key, token);

        this.stub = this.stub.withInterceptors(MetadataUtils.newAttachHeadersInterceptor(header));
        LOGGER.info("Added the Authorization token to the gRPC client header");
    }

    public UserResponse saveUser(User user) {

        UserRequest userRequest = UserRequest.newBuilder()

            .setUserID(user.getUserID())
            .setUserType(user.getUserType())
            .setDisplayName(user.getDisplayName())
            .setAvatarUrl(user.getAvatarUrl())
            .setDob(user.getDob())
            .setEmailAddress1(user.getEmailAddress1())
            .setEmailAddress2(user.getEmailAddress2())
            .setFirstName(user.getFirstName())
            .setGender(user.getGender())
            .setIdNo(user.getIdNo())
            .setIdType(user.getIdType())
            .setJointFirstName(user.getJointFirstName())
            .setJointGender(user.getJointGender())
            .setJointLastName(user.getJointLastName())
            .setJointMaritalStatus(user.getJointMaritalStatus())
            .setJointZipPostalCode(user.getJointZipPostalCode())

            .setCommissionID(user.getCommissionID())
            .setCommissionGroupID(user.getCommissionGroupID())
            .setCommissionRate(user.getCommissionRate())

            .setAgreementID(user.getAgreementID())
            .setAckCustomerAgreement(user.getAckCustomerAgreement())
            .setAckDataSharing(user.getAckDataSharing())
            .setAckJointSignedBy(user.getAckJointSignedBy())
            .setAckJointSignedWhen(user.getAckJointSignedWhen())
            .setAckMarginAgreementWhen(user.getAckMarginAgreementWhen())
            .setAckSignedWhen(user.getAckSignedWhen())

            .setAddressID(user.getAddressID())
            .setAddressLine1(user.getAddressLine1())
            .setAddressLine2(user.getAddressLine2())
            .setCity(user.getCity())
            .setCountryID(user.getCountryID())
            .setCreatedWhen(user.getCreatedWhen())
            .setEmployerCity(user.getEmployerCity())
            .setEmployerCompany(user.getEmployerCompany())
            .setEmployerCountryID(user.getEmployerCountryID())
            .setJointAddressLine1(user.getJointAddressLine1())
            .setJointAddressLine2(user.getJointAddressLine2())
            .setJointCitizenship(user.getJointCitizenship())
            .setJointCity(user.getJointCity())
            .setJointCountryID(user.getJointCountryID())
            .setZipPostalCode(user.getZipPostalCode())

            .build();

        final UserResponse userResponse = stub.saveUser(userRequest);
        return userResponse;
    }

    public  UserResponse deleteUser(UserRequest userRequest) {

        final UserResponse userResponse = stub.deleteUser(userRequest);
        return userResponse;
    }

    public   UserRequest getUser(UserRequest userRequest) {

        final UserRequest request = stub.getUser(userRequest);
        return request;
    }


    public  UserResponse updateUser(UserRequest userRequest) {

        final UserResponse userResponse = stub.updateUser(userRequest);
        return userResponse;
    }

}

