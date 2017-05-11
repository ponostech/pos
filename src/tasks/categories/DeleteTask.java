/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tasks.categories;

import javafx.concurrent.Task;
import jpa.CategoryJpa;
import ponospos.entities.Category;

/**
 *
 * @author Sawmtea
 */
public class DeleteTask extends Task<Category>{
        private Category category;
        
        @Override
        protected Category call() throws Exception {
            return CategoryJpa.deleteCategory(category);
        }
        public void setCategory(Category category){
            this.category=category;
        }
        
    }
