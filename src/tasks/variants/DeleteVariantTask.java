/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tasks.variants;

import javafx.concurrent.Task;
import jpa.VariantJpa;
import ponospos.entities.Variant;

/**
 *
 * @author Sawmtea
 */
public class DeleteVariantTask extends Task<Variant>{

    private Variant variant;
    @Override
    protected Variant call() throws Exception {
        return VariantJpa.deleteVariant(variant);
    }
    public void setVariant(Variant var){
        this.variant=var;
    }
    
}
