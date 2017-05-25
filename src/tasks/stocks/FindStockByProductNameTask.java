/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tasks.stocks;

import java.util.List;
import javafx.concurrent.Task;
import jpa.StockJpa;
import ponospos.entities.Stock;

/**
 *
 * @author Sawmtea
 */
public class FindStockByProductNameTask extends Task<List<Stock>> {

    private String param="";
    @Override
    protected List<Stock> call() throws Exception {
        return StockJpa.findStockByName(param);
    }

    public void setParam(String param) {
        this.param = param;
    }
    
    
}
