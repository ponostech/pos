/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tasks.settings;

import java.util.List;
import javafx.concurrent.Task;
import jpa.UnitJpa;
import ponospos.entities.Unit;

/**
 *
 * @author Sawmtea
 */
public class FetchAllUnit extends Task<List<Unit>>{

    public Unit unit;
    @Override
    protected List<Unit> call() throws Exception {
        return UnitJpa.getAllUnit();
    }
    
}
