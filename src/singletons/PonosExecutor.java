/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package singletons;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author Sawmtea
 */
public class PonosExecutor {
    private ExecutorService executor;
    
    private PonosExecutor() {
        if (executor==null) {
            executor=Executors.newFixedThreadPool(6);
        }
    }
    
    public static PonosExecutor getInstance() {
        return PonosExecutorHolder.INSTANCE;
    }
    
    private static class PonosExecutorHolder {

        private static final PonosExecutor INSTANCE = new PonosExecutor();
    }
    
    public ExecutorService getExecutor(){
        return executor;
    }
}
