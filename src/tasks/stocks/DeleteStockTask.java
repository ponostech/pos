/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tasks.stocks;

import javafx.concurrent.Task;
import jpa.StockJpa;
import ponospos.entities.Stock;

/**
 *
 * @author Sawmtea
 */
public class DeleteStockTask extends Task<Stock>{
    private Stock stock;

    public void setStock(Stock stock) {
        this.stock = stock;
    }
    
    @Override
    protected Stock call() throws Exception {
        return StockJpa.deleteStock(stock);
    }
}
