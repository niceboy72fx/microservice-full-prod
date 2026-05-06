package com.email.app.notification.worker;

import com.email.app.notification.model.NotificationEvent;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EmailSendWorker {

    private static final Logger log = LoggerFactory.getLogger(EmailSendWorker.class);

    private final ExecutorService virtualThreadExecutor = Executors.newVirtualThreadPerTaskExecutor();
    private final Semaphore maxInFlight;

    public EmailSendWorker(@Value("${app.notification.smtp.max-in-flight:200}") int maxInFlight) {
        this.maxInFlight = new Semaphore(Math.max(1, maxInFlight));
    }

    public CompletableFuture<Void> sendAsync(NotificationEvent event) {
        return CompletableFuture.runAsync(() -> sendEmail(event), virtualThreadExecutor);
    }

    private void sendEmail(NotificationEvent event) {
        boolean acquired = false;
        try {
            acquired = maxInFlight.tryAcquire(5, TimeUnit.SECONDS);
            if (!acquired) {
                throw new IllegalStateException("SMTP max-in-flight limit reached");
            }

            // Replace this block with real SMTP provider integration.
            log.info("Send email eventId={} correlationId={} recipient={} subject={} retryCount={}",
                    event.eventId(), event.correlationId(), event.recipient(), event.subject(), event.retryCount());
            Thread.sleep(Duration.ofMillis(120));
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while sending email", exception);
        } finally {
            if (acquired) {
                maxInFlight.release();
            }
        }
    }
}
