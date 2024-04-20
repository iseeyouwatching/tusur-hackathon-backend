//package ru.hits.tusurhackathon.stream;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.amqp.AmqpRejectAndDontRequeueException;
//import org.springframework.amqp.rabbit.annotation.EnableRabbit;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.stereotype.Component;
//
//@Component
//@EnableRabbit
//@RequiredArgsConstructor
//@Slf4j
//public class RabbitTransactionEventListener {
//
//    private final TransactionService transactionService;
//    private final RabbitTemplate rabbitTemplate;
//
//    @RabbitListener(queues = "transactions1")
//    public void createTransactionEvent(String message) {
//        ObjectMapper objectMapper = new ObjectMapper();
//        try {
//            transactionService.createTransaction(objectMapper.readValue(message, CreateTransactionMessage.class));
//        }  catch (JsonProcessingException e) {
//            throw new AmqpRejectAndDontRequeueException(e.getMessage());
//        }
//    }
//
//}
