package com.busleiman.kafkamodel2.output;

import avro.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaProducerException;
import org.springframework.kafka.core.KafkaSendCallback;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/producer")
public class KafkaProducer {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducer.class);

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @GetMapping(value = "/{message}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> sendMessage(@PathVariable("message") String message) {

        Order order = Order.newBuilder()
                .setOrderId("OId234")
                .setCustomerId("CId432")
                .setSupplierId("SId543")
                .setItems(4)
                .setFirstName("Sunil")
                .setLastName("V")
                .setPrice(178f)
                .setWeight(75f)
                .setAutomatedEmail(true)
                .build();

        ListenableFuture<SendResult<String, Object>> listenableFuture =
                kafkaTemplate.send("learningKafka", order);

        logger.info(message);

        listenableFuture.addCallback(new KafkaSendCallback<String, Object>() {


            @Override
            public void onSuccess(SendResult<String, Object> stringStringSendResult) {

                logger.info("message sent: " + stringStringSendResult);

            }

            @Override
            public void onFailure(Throwable ex) {
                KafkaSendCallback.super.onFailure(ex);

                logger.info("message failure: " + ex);
            }

            @Override
            public void onFailure(KafkaProducerException e) {
                logger.info("message failure: " + e);
            }
        });

        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/messages/{message}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> sendMessages(@PathVariable("message") String message) {

        for (int i = 0; i <= 1000; i++) {

            kafkaTemplate.send("learningKafka", message + i);
        }

        return ResponseEntity.ok().build();
    }

  //  @Scheduled(initialDelay = 1000, fixedDelay = 5000)
    public void sendMessagesScheduled() {

        int counter = 0;

        kafkaTemplate.send("learningKafka", ++counter + " come on");

    }
}
