/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ponospos.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author Sawmtea
 */
@Entity
@Table(name = "invoice_item")
@NamedQueries({
    @NamedQuery(name = "InvoiceItem.findAll", query = "SELECT i FROM InvoiceItem i")
    , @NamedQuery(name = "InvoiceItem.findById", query = "SELECT i FROM InvoiceItem i WHERE i.id = :id")
    , @NamedQuery(name = "InvoiceItem.findByInvoiceId", query = "SELECT i FROM InvoiceItem i WHERE i.invoice = :invoice")
    , @NamedQuery(name = "InvoiceItem.findByItemId", query = "SELECT i FROM InvoiceItem i WHERE i.item = :item")
    , @NamedQuery(name = "InvoiceItem.findByQuantity", query = "SELECT i FROM InvoiceItem i WHERE i.quantity = :quantity")})
public class InvoiceItem implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    
    @ManyToOne(optional = false)
    @JoinColumn(name = "invoice")
    private Invoice invoice;
    
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "item_id")
    private Product item;
    
    @Basic(optional = false)
    @Column(name = "quantity")
    private int quantity;

    public InvoiceItem() {
    }

    public InvoiceItem(Integer id) {
        this.id = id;
    }

    

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public Product getItem() {
        return item;
    }

    public void setItem(Product item) {
        this.item = item;
    }
  

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
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
        if (!(object instanceof InvoiceItem)) {
            return false;
        }
        InvoiceItem other = (InvoiceItem) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ponospos.entities.InvoiceItem[ id=" + id + " ]";
    }
    
}
