/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

/**
 *
 * @author Sawmtea
 */
public enum PaymentMethod {
    CASH_IN_HAND("Cash in Hand"),DEBIT_CARD("By Debit card"),CREDIT_CARD("By Credit card"),CHECK("By Check");
    private String val;
    PaymentMethod(String val){
        this.val=val;
    }

    @Override
    public String toString() {
        return val;
    }
    
}
