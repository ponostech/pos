/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import controllers.customers.CustomersController;
import controllers.subcontrollers.UserMenuController;
import controllers.users.UsersController;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

/**
 *
 * @author Sawmtea
 */
public class SideBarController extends VBox {
    public interface SideBarListener{
        public void onUserMenuClick();
        public void onCustomerMenuClick();
        public void onDashboardMenuClick();
        public void onProductMenuClick();
        public void onCategoryMenuClick();
        public void onSupplierMenuClick();
        public void onStoreMenuClick();

        public void onStockControlClick();

        public void onSellMenuClick();

        public void onSellHistoryMenuClick();

        public void onSettingMenuClick();

        public void onInvoiceClick();
        public void onStockTransferClick();
        public void onPaymentHistoryClick();
    }
   
    private DashboardController dashboard;
    private BorderPane container;
    private UserMenuController userMenuController;
    private UsersController userController;
    private CustomersController customerController;
    private SideBarListener listener;
    
    public SideBarController(){
        try {
            FXMLLoader loader=new FXMLLoader();
            loader.setLocation(this.getClass().getResource("/views/side_bar.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
            this.getStyleClass().add("side-bar");          
        } catch (IOException ex) {
            Logger.getLogger(SideBarController.class.getName()).log(Level.SEVERE, null, ex);
        }
       
    }
   
    void setDashboard(DashboardController dashboard) {
        this.dashboard=dashboard;
    }
    
    public void setListener(SideBarListener listener){
        this.listener=listener;
    }
    
    public void displayDashBoard(){
        listener.onDashboardMenuClick();
    }
    
    @FXML
    private void onCategoryMenuClick(ActionEvent event) {
        listener.onCategoryMenuClick();
    }

    @FXML
    private void onCustomerMenuClick(ActionEvent event) {
        listener.onCustomerMenuClick();
    }

    @FXML
    private void onSupplierMenuClick(ActionEvent event) {
       listener.onSupplierMenuClick();
    }
    @FXML
    private void onUserMenuClick(ActionEvent event){
        listener.onUserMenuClick();
    }
    @FXML
    private void onStoreMenuClick(ActionEvent event){
        listener.onStoreMenuClick();
    }
     @FXML
    public void onProductMenuClick(ActionEvent e){
        listener.onProductMenuClick();
    }
    @FXML
    private void onDashboardMenuClick(ActionEvent event){
        listener.onDashboardMenuClick();
    }
    @FXML
    private void onStockControlMenuClick(ActionEvent event){
        listener.onStockControlClick();
    }
    @FXML
    private void onSellMenuClick(ActionEvent event){
        listener.onSellMenuClick();
    }
    @FXML
    private void onSellHistoryMenuClick(ActionEvent event){
        listener.onSellHistoryMenuClick();
    }
    @FXML
    private void onSettingMenuClick(ActionEvent event){
        listener.onSettingMenuClick();
    }
    @FXML
    private void onInvoiceMenuClick(ActionEvent event){
        listener.onInvoiceClick();
    }
    @FXML
    private void onStockTransferMenuClick(ActionEvent event){
        listener.onStockTransferClick();
    }
    @FXML
    private void onPaymentHistoryClick(ActionEvent event){
        listener.onPaymentHistoryClick();
    }

}
