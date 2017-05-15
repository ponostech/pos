/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tasks.suppliers;

import java.util.List;
import javafx.concurrent.Task;
import jpa.SupplierJpa;
import ponospos.entities.Supplier;

/**
 *
 * @author Sawmtea
 */
public class SearchSupplierByNameTask extends Task<List<Supplier>>{

    private String name;
    @Override
    protected List<Supplier> call() throws Exception {
         return SupplierJpa.findSupplierByName(name);
    }

    public void setName(String name){
        this.name=name;
    }
    
}
