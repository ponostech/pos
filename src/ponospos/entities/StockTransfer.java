/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ponospos.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Sawmtea
 */
@Entity
@Table(name="stock_transfer")
public class StockTransfer implements Serializable{

    @Id
    @SequenceGenerator(name = "generator", sequenceName = "ST")
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "generator")
    private Long id;
    

    @ManyToOne(targetEntity = User.class)
        @JoinColumn(nullable = false,name = "user_id")
    private User user;
    
    @ManyToOne(targetEntity = Stores.class)
        @JoinColumn(nullable = false,name="from_store")
    private Stores from;
    
    @ManyToOne(targetEntity = Stores.class)
    @JoinColumn(nullable=false,name="to_store")
    private Stores to;
    
    @OneToMany(mappedBy = "transfer",orphanRemoval = true,cascade = CascadeType.ALL)
    private List<StockTransferItem> items;
    
    @Column(nullable = false, name = "transfer_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    public StockTransfer() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Stores getFrom() {
        return from;
    }

    public void setFrom(Stores from) {
        this.from = from;
    }

    public Stores getTo() {
        return to;
    }

    public void setTo(Stores to) {
        this.to = to;
    }

    public List<StockTransferItem> getItems() {
        return items;
    }

    public void setItems(List<StockTransferItem> items) {
        this.items = items;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
    
    

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.id);
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
        final StockTransfer other = (StockTransfer) obj;
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
    
}
