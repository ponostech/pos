/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tasks.variants;

import java.util.List;
import javafx.concurrent.Task;
import jpa.VariantJpa;
import ponospos.entities.Variant;

/**
 *
 * @author Sawmtea
 */
public class FetchAllVariant extends Task<List<Variant>>{

    @Override
    protected List<Variant> call() throws Exception {
        return VariantJpa.getAllVariant();
    }
    
    
}
