/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tasks.products;

import java.util.List;
import javafx.concurrent.Task;
import jpa.ProductJpa;
import ponospos.entities.Product;

/**
 *
 * @author Sawmtea
 */
public class FindProductTask extends Task<List<Product>>{

    private String param="";
    @Override
    protected List<Product> call() throws Exception {
        return ProductJpa.findProductNameOrBarcode(param);
    }

    public void setParam(String param) {
        this.param = param;
    }
    
    
}
