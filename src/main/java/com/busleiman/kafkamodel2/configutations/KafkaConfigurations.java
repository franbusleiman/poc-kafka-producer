package com.busleiman.kafkamodel2.configutations;


import com.busleiman.kafkadto.model.Message;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfigurations {

    @Value(value = "${kafka.bootstrapAddress:localhost:29091,localhost:29092}")
    private String bootstrapAddress;

    public Map<String, Object> producerProperties() {

        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ProducerConfig.RETRIES_CONFIG, 0);
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return props;
    }

    public Map<String, Object> consumerProperties() {

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "group");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "100");
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "15000");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, new JsonDeserializer<Message>());

        return props;
    }

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }


    @Bean
    public ProducerFactory<String, Object> getProducerFactory() {
        ProducerFactory<String, Object> producerFactory = new DefaultKafkaProducerFactory<String, Object>(producerProperties());
        return producerFactory;

    }

    @Bean
    @ConditionalOnProperty(
            value="kafka.consumer.enabled",
            havingValue = "true",
            matchIfMissing = true)
    public ConsumerFactory<String, Message> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerProperties(), new StringDeserializer(),
                new JsonDeserializer<>(Message.class, false));
    }

    @Bean
    @ConditionalOnProperty(
            value="kafka.consumer.enabled",
            havingValue = "true",
            matchIfMissing = true)
    public KafkaMessageListenerContainer<String, Message> replyContainer() {
        ContainerProperties containerProperties = new ContainerProperties(newTopic1().name(), newTopic2().name());

        return new KafkaMessageListenerContainer<>(consumerFactory(), containerProperties);
    }


    @Bean
    public ReplyingKafkaTemplate<String, Object, Message> createTemplate() {

        ReplyingKafkaTemplate<String, Object, Message> template = new ReplyingKafkaTemplate<>(getProducerFactory(), replyContainer());

        template.setSharedReplyTopic(true);
        return template;
    }

    @Bean
    public NewTopic newTopic1() {
        return TopicBuilder.name("Service-A-reply")
                .partitions(2)
                .replicas(2)
                .build();
    }

    @Bean
    public NewTopic newTopic2() {
        return TopicBuilder.name("Service-B-reply")
                .partitions(2)
                .replicas(2)
                .build();
    }
    @Bean
    public NewTopic newTopic3() {
        return TopicBuilder.name("Service-A")
                .partitions(2)
                .replicas(2)
                .build();
    }

    @Bean
    public NewTopic newTopic4() {
        return TopicBuilder.name("Service-B")
                .partitions(2)
                .replicas(2)
                .build();
    }
}
