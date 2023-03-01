package com.Microservice.ModulosPagoTransaction.entities;

import com.Microservice.ModulosPagoTransaction.dtos.TransactionDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO,generator = "native")
    @GenericGenerator(name ="native",strategy = "native")
    private Long id;
    private Double amount;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime dateTransaction;
    @JsonFormat(pattern = "dd-MM-yyyy")
    //@JsonSerialize(using = DateSerializer.class)
    private LocalDate scheduleDate;
    private String beneficiary;
    private TransactionType transactionType;
    private PaymentType paymentType;
    private String accountNumberSender;
    private String accountNumberReceiver;

    private String description;

    private Boolean wasProcessed;


// seters and getters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public LocalDateTime getDateTransaction() {
        return dateTransaction;
    }

    public void setDateTransaction(LocalDateTime dateTransaction) {
        this.dateTransaction = dateTransaction;
    }

    public String getBeneficiary() {
        return beneficiary;
    }

    public void setBeneficiary(String beneficiary) {
        this.beneficiary = beneficiary;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public String getAccountNumberSender() {
        return accountNumberSender;
    }

    public void setAccountNumberSender(String accountNumberSender) {
        this.accountNumberSender = accountNumberSender;
    }

    public String getAccountNumberReceiver() {
        return accountNumberReceiver;
    }

    public void setAccountNumberReceiver(String accountNumberReceiver) {
        this.accountNumberReceiver = accountNumberReceiver;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(LocalDate scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public Boolean getWasProcessed() {
        return wasProcessed;
    }

    public void setWasProcessed(Boolean wasProcessed) {
        this.wasProcessed = wasProcessed;
    }

    //Construtor
    public Transaction() {
    }

    public Transaction(TransactionDTO transactionDTO){
        this.amount = transactionDTO.getAmount();
        this.scheduleDate = transactionDTO.getScheduleDate();
        this.dateTransaction = LocalDateTime.now();
        this.beneficiary = transactionDTO.getBeneficiary();
        this.transactionType = transactionDTO.getTransactionType();
        this.paymentType = transactionDTO.getPaymentType();
        this.accountNumberSender = transactionDTO.getAccountNumberSender();
        this.accountNumberReceiver = transactionDTO.getAccountNumberReceiver();
        this.description = transactionDTO.getDescription();
        this.wasProcessed = Boolean.FALSE;
    }

}
