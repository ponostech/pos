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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;

/**
 *
 * @author Sawmtea
 */
public class PonosTextfield extends TextField{
    private List<String> data;
    private HBox hbox;

    public PonosTextfield() {
        super();
        data=new ArrayList<>();
        hbox=new HBox(2);
        hbox.getChildren().add(this);
        this.setPrefSize(200, 50);
        this.setOnKeyPressed(e->{
            System.out.println(e.getCode());
            if (e.getCode()==KeyCode.COMMA) {
                System.out.println("hadle");
                String str=this.getText().trim();
                data.add(str);
                Label label=new Label();
                FontAwesomeIconView icon=new FontAwesomeIconView(FontAwesomeIcon.TRASH);
                icon.setOnMouseClicked(v->{
                    data.remove(str);
                    hbox.getChildren().remove(label);
                });
                label.setText(str);
                label.setGraphic(icon);
                hbox.getChildren().add(label);
            }
        });
    }

    public PonosTextfield(String text) {
        super(text);
        data=new ArrayList<>();
        hbox=new HBox(2);
        
    }
    public List<String> getData(){
        return data;
    }
    public void clearAll(){
        this.setText("");
        hbox.getChildren().clear();
        data.clear();
    }
    
}
