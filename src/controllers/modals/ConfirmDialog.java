/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.modals;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXSnackbar;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Circle;

/**
 *
 * @author Sawmtea
 */
public class ConfirmDialog extends JFXDialog {
    public interface ConfirmDialogListener{
        public void onClickYes(Object obj);
    }
    private final Circle iconContainer;
    private final GridPane layout;
    private final Label title;
    private final Label message;
    private final JFXButton yesBtn;
    private final JFXButton noBtn;
    private ConfirmDialogListener listener;
    private Object model;
    public ConfirmDialog() {
        super();
        
        iconContainer=new Circle(10);
        title=new Label("Confirmation");
        message=new Label("Are you sure you want to delete?");
        
        yesBtn=new JFXButton("Yes");
        noBtn=new JFXButton("No");
        
        
        layout=new GridPane();
        layout.setHgap(10);
        layout.setVgap(10);
        layout.setPadding(new Insets(10));
        layout.add(iconContainer, 0, 0);
        layout.add(title, 1, 0);
        layout.add(message,0,1,1,3);
        layout.add(yesBtn,2,2);
        layout.add(noBtn,3,2);
        
        this.setContent(layout);
        yesBtn.setOnAction(e->{
            this.close(); 
            listener.onClickYes(model);
        });
        noBtn.setOnAction(e->this.close());
    }
    
    
    
    public JFXDialog title(String text){
        this.title.setText(text);
        return this;
    }
    
    public JFXDialog setMessage(String text){
        this.message.setText(text);
        return this;
    }

    public JFXDialog getConfirmDialog() {
        return this;
    }
    
    public JFXDialog setListener(ConfirmDialogListener listener){
        this.listener=listener;
        yesBtn.setOnAction(e->{
            this.close();
            listener.onClickYes(model);
        });
        return this;
    }
    public JFXDialog model(Object obj){
        this.model=obj;
        return this;
    }

    public void setModel(Object model) {
        this.model = model;
    }
    
    
    
   
}
