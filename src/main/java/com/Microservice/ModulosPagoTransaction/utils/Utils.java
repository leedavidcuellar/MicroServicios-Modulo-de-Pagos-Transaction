package com.Microservice.ModulosPagoTransaction.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;

public final class Utils {

    public Utils() {
    }

    public static Boolean verifyNumber(String number){
        try {
            Double.parseDouble(number);
            return true;
        }catch (NumberFormatException e){
            e.getMessage();
            return false;
        }
    }

    public static Boolean verifyTwoDecimal(Double number){
        int counter =0;
        try {
            int index = number.toString().indexOf(".");
            for (int i= index; i < number.toString().length(); i++){
                counter ++;
            }
            if(counter > 3){
                return true;
            } else {
                return false;
            }
        }catch (NumberFormatException e){
            e.getMessage();
            return false;
        }
    }

    public static boolean checkFormatDate(LocalDate dateTime) {
        boolean isCorrect = false;

        try {
            //Format date (DD/MM/AAA)
            SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");
            formatDate.setLenient(false);
            String day = Integer.toString(dateTime.getDayOfMonth());
            String month = Integer.toString(dateTime.getMonthValue());
            String year = Integer.toString(dateTime.getYear());
            //check date
            formatDate.parse( day + "/" + month + "/" + year );
            isCorrect = true;
        } catch (ParseException e) {
            //if dat not correct show that
            isCorrect = false;
        }
        return isCorrect;
    }

}
