/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tasks.inventory;

import java.util.List;
import javafx.concurrent.Task;
import jpa.InventoryJpa;
import ponospos.entities.Inventory;

/**
 *
 * @author Sawmtea
 */
public class FetchAllInventoryTask extends Task<List<Inventory>> {

    @Override
    protected List<Inventory> call() throws Exception {
        return InventoryJpa.getAllProductInInventory();
    }
    
}
