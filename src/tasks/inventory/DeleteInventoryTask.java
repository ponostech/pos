package tasks.inventory;

import javafx.concurrent.Task;
import jpa.InventoryJpa;
import ponospos.entities.Inventory;

public class DeleteInventoryTask extends Task<Inventory> {
    private Inventory product;
    @Override
    protected Inventory call() throws Exception {
        return InventoryJpa.deleteProduct(product);
    }
    
    public void setProduct(Inventory product){
        this.product=product;
    }
}
