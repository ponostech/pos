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
public enum TransactionType {
    STOCK_TRANSFER("Transfer"),STOCK_UPDATE("Manual Update"),INVOICE("Invoice");
    
    private String val="";

    private TransactionType(String val) {
        this.val=val;
    }

    @Override
    public String toString() {
        return this.val;
    }
    
    
}
