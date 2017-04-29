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
import ponospos.entities.User;
import singletons.Auth;
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
    @Override
    public void init() {
        try { 
            super.init();
            executors= PonosExecutor.getInstance().getExecutor();
            PonosExecutor.getInstance();
        } catch (Exception ex) {
            Logger.getLogger(PonosPos.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    @Override
    public void start(Stage stage){
        
        mainController=new MainController(this);
        mainController.initDependencies();
        mainController.initControls();
        mainController.hookupEvent();
        mainController.bindControls();
        
        loginController =new LoginController();
        loginController.setApp(this);
        
        this.primaryStage=stage;
        
        this.primaryScene=new Scene(loginController,900,600);
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
    
    public void displayMainScreen() {
        primaryScene.setRoot(mainController);
    }

    public void displayLoginScreen() {
        primaryScene.setRoot(loginController);
    }
    
    
   
 
    
}
