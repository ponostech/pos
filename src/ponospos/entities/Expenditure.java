/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ponospos.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Sawmtea
 */
@Entity
@Table(name="expenditure")
@NamedQueries({
    @NamedQuery(name = "Expenditure.findAll", query = "SELECT e FROM Expenditure e")
    , @NamedQuery(name = "Expenditure.findById", query = "SELECT c FROM Expenditure c WHERE c.id = :id")
    , @NamedQuery(name = "Expenditure.findByDate", query = "SELECT c FROM Expenditure c WHERE c.createdAt = :param")
})
public class Expenditure implements Serializable{
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    
    @Column(name = "description",nullable = true)
    private String description;
    
    @Column(name="amount",precision = 10,scale = 2,nullable = false)
    private BigDecimal amount;
    
    @ManyToOne()
    @JoinColumn(name = "head_id",nullable = true)
    private ExpenseHeading head;
    
    @ManyToOne
    @JoinColumn(name="create_user_id",nullable = false)
    private User createdBy;
    
    @ManyToOne
    @JoinColumn(name = "update_user_id",nullable = true)
    private User editedBy;
    
    @Column(name="created_at",nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    
    @Column(name="edited_at",nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date editedAt;

    public Expenditure() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ExpenseHeading getHead() {
        return head;
    }

    public void setHead(ExpenseHeading head) {
        this.head = head;
    }

    
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public User getEditedBy() {
        return editedBy;
    }

    public void setEditedBy(User editedBy) {
        this.editedBy = editedBy;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getEditedAt() {
        return editedAt;
    }

    public void setEditedAt(Date editedAt) {
        this.editedAt = editedAt;
    }
    

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + Objects.hashCode(this.id);
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
        final Expenditure other = (Expenditure) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Expenditure{" + "id=" + id + '}';
    }
    
    
}
