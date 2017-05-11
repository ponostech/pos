/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tasks.stores;

import javafx.concurrent.Task;
import jpa.StoreJpa;
import ponospos.entities.Stores;

/**
 *
 * @author Sawmtea
 */
public class DeleteTask extends Task<Stores>{
        private Stores store;
        
        @Override
        protected Stores call() throws Exception {
            return StoreJpa.deleteStore(store);
        }
        public void setStore(Stores store){
            this.store=store;
        }
        
    }
