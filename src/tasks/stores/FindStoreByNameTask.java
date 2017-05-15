/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tasks.stores;

import java.util.List;
import javafx.concurrent.Task;
import jpa.StoreJpa;
import ponospos.entities.Stores;

/**
 *
 * @author Sawmtea
 */
public class FindStoreByNameTask extends Task<List<Stores>> {

    private String param="";
    
    @Override
    protected List<Stores> call() throws Exception {
        return StoreJpa.findStoreByName(param);
    }
    public void setParam(String param){
        this.param=param;
    }
    
}
