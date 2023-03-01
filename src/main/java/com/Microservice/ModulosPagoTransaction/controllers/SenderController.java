package com.Microservice.ModulosPagoTransaction.controllers;

import com.Microservice.ModulosPagoTransaction.dtos.TransactionDTO;
import com.Microservice.ModulosPagoTransaction.entities.Transaction;
import com.fasterxml.jackson.databind.ser.std.SerializableSerializer;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/sender")
public class SenderController {
    @Autowired
    JmsTemplate jmsTemplate;

    @GetMapping("/{message}")
            public String send(@PathVariable("message")String message) {
        jmsTemplate.send("demo", new MessageCreator() {

            @Override
            public Message createMessage(Session session) throws JMSException {
                ObjectMessage object = session.createObjectMessage(message);
                return object;
            }

        });

		return message;
    }

    public String convertAndSend(Transaction transactionDTO) {
        jmsTemplate.send("ProcessTransaction", new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("amount",transactionDTO.getAmount());
                jsonObject.put("accountNumberSender",transactionDTO.getAccountNumberSender());
                jsonObject.put("accountNumberReceiver",transactionDTO.getAccountNumberReceiver());
                jsonObject.put("transactionType",transactionDTO.getTransactionType());
                jsonObject.put("scheduleDate", transactionDTO.getScheduleDate().toString());
                ObjectMessage object = session.createObjectMessage(jsonObject.toString());
                System.out.println(object);
                return object;
            }
        });
        return "Your Transaction is in process...";
    }
}
