/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ponospos;

import controllers.LoginController;
import controllers.MainController;
import controllers.MainDrawer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import singletons.PonosExecutor;



/**
 *
 * @author Sawmtea
 */
public class PonosPos extends Application {
    private ExecutorService executors;
    private EntityManagerFactory factory;
    private Stage primaryStage;
    private Scene primaryScene;
    private LoginController loginController;
    private MainController mainController;
    private MainDrawer mainScreen;
    @Override
    public void init() {
        try { 
            super.init();
            executors= PonosExecutor.getInstance().getExecutor();
            factory=Persistence.createEntityManagerFactory("PonosPosPU");
        } catch (Exception ex) {
            Logger.getLogger(PonosPos.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    @Override
    public void start(Stage stage){
        loginController =new LoginController();
        mainScreen=new MainDrawer();
        mainController=new MainController();
        loginController.setApp(this);
        this.primaryStage=stage;
        this.primaryScene=new Scene(mainController,600,400);
        this.primaryStage.setScene(primaryScene); 
        this.primaryStage.setOnCloseRequest(e->{
            executors.shutdown();
            System.out.println("executors shutdown");
                });
        this.primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    

    public ExecutorService getExecutors() {
        return executors;
    }
    public void displayMainScreen() {
        primaryScene.setRoot(mainController);
    }
    
 
    
}
