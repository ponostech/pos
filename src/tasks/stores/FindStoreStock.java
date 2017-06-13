/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tasks.stores;

import java.util.List;
import javafx.concurrent.Task;
import jpa.StoreJpa;
import ponospos.entities.Product;
import ponospos.entities.Stores;

/**
 *
 * @author Sawmtea
 */
public class FindStoreStock extends Task<List<Product>> {

    public Stores store;
    @Override
    protected List<Product> call() throws Exception {
        return StoreJpa.findStoreStock(store);
    }
    
}
