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

public class FetchAllTask extends Task<List<Product>>{

    @Override
    protected List<Product> call() throws Exception {
        return ProductJpa.getAllProduct();
    }
    
}
