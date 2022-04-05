//package com.drivewealth.dbproxy.controller;
//
//import com.drivewealth.dbproxy.exception.DBProxyException;
//import com.drivewealth.dbproxy.service.DBProxyService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.io.buffer.DataBuffer;
//import org.springframework.core.io.buffer.DefaultDataBufferFactory;
//import org.springframework.http.server.reactive.ServerHttpRequest;
//import org.springframework.http.server.reactive.ServerHttpResponse;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RestController;
//import reactor.core.publisher.Mono;
//
//import java.io.IOException;
//import java.lang.reflect.InvocationTargetException;
//import java.net.URISyntaxException;
//import java.nio.charset.StandardCharsets;
//import java.util.*;
//import java.util.concurrent.ExecutionException;
//import java.util.concurrent.TimeoutException;
//
//import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
//
//
//@RestController
//public class DBProxyController {
//
//
//    private static final Logger LOGGER = LoggerFactory.getLogger(DBProxyController.class);
//
//    private static final String X_AWS_JSON = "application/x-amz-json-1.0";
//    private static final String X_AMZN_REQUESTID = "x-amzn-RequestId";
//
//    private static final String X_AMZN_RESPONSE = "x-amzn-Response";
//    // "x-amzn-Response"
//    private static final String X_AMZN_TARGET = "x-amz-target";
//    private static final String AMZ_SDK_INVOCATION_ID = "amz-sdk-invocation-id";
//
//
//    @Autowired
//    private DBProxyService dbProxyService;
//
//
//    @RequestMapping(value = "/", method = RequestMethod.POST,
//        consumes = APPLICATION_JSON_VALUE,
//        produces = APPLICATION_JSON_VALUE
//    )
//    @SuppressWarnings("BlockingMethodInNonBlockingContext")
//    public Mono<Void> handleRequest(@RequestBody String requestBody, ServerHttpResponse serverHttpResponse, ServerHttpRequest request) throws IOException, URISyntaxException, ExecutionException, InterruptedException, TimeoutException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
//
//        LOGGER.info("handle request in the DBProxy controller ....");
//
//        org.springframework.http.HttpHeaders headers = request.getHeaders();
//
//        Map<String, String> responseMap = dbProxyService.processRequest(requestBody, headers);
//
//        String response = responseMap.get(X_AMZN_RESPONSE);
//
//        if (response.equalsIgnoreCase("{}")) {
//            LOGGER.info("We are ot able to sucessfully process the request and reutning an empty response");
//        }
//
//        return serverHttpResponse.writeWith(strToDataBuffer(response));
//    }
//
//
//    private Mono<DataBuffer> strToDataBuffer(String string) {
//
//        byte[] bytes = string.getBytes(StandardCharsets.UTF_8);
//        DefaultDataBufferFactory defaultDataBufferFactory = new DefaultDataBufferFactory();
//        return Mono.just(defaultDataBufferFactory.wrap(bytes));
//    }
//
//}
