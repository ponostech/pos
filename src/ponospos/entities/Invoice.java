/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ponospos.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
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
import singletons.Auth;

/**
 *
 * @author Sawmtea
 */
@Entity
@Table(name = "invoice")
@NamedQueries({
    @NamedQuery(name = "Invoice.findAll", query = "SELECT i FROM Invoice i")
    , @NamedQuery(name = "Invoice.findById", query = "SELECT i FROM Invoice i WHERE i.id = :id")
    , @NamedQuery(name = "Invoice.findByCustomer", query = "SELECT i FROM Invoice i WHERE i.customer = :customer")
    , @NamedQuery(name = "Invoice.findByUser", query = "SELECT i FROM Invoice i WHERE i.soldBy = :user")
    , @NamedQuery(name = "Invoice.findByDiscount", query = "SELECT i FROM Invoice i WHERE i.discount = :discount")
    , @NamedQuery(name = "Invoice.findByPaymentAmount", query = "SELECT i FROM Invoice i WHERE i.paymentAmount = :paymentAmount")
    , @NamedQuery(name = "Invoice.findByTotal", query = "SELECT i FROM Invoice i WHERE i.total = :total")
    , @NamedQuery(name = "Invoice.findByPaymentDate", query = "SELECT i FROM Invoice i WHERE i.paymentDate = :paymentDate")
    , @NamedQuery(name = "Invoice.findByTax", query = "SELECT i FROM Invoice i WHERE i.tax = :tax")
    , @NamedQuery(name = "Invoice.findByStatus", query = "SELECT i FROM Invoice i WHERE i.status = :status")})
public class Invoice implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    
    @ManyToOne()
    @JoinColumn(name = "customer_id")
    private Customer customer;
    
    @ManyToOne()
    @JoinColumn(name = "user_id")
    private User soldBy;
    
    @Basic(optional = false)
    @Column(name = "discount")
    private BigDecimal discount;
    
    @Basic(optional = false)
    @Column(name = "payment_amount")
    private BigDecimal paymentAmount;
    
    @Basic(optional = false)
    @Column(name = "total")
    private BigDecimal total;
    
    @Basic(optional = false)
    @Column(name = "inv_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date soldDate;
    
    @Basic(optional = false)
    @Column(name = "payment_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date paymentDate;
    
    @Basic(optional = false)
    @Column(name = "tax")
    private float tax;
    
    @Column(name = "status")
    private String status;
    
    @Lob
    @Column(name = "remark")
    private String remark;
    
    @OneToMany(mappedBy = "invoice")
    private List<Stock>stocks;
    
    @OneToMany(fetch = FetchType.EAGER,mappedBy = "invoice",orphanRemoval = true)
    private List<InvoiceItem>invoiceItem;

    public Invoice() {
        this.discount=new BigDecimal("0");
        this.paymentAmount=new BigDecimal("0");
        this.soldBy=Auth.getInstance().getUser();
        this.tax=0.0f;
    }

    public Invoice(Integer id) {
        this.id = id;
    }

   

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public User getSoldBy() {
        return soldBy;
    }

    public void setSoldBy(User soldBy) {
        this.soldBy = soldBy;
    }

    
    public List<Stock> getStocks() {
        return stocks;
    }

    public void setStocks(List<Stock> stocks) {
        this.stocks = stocks;
    }

    public List<InvoiceItem> getInvoiceItem() {
        return invoiceItem;
    }

    public void setInvoiceItem(List<InvoiceItem> invoiceItem) {
        this.invoiceItem = invoiceItem;
    }
    
   
    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public BigDecimal getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(BigDecimal paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public Date getSoldDate() {
        return soldDate;
    }

    public void setSoldDate(Date soldDate) {
        this.soldDate = soldDate;
    }

    public float getTax() {
        return tax;
    }

    public void setTax(float tax) {
        this.tax = tax;
    }

    
   

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

  
    public void setTax(long tax) {
        this.tax = tax;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
        if (!(object instanceof Invoice)) {
            return false;
        }
        Invoice other = (Invoice) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return Integer.toString(this.id);
    }
    
}
