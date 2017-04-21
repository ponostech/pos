/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.subcontrollers;

import java.util.function.Consumer;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;

public class ActionTableColumn<S>extends TableCell<S, Void> {
        private final Button delBtn;
        private final Button editBtn;
        private final HBox hb;

        public ActionTableColumn(String text, Consumer<Integer> action) {
            this.hb = new HBox(2.0);
            this.delBtn = new Button("Delete");
            this.delBtn.setTooltip(new Tooltip("Delete"));
//            this.delBtn.setGraphic((Node)new ImageView(new Image(this.getClass().getResourceAsStream("/image/icon/delSmall.png"))));
//            this.delBtn.setOnMouseEntered(e -> {
//                this.delBtn.setGraphic((Node)new ImageView(new Image(this.getClass().getResourceAsStream("/image/icon/delSmallRed.png"))));
//            }
//            );
//            this.delBtn.setOnMouseExited(e -> {
//                this.delBtn.setGraphic((Node)new ImageView(new Image(this.getClass().getResourceAsStream("/image/icon/delSmall.png"))));
//            }
//            );
            this.editBtn = new Button("Edit");
            this.editBtn.setTooltip(new Tooltip("Edit"));
//            this.editBtn.setGraphic((Node)new ImageView(new Image(this.getClass().getResourceAsStream("/image/icon/editicon.png"))));
//            this.editBtn.setOnMouseEntered(e -> {
//                this.editBtn.setGraphic((Node)new ImageView(new Image(this.getClass().getResourceAsStream("/image/icon/editiconRed.png"))));
//            }
//            );
//            this.editBtn.setOnMouseExited(e -> {
//                this.editBtn.setGraphic((Node)new ImageView(new Image(this.getClass().getResourceAsStream("/image/icon/editicon.png"))));
//            }
//            );
           
            this.delBtn.setOnAction(e -> {
            }
            );
            
            this.editBtn.setOnAction(e -> {
                
            }
            );
            this.hb.setAlignment(Pos.CENTER);
            this.hb.getChildren().addAll(this.editBtn, this.delBtn);
            this.setAlignment(Pos.CENTER);
        }

        protected void updateItem(Void item, boolean empty) {
            this.setGraphic(null);
            if (!empty) {
                this.setGraphic((Node)this.hb);
            }
        }
    }
