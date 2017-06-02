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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.eclipse.persistence.jpa.config.Cascade;
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
    , @NamedQuery(name = "Invoice.findByTotal", query = "SELECT i FROM Invoice i WHERE i.total = :total")
    , @NamedQuery(name = "Invoice.findByPaymentDate", query = "SELECT i FROM Invoice i WHERE i.invoiceDate = :paymentDate")
    , @NamedQuery(name = "Invoice.findByTax", query = "SELECT i FROM Invoice i WHERE i.tax = :tax")
    , @NamedQuery(name = "Invoice.findBetweenDates", query = "SELECT i FROM Invoice i WHERE i.invoiceDate BETWEEN :from AND :to")
    })
public class Invoice implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    
    @ManyToOne()
    @JoinColumn(name = "customer_id",nullable = true)
    private Customer customer;
    
    @ManyToOne()
    @JoinColumn(name = "user_id",nullable=false)
    private User soldBy;
    
    @Basic(optional = false)
    @Column(name = "discount",nullable=false)
    private BigDecimal discount;
    
//    @Basic(optional = false)
//    @Column(name = "payment_amount")
//    private BigDecimal paymentAmount;
    
    @Basic(optional = false)
    @Column(name = "total",scale =2,precision = 10)
    private BigDecimal total;
  
    @Basic(optional = false)
    @Column(name = "invoice_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date invoiceDate;
    
    @Basic(optional = false)
    @Column(name = "tax",scale = 2,precision = 10)
    private BigDecimal tax;
    
    @Basic(optional = false)
    @Column(name = "status",nullable = true)
    private String status;
    
    @Lob
    @Column(name = "remark",nullable=true)
    private String remark;
    
    @OneToMany(mappedBy = "invoice",orphanRemoval = true,cascade =CascadeType.ALL ,targetEntity = Stock.class)
    private List<Stock>stocks;
    
    @OneToMany(mappedBy = "invoice",orphanRemoval = true,cascade = CascadeType.ALL,targetEntity = InvoiceItem.class)
    private List<InvoiceItem>invoiceItem;

    @OneToOne(mappedBy = "invoice",orphanRemoval = true,cascade = CascadeType.ALL,targetEntity = Payment.class)
    private Payment payment;
    
    public Invoice() {
        this.discount=new BigDecimal("0");
//        this.paymentAmount=new BigDecimal("0");
        this.soldBy=Auth.getInstance().getUser();
        
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

   
    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public BigDecimal getTax() {
        return tax;
    }

    public void setTax(BigDecimal tax) {
        this.tax = tax;
    }

    

    public Date getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(Date invoiceDate) {
        this.invoiceDate = invoiceDate;
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
