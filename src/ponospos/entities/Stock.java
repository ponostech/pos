/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ponospos.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Sawmtea
 */
@Entity
@Table(name = "stock")
@NamedQueries({
    @NamedQuery(name = "Stock.findAll", query = "SELECT s FROM Stock s")
    , @NamedQuery(name = "Stock.findById", query = "SELECT s FROM Stock s WHERE s.id = :id")
    , @NamedQuery(name = "Stock.findByItem", query = "SELECT s FROM Stock s WHERE s.product.name LIKE :param")
    , @NamedQuery(name = "Stock.findByQuantity", query = "SELECT s FROM Stock s WHERE s.quantity = :quantity")
    , @NamedQuery(name = "Stock.findByInvoice", query = "SELECT s FROM Stock s WHERE s.invoice = :invoice")
    , @NamedQuery(name = "Stock.findByUpdateAt", query = "SELECT s FROM Stock s WHERE s.updateAt = :updateAt")
    , @NamedQuery(name = "Stock.findByStore", query = "SELECT s FROM Stock s WHERE s.store = :store")
    , @NamedQuery(name = "Stock.findByTransactionType", query = "SELECT s FROM Stock s WHERE s.transactionType = :transactionType")
    , @NamedQuery(name = "Stock.findByRemark", query = "SELECT s FROM Stock s WHERE s.remark = :remark")})
public class Stock implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="item_id",nullable = false)
    private Product product;
    
    @Basic(optional = false)
    @Column(name = "quantity")
    private int quantity;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="invoice_id",insertable = false,updatable = false,nullable = true)
    private Invoice invoice;
    
    @Basic(optional = false)
    @Column(name = "update_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateAt;
    
    @Basic(optional = false)
    @Column(name = "created_at",nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="store_id",nullable = false)
    private Stores store;
    
    @ManyToOne
    @JoinColumn(name="user_id",nullable=false)
    private User user;
    
    @Column(name = "transaction_type")
    private String transactionType;
    
    @Column(name = "remark")
    private String remark;

    public Stock() {
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    
    public Stock(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    
    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }


    public Date getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Date updateAt) {
        this.updateAt = updateAt;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public Stores getStore() {
        return store;
    }

    public void setStore(Stores store) {
        this.store = store;
    }

    

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Stock)) {
            return false;
        }
        Stock other = (Stock) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ponospos.entities.Stock[ id=" + id + " ]";
    }
    
}
