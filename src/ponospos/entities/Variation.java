/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ponospos.entities;

import java.io.Serializable;
import javax.persistence.Basic;
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
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Sawmtea
 */
@Entity
@Table(name = "variations")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Variation.findAll", query = "SELECT v FROM Variation v")
    , @NamedQuery(name = "Variation.findById", query = "SELECT v FROM Variation v WHERE v.id = :id")
    , @NamedQuery(name = "Variation.findByCategories", query = "SELECT v FROM Variation v WHERE v.category = :category")
    , @NamedQuery(name = "Variation.findByName", query = "SELECT v FROM Variation v WHERE v.name = :name")
    , @NamedQuery(name = "Variation.findByValue", query = "SELECT v FROM Variation v WHERE v.value = :value")})
public class Variation implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    
    @Basic(optional = false)
    @Column(name = "name")
    private String name;
    
    @Basic(optional = false)
    @Column(name = "value")
    private String value;
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="categories")
    private Category category;

    public Variation() {
    }

    public Variation(Integer id) {
        this.id = id;
    }

    public Variation(Integer id, Category categories, String name, String value) {
        this.id = id;
        this.category = categories;
        this.name = name;
        this.value = value;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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
        if (!(object instanceof Variation)) {
            return false;
        }
        Variation other = (Variation) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ponospos.entities.Variation[ id=" + id + " ]";
    }
    
}
