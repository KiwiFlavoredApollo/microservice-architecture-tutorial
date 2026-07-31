package com.example.orderservice.kafka;

import com.example.orderservice.dto.KafkaOrderDto;
import com.example.orderservice.dto.OrderDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class OrderProducerService {
    private KafkaTemplate<String, String> kafkaTemplate;

    private static final List<KafkaOrderDto.Field> FIELDS = List.of(
            new KafkaOrderDto.Field("string", true, "order_id"),
            new KafkaOrderDto.Field("string", true, "user_id"),
            new KafkaOrderDto.Field("string", true, "product_id"),
            new KafkaOrderDto.Field("int32", true, "qty"),
            new KafkaOrderDto.Field("int32", true, "total_price"),
            new KafkaOrderDto.Field("int32", true, "unit_price")
    );

    private static final KafkaOrderDto.Schema SCHEMA = KafkaOrderDto.Schema.builder()
            .type("struct")
            .fields(FIELDS)
            .optional(false)
            .name("orders")
            .build();

    @Autowired
    public OrderProducerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public OrderDto send(String topic, OrderDto orderDto) {
        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = "";
        try {
            jsonInString = mapper.writeValueAsString(orderDto);
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
        }

        kafkaTemplate.send(topic, jsonInString);
        log.info("Kafka Producer sent data from the Order microservice: " + orderDto);

        return orderDto;
    }

    public OrderDto sendForSinkConnect(String topic, OrderDto orderDto) {
        KafkaOrderDto.Payload.PayloadBuilder builder = KafkaOrderDto.Payload.builder();
        builder.order_id(orderDto.getOrderId());
        builder.user_id(orderDto.getUserId());
        builder.product_id(orderDto.getProductId());
        builder.qty(orderDto.getQty());
        builder.unit_price(orderDto.getUnitPrice());
        builder.total_price(orderDto.getTotalPrice());
        KafkaOrderDto.Payload payload = builder.build();

        KafkaOrderDto kafkaOrderDto = new KafkaOrderDto(SCHEMA, payload);

        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = "";
        try {
            jsonInString = mapper.writeValueAsString(kafkaOrderDto);
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
        }

        kafkaTemplate.send(topic, jsonInString);
        log.info("Kafka Producer sent data for SINK CONNECT from the Order microservice: " + kafkaOrderDto);

        return orderDto;
    }
}
