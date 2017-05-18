/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ponospos.entities;

import com.jfoenix.controls.JFXTextField;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import singletons.Auth;

/**
 *
 * @author Sawmtea
 */
@Entity
@Table(name = "products")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Product.findAll", query = "SELECT p FROM Product p")
    , @NamedQuery(name = "Product.findById", query = "SELECT p FROM Product p WHERE p.id = :id")
    , @NamedQuery(name = "Product.findByName", query = "SELECT p FROM Product p WHERE p.name LIKE :name")
    , @NamedQuery(name = "Product.findByBarcode", query = "SELECT p FROM Product p WHERE p.barcode = :barcode")
    , @NamedQuery(name = "Product.findByCostPrice", query = "SELECT p FROM Product p WHERE p.costPrice = :costPrice")
    , @NamedQuery(name = "Product.findBySellingPrice", query = "SELECT p FROM Product p WHERE p.sellingPrice = :sellingPrice")
    , @NamedQuery(name = "Product.findByCategory", query = "SELECT p FROM Product p WHERE p.category = :category")
    , @NamedQuery(name = "Product.findByIsActive", query = "SELECT p FROM Product p WHERE p.isActive = :isActive")
    , @NamedQuery(name = "Product.findByAddedBy", query = "SELECT p FROM Product p WHERE p.addedBy = :addedBy")
    , @NamedQuery(name = "Product.findByEdittedBy", query = "SELECT p FROM Product p WHERE p.edittedBy = :edittedBy")
    , @NamedQuery(name = "Product.findByCreatedAt", query = "SELECT p FROM Product p WHERE p.createdAt = :createdAt")})
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    
    @Basic(optional = false)
    @Column(name = "name")
    private String name;
    
    @Column(name = "barcode")
    private String barcode;
    
    @Lob
    @Column(name = "description")
    @Basic(optional = false)
    private String description;
    
    
    @Column(name = "cost_price")
    private BigDecimal costPrice;
    
    @Basic(optional = false)
    @Column(name = "selling_price")
    private BigDecimal sellingPrice;
    
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="category_id",nullable = true)
    private Category category;
    
    @Basic(optional = false)
    @Column(name = "is_active")
    private boolean isActive;
    
    @Basic(optional=false)
    @Column(name="tax_include")
    private boolean includeTax;
    
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="added_by",nullable = true)
    private User addedBy;
    
    @ManyToOne(fetch=FetchType.EAGER )
    @JoinColumn(name="editted_by",nullable = true)
    private User edittedBy;
    
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    
    @OneToMany(mappedBy="product",cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    List<Attribute> attributes;

    public Product() {
        this.name="";
        this.barcode="";
        this.description="";
        this.addedBy=Auth.getInstance().getUser();
        this.createdAt=new Date(System.currentTimeMillis());
        
        this.costPrice=new BigDecimal("0");
        this.sellingPrice=new BigDecimal("0");
    }

    public Product(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getDescription() {
        return description;
    }
    

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(BigDecimal costPrice) {
        this.costPrice = costPrice;
    }

    public BigDecimal getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(BigDecimal sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public User getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(User addedBy) {
        this.addedBy = addedBy;
    }

    public User getEdittedBy() {
        return edittedBy;
    }

    public void setEdittedBy(User edittedBy) {
        this.edittedBy = edittedBy;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }

    public boolean isIncludeTax() {
        return includeTax;
    }

    public void setIncludeTax(boolean includeTax) {
        this.includeTax = includeTax;
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
        if (!(object instanceof Product)) {
            return false;
        }
        Product other = (Product) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ponospos.entities.Product[ id=" + id + " ]";
    }
    
   
}
