package com.Microservice.ModulosPagoTransaction.repositories;

import com.Microservice.ModulosPagoTransaction.entities.PaymentType;
import com.Microservice.ModulosPagoTransaction.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RepositoryRestResource
public interface TransactionRepository extends JpaRepository<Transaction,Long> {
    List<Transaction> findByDateTransactionBetween(LocalDateTime date1,LocalDateTime date2);
    List<Transaction>findByAccountNumberSenderOrAccountNumberReceiver(String accountNumber1, String accountNumber2);

    List<Transaction> findByScheduleDateAndWasProcessedFalse(LocalDate scheduleDate);
    List<Transaction>findByPaymentType(PaymentType paymentType);
}
