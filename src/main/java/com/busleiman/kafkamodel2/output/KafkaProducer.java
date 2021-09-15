package com.busleiman.kafkamodel2.output;

import com.busleiman.kafkadto.model.Message;
import com.busleiman.kafkadto.model.Order;
import com.busleiman.kafkadto.model.Response;
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

import java.util.Objects;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/producer")
public class KafkaProducer {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducer.class);

    @Autowired
    private ReplyingKafkaTemplate<String, Object, Message> replyingKafkaTemplate;

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

        if(Objects.equals(message, "secret")){
            order.setFirstName("secret");
        }

        Message message1  = new Message();
        message1.setMessageCode(200);
        message1.setMessageContent(order);

        ProducerRecord<String, Object> record = new ProducerRecord("learningKafka", message1);

        record.headers().add(new RecordHeader(KafkaHeaders.REPLY_TOPIC, "requestReplyTopic".getBytes()));

        RequestReplyFuture<String, Object, Message> sendAndReceive = replyingKafkaTemplate.sendAndReceive(record);

        SendResult<String, Object> sendResult = sendAndReceive.getSendFuture().get();

        //print all headers
        sendResult.getProducerRecord().headers().forEach(header -> System.out.println(header.key() + ":" + header.value().toString()));

        ConsumerRecord<String, Message> consumerRecord = sendAndReceive.get();

        Message message2 = consumerRecord.value();

        if (message2.getMessageContent() instanceof Response) {
            System.out.println("working");
        }
        else if(message2.getMessageContent() instanceof Long){
            System.out.println("Long received: " + message2.getMessageContent());
        }
        else {
            System.out.println("not working");
        }
        return ResponseEntity.ok().build();
    }
}
