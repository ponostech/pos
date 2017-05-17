package tasks.products;

import javafx.concurrent.Task;
import jpa.ProductJpa;
import ponospos.entities.Product;

public class DeleteTask extends Task<Product> {
    private Product product;
    @Override
    protected Product call() throws Exception {
        return ProductJpa.deleteProduct(product);
    }
    
    public void setProduct(Product product){
        this.product=product;
    }
}
