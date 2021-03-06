/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ponospos.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Sawmtea
 */
@Entity
@Table(name = "stores")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Stores.findAll", query = "SELECT s FROM Stores s ")
    , @NamedQuery(name = "Stores.findById", query = "SELECT s FROM Stores s WHERE s.id = :id")
    , @NamedQuery(name = "Stores.findByName", query = "SELECT s FROM Stores s WHERE s.name LIKE :name")
    , @NamedQuery(name = "Stores.findByAddress", query = "SELECT s FROM Stores s WHERE s.address = :address")
    , @NamedQuery(name = "Stores.findByContact", query = "SELECT s FROM Stores s WHERE s.contact = :contact")
    , @NamedQuery(name = "Stores.findStock", query = "SELECT s.product FROM Stock s WHERE s.store = :store")
    , @NamedQuery(name = "Stores.findByCreatedAt", query = "SELECT s FROM Stores s WHERE s.createdAt = :createdAt")})
public class Stores implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    
    @Basic(optional = false)
    @Column(name = "name")
    private String name;
    
    
    @Column(name = "address",nullable = true)
    private String address;
    
    @Column(name = "contact",nullable = true)
    private String contact;
    
    @Basic(optional = false)
    @Column(name="active",nullable = false)
    private Boolean active;
    
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    
    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true,targetEntity = Stock.class)
    private List<Stock>stocks;
    

    public Stores() {
        name="";
        address="";
        contact="";
    }

    public Stores(Integer id) {
        this.id = id;
    }

    public Stores(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContact() {
        return contact;
    }

    public List<Stock> getStocks() {
        return stocks;
    }

    public void setStocks(List<Stock> stocks) {
        this.stocks = stocks;
    }

    
    public void setContact(String contact) {
        this.contact = contact;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
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
        if (!(object instanceof Stores)) {
            return false;
        }
        Stores other = (Stores) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return name;
    }
    
}
