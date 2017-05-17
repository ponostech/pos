/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tasks.products;

import javafx.concurrent.Task;
import jpa.ProductJpa;
import ponospos.entities.Product;

/**
 *
 * @author Sawmtea
 */
public class CreateTask extends Task<Product> {

    private Product product;
    @Override
    protected Product call() throws Exception {
        return ProductJpa.createProduct(product);
    }
    public void setProduct(Product product){
        this.product=product;
    }
    
}
