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
public enum InvoiceStatus {
    PARTIAL("Partial"),PENDING("Pending"),FULL_PAYMENT("Full Payment");
    
    private String val="";
    InvoiceStatus(String val){
        this.val=val;
    }
    @Override
    public String toString() {
        return val;
    }
    
}
