package com.busleiman.kafkamodel2.configutations;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.avro.Schema;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfigurations {
    public Map<String, Object> producerProperties() {

        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092");
        props.put(ProducerConfig.RETRIES_CONFIG, 0);
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,  KafkaAvroSerializer.class.getName());
        props.put("schema.registry.url", "http://localhost:8081");

        return props;
    }

    @Bean
    public ProducerFactory<String, Object> getProducerFactory(){
        ProducerFactory<String, Object> producerFactory =  new DefaultKafkaProducerFactory<String, Object>(producerProperties());
        return producerFactory;

    }

    @Bean
    public KafkaTemplate<String, Object> createTemplate() {

        KafkaTemplate<String, Object> template =new KafkaTemplate<>(getProducerFactory());


        return template;
    }

}
