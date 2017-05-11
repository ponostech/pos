/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tasks.suppliers;

import javafx.concurrent.Task;
import jpa.SupplierJpa;
import jpa.UserJpa;
import ponospos.entities.Supplier;
import ponospos.entities.User;

/**
 *
 * @author Sawmtea
 */
public class DeleteTask extends Task<Supplier>{
        private Supplier supplier;
        @Override
        protected Supplier call() throws Exception {
            return SupplierJpa.deleteSupplier(supplier);
        }
        public void setSupplier(Supplier supplier){
            this.supplier=supplier;
        }
        
    }
