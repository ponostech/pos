/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tasks.inventory;

import javafx.concurrent.Task;
import jpa.InventoryJpa;
import ponospos.entities.Inventory;

/**
 *
 * @author Thangtea
 */
public class CreateInventoryTask extends Task<Inventory>{

    private Inventory inventory;
    @Override
    protected Inventory call() throws Exception {
        return InventoryJpa.createProduct(inventory);
    }
    
    private void setInventory(Inventory inv){
        this.inventory=inv;
    }
    
}
