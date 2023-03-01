package com.Microservice.ModulosPagoTransaction.services;

import com.Microservice.ModulosPagoTransaction.entities.PaymentType;
import com.Microservice.ModulosPagoTransaction.entities.Transaction;
import com.Microservice.ModulosPagoTransaction.models.Account;
import com.Microservice.ModulosPagoTransaction.dtos.TransactionDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface InterfaceTransactionService {

 public Transaction createTransaction(TransactionDTO transactionDTO);

 public Transaction createSpecialTransaction(TransactionDTO transactionDTO);

 public Boolean checkAccountNoExist(String accountNumber);

 public List<Account> checkUserNoExist(String idUser);

 public Double getBalanceAccountSender(String accountNumber);

 public String getNameUserAccount(String accountNumber);

 public List<Transaction> findAllTransaction();

 public Transaction findByIdTransaction(Long id);

 public List<Transaction>findByDateBetween(LocalDateTime date1,LocalDateTime date2);

 public List<Transaction>findTransactionsUserByAccountNumber(String accountNumber);

 public List<Transaction>findByPaymentType(PaymentType paymentType);

 public List<Transaction> findByScheduleDateAndWasProcessedFalse();

 public void transactionScheduled();
}
