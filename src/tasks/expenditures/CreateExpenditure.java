/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tasks.expenditures;

import javafx.concurrent.Task;
import jpa.ExpenditureJpa;
import ponospos.entities.Expenditure;

/**
 *
 * @author Sawmtea
 */
public class CreateExpenditure extends Task<Expenditure> {

    public Expenditure expenditure;
    @Override
    protected Expenditure call() throws Exception {
        return ExpenditureJpa.createExpenditure(expenditure);
    }
    
}
