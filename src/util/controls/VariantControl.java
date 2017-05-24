/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.controls;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javafx.geometry.Insets;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import ponospos.entities.Attribute;

/**
 *
 * @author Sawmtea
 */
 public class VariantControl extends FlowPane{

    private List<Attribute> attributes;
    public VariantControl() {
        this.setPrefSize(USE_PREF_SIZE, USE_PREF_SIZE);
        attributes=new ArrayList<>();
        this.setPadding(new Insets(5));
        
    }
    public void addAttribute(Attribute attr){
        attributes.add(attr);
        display(attr);
    }
    private void display(Attribute attr){
        Label text=new Label(attr.getName()+" : "+attr.getValue() +" ");
        FontAwesomeIconView icon=new FontAwesomeIconView(FontAwesomeIcon.TRASH);
        icon.setFill(Color.RED);
        text.setGraphic(icon);
        text.setContentDisplay(ContentDisplay.BOTTOM);
        
        this.getChildren().add(text);
        
        icon.setOnMouseClicked(e->{
            attributes.remove(attr);
            VariantControl.this.getChildren().remove(text);
        });
       
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Attribute> attributes) {
        this.attributes.clear();
        this.attributes.addAll(attributes);
    }
    
    
 }