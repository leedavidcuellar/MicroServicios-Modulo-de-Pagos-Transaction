package com.Microservice.ModulosPagoTransaction.controllers;

import com.Microservice.ModulosPagoTransaction.entities.PaymentType;
import com.Microservice.ModulosPagoTransaction.entities.Transaction;
import com.Microservice.ModulosPagoTransaction.models.Account;
import com.Microservice.ModulosPagoTransaction.dtos.TransactionDTO;
import com.Microservice.ModulosPagoTransaction.services.InterfaceTransactionService;
import com.Microservice.ModulosPagoTransaction.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/transaction")
public class TransactionController {
    @Autowired
    @Qualifier("transactionServicesRestTemplate")
    private InterfaceTransactionService interfaceTransactionService;
    @Autowired
    private SenderController senderController;
    @Transactional
    @PostMapping("/generateTransactions")
    public ResponseEntity<Object> generateTransactions(@RequestBody TransactionDTO transactionDTO) {
        try {
            if (interfaceTransactionService.checkAccountNoExist(transactionDTO.getAccountNumberSender())) {
                return new ResponseEntity<>("Account Sender NO exist, check data", HttpStatus.FORBIDDEN);
            }
            if (interfaceTransactionService.checkAccountNoExist(transactionDTO.getAccountNumberReceiver())) {
                return new ResponseEntity<>("Account Receiver NO exist, check data", HttpStatus.FORBIDDEN);
            }
            if (transactionDTO.getAmount().toString().trim().isEmpty()||transactionDTO.getDescription().trim().isEmpty() || transactionDTO.getAccountNumberSender().trim().isEmpty() || transactionDTO.getAccountNumberReceiver().trim().isEmpty()) {
                return new ResponseEntity<>("Error Missing data, description or account sender or account destination is empty or amount", HttpStatus.NOT_ACCEPTABLE);
            }
            if(transactionDTO.getAccountNumberSender().equals(transactionDTO.getAccountNumberReceiver())){
                return new ResponseEntity<>("Error the number account of Sender and Receiver MUST NOT BE the same", HttpStatus.NOT_ACCEPTABLE);
            }
            if (!(transactionDTO.getAccountNumberSender().length() == 10)) {
                return new ResponseEntity<>("Error check Account Number of Sender, it will be 10 digits", HttpStatus.NOT_ACCEPTABLE);
            }
            if (!Utils.verifyNumber(transactionDTO.getAccountNumberSender())) {
                return new ResponseEntity<>("Error in Account Sender, please check it only numbers.", HttpStatus.NOT_ACCEPTABLE);
            }
            if (!(transactionDTO.getAccountNumberReceiver().length() == 10)) {
                return new ResponseEntity<>("Error check Account Number of Receiver, it will be 10 digits", HttpStatus.NOT_ACCEPTABLE);
            }
            if (!Utils.verifyNumber(transactionDTO.getAccountNumberReceiver())) {
                return new ResponseEntity<>("Error in Account Receiver, please check it only numbers.", HttpStatus.NOT_ACCEPTABLE);
            }
            if(transactionDTO.getAmount() < 0.00){
                return new ResponseEntity<>("Error Amount must to be major 0.", HttpStatus.NOT_ACCEPTABLE);
            }
            if (!Utils.verifyNumber(transactionDTO.getAmount().toString())) {
                return new ResponseEntity<>("Error in Amount, please check it only numbers.", HttpStatus.NOT_ACCEPTABLE);
            }
            if(Utils.verifyTwoDecimal(transactionDTO.getAmount())){
                return new ResponseEntity<>("Error in Amount, must be only two decimal, please check it only numbers.", HttpStatus.NOT_ACCEPTABLE);
            }
            if (interfaceTransactionService.getBalanceAccountSender(transactionDTO.getAccountNumberSender()) < transactionDTO.getAmount()) {
                return new ResponseEntity<>("Error The Account do not have balance", HttpStatus.NOT_ACCEPTABLE);
            }
            if(transactionDTO.getScheduleDate()==null || transactionDTO.getScheduleDate().toString().trim().isEmpty()){
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                transactionDTO.setScheduleDate(LocalDate.parse(LocalDate.now().toString(), formatter));
            }
            if(transactionDTO.getScheduleDate().isBefore(LocalDate.now())){
                return new ResponseEntity<>("Error The Date must be now or bigger", HttpStatus.NOT_ACCEPTABLE);
            }
            if(!Utils.checkFormatDate(transactionDTO.getScheduleDate())){
                return new ResponseEntity<>("Error with format date, remember DD/HH/YYYY", HttpStatus.NOT_ACCEPTABLE);
            }
            transactionDTO.setBeneficiary(interfaceTransactionService.getNameUserAccount(transactionDTO.getAccountNumberReceiver()));

            if (transactionDTO.getPaymentType().equals(PaymentType.ECHEQS)
                    || transactionDTO.getPaymentType().equals(PaymentType.CARD)
                    || transactionDTO.getScheduleDate().isAfter(LocalDate.now())) {
                interfaceTransactionService.transactionScheduled();
                Transaction transaction =interfaceTransactionService.createSpecialTransaction(transactionDTO);
                return new ResponseEntity<>("The transaction will be processed", HttpStatus.OK);
            }else{
                Transaction transaction =interfaceTransactionService.createTransaction(transactionDTO);
                return new ResponseEntity<>(transaction,HttpStatus.CREATED);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            ex.getMessage();
            return new ResponseEntity<>("Unexpected error: "+ ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/listAll")
    public List<Transaction> getListAllTransaction() {
        return interfaceTransactionService.findAllTransaction();
    }

    @GetMapping("/listByUser/{idUser}")
    public ResponseEntity<Object> getListByUser(@PathVariable Long idUser) {
        List<Account> accountList = interfaceTransactionService.checkUserNoExist(idUser.toString());
        if(accountList==null) {
            return new ResponseEntity<>("Error User NO exits", HttpStatus.NOT_ACCEPTABLE);
        }
        if(accountList.isEmpty()){
            return new ResponseEntity<>("Error User NO have account", HttpStatus.NOT_ACCEPTABLE);
        }
        List<Transaction> transactionListFull = new ArrayList<>();
        for (Account accountAux: accountList) {
             List<Transaction> transactionList= interfaceTransactionService.findTransactionsUserByAccountNumber(accountAux.getNumberAccount());
             transactionListFull.addAll(transactionList);
        }
        return new ResponseEntity<>(transactionListFull,HttpStatus.OK);
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<Object> detailUser(@PathVariable Long id) {
        Transaction transaction = interfaceTransactionService.findByIdTransaction(id);
        if(transaction==null){
            return new ResponseEntity<>("Transaction NO exits, check information", HttpStatus.NOT_ACCEPTABLE);
        }
        return new ResponseEntity<>(transaction, HttpStatus.OK);
    }

    @GetMapping("/BetweenDates")
    public ResponseEntity<Object> getTransactionBetweenDates(@RequestParam String date1, @RequestParam String date2){
        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        if(!Utils.checkFormatDate(LocalDate.parse(date1,formatter1))){
            return new ResponseEntity<>("Error with format date1, remember DD/HH/YYYY", HttpStatus.NOT_ACCEPTABLE);
        }
        if(!Utils.checkFormatDate(LocalDate.parse(date2,formatter1))){
            return new ResponseEntity<>("Error with format date2, remember DD/HH/YYYY", HttpStatus.NOT_ACCEPTABLE);
        }
        date1 = date1 + " 00:00:00";
        date2 = date2 + " 23:59:59";
        List<Transaction> transactionList = interfaceTransactionService.findByDateBetween(LocalDateTime.parse(date1,formatter2),LocalDateTime.parse(date2,formatter2));
        return new ResponseEntity<>(transactionList,HttpStatus.OK);
    }

    @GetMapping("/findByDate")
    public ResponseEntity<Object> getTransactionFindByDate(@RequestParam String date){
        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        if(!Utils.checkFormatDate(LocalDate.parse(date,formatter1))){
            return new ResponseEntity<>("Error with format date1, remember DD/HH/YYYY", HttpStatus.NOT_ACCEPTABLE);
        }
        String date1 = date + " 00:00:00";
        String date2 = date + " 23:59:59";
        List<Transaction> transactionList = interfaceTransactionService.findByDateBetween(LocalDateTime.parse(date1,formatter2),LocalDateTime.parse(date2,formatter2));
        return new ResponseEntity<>(transactionList,HttpStatus.OK);
    }

    @GetMapping("/transactionType/")
    public ResponseEntity<Object> getByTransactionType(@RequestParam String paymentType){
        PaymentType paymentTypeResponse = PaymentType.valueOf(paymentType);
        if(paymentTypeResponse==null){
            return new ResponseEntity<>("Error this type no exist",HttpStatus.NOT_ACCEPTABLE);
        }
        List<Transaction> transactionList= interfaceTransactionService.findByPaymentType(paymentTypeResponse);
        return  new ResponseEntity<>(transactionList,HttpStatus.OK);
    }

}
