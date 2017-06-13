/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tasks.products;

import javafx.concurrent.Task;
import jpa.ProductJpa;

public class FindByBarcode extends Task<Boolean>{

    public String barcode;
    @Override
    protected Boolean call() throws Exception {
        
        return ProductJpa.existBarcode(barcode);
    }
    
}
