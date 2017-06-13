/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tasks.headings;

import javafx.concurrent.Task;
import jpa.HeadingJpa;
import ponospos.entities.ExpenseHeading;

/**
 *
 * @author Sawmtea
 */
public class CreateHeading  extends Task<ExpenseHeading>{

    public ExpenseHeading head;
    @Override
    protected ExpenseHeading call() throws Exception {
        return HeadingJpa.createExpenseHeading(head);
    }
    
}
