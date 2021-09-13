package com.busleiman.kafkamodel2.output;

import com.busleiman.kafkamodel2.model.Order;
import com.busleiman.kafkamodel2.model.Response;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/producer")
public class KafkaProducer {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducer.class);

    @Autowired
    private ReplyingKafkaTemplate<String, Object, Object> replyingKafkaTemplate;

    @GetMapping(value = "/{message}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> sendMessage(@PathVariable("message") String message) throws ExecutionException, InterruptedException {

        Order order = Order.builder()
                .orderId("OId234")
                .customerId("CId432")
                .supplierId("SId543")
                .items(4)
                .firstName("Sunil")
                .lastName("V")
                .price(178f)
                .weight(75f)
                .automatedEmail(true)
                .build();

        ProducerRecord<String, Object> record = new ProducerRecord("learningKafka", order);

        record.headers().add(new RecordHeader(KafkaHeaders.REPLY_TOPIC, "requestReplyTopic".getBytes()));

        RequestReplyFuture<String, Object, Object> sendAndReceive = replyingKafkaTemplate.sendAndReceive(record);

        SendResult<String, Object> sendResult = sendAndReceive.getSendFuture().get();

        //print all headers
        sendResult.getProducerRecord().headers().forEach(header -> System.out.println(header.key() + ":" + header.value().toString()));

        ConsumerRecord<String, Object> consumerRecord = sendAndReceive.get();

        System.out.println(consumerRecord.value());
        return ResponseEntity.ok().build();
    }
}
