/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tasks.categories;

import java.util.List;
import javafx.concurrent.Task;
import jpa.CategoryJpa;
import ponospos.entities.Category;

/**
 *
 * @author Sawmtea
 */
public class SearchCategoryByNameTask extends Task<List<Category>>{

    private String name;
    @Override
    protected List<Category> call() throws Exception {
        return CategoryJpa.findCategoryName(name);
    }
    public void setName(String name){
        this.name=name;
    }
    
}
