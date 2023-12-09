package com.heima.kafka.sample;

import org.apache.kafka.clients.producer.*;
import org.apache.logging.log4j.core.appender.mom.kafka.DefaultKafkaProducerFactory;
import org.apache.logging.log4j.core.appender.mom.kafka.KafkaProducerFactory;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class ProducerQuickStart {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // 1. kafka 配置信息
        Properties properties = new Properties();
        // kafka 地址
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.200.130:9092");
        // key & value 的序列化方式
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        properties.put(ProducerConfig.ACKS_CONFIG, "1");
        properties.put(ProducerConfig.RETRIES_CONFIG, 10);
        properties.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "lz4");

        // 2. 创建 kafka 生产对象
        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);

        // 3. 发送消息
        /**
         * 第一个参数为 : topic
         * 第二个参数为 : 消息的 key
         * 第二个参数为 : 消息的 value
         */
        for (int i = 0; i < 5; i++) {
            ProducerRecord<String, String> producerRecord = new ProducerRecord<>("itcast-topic-input", "hello kafka");
            producer.send(producerRecord);
        }
        // 异步的方式发送消息
        /*producer.send(producerRecord, new Callback() {
            @Override
            public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                if (e != null) {
                    System.out.println("记录异常信息到日志列表中!");
                }

                // 获取偏移量
                System.out.println(recordMetadata.offset());
            }
        });*/

        // 消息信息
        // RecordMetadata recordMetadata = producer.send(producerRecord).get();
        /*// 获取偏移量
        System.out.println(recordMetadata.offset());*/


        // 4. 关系消息通道
        producer.close();
    }
}
