/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ponospos.entities;

import java.io.Serializable;
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
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Sawmtea
 */
@Entity
@Table(name = "variants")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Variant.findAll", query = "SELECT v FROM Variant v")
    , @NamedQuery(name = "Variant.findById", query = "SELECT v FROM Variant v WHERE v.id = :id")
    , @NamedQuery(name = "Variant.findByName", query = "SELECT v FROM Variant v WHERE v.name = :name")})
public class Variant implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    
    @Basic(optional = false)
    @Column(name = "name")
    private String name;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;
    
    @OneToMany(mappedBy = "variant",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<VariantValue> values;

    public Variant() {
        name="";
    }

    public Variant(Integer id) {
        this.id = id;
    }

    public Variant(Integer id, String name, Product product) {
        this.id = id;
        this.name = name;
        this.product = product;
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

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
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
        if (!(object instanceof Variant)) {
            return false;
        }
        Variant other = (Variant) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ponospos.entities.Variants[ id=" + id + " ]";
    }
    
}
