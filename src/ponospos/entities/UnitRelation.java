/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ponospos.entities;

import java.io.Serializable;
import javax.persistence.Embeddable;

/**
 *
 * @author Sawmtea
 */
@Embeddable
public class UnitRelation implements Serializable{
    private String name;
    private double quantity;
    private String relation;

    public UnitRelation() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    @Override
    public String toString() {
        return "UnitRelation{" + "name=" + name + ", quantity=" + quantity + ", relation=" + relation + '}';
    }
    
    
    
}
