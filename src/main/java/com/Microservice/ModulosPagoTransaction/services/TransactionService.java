package com.Microservice.ModulosPagoTransaction.services;

import com.Microservice.ModulosPagoTransaction.controllers.SenderController;
import com.Microservice.ModulosPagoTransaction.entities.PaymentType;
import com.Microservice.ModulosPagoTransaction.entities.Transaction;
import com.Microservice.ModulosPagoTransaction.models.Account;
import com.Microservice.ModulosPagoTransaction.dtos.TransactionDTO;
import com.Microservice.ModulosPagoTransaction.models.User;
import com.Microservice.ModulosPagoTransaction.repositories.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service("transactionServicesRestTemplate")

public class TransactionService implements InterfaceTransactionService{

    @Autowired
    RestTemplate clientRest;
    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    private SenderController senderController;

    private Logger logger = LoggerFactory.getLogger(TransactionService.class);

    @Override
    public Transaction createTransaction(TransactionDTO transactionDTO) {
        Transaction transaction = new Transaction(transactionDTO);
        clientRest.postForEntity("http://localhost:8001/api/account/updateBalance", transactionDTO, Account.class);
        logger.info("MSTransaction RestTemplated: created Transaction and Processed");
        transaction.setWasProcessed(Boolean.TRUE);
        return transactionRepository.save(transaction);
    }

    @Override
    public Transaction createSpecialTransaction(TransactionDTO transactionDTO) {
        Transaction transaction = new Transaction(transactionDTO);
        clientRest.postForEntity("http://localhost:8001/api/account/updateBalanceAccountSender", transactionDTO, Account.class);
        logger.info("MSTransaction RestTemplated: created Special Transaction");
        return transactionRepository.save(transaction);
    }

    @Override
    public Boolean checkAccountNoExist(String accountNumber) {
        Map<String,String> pathVariables = new HashMap<String,String>();
        pathVariables.put("number",accountNumber);
        Account account = clientRest.getForObject("http://localhost:8001/api/account/list/number/{number}",Account.class,pathVariables);
        logger.info("MSTransaction RestTemplated: check account exists");
        if(account!= null){
            return false;
        } else {
            return true;
        }
    }

    @Override
    public List<Account> checkUserNoExist(String idUser) {
        Map<String,String> pathVariables = new HashMap<String,String>();
        pathVariables.put("id",idUser);
        User user = clientRest.getForObject("http://localhost:8010/api/user/detail/{id}",User.class,pathVariables);
        logger.info("MSTransaction RestTemplated: check user exits");
        if(user!= null){
            Map<String,String> pathVariables1 = new HashMap<String,String>();
            pathVariables1.put("idUser",user.getId().toString());
            List<Account> accountList = Arrays.stream(clientRest.getForObject("http://localhost:8001/api/account/listAccount/{idUser}",Account[].class,pathVariables1)).toList();
            return accountList;
        } else {
            return null;
        }
    }

    @Override
    public Double getBalanceAccountSender(String accountNumber) {
        Map<String,String> pathVariables = new HashMap<String,String>();
        pathVariables.put("number",accountNumber);
        Account account = clientRest.getForObject("http://localhost:8001/api/account/list/number/{number}",Account.class,pathVariables);
        logger.info("MSTransaction RestTemplated: created check balance sender");
        return account.getBalance().doubleValue();
    }

    @Override
    public String getNameUserAccount(String accountNumber) {
        Map<String,String> pathVariables = new HashMap<String,String>();
        pathVariables.put("number",accountNumber);
        Account account = clientRest.getForObject("http://localhost:8001/api/account/list/number/{number}",Account.class,pathVariables);
        Map<String,String> pathVariables1 = new HashMap<String,String>();
        pathVariables1.put("id",account.getIdUser().toString());
        User user = clientRest.getForObject("http://localhost:8010/api/user/detail/{id}",User.class,pathVariables1);
        logger.info("MSTransaction RestTemplated: get name destination");
        return user.getName() + " "+ user.getLastName();
    }

    @Override
    public List<Transaction> findAllTransaction() {
        logger.info("MSTransaction RestTemplated: list all Transaction");
        return transactionRepository.findAll();
    }

    @Override
    public Transaction findByIdTransaction(Long id) {
        logger.info("MSTransaction RestTemplated: list Transaction by id");
        return transactionRepository.findById(id).orElse(null);
    }

    @Override
    public List<Transaction> findByDateBetween(LocalDateTime date1, LocalDateTime date2) {
        logger.info("MSTransaction RestTemplated: list Transaction by two dates");
        return transactionRepository.findByDateTransactionBetween(date1,date2);
    }

    @Override
    public List<Transaction> findTransactionsUserByAccountNumber(String accountNumber) {
        logger.info("MSTransaction RestTemplated: list Transaction by user with account number");
        return transactionRepository.findByAccountNumberSenderOrAccountNumberReceiver(accountNumber, accountNumber);
    }

    @Override
    public List<Transaction> findByPaymentType(PaymentType paymentType) {
        logger.info("MSTransaction RestTemplated: list Transaction by payment type");
        return transactionRepository.findByPaymentType(paymentType);
    }

   @Override
    public List<Transaction> findByScheduleDateAndWasProcessedFalse() {
        List<Transaction> transactionListToday= transactionRepository.findByScheduleDateAndWasProcessedFalse(LocalDate.now());
       if (transactionListToday.isEmpty() || transactionListToday == null) {
           return null;
       } else {
           return transactionListToday;
       }
    }

    public void transactionScheduled() {
        Timer timer = new Timer();
        TimerTask transactionToday = new TimerTask() {
            @Override
            public void run() {
                List<Transaction> listTodayTransaction = findByScheduleDateAndWasProcessedFalse();
                if (listTodayTransaction.isEmpty() || listTodayTransaction == null) {
                    System.out.println("There is NOT transaction for today");
                } else {
                    System.out.println("Transactions list = " + listTodayTransaction.size());
                    for (Transaction aux : listTodayTransaction) {
                        senderController.convertAndSend(aux);
                        aux.setWasProcessed(Boolean.TRUE);
                        transactionRepository.save(aux);
                        System.out.println("Transaction n° " + aux.getId() + " was processed");
                    }
                }
            }
        };
        timer.schedule(transactionToday, 0, 60000);
    }
}

/*
        Map <String,String> pathVariables = new HashMap<String,String>();
        pathVariables.put("id",idAccount.toString());
        Account account = clientRest.getForObject("http://localhost:8001/detail/{id}",Account.class,pathVariables);

        Map <String,String> pathVariables = new HashMap<String,String>();
        pathVariables.put("idUser",user.getId().toString());
        Set<Account> accountList = Arrays.stream(clientRest.getForObject("http://localhost:8001/api/account/listAccount/{idUser}",Account[].class,pathVariables)).collect(Collectors.toSet());
 */
