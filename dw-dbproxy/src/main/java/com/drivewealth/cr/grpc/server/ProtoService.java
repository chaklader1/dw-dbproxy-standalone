package com.drivewealth.cr.grpc.server;

import io.grpc.ManagedChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import static com.drivewealth.dbproxy.util.Parameters.*;


public class ProtoService {


    private static final Logger LOGGER = LoggerFactory.getLogger(ProtoService.class);


    public static void shutdownManagedChannel(ManagedChannel managedChannel) throws InterruptedException {

        LOGGER.info("We are shitting down the managed channel .....");

        if (managedChannel == null) {
            LOGGER.info("The managed channel is null and dependency is not injected properly");
            return;
        }

        if (!managedChannel.isShutdown()) {
            try {
                managedChannel.shutdown();
                if (!managedChannel.awaitTermination(TIMEOUT_SHUTDOWN, TimeUnit.SECONDS)) {
                    LOGGER.warn("Timed out gracefully shutting down connection: {}. ", managedChannel);
                }
            } catch (Exception e) {
                LOGGER.error("Unexpected exception while waiting for channel termination", e);
            }
        }
        else {
            LOGGER.error("Shutdown successfully");
            return;
        }

        Thread.sleep(25);

        if (!managedChannel.isTerminated()) {
            try {
                managedChannel.shutdownNow();
                Thread.sleep(25);

                if (!managedChannel.awaitTermination(TIMEOUT_SHUTDOWN_NOW, TimeUnit.SECONDS)) {
                    LOGGER.warn("Timed out forcefully shutting down connection: {}. ", managedChannel);
                }
            } catch (Exception e) {
                LOGGER.error("Unexpected exception while waiting for channel termination", e);
            }
        }

        LOGGER.info("managed channel is shutdown");
    }
}
