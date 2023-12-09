package com.heima.kafka.sample;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.ValueMapper;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

/**
 * 流式处理
 */
public class KafkaStreamQuickStart {
    public static void main(String[] arhs) {
        // 1. kafka 配置中心
        Properties properties = new Properties();
        // kafka 地址
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.200.130:9092");
        // key & value 的序列化方式
        properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        // 应用id
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-quickstart");

        // stream 构造器
        StreamsBuilder streamsBuilder = new StreamsBuilder();

        // 2. 流式计算
        streamProcessor(streamsBuilder);

        // 3. 创建 streams 对象, 并开始流式计算
        KafkaStreams kafkaStreams = new KafkaStreams(streamsBuilder.build(), properties);
        kafkaStreams.start();
    }

    /**
     * 流式计算
     *
     * @param streamsBuilder
     */
    private static void streamProcessor(StreamsBuilder streamsBuilder) {
        // 创建 kstream 对象, 同时指定从哪个 topic 中接收消息
        KStream<String, String> stream = streamsBuilder.stream("itcast-topic-input");

        /**
         * 处理消息的 value
         */
        stream.flatMapValues(new ValueMapper<String, Iterable<String>>() {
                    @Override
                    public Iterable<String> apply(String value) {
                        String[] split = value.split(";");
                        return Arrays.asList(split);
                    }
                })

                // 按照 value 进行聚合处理
                .groupBy((key, value) -> value)
                // 时间窗口, 指定时间的消息会被聚合在一块
                .windowedBy(TimeWindows.of(Duration.ofSeconds(10)))
                // 统计单词的个数
                .count()
                .toStream()
                .map((key, value) -> {
                    System.out.println("key: " + key + ", value: " + value);
                    return new KeyValue<>(key.key().toString(), value.toString());
                })

                // 发送消息
                .to("itcast-topic-out");
    }
}
