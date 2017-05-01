/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ponospos;

import com.jfoenix.controls.JFXDecorator;
import controllers.LoginController;
import controllers.MainController;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javax.persistence.EntityManagerFactory;
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
    private JFXDecorator decorator;
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
        
        decorator = new JFXDecorator(stage, mainController);
        decorator.setCustomMaximize(true);        
        
        this.primaryScene=new Scene(decorator,900,600);
        this.primaryStage.setScene(primaryScene); 
        this.primaryStage.setOnCloseRequest(e->{
            executors.shutdown();
            System.out.println("executors shutdown");
                });
        decorator.setOnCloseButtonAction(new Runnable() {
            @Override
            public void run() {
                executors.shutdown();
                Platform.exit();
                System.out.println("executor shutdown");
            }
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
        decorator.setContent(mainController);
        mainController.greetUser();
    }

    public void displayLoginScreen() {
        decorator.setContent(loginController);
    }
    
    
   
 
    
}
