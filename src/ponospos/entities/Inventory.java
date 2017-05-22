/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ponospos.entities;

import java.io.Serializable;
import java.math.BigDecimal;
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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Sawmtea
 */
@Entity
@Table(name = "inventory")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Inventory.findAll", query = "SELECT i FROM Inventory i")
    , @NamedQuery(name = "Inventory.findById", query = "SELECT i FROM Inventory i WHERE i.id = :id")
    , @NamedQuery(name = "Inventory.findBySku", query = "SELECT i FROM Inventory i WHERE i.sku = :sku")
    , @NamedQuery(name = "Inventory.findByNameCombination", query = "SELECT i FROM Inventory i WHERE i.nameCombination = :nameCombination")
    , @NamedQuery(name = "Inventory.findByStock", query = "SELECT i FROM Inventory i WHERE i.stock = :stock")
    , @NamedQuery(name = "Inventory.findByPrice", query = "SELECT i FROM Inventory i WHERE i.price = :price")})

@SequenceGenerator(name = "SKU",initialValue = 1,allocationSize = 100)
public class Inventory implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    
    @GeneratedValue(generator = "SKU",strategy = GenerationType.SEQUENCE)
    @Column(name = "sku")
    private String sku;
    
    @ManyToOne(fetch=FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinColumn(name = "product_id")
    private Product product;
    
    @Basic(optional = false)
    @Column(name = "name_combination")
    private String nameCombination;
    
    @Basic(optional = false)
    @Column(name = "stock")
    private int stock;
    
    @Basic(optional = false)
    @Column(name = "price")
    private BigDecimal price;

    public Inventory() {
        
    }

    public Inventory(Integer id) {
        this.id = id;
    }

    public Inventory(Integer id, String sku, Product product, String nameCombination, int stock, BigDecimal price) {
        this.id = id;
        this.sku = sku;
        this.product = product;
        this.nameCombination = nameCombination;
        this.stock = stock;
        this.price = price;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Product getProduct() {
        return product;
    }

    public void setProductId(Product product) {
        this.product = product;
    }

    public String getNameCombination() {
        return nameCombination;
    }

    public void setNameCombination(String nameCombination) {
        this.nameCombination = nameCombination;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
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
        if (!(object instanceof Inventory)) {
            return false;
        }
        Inventory other = (Inventory) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ponospos.entities.Inventory[ id=" + id + " ]";
    }
    
}
