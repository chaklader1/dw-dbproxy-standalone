//package com.drivewealth.service;
//
//
//import com.amazonaws.services.dynamodbv2.model.*;
//import com.drivewealth.cr.grpc.server.*;
//import com.drivewealth.dbproxy.config.DynamoDBConfiguration;
//import com.drivewealth.dbproxy.entity.Account;
//import com.drivewealth.dbproxy.entity.User;
//import com.drivewealth.dbproxy.mapper.GRPCMapper;
//import com.drivewealth.dbproxy.mapper.RequestMapper;
//import com.drivewealth.dbproxy.mapper.ServiceMapper;
//import com.drivewealth.dbproxy.service.PersistenceManager;
//import com.drivewealth.dbproxy.util.DynamodbRequestBuilder;
//
//
//import com.drivewealth.dbproxy.util.ProtoUtil;
//import com.drivewealth.repository.AccountRepository;
//import com.drivewealth.repository.UserRepository;
//import com.drivewealth.server.*;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.exc.MismatchedInputException;
//import com.google.protobuf.*;
//import com.google.protobuf.util.JsonFormat;
//import io.grpc.ManagedChannel;
//import io.grpc.Server;
//import io.grpc.inprocess.InProcessChannelBuilder;
//import io.grpc.inprocess.InProcessServerBuilder;
//import io.grpc.stub.StreamObserver;
//import org.apache.commons.lang3.StringUtils;
//import org.json.JSONObject;
//import org.junit.jupiter.api.*;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.*;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.annotation.DirtiesContext;
//import org.springframework.test.context.web.WebAppConfiguration;
//
//import java.io.*;
//import java.lang.reflect.InvocationTargetException;
//import java.net.URISyntaxException;
//import java.util.List;
//import java.util.UUID;
//import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.ExecutionException;
//import java.util.concurrent.TimeUnit;
//import java.util.concurrent.TimeoutException;
//import java.util.function.Supplier;
//
//import org.junit.jupiter.api.TestMethodOrder;
//
//import static com.drivewealth.dbproxy.util.Parameters.*;
//import static com.drivewealth.utils.TestUtils.readFileFromResourcesFolder;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.AdditionalAnswers.delegatesTo;
//import static org.mockito.ArgumentMatchers.any;
//
//
//@EnableAutoConfiguration
//@ExtendWith({MockitoExtension.class})
//@WebAppConfiguration
//@SpringBootTest(
//    classes = {DynamoDBConfiguration.class, DynamodbRequestBuilder.class, PersistenceManager.class},
//    properties =
//        {
//            "grpc.server.inProcessName=test",
//            "grpc.server.port=-1",
//            "grpc.client.chatService.address=in-process:test"
//        }
//)
//@AutoConfigureMockMvc
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//@DirtiesContext
//public class CockroachDbPersistenceManagerMockedTest {
//
//
//    public static final Logger LOGGER = LoggerFactory.getLogger(CockroachDbPersistenceManagerMockedTest.class);
//
//    private final String USER_ID = UUID.randomUUID().toString();
//    private final String ACCOUNT_ID = UUID.randomUUID().toString();
//
//
////    @Rule
////    public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private AccountRepository accountRepository;
//
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Autowired
//    private DynamodbRequestBuilder dynamodbRequestBuilder;
//
//    @Autowired
//    private PersistenceManager persistenceManager;
//
//
//
////    @Mock
////    private UserServiceGrpc.UserServiceBlockingStub userServiceBlockingStub;
//
//
//    private final com.drivewealth.server.AddressServiceImpl addressServiceImpl = new AddressServiceImpl();
//    private final com.drivewealth.server.AgreementServiceImpl agreementServiceImpl = new AgreementServiceImpl();
//    private final com.drivewealth.server.CommissionServiceImpl commissionServiceImpl = new CommissionServiceImpl();
//
//    private final UserServiceGrpc.UserServiceImplBase userServiceImplBase = Mockito.mock(
//
//        UserServiceGrpc.UserServiceImplBase.class,
//        delegatesTo(
//            new UserServiceGrpc.UserServiceImplBase() {
//                public void saveUser(UserRequest userRequest, StreamObserver<UserResponse> responseObserver) {
//
//                    try {
//                        LOGGER.info(" UserServiceImpl save User Started");
//
//                        long startDTime = System.currentTimeMillis();
//                        int count = userRepository.insertUser(userRequest);
//                        long dDuration = System.currentTimeMillis() - startDTime;
//
//                        LOGGER.info(" UserServiceImpl -  UserRequest - save User took " + dDuration + " milliseconds to run");
//
//                        // check if the count =1
//                        UserResponse userResponse = UserResponse.newBuilder()
//                            .setResponse(count)
//                            .build();
//
//                        responseObserver.onNext(userResponse);
//                        responseObserver.onCompleted();
//
//                        long duration = System.currentTimeMillis() - startDTime;
//
//                        LOGGER.info(" UserServiceImpl - save User - took " + duration + " milliseconds to run");
//                        LOGGER.info(" UserServiceImpl save User End");
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//
//
//                public void getUser(UserRequest userRequest, StreamObserver<UserRequest> responseObserver) {
//
//                    try {
//
//                        final String userID = userRequest.getUserID();
//
//                        LOGGER.info(" UserServiceImpl Get User Started");
//                        LOGGER.info(" UserServiceImpl Get User userRequest\t" + userID);
//
//                        long startDTime = System.currentTimeMillis();
//
//                        User responseUser = userRepository.selectUser(userID);
//                        long dDuration = System.currentTimeMillis() - startDTime;
//
//                        LOGGER.info(" UserServiceImpl -  UserRequest - Get User took " + dDuration + " milliseconds to run");
//
//                        String userResponseJSON = objectMapper.writeValueAsString(responseUser);
//                        UserRequest protoUserRequest = ProtoUtil.toProto(userResponseJSON, UserRequest.getDefaultInstance());
//
//                        responseObserver.onNext(protoUserRequest);
//                        responseObserver.onCompleted();
//
//                        long duration = System.currentTimeMillis() - startDTime;
//
//                        LOGGER.info(" UserServiceImpl - Get User - took " + duration + " milliseconds to run");
//                        LOGGER.info(" UserServiceImpl Get User End");
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//
//                }
//
//                public void deleteUser(UserRequest userRequest, StreamObserver<UserResponse> responseObserver) {
//
//                    try {
//                        LOGGER.info(" UserServiceImpl Delete User Started");
//
//                        long startDTime = System.currentTimeMillis();
//                        int count = userRepository.deleteUser(userRequest.getUserID());
//                        long dDuration = System.currentTimeMillis() - startDTime;
//
//                        LOGGER.info(" UserServiceImpl -  UserRequest - Delete User took " + dDuration + " milliseconds to run");
//
//                        UserResponse userResponse = UserResponse.newBuilder()
//                            .setResponse(count)
//                            .build();
//
//                        responseObserver.onNext(userResponse);
//                        responseObserver.onCompleted();
//
//                        long duration = System.currentTimeMillis() - startDTime;
//
//                        LOGGER.info(" UserServiceImpl - Delete User - took " + duration + " milliseconds to run");
//                        LOGGER.info(" UserServiceImpl Delete User End");
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//
//                }
//
//
//                public void updateUser(UserRequest userRequest, StreamObserver<UserResponse> responseObserver) {
//
//                    try {
//                        LOGGER.info(" UserServiceImpl Update User Started");
//                        LOGGER.info(" UserServiceImpl Update User getUserID" + userRequest.getUserID());
//                        LOGGER.info(" UserServiceImpl Update User getDisplayName" + userRequest.getDisplayName());
//
//                        long startDTime = System.currentTimeMillis();
//                        int count = userRepository.updateUser(userRequest);
//
//                        long dDuration = System.currentTimeMillis() - startDTime;
//
//                        LOGGER.info(" UserServiceImpl -  UserRequest - Update User took " + dDuration + " milliseconds to run");
//                        UserResponse userResponse = UserResponse.newBuilder()
//                            .setResponse(count)
//                            .build();
//
//                        responseObserver.onNext(userResponse);
//                        responseObserver.onCompleted();
//
//                        long duration = System.currentTimeMillis() - startDTime;
//
//                        LOGGER.info(" UserServiceImpl - Update User - took " + duration + " milliseconds to run");
//                        LOGGER.info(" UserServiceImpl Update User End");
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        )
//    );
//
//    private final AccountServiceGrpc.AccountServiceImplBase accountServiceImplBase = Mockito.mock(
//        AccountServiceGrpc.AccountServiceImplBase.class,
//        delegatesTo(
//            new AccountServiceGrpc.AccountServiceImplBase(){
//
//                public void saveAccount(AccountRequest accountRequest, StreamObserver<AccountResponse> responseObserver) {
//
//                    try {
//                        LOGGER.info("AccountServiceImpl save Account Started");
//
//                        long startDTime = System.currentTimeMillis();
//                        int count = accountRepository.insertAccount(accountRequest);
//                        long dDuration = System.currentTimeMillis() - startDTime;
//
//                        LOGGER.info(" AccountServiceImpl -  AccountRequest - save Account took " + dDuration + " milliseconds to run");
//                        AccountResponse accountResponse = AccountResponse.newBuilder()
//                            .setResponse(count)
//                            .build();
//
//                        responseObserver.onNext(accountResponse);
//                        responseObserver.onCompleted();
//
//                        long duration = System.currentTimeMillis() - startDTime;
//
//                        LOGGER.info(" AccountServiceImpl - save Account - took " + duration + " milliseconds to run");
//                        LOGGER.info(" AccountServiceImpl save Account End");
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                public void getAccount(AccountRequest accountRequest, StreamObserver<AccountRequest> responseObserver) {
//
//                    try {
//
//                        final String id = accountRequest.getId();
//
//                        LOGGER.info(" AccountServiceImpl Get Account Started");
//                        LOGGER.info(" AccountServiceImpl Get Account accountRequest\t" + id);
//
//                        long startDTime = System.currentTimeMillis();
//
//                        final Account request = accountRepository.selectAccount(id);
//                        long dDuration = System.currentTimeMillis() - startDTime;
//
//                        LOGGER.info(" AccountServiceImpl -  AccountRequest - Get Account took " + dDuration + " milliseconds to run");
//
//                        String accountResponseJSON = objectMapper.writeValueAsString(request);
//                        AccountRequest protoAccountRequest = ProtoUtil.toProto(accountResponseJSON, AccountRequest.getDefaultInstance());
//
//                        responseObserver.onNext(protoAccountRequest);
//                        responseObserver.onCompleted();
//
//                        long duration = System.currentTimeMillis() - startDTime;
//
//                        LOGGER.info(" UserServiceImpl - Get User - took " + duration + " milliseconds to run");
//                        LOGGER.info(" UserServiceImpl Get User End");
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//
//                }
//
//
//                public void updateAccount(AccountRequest accountRequest, StreamObserver<AccountResponse> responseObserver) {
//
//                    try {
//                        LOGGER.info(" AccountServiceImpl Update Account Started");
//                        LOGGER.info(" AccountServiceImpl Update Account getID" + accountRequest.getId());
//                        LOGGER.info(" AccountServiceImpl Update Account getAccountID" + accountRequest.getAccountID());
//
//                        long startDTime = System.currentTimeMillis();
//                        int count = accountRepository.updateAccount(accountRequest);
//
//                        long dDuration = System.currentTimeMillis() - startDTime;
//
//                        LOGGER.info(" AccountServiceImpl -  AccountRequest - Update Account took " + dDuration + " milliseconds to run");
//
//                        AccountResponse userResponse = AccountResponse.newBuilder()
//                            .setResponse(count)
//                            .build();
//                        responseObserver.onNext(userResponse);
//                        responseObserver.onCompleted();
//
//                        long duration = System.currentTimeMillis() - startDTime;
//
//                        LOGGER.info(" AccountServiceImpl - Update Account - took " + duration + " milliseconds to run");
//                        LOGGER.info(" AccountServiceImpl Update Account End");
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                public void deleteAccount(AccountRequest accountRequest, StreamObserver<AccountResponse> responseObserver) {
//
//                    try {
//                        LOGGER.info("AccountServiceImpl delete Account Started");
//
//                        long startDTime = System.currentTimeMillis();
//                        int count = accountRepository.deleteAccount(accountRequest.getId());
//                        long dDuration = System.currentTimeMillis() - startDTime;
//
//                        LOGGER.info(" AccountServiceImpl -  AccountRequest - delete Account took " + dDuration + " milliseconds to run");
//
//                        AccountResponse accountResponse = AccountResponse.newBuilder()
//                            .setResponse(count)
//                            .build();
//
//                        responseObserver.onNext(accountResponse);
//                        responseObserver.onCompleted();
//
//                        long duration = System.currentTimeMillis() - startDTime;
//
//                        LOGGER.info(" AccountServiceImpl - delete Account - took " + duration + " milliseconds to run");
//                        LOGGER.info(" AccountServiceImpl delete Account End");
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//
//                }
//            }
//        )
//    );
//
//    private final AddressServiceGrpc.AddressServiceImplBase addressServiceImplBase = Mockito.mock(
//        AddressServiceGrpc.AddressServiceImplBase.class,
//        delegatesTo(
//            addressServiceImpl
//        )
//    );
//
//    private final AgreementServiceGrpc.AgreementServiceImplBase agreementServiceImplBase = Mockito.mock(
//        AgreementServiceGrpc.AgreementServiceImplBase.class,
//        delegatesTo(agreementServiceImpl)
//    );
//
//    private final CommissionServiceGrpc.CommissionServiceImplBase commissionServiceImplBase = Mockito.mock(
//        CommissionServiceGrpc.CommissionServiceImplBase.class,
//        delegatesTo(commissionServiceImpl)
//    );
//
//
//    /*
//     * this will create the gRPC managed channel and add all the required services
//     * */
//    public Supplier<ManagedChannel> createManagedChannel() throws IOException {
//        String serverName = InProcessServerBuilder.generateName();
//
//        final Server server = InProcessServerBuilder
//            .forName(serverName).directExecutor()
//            .addService(userServiceImplBase)
//            .addService(addressServiceImplBase)
//            .addService(agreementServiceImplBase)
//            .addService(commissionServiceImplBase)
//            .addService(accountServiceImplBase)
//            .build().start();
//
//        final Supplier<ManagedChannel> sClient = () -> InProcessChannelBuilder.forName(serverName).directExecutor().build();
//
////        grpcCleanup.register(server);
////        grpcCleanup.register(client);
//
//
//        return sClient;
//    }
//
//
//
//
//    public Supplier<ManagedChannel> supplierManagedChannel = null;
//
//
//    @BeforeAll
//    public static void setupClass() throws Exception {
//
//        LOGGER.info("This is running before ALL (@BeforeClass) to complete the setup");
//    }
//
//    @AfterAll
//    public static void tearDownClass() throws Exception {
//
//        LOGGER.info("This is running after ALL (@AfterClass) to tear down everything");
//    }
//
//
//    @BeforeEach
//    public void setup() throws IOException {
//        LOGGER.info("We are making the setup for every test to make it run");
//
//        supplierManagedChannel = createManagedChannel();
//    }
//
//    @AfterEach
//    public void tearDown() throws InterruptedException {
//
//        LOGGER.info("We are teardown the setup after every test so the next test will run smoothly.");
//
//        ManagedChannel managedChannel = supplierManagedChannel.get();
//        ProtoService.shutdownManagedChannel(managedChannel);
//    }
//
//
//
//
//
//
//    // GET USER
//        // 1. Sync
//    @Test
//    public void test_sync_getUserWithUserId_willReturnUser() throws IOException, URISyntaxException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
//
//        ManagedChannel managedChannel = supplierManagedChannel.get();
//
//        String userStr = readFileFromResourcesFolder("responses/user.json");
//        com.drivewealth.dbproxy.entity.User user = objectMapper.readValue(userStr, User.class);
//
//        Mockito.when(userRepository.selectUser(any(String.class))).thenReturn(user);
//
//
////        ArgumentCaptor<UserRequest> requestCaptor = ArgumentCaptor.forClass(UserRequest.class);
//        ArgumentCaptor<String> captureUserID = ArgumentCaptor.forClass(String.class);
//
//        final GetItemRequest getItemRequest = createGetRequest(USER_ID, "User");
//
//        JSONObject getItemRequestJsonObj = new JSONObject(getItemRequest);
//        String getItemRequestRequestJsonStr = getItemRequestJsonObj.toString();
//
//
//        RequestMapper getRequestMapper = ServiceMapper.getServices(GETITEM, USER_TABLE);
//        List<GRPCMapper> glGRPCMapper = getRequestMapper.getGRPCMapper();
//
//        final Message cockroachDBItemSyncGrpc = persistenceManager.getCockroachDBItemSyncGrpc(glGRPCMapper, getItemRequestRequestJsonStr, managedChannel);
//
//
//        final JSONObject jsonObject = createJsonWithMessage(cockroachDBItemSyncGrpc);
//
//        final String city = jsonObject.getString("city");
//        final String countryID = jsonObject.getString("countryID");
//
//        Assertions.assertEquals("Dhaka", city);
//        Assertions.assertEquals("Bangladesh", countryID);
//
//
//        Assertions.assertNotNull(cockroachDBItemSyncGrpc);
//
//
//        Mockito.verify(userRepository, Mockito.times(1)).selectUser(captureUserID.capture());
//        Assertions.assertEquals(USER_ID, captureUserID.getValue(), "The user id needs to be the same");
//
////        Mockito.verify(userServiceImplBase, Mockito.times(1))
////            .getUser(requestCaptor.capture(), ArgumentMatchers.<StreamObserver<UserRequest>>any());
////        Assertions.assertEquals("Dhaka", requestCaptor.getValue().getCity());
//    }
//
//    @Test
//    public void test_sync_getUserWithEmptyGetItemRequest_willThrowNullPointerException() throws IOException, URISyntaxException {
//        ManagedChannel managedChannel = supplierManagedChannel.get();
//
//        RequestMapper getRequestMapper = ServiceMapper.getServices(GETITEM, USER_TABLE);
//        List<GRPCMapper> glGRPCMapper = getRequestMapper.getGRPCMapper();
//
//        final MismatchedInputException exception = assertThrows(MismatchedInputException.class, () -> {
//
//            persistenceManager.getCockroachDBItemSyncGrpc(glGRPCMapper, "", managedChannel);
//        }, "Sending the empty request body will throw the MismatchedInputException: No content to map due to end-of-input");
//
//        String errorMessage = exception.getMessage();
//        Assertions.assertTrue(StringUtils.containsIgnoreCase(errorMessage, "No content to map due to end-of-input"), "Sending the empty request body will throw the MismatchedInputException: No content to map due to end-of-input");
//    }
//
////    @Test
////    public void test_sync_getUserWithNullGetItemRequest_willThrowNullPointerException() throws IOException, URISyntaxException {
////        ManagedChannel managedChannel = supplierManagedChannel.get();
////
////        RequestMapper getRequestMapper = ServiceMapper.getServices(GETITEM, USER_TABLE);
////        List<GRPCMapper> glGRPCMapper = getRequestMapper.getGRPCMapper();
////
////        Exception exception = assertThrows(RuntimeException.class, () -> {
////
////            persistenceManager.getCockroachDBItemSyncGrpc(glGRPCMapper, null, managedChannel);
////        }, "Sending the null request body will throw the NullPointerException");
////
////        assertNull(exception.getMessage(), "Sending the null request body will throw the NullPointerException");
////    }
//
//    // GET USER
//        // 2. Async
//    @Test
//    public void test_async_getUserWithUserId_willReturnUser() throws IOException, URISyntaxException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, ExecutionException, InterruptedException, TimeoutException {
//
//        ManagedChannel managedChannel = supplierManagedChannel.get();
//
//        String userStr = readFileFromResourcesFolder("responses/user.json");
//        com.drivewealth.dbproxy.entity.User user = objectMapper.readValue(userStr, User.class);
//
//        Mockito.when(userRepository.selectUser(any(String.class))).thenReturn(user);
//
//
////        ArgumentCaptor<UserRequest> requestCaptor = ArgumentCaptor.forClass(UserRequest.class);
//        ArgumentCaptor<String> captureUserID = ArgumentCaptor.forClass(String.class);
//
//        final GetItemRequest getItemRequest = createGetRequest(USER_ID, "User");
//
//        JSONObject getItemRequestJsonObj = new JSONObject(getItemRequest);
//        String getItemRequestRequestJsonStr = getItemRequestJsonObj.toString();
//
//
//        RequestMapper getRequestMapper = ServiceMapper.getServices(GETITEM, USER_TABLE);
//        List<GRPCMapper> glGRPCMapper = getRequestMapper.getGRPCMapper();
//
//        final CompletableFuture<Message> cockroachdbAsyncGrpc = persistenceManager.getCockroachdbAsyncGrpc(glGRPCMapper, getItemRequestRequestJsonStr, managedChannel);
//        final Message cockroachDBItemSyncGrpc = cockroachdbAsyncGrpc.get(3000, TimeUnit.MILLISECONDS);
//
//
//        final JSONObject jsonObject = createJsonWithMessage(cockroachDBItemSyncGrpc);
//
//        final String city = jsonObject.getString("city");
//        final String countryID = jsonObject.getString("countryID");
//
//        Assertions.assertEquals("Dhaka", city);
//        Assertions.assertEquals("Bangladesh", countryID);
//
//
//        Assertions.assertNotNull(cockroachDBItemSyncGrpc);
//
//
//        Mockito.verify(userRepository, Mockito.times(1)).selectUser(captureUserID.capture());
//        Assertions.assertEquals(USER_ID, captureUserID.getValue(), "The user id needs to be the same");
//
////        Mockito.verify(userServiceImplBase, Mockito.times(1))
////            .getUser(requestCaptor.capture(), ArgumentMatchers.<StreamObserver<UserRequest>>any());
////        Assertions.assertEquals("Dhaka", requestCaptor.getValue().getCity());
//    }
//    // GET ACCOUNT
//        // 1. Sync
//    @Test
//    public void test_sync_getAccountWithAccountId_willReturnAccount() throws IOException, URISyntaxException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
//
//        ManagedChannel managedChannel = supplierManagedChannel.get();
//
//        String accountStr = readFileFromResourcesFolder("responses/account.json");
//        com.drivewealth.dbproxy.entity.Account account = objectMapper.readValue(accountStr, Account.class);
//
//        Mockito.when(accountRepository.selectAccount(any(String.class))).thenReturn(account);
//
//
////        ArgumentCaptor<UserRequest> requestCaptor = ArgumentCaptor.forClass(UserRequest.class);
//        ArgumentCaptor<String> captureUserID = ArgumentCaptor.forClass(String.class);
//
//        final GetItemRequest getItemRequest = createGetRequest(ACCOUNT_ID, "Account");
//
//        JSONObject getItemRequestJsonObj = new JSONObject(getItemRequest);
//        String getItemRequestRequestJsonStr = getItemRequestJsonObj.toString();
//
//
//        RequestMapper getRequestMapper = ServiceMapper.getServices(GETITEM, ACCOUNT_TABLE);
//        List<GRPCMapper> glGRPCMapper = getRequestMapper.getGRPCMapper();
//
//        final Message cockroachDBItemSyncGrpc = persistenceManager.getCockroachDBItemSyncGrpc(glGRPCMapper, getItemRequestRequestJsonStr, managedChannel);
//
//
//        final JSONObject jsonObject = createJsonWithMessage(cockroachDBItemSyncGrpc);
//
//        final String accountID = jsonObject.getString("accountID");
//        final String accountNo = jsonObject.getString("accountNo");
//
//        Assertions.assertEquals("ceaa09f1-a08c-41b9-8894-38bea6749640", accountID);
//        Assertions.assertEquals("951bcfe3-d5ec-434f-94e9-8a7e1687d4e7", accountNo);
//
//
//        Assertions.assertNotNull(cockroachDBItemSyncGrpc);
//
//
//        Mockito.verify(accountRepository, Mockito.times(1)).selectAccount(captureUserID.capture());
//        Assertions.assertEquals(ACCOUNT_ID, captureUserID.getValue(), "The account id needs to be the same");
//
////        Mockito.verify(userServiceImplBase, Mockito.times(1))
////            .getUser(requestCaptor.capture(), ArgumentMatchers.<StreamObserver<UserRequest>>any());
////        Assertions.assertEquals("Dhaka", requestCaptor.getValue().getCity());
//    }
//
//    @Test
//    public void test_sync_getAccountWithEmptyGetItemRequest_willThrowNullPointerException() throws IOException, URISyntaxException {
//        ManagedChannel managedChannel = supplierManagedChannel.get();
//
//        RequestMapper getRequestMapper = ServiceMapper.getServices(GETITEM, ACCOUNT_TABLE);
//        List<GRPCMapper> glGRPCMapper = getRequestMapper.getGRPCMapper();
//
//        final MismatchedInputException exception = assertThrows(MismatchedInputException.class, () -> {
//
//            persistenceManager.getCockroachDBItemSyncGrpc(glGRPCMapper, "", managedChannel);
//        }, "Sending the empty request body will throw the MismatchedInputException: No content to map due to end-of-input");
//
//        String errorMessage = exception.getMessage();
//        Assertions.assertTrue(StringUtils.containsIgnoreCase(errorMessage, "No content to map due to end-of-input"), "Sending the empty request body will throw the MismatchedInputException: No content to map due to end-of-input");
//    }
//
//    // GET ACCOUNT
//    // 1. Async
//    @Test
//    public void test_async_getAccountWithAccountId_willReturnAccount() throws IOException, URISyntaxException, ExecutionException, InterruptedException, TimeoutException {
//
//        ManagedChannel managedChannel = supplierManagedChannel.get();
//
//        String accountStr = readFileFromResourcesFolder("responses/account.json");
//        com.drivewealth.dbproxy.entity.Account account = objectMapper.readValue(accountStr, Account.class);
//
//        Mockito.when(accountRepository.selectAccount(any(String.class))).thenReturn(account);
//
//
////        ArgumentCaptor<UserRequest> requestCaptor = ArgumentCaptor.forClass(UserRequest.class);
//        ArgumentCaptor<String> captureUserID = ArgumentCaptor.forClass(String.class);
//
//        final GetItemRequest getItemRequest = createGetRequest(ACCOUNT_ID, "Account");
//
//        JSONObject getItemRequestJsonObj = new JSONObject(getItemRequest);
//        String getItemRequestRequestJsonStr = getItemRequestJsonObj.toString();
//
//
//        RequestMapper getRequestMapper = ServiceMapper.getServices(GETITEM, ACCOUNT_TABLE);
//        List<GRPCMapper> glGRPCMapper = getRequestMapper.getGRPCMapper();
//
//        final CompletableFuture<Message> cockroachdbAsyncGrpc = persistenceManager.getCockroachdbAsyncGrpc(glGRPCMapper, getItemRequestRequestJsonStr, managedChannel);
//        final Message cockroachDBItemSyncGrpc = cockroachdbAsyncGrpc.get(3000, TimeUnit.MILLISECONDS);
//
//
//        final JSONObject jsonObject = createJsonWithMessage(cockroachDBItemSyncGrpc);
//
//        final String accountID = jsonObject.getString("accountID");
//        final String accountNo = jsonObject.getString("accountNo");
//
//        Assertions.assertEquals("ceaa09f1-a08c-41b9-8894-38bea6749640", accountID);
//        Assertions.assertEquals("951bcfe3-d5ec-434f-94e9-8a7e1687d4e7", accountNo);
//
//
//        Assertions.assertNotNull(cockroachDBItemSyncGrpc);
//
//
//        Mockito.verify(accountRepository, Mockito.times(1)).selectAccount(captureUserID.capture());
//        Assertions.assertEquals(ACCOUNT_ID, captureUserID.getValue(), "The account id needs to be the same");
//
////        Mockito.verify(userServiceImplBase, Mockito.times(1))
////            .getUser(requestCaptor.capture(), ArgumentMatchers.<StreamObserver<UserRequest>>any());
////        Assertions.assertEquals("Dhaka", requestCaptor.getValue().getCity());
//    }
//
//
//
//
//
//
//
//
//    // PUT USER
//        // 1. Sync
//    @Test
//    public void test_sync_putUserWithUserData_willReturnSuccessResponse() throws IOException, URISyntaxException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
//
//        ManagedChannel managedChannel = supplierManagedChannel.get();
//
//        Mockito.when(userRepository.insertUser(Mockito.any(UserRequest.class))).thenReturn(1);
//
//        ArgumentCaptor<UserRequest> requestCaptor = ArgumentCaptor.forClass(UserRequest.class);
//
//        final PutItemRequest putItemRequest = createPutRequestForEntity("User");
//
//        JSONObject putItemRequestJsonObj = new JSONObject(putItemRequest);
//        String putRequestJSON = putItemRequestJsonObj.toString();
//
//
//        RequestMapper requestMapper = ServiceMapper.getServices(PUTITEM, USER_TABLE);
//        List<GRPCMapper> glGRPCMapper = requestMapper.getGRPCMapper();
//
//        final List<Message> messages = persistenceManager.saveCockroachDBSyncGrpc(glGRPCMapper, putRequestJSON, managedChannel);
//
//
//        assertNotNull(messages);
//        final Message saveMessage = messages.get(0);
//
//        final JSONObject jsonObject = createJsonWithMessage(saveMessage);
//        LOGGER.info(jsonObject.toString());
//
//        final Object obj = jsonObject.get("response");
//        final int actual = Integer.parseInt(obj.toString());
//
//        Assertions.assertEquals(1, actual);
//
//        Mockito.verify(userRepository, Mockito.times(1)).insertUser(requestCaptor.capture());
//
//        final UserRequest userRequestCaptured = requestCaptor.getValue();
//
//        Assertions.assertEquals("Dhaka", userRequestCaptured.getCity());
//        Assertions.assertEquals("Bangladesh", userRequestCaptured.getCountryID());
//    }
//
//    @Test
//    public void test_sync_putUserWithEmptyUserData_willReturnNullPointerException() throws IOException, URISyntaxException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
//
//        ManagedChannel managedChannel = supplierManagedChannel.get();
//
//        RequestMapper requestMapper = ServiceMapper.getServices(PUTITEM, USER_TABLE);
//        List<GRPCMapper> glGRPCMapper = requestMapper.getGRPCMapper();
//
//        final MismatchedInputException exception = assertThrows(MismatchedInputException.class, () -> {
//
//            persistenceManager.saveCockroachDBSyncGrpc(glGRPCMapper, "", managedChannel);
//        }, "Sending the empty request body will throw the MismatchedInputException: No content to map due to end-of-input");
//
//        String errorMessage = exception.getMessage();
//        Assertions.assertTrue(StringUtils.containsIgnoreCase(errorMessage, "No content to map due to end-of-input"), "Sending the empty request body will throw the MismatchedInputException: No content to map due to end-of-input");
//    }
//
////    @Test
////    public void test_sync_putUserWithNullUserData_willReturnNullPointerException() throws IOException, URISyntaxException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
////        ManagedChannel managedChannel = supplierManagedChannel.get();
////
////        RequestMapper requestMapper = ServiceMapper.getServices(PUTITEM, USER_TABLE);
////        List<GRPCMapper> glGRPCMapper = requestMapper.getGRPCMapper();
////
////        final Exception exception = assertThrows(RuntimeException.class, () -> {
////
////            persistenceManager.saveCockroachDBSyncGrpc(glGRPCMapper, null, managedChannel);
////            Thread.sleep(1000);
////        }, "Sending the null request body will throw the NullPointerException");
////
//////        assertNull(exception.getMessage(), "Sending the null request body will throw the NullPointerException");
////
////        assertEquals("Cannot invoke \"String.length()\" because \"content\" is null",exception.getMessage(), "Sending the null request body will throw the NullPointerException");
////    }
//
//
//    // PUT USER
//        // 1. Async
//    @Test
//    public void test_async_putUserWithUserData_willReturnSuccessResponse() throws IOException, URISyntaxException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, ExecutionException, InterruptedException, TimeoutException {
//        ManagedChannel managedChannel = supplierManagedChannel.get();
//
//        Mockito.when(userRepository.insertUser(Mockito.any(UserRequest.class))).thenReturn(1);
//
//        ArgumentCaptor<UserRequest> requestCaptor = ArgumentCaptor.forClass(UserRequest.class);
//
//        final PutItemRequest putItemRequest = createPutRequestForEntity("User");
//
//        JSONObject putItemRequestJsonObj = new JSONObject(putItemRequest);
//        String putRequestJSON = putItemRequestJsonObj.toString();
//
//
//        RequestMapper requestMapper = ServiceMapper.getServices(PUTITEM, USER_TABLE);
//        List<GRPCMapper> glGRPCMapper = requestMapper.getGRPCMapper();
//
//        final CompletableFuture<List<Message>> listCompletableFuture = persistenceManager.saveCockroachdbAsyncGrpc(glGRPCMapper, putRequestJSON, managedChannel);
//        final List<Message> messages = listCompletableFuture.get(3000, TimeUnit.MILLISECONDS);
//
//
//        assertNotNull(messages);
//        final Message saveMessage = messages.get(0);
//
//        final JSONObject jsonObject = createJsonWithMessage(saveMessage);
//        LOGGER.info(jsonObject.toString());
//
//        final Object obj = jsonObject.get("response");
//        final int actual = Integer.parseInt(obj.toString());
//
//        Assertions.assertEquals(1, actual);
//
//        Mockito.verify(userRepository, Mockito.times(1)).insertUser(requestCaptor.capture());
//
//        final UserRequest userRequestCaptured = requestCaptor.getValue();
//
//        Assertions.assertEquals("Dhaka", userRequestCaptured.getCity());
//        Assertions.assertEquals("Bangladesh", userRequestCaptured.getCountryID());
//    }
//
//    // PUT ACCOUNT
//        // 1. Sync
//    @Test
//    public void test_sync_putAccountWithAccountData_willReturnSuccessResponse() throws IOException, URISyntaxException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
//
//        ManagedChannel managedChannel = supplierManagedChannel.get();
//
//        Mockito.when(accountRepository.insertAccount(Mockito.any(AccountRequest.class))).thenReturn(1);
//
//        ArgumentCaptor<AccountRequest> requestCaptor = ArgumentCaptor.forClass(AccountRequest.class);
//
//        final PutItemRequest putItemRequest = createPutRequestForEntity("Account");
//
//        JSONObject putItemRequestJsonObj = new JSONObject(putItemRequest);
//        String putRequestJSON = putItemRequestJsonObj.toString();
//
//
//        RequestMapper requestMapper = ServiceMapper.getServices(PUTITEM, ACCOUNT_TABLE);
//        List<GRPCMapper> glGRPCMapper = requestMapper.getGRPCMapper();
//
//        final List<Message> messages = persistenceManager.saveCockroachDBSyncGrpc(glGRPCMapper, putRequestJSON, managedChannel);
//
//
//        assertNotNull(messages);
//        final Message saveMessage = messages.get(0);
//
//        final JSONObject jsonObject = createJsonWithMessage(saveMessage);
//        LOGGER.info(jsonObject.toString());
//
//        final Object obj = jsonObject.get("response");
//        final int actual = Integer.parseInt(obj.toString());
//
//        Assertions.assertEquals(1, actual);
//
//        Mockito.verify(accountRepository, Mockito.times(1)).insertAccount(requestCaptor.capture());
//
//        final AccountRequest accountRequestCaptured = requestCaptor.getValue();
//
//        Assertions.assertEquals("951bcfe3-d5ec-434f-94e9-8a7e1687d4e7", accountRequestCaptured.getAccountNo());
//        Assertions.assertEquals("ceaa09f1-a08c-41b9-8894-38bea6749640", accountRequestCaptured.getAccountID());
//    }
//
//    @Test
//    public void test_sync_putAccountWithEmptyAccountData_willReturnNullPointerException() throws IOException, URISyntaxException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
//
//        ManagedChannel managedChannel = supplierManagedChannel.get();
//
//        RequestMapper requestMapper = ServiceMapper.getServices(PUTITEM, ACCOUNT_TABLE);
//        List<GRPCMapper> glGRPCMapper = requestMapper.getGRPCMapper();
//
//        final MismatchedInputException exception = assertThrows(MismatchedInputException.class, () -> {
//
//            persistenceManager.saveCockroachDBSyncGrpc(glGRPCMapper, "", managedChannel);
//        }, "Sending the empty request body will throw the MismatchedInputException: No content to map due to end-of-input");
//
//        String errorMessage = exception.getMessage();
//        Assertions.assertTrue(StringUtils.containsIgnoreCase(errorMessage, "No content to map due to end-of-input"), "Sending the empty request body will throw the MismatchedInputException: No content to map due to end-of-input");
//    }
//
//    // PUT ACCOUNT
//        // 1. Async
//    @Test
//    public void test_async_putAccountWithAccountData_willReturnSuccessResponse() throws IOException, URISyntaxException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, ExecutionException, InterruptedException, TimeoutException {
//
//        ManagedChannel managedChannel = supplierManagedChannel.get();
//
//        Mockito.when(accountRepository.insertAccount(Mockito.any(AccountRequest.class))).thenReturn(1);
//
//        ArgumentCaptor<AccountRequest> requestCaptor = ArgumentCaptor.forClass(AccountRequest.class);
//
//        final PutItemRequest putItemRequest = createPutRequestForEntity("Account");
//
//        JSONObject putItemRequestJsonObj = new JSONObject(putItemRequest);
//        String putRequestJSON = putItemRequestJsonObj.toString();
//
//
//        RequestMapper requestMapper = ServiceMapper.getServices(PUTITEM, ACCOUNT_TABLE);
//        List<GRPCMapper> glGRPCMapper = requestMapper.getGRPCMapper();
//
//        final CompletableFuture<List<Message>> listCompletableFuture = persistenceManager.saveCockroachdbAsyncGrpc(glGRPCMapper, putRequestJSON, managedChannel);
//        final List<Message> messages = listCompletableFuture.get(3000, TimeUnit.MILLISECONDS);
//
//
//        assertNotNull(messages);
//        final Message saveMessage = messages.get(0);
//
//        final JSONObject jsonObject = createJsonWithMessage(saveMessage);
//        LOGGER.info(jsonObject.toString());
//
//        final Object obj = jsonObject.get("response");
//        final int actual = Integer.parseInt(obj.toString());
//
//        Assertions.assertEquals(1, actual);
//
//        Mockito.verify(accountRepository, Mockito.times(1)).insertAccount(requestCaptor.capture());
//
//        final AccountRequest accountRequestCaptured = requestCaptor.getValue();
//
//
//        Assertions.assertEquals("951bcfe3-d5ec-434f-94e9-8a7e1687d4e7", accountRequestCaptured.getAccountNo());
//        Assertions.assertEquals("ceaa09f1-a08c-41b9-8894-38bea6749640", accountRequestCaptured.getAccountID());
//    }
//
//
//
//
//    // UPDATE USER
//        // 1. Sync
//    @Test
//    public void test_sync_updateUserWithUserData_willReturnSuccessResponse() throws IOException, URISyntaxException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
//
//        ManagedChannel managedChannel = supplierManagedChannel.get();
//
//        Mockito.when(userRepository.updateUser(Mockito.any(UserRequest.class))).thenReturn(1);
//
//        ArgumentCaptor<UserRequest> requestCaptor = ArgumentCaptor.forClass(UserRequest.class);
//
//        final UpdateItemRequest updateItemRequest = createUpdateRequestForEntity(USER_ID, "User");
//
//        JSONObject updateItemRequestJsonObj = new JSONObject(updateItemRequest);
//        String updateRequestJSON = updateItemRequestJsonObj.toString();
//
//
//        RequestMapper requestMapper = ServiceMapper.getServices(UPDATEITEM, USER_TABLE);
//        List<GRPCMapper> glGRPCMapper = requestMapper.getGRPCMapper();
//
//        final List<Message> updateMessages = persistenceManager.updateCockroachDBItemSyncGrpc(glGRPCMapper, updateRequestJSON, managedChannel);
//
//
//        assertNotNull(updateMessages);
//        final Message updateMessage = updateMessages.get(0);
//
//        final JSONObject jsonObject = createJsonWithMessage(updateMessage);
//        LOGGER.info(jsonObject.toString());
//
//        final Object obj = jsonObject.get("response");
//        final int actual = Integer.parseInt(obj.toString());
//
//        Assertions.assertEquals(1, actual);
//
//        Mockito.verify(userRepository, Mockito.times(1)).updateUser(requestCaptor.capture());
//
//        final UserRequest userRequestCaptured = requestCaptor.getValue();
//
//        Assertions.assertEquals("Dhaka", userRequestCaptured.getCity());
//        Assertions.assertEquals("Bangladesh", userRequestCaptured.getCountryID());
//    }
//
//    @Test
//    public void test_sync_updateUserWithEmptyUserData_willReturnNullPointerException() throws IOException, URISyntaxException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
//
//        ManagedChannel managedChannel = supplierManagedChannel.get();
//
//        RequestMapper requestMapper = ServiceMapper.getServices(UPDATEITEM, USER_TABLE);
//        List<GRPCMapper> glGRPCMapper = requestMapper.getGRPCMapper();
//
//        final MismatchedInputException exception = assertThrows(MismatchedInputException.class, () -> {
//
//            persistenceManager.updateCockroachDBItemSyncGrpc(glGRPCMapper, "", managedChannel);
//        }, "Sending the empty request body will throw the MismatchedInputException: No content to map due to end-of-input");
//
//        final String errorMessage = exception.getMessage();
//
//        Assertions.assertTrue(StringUtils.containsIgnoreCase(errorMessage, "No content to map due to end-of-input"), "Sending the empty request body will throw the MismatchedInputException: No content to map due to end-of-input");
//    }
//
////    @Test
////    public void test_sync_updateUserWithNullUserData_willReturnNullPointerException() throws IOException, URISyntaxException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
////        ManagedChannel managedChannel = supplierManagedChannel.get();
////
////        RequestMapper requestMapper = ServiceMapper.getServices(UPDATEITEM, USER_TABLE);
////        List<GRPCMapper> glGRPCMapper = requestMapper.getGRPCMapper();
////
////        final Exception exception = assertThrows(RuntimeException.class, () -> {
////
////            persistenceManager.updateCockroachDBItemSyncGrpc(glGRPCMapper, null, managedChannel);
////        }, "Sending the null request body will throw the NullPointerException");
////
////        Assertions.assertNull(exception.getMessage(), "Sending the null request body will throw the NullPointerException");
////    }
//
//    // UPDATE USER
//        // 2. Async
//    @Test
//    public void test_async_updateUserWithUserData_willReturnSuccessResponse() throws IOException, URISyntaxException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, ExecutionException, InterruptedException {
//
//        ManagedChannel managedChannel = supplierManagedChannel.get();
//
//        Mockito.when(userRepository.updateUser(Mockito.any(UserRequest.class))).thenReturn(1);
//
//        ArgumentCaptor<UserRequest> requestCaptor = ArgumentCaptor.forClass(UserRequest.class);
//
//        final UpdateItemRequest updateItemRequest = createUpdateRequestForEntity(USER_ID, "User");
//
//        JSONObject updateItemRequestJsonObj = new JSONObject(updateItemRequest);
//        String updateRequestJSON = updateItemRequestJsonObj.toString();
//
//
//        RequestMapper requestMapper = ServiceMapper.getServices(UPDATEITEM, USER_TABLE);
//        List<GRPCMapper> glGRPCMapper = requestMapper.getGRPCMapper();
//
//        final CompletableFuture<List<Message>> listCompletableFuture = persistenceManager.updateCockroachdbAsyncGrpc(glGRPCMapper, updateRequestJSON, managedChannel);
//        final List<Message> messages = listCompletableFuture.get();
//
//        assertNotNull(messages);
//        final Message updateMessage = messages.get(0);
//
//        final JSONObject jsonObject = createJsonWithMessage(updateMessage);
//        LOGGER.info(jsonObject.toString());
//
//        final Object obj = jsonObject.get("response");
//        final int actual = Integer.parseInt(obj.toString());
//
//        Assertions.assertEquals(1, actual);
//
//        Mockito.verify(userRepository, Mockito.times(1)).updateUser(requestCaptor.capture());
//
//        final UserRequest userRequestCaptured = requestCaptor.getValue();
//
//        Assertions.assertEquals("Dhaka", userRequestCaptured.getCity());
//        Assertions.assertEquals("Bangladesh", userRequestCaptured.getCountryID());
//    }
//
//    // UPDATE ACCOUNT
//        // 1. Sync
//    @Test
//    public void test_sync_updateAccountWithAccountData_willReturnSuccessResponse() throws IOException, URISyntaxException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
//
//        ManagedChannel managedChannel = supplierManagedChannel.get();
//
//        Mockito.when(accountRepository.updateAccount(Mockito.any(AccountRequest.class))).thenReturn(1);
//
//        ArgumentCaptor<AccountRequest> requestCaptor = ArgumentCaptor.forClass(AccountRequest.class);
//
//        final UpdateItemRequest updateItemRequest = createUpdateRequestForEntity(ACCOUNT_ID, "Account");
//
//        JSONObject updateItemRequestJsonObj = new JSONObject(updateItemRequest);
//        String updateRequestJSON = updateItemRequestJsonObj.toString();
//
//
//        RequestMapper requestMapper = ServiceMapper.getServices(UPDATEITEM, ACCOUNT_TABLE);
//        List<GRPCMapper> glGRPCMapper = requestMapper.getGRPCMapper();
//
//        final List<Message> updateMessages = persistenceManager.updateCockroachDBItemSyncGrpc(glGRPCMapper, updateRequestJSON, managedChannel);
//
//
//        assertNotNull(updateMessages);
//        final Message updateMessage = updateMessages.get(0);
//
//        final JSONObject jsonObject = createJsonWithMessage(updateMessage);
//        LOGGER.info(jsonObject.toString());
//
//        final Object obj = jsonObject.get("response");
//        final int actual = Integer.parseInt(obj.toString());
//
//        Assertions.assertEquals(1, actual);
//
//        Mockito.verify(accountRepository, Mockito.times(1)).updateAccount(requestCaptor.capture());
//
//        final AccountRequest accountRequestCaptured = requestCaptor.getValue();
//
//        Assertions.assertEquals("951bcfe3-d5ec-434f-94e9-8a7e1687d4e7", accountRequestCaptured.getAccountNo());
//        Assertions.assertEquals("ceaa09f1-a08c-41b9-8894-38bea6749640", accountRequestCaptured.getAccountID());
//    }
//
//    @Test
//    public void test_sync_updateAccountWithEmptyAccountData_willReturnNullPointerException() throws IOException, URISyntaxException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
//
//        ManagedChannel managedChannel = supplierManagedChannel.get();
//
//        RequestMapper requestMapper = ServiceMapper.getServices(UPDATEITEM, ACCOUNT_TABLE);
//        List<GRPCMapper> glGRPCMapper = requestMapper.getGRPCMapper();
//
//        final MismatchedInputException exception = assertThrows(MismatchedInputException.class, () -> {
//
//            persistenceManager.updateCockroachDBItemSyncGrpc(glGRPCMapper, "", managedChannel);
//        }, "Sending the empty request body will throw the MismatchedInputException: No content to map due to end-of-input");
//
//        final String errorMessage = exception.getMessage();
//
//        Assertions.assertTrue(StringUtils.containsIgnoreCase(errorMessage, "No content to map due to end-of-input"), "Sending the empty request body will throw the MismatchedInputException: No content to map due to end-of-input");
//    }
//
//    // UPDATE ACCOUNT
//        // 1. Async
//    @Test
//    public void test_async_updateAccountWithAccountData_willReturnSuccessResponse() throws IOException, URISyntaxException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, ExecutionException, InterruptedException, TimeoutException {
//
//        ManagedChannel managedChannel = supplierManagedChannel.get();
//
//        Mockito.when(accountRepository.updateAccount(Mockito.any(AccountRequest.class))).thenReturn(1);
//
//        ArgumentCaptor<AccountRequest> requestCaptor = ArgumentCaptor.forClass(AccountRequest.class);
//
//        final UpdateItemRequest updateItemRequest = createUpdateRequestForEntity(ACCOUNT_ID, "Account");
//
//        JSONObject updateItemRequestJsonObj = new JSONObject(updateItemRequest);
//        String updateRequestJSON = updateItemRequestJsonObj.toString();
//
//
//        RequestMapper requestMapper = ServiceMapper.getServices(UPDATEITEM, ACCOUNT_TABLE);
//        List<GRPCMapper> glGRPCMapper = requestMapper.getGRPCMapper();
//
//        final CompletableFuture<List<Message>> listCompletableFuture = persistenceManager.updateCockroachdbAsyncGrpc(glGRPCMapper, updateRequestJSON, managedChannel);
//        final List<Message> messages = listCompletableFuture.get(3000, TimeUnit.MILLISECONDS);
//
//
//        assertNotNull(messages);
//        final Message updateMessage = messages.get(0);
//
//        final JSONObject jsonObject = createJsonWithMessage(updateMessage);
//        LOGGER.info(jsonObject.toString());
//
//        final Object obj = jsonObject.get("response");
//        final int actual = Integer.parseInt(obj.toString());
//
//        Assertions.assertEquals(1, actual);
//
//        Mockito.verify(accountRepository, Mockito.times(1)).updateAccount(requestCaptor.capture());
//
//        final AccountRequest accountRequestCaptured = requestCaptor.getValue();
//
//        Assertions.assertEquals("951bcfe3-d5ec-434f-94e9-8a7e1687d4e7", accountRequestCaptured.getAccountNo());
//        Assertions.assertEquals("ceaa09f1-a08c-41b9-8894-38bea6749640", accountRequestCaptured.getAccountID());
//    }
//
//
//
//
//
//    // DELETE USER
//        // 1. Sync
//    @Test
//    public void test_sync_deleteUserWithUserID_willReturnSuccessResponse() throws IOException, URISyntaxException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
//
//        ManagedChannel managedChannel = supplierManagedChannel.get();
//
//        Mockito.when(userRepository.deleteUser(Mockito.any(String.class))).thenReturn(1);
//
//        ArgumentCaptor<String> requestCaptor = ArgumentCaptor.forClass(String.class);
//
//        final DeleteItemRequest deleteItemRequest = createDeleteRequestForEntity(USER_ID, "User");
//
//        JSONObject deleteItemRequestJsonObj = new JSONObject(deleteItemRequest);
//        String deleteRequestJSON = deleteItemRequestJsonObj.toString();
//
//
//        RequestMapper requestMapper = ServiceMapper.getServices(DELETEITEM, USER_TABLE);
//        List<GRPCMapper> glGRPCMapper = requestMapper.getGRPCMapper();
//
//        final List<Message> messages = persistenceManager.deleteCockroachDBItemSyncGrpc(glGRPCMapper, deleteRequestJSON, managedChannel);
//
//
//        assertNotNull(messages);
//        final Message deleteMessage = messages.get(0);
//
//        final JSONObject jsonObject = createJsonWithMessage(deleteMessage);
//        LOGGER.info(jsonObject.toString());
//
//        final Object obj = jsonObject.get("response");
//        final int actual = Integer.parseInt(obj.toString());
//
//        Assertions.assertEquals(1, actual);
//
//        Mockito.verify(userRepository, Mockito.times(1)).deleteUser(requestCaptor.capture());
//
//        final String userID = requestCaptor.getValue();
//
//        Assertions.assertEquals(USER_ID, userID);
//    }
//
//
//    @Test
//    public void test_sync_deleteUserWithEmptyUserData_willReturnNullPointerException() throws IOException, URISyntaxException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
//
//        ManagedChannel managedChannel = supplierManagedChannel.get();
//
//        RequestMapper requestMapper = ServiceMapper.getServices(DELETEITEM, USER_TABLE);
//        List<GRPCMapper> glGRPCMapper = requestMapper.getGRPCMapper();
//
//        final MismatchedInputException exception = assertThrows(MismatchedInputException.class, () -> {
//
//            persistenceManager.deleteCockroachDBItemSyncGrpc(glGRPCMapper, "", managedChannel);
//        }, "Sending the empty request body will throw the MismatchedInputException: No content to map due to end-of-input");
//
//        String errorMessage = exception.getMessage();
//        Assertions.assertTrue(StringUtils.containsIgnoreCase(errorMessage, "No content to map due to end-of-input"), "Sending the empty request body will throw the MismatchedInputException: No content to map due to end-of-input");
//    }
//
////    @Test
////    public void test_sync_deleteUserWithNullUserData_willReturnNullPointerException() throws IOException, URISyntaxException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
////        ManagedChannel managedChannel = supplierManagedChannel.get();
////
////        RequestMapper requestMapper = ServiceMapper.getServices(DELETEITEM, USER_TABLE);
////        List<GRPCMapper> glGRPCMapper = requestMapper.getGRPCMapper();
////
////        final Exception exception = assertThrows(RuntimeException.class, () -> {
////
////            persistenceManager.deleteCockroachDBItemSyncGrpc(glGRPCMapper, null, managedChannel);
////        }, "Sending the null request body will throw the NullPointerException");
////
////        assertNull(exception.getMessage(), "Sending the null request body will throw the NullPointerException");
////    }
//
//
//    // DELETE USER
//        // 2. Async
//    @Test
//    public void test_async_deleteUserWithUserID_willReturnSuccessResponse() throws IOException, URISyntaxException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, ExecutionException, InterruptedException {
//
//        ManagedChannel managedChannel = supplierManagedChannel.get();
//
//        Mockito.when(userRepository.deleteUser(Mockito.any(String.class))).thenReturn(1);
//
//        ArgumentCaptor<String> requestCaptor = ArgumentCaptor.forClass(String.class);
//
//        final DeleteItemRequest deleteItemRequest = createDeleteRequestForEntity(USER_ID, "User");
//
//        JSONObject deleteItemRequestJsonObj = new JSONObject(deleteItemRequest);
//        String deleteRequestJSON = deleteItemRequestJsonObj.toString();
//
//
//        RequestMapper requestMapper = ServiceMapper.getServices(DELETEITEM, USER_TABLE);
//        List<GRPCMapper> glGRPCMapper = requestMapper.getGRPCMapper();
//
//        final CompletableFuture<List<Message>> listCompletableFuture = persistenceManager.deleteCockroachdbAsyncGrpc(glGRPCMapper, deleteRequestJSON, managedChannel);
//        final List<Message> messages = listCompletableFuture.get();
//
//        assertNotNull(messages);
//        final Message deleteMessage = messages.get(0);
//
//        final JSONObject jsonObject = createJsonWithMessage(deleteMessage);
//        LOGGER.info(jsonObject.toString());
//
//        final Object obj = jsonObject.get("response");
//        final int actual = Integer.parseInt(obj.toString());
//
//        Assertions.assertEquals(1, actual);
//
//        Mockito.verify(userRepository, Mockito.times(1)).deleteUser(requestCaptor.capture());
//
//        final String userID = requestCaptor.getValue();
//        Assertions.assertEquals(USER_ID, userID);
//    }
//
//    // DELETE ACCOUNT
//        // 1. Sync
//    @Test
//    public void test_sync_deleteAccountWithAccountID_willReturnSuccessResponse() throws IOException, URISyntaxException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
//
//        ManagedChannel managedChannel = supplierManagedChannel.get();
//
//        Mockito.when(accountRepository.deleteAccount(Mockito.any(String.class))).thenReturn(1);
//
//        ArgumentCaptor<String> requestCaptor = ArgumentCaptor.forClass(String.class);
//
//        final DeleteItemRequest deleteItemRequest = createDeleteRequestForEntity(ACCOUNT_ID, "Account");
//
//        JSONObject deleteItemRequestJsonObj = new JSONObject(deleteItemRequest);
//        String deleteRequestJSON = deleteItemRequestJsonObj.toString();
//
//
//        RequestMapper requestMapper = ServiceMapper.getServices(DELETEITEM, ACCOUNT_TABLE);
//        List<GRPCMapper> glGRPCMapper = requestMapper.getGRPCMapper();
//
//        final List<Message> messages = persistenceManager.deleteCockroachDBItemSyncGrpc(glGRPCMapper, deleteRequestJSON, managedChannel);
//
//
//        assertNotNull(messages);
//        final Message deleteMessage = messages.get(0);
//
//        final JSONObject jsonObject = createJsonWithMessage(deleteMessage);
//        LOGGER.info(jsonObject.toString());
//
//        final Object obj = jsonObject.get("response");
//        final int actual = Integer.parseInt(obj.toString());
//
//        Assertions.assertEquals(1, actual);
//
//        Mockito.verify(accountRepository, Mockito.times(1)).deleteAccount(requestCaptor.capture());
//
//        final String accountID = requestCaptor.getValue();
//
//        Assertions.assertEquals(ACCOUNT_ID, accountID);
//    }
//
//    @Test
//    public void test_sync_deleteAccountWithEmptyAccountData_willReturnNullPointerException() throws IOException, URISyntaxException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
//
//        ManagedChannel managedChannel = supplierManagedChannel.get();
//
//        RequestMapper requestMapper = ServiceMapper.getServices(DELETEITEM, ACCOUNT_TABLE);
//        List<GRPCMapper> glGRPCMapper = requestMapper.getGRPCMapper();
//
//        final MismatchedInputException exception = assertThrows(MismatchedInputException.class, () -> {
//
//            persistenceManager.deleteCockroachDBItemSyncGrpc(glGRPCMapper, "", managedChannel);
//        }, "Sending the empty request body will throw the MismatchedInputException: No content to map due to end-of-input");
//
//        String errorMessage = exception.getMessage();
//        Assertions.assertTrue(StringUtils.containsIgnoreCase(errorMessage, "No content to map due to end-of-input"), "Sending the empty request body will throw the MismatchedInputException: No content to map due to end-of-input");
//    }
//
//    // DELETE ACCOUNT
//        // 1. Async
//    @Test
//    public void test_async_deleteAccountWithAccountID_willReturnSuccessResponse() throws IOException, URISyntaxException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, ExecutionException, InterruptedException, TimeoutException {
//
//        ManagedChannel managedChannel = supplierManagedChannel.get();
//
//        Mockito.when(accountRepository.deleteAccount(Mockito.any(String.class))).thenReturn(1);
//
//        ArgumentCaptor<String> requestCaptor = ArgumentCaptor.forClass(String.class);
//
//        final DeleteItemRequest deleteItemRequest = createDeleteRequestForEntity(ACCOUNT_ID, "Account");
//
//        JSONObject deleteItemRequestJsonObj = new JSONObject(deleteItemRequest);
//        String deleteRequestJSON = deleteItemRequestJsonObj.toString();
//
//
//        RequestMapper requestMapper = ServiceMapper.getServices(DELETEITEM, ACCOUNT_TABLE);
//        List<GRPCMapper> glGRPCMapper = requestMapper.getGRPCMapper();
//
//        final CompletableFuture<List<Message>> listCompletableFuture = persistenceManager.deleteCockroachdbAsyncGrpc(glGRPCMapper, deleteRequestJSON, managedChannel);
//        final List<Message> messages = listCompletableFuture.get(3000, TimeUnit.MILLISECONDS);
//
//
//        assertNotNull(messages);
//        final Message deleteMessage = messages.get(0);
//
//        final JSONObject jsonObject = createJsonWithMessage(deleteMessage);
//        LOGGER.info(jsonObject.toString());
//
//        final Object obj = jsonObject.get("response");
//        final int actual = Integer.parseInt(obj.toString());
//
//        Assertions.assertEquals(1, actual);
//
//        Mockito.verify(accountRepository, Mockito.times(1)).deleteAccount(requestCaptor.capture());
//
//        final String accountID = requestCaptor.getValue();
//
//        Assertions.assertEquals(ACCOUNT_ID, accountID);
//    }
//
//
//
//
//
//
//
//
//
//
//    // utils
//    private static JSONObject createJsonWithMessage(Message message) throws InvalidProtocolBufferException {
//
//        final String cockroachResponseJsonStr = JsonFormat.printer().print(message);
//        JSONObject jsonOb = new JSONObject(cockroachResponseJsonStr);
//
//        return jsonOb;
//    }
//
//
//    private GetItemRequest createGetRequest(String entityID, String type) throws UnsupportedEncodingException {
//
//        JSONObject jsonObject = new JSONObject();
//
//        switch (type.toUpperCase()){
//
//            case "USER":
//            {
//                jsonObject.put("userID", entityID);
//                String requestBody = jsonObject.toString();
//
//                GetItemRequest getItemRequest = dynamodbRequestBuilder.createGetItemRequest(USER_TABLE, requestBody);
//
//                return getItemRequest;
//            }
//
//            case "ACCOUNT":
//            {
//                jsonObject.put("id", entityID);
//                String requestBody = jsonObject.toString();
//
//                GetItemRequest getItemRequest = dynamodbRequestBuilder.createGetItemRequest(ACCOUNT_TABLE, requestBody);
//
//                return getItemRequest;
//            }
//
//            default:
//                throw new UnsupportedEncodingException("This type of entity is not supported.");
//        }
//    }
//
//    private DeleteItemRequest createDeleteRequestForEntity(String entityID, String type) throws UnsupportedEncodingException {
//
//        switch (type.toUpperCase()){
//
//            case "USER":
//            {
//                JSONObject jsonObject = new JSONObject();
//                jsonObject.put("userID", entityID);
//                String requestBody = jsonObject.toString();
//
//                final DeleteItemRequest deleteItemRequest = dynamodbRequestBuilder.createDeleteItemRequest(USER_TABLE, requestBody);
//
//                return deleteItemRequest;
//            }
//
//            case "ACCOUNT":
//            {
//                JSONObject jsonObject = new JSONObject();
//                jsonObject.put("id", entityID);
//                String requestBody = jsonObject.toString();
//
//                final DeleteItemRequest deleteItemRequest = dynamodbRequestBuilder.createDeleteItemRequest(ACCOUNT_TABLE, requestBody);
//
//                return deleteItemRequest;
//            }
//
//            default:
//            {
//                throw new UnsupportedEncodingException("This type of entity is not supported.");
//            }
//        }
//    }
//
//
//    private PutItemRequest createPutRequestForEntity(String type) throws IOException {
//
//        switch (type.toUpperCase()) {
//
//            case "USER": {
//                final String requestBdy = readFileFromResourcesFolder("responses/user.json");
//                final PutItemRequest putItemRequest = dynamodbRequestBuilder.createPutItemRequest(requestBdy, USER_TABLE);
//
//                return putItemRequest;
//            }
//
//            case "ACCOUNT": {
//                final String requestBdy = readFileFromResourcesFolder("responses/account.json");
//                final PutItemRequest putItemRequest = dynamodbRequestBuilder.createPutItemRequest(requestBdy, ACCOUNT_TABLE);
//
//                return putItemRequest;
//            }
//
//            default:
//            {
//                throw new UnsupportedEncodingException("This entity type is not supported for put request creation");
//            }
//        }
//    }
//
//
//    private UpdateItemRequest createUpdateRequestForEntity(String entityID, String type) throws IOException {
//
//        switch (type.toUpperCase()){
//
//            case "USER":
//            {
//                final String requestBdy = readFileFromResourcesFolder("responses/user.json");
//
//                JSONObject jsonObject = new JSONObject(requestBdy);
//                jsonObject.put("userID", entityID);
//
//                final UpdateItemRequest updateItemRequest = dynamodbRequestBuilder.createUpdateItemRequest(USER_TABLE, jsonObject.toString());
//                return updateItemRequest;
//            }
//
//
//            case "ACCOUNT":
//            {
//                final String requestBdy = readFileFromResourcesFolder("responses/account.json");
//
//                JSONObject jsonObject = new JSONObject(requestBdy);
//                jsonObject.put("id", entityID);
//
//                final UpdateItemRequest updateItemRequest = dynamodbRequestBuilder.createUpdateItemRequest(ACCOUNT_TABLE, jsonObject.toString());
//                return updateItemRequest;
//            }
//
//            default:
//            {
//                throw new UnsupportedEncodingException("This entity type is not supported for update request creation");
//            }
//        }
//    }
//
//}
