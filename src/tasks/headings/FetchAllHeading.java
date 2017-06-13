/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tasks.headings;

import java.util.List;
import javafx.concurrent.Task;
import jpa.HeadingJpa;
import ponospos.entities.ExpenseHeading;


public class FetchAllHeading  extends Task<List<ExpenseHeading>>{

    public ExpenseHeading head;
    @Override
    protected List<ExpenseHeading> call() throws Exception {
        return HeadingJpa.getAllExpenseHeading();
    }
    
}
