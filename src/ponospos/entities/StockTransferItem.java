/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ponospos.entities;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author Sawmtea
 */
@Entity
@Table(name="stock_transfer_item")
public class StockTransferItem implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @ManyToOne(targetEntity = Product.class)
    @JoinColumn(name="product_id",nullable = false)
    private Product product;
    
    
    @Column(name="qty",nullable = false)
    private Integer quantity;
    
    @ManyToOne(optional = false)
    @JoinColumn(name="stock_transfer_id",nullable = false)
    private StockTransfer transfer;

    public StockTransferItem() {
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public StockTransfer getTransfer() {
        return transfer;
    }

    public void setTransfer(StockTransfer transfer) {
        this.transfer = transfer;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final StockTransferItem other = (StockTransferItem) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }
    
    
    

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "StockTransferItem{" + "id=" + id + ", product=" + product + ", quantity=" + quantity + ", transfer=" + transfer + '}';
    }
    
    
}
