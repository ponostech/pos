/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.controls;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;

/**
 *
 * @author Sawmtea
 */
public class VariantValueField extends FlowPane{

    
    private List<String>data;
    private TextField inputField;
    public VariantValueField() {
        super();
        data=new ArrayList<>();
        
        inputField=new TextField();
        inputField.setMinSize(30, 30);
        inputField.setPrefSize(350, 50);
        inputField.setMaxSize(350, 50);
        inputField.setPromptText("Separate by comma");
        this.getChildren().add(0,inputField);
        
        this.setPadding(new Insets(5));
        this.setHgap(5);
        this.setVgap(5);
        
        
        inputField.setOnKeyReleased(e->handle(e));
    }
    private void handle(KeyEvent e){
        if (e.getCode()==KeyCode.COMMA) {
                String text = inputField.getText().substring(0,inputField.getText().length()-1);
                data.add(text);
                inputField.clear();
                FontAwesomeIconView icon=new FontAwesomeIconView(FontAwesomeIcon.TRASH);
                icon.setFill(Color.DARKRED);
                
                Label label=new Label(text,icon);
                
                VariantValueField.this.getChildren().add(label);
                icon.setOnMouseClicked(me->{
                    data.remove(label.getText());
                    VariantValueField.this.getChildren().remove(label);
                });
            }
    }
    
    
    
}
