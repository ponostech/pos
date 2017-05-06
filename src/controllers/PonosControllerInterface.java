/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

/**
 *
 * @author Sawmtea
 */
public interface PonosControllerInterface {
    public void initDependencies();
    public void initControls();
    public void bindControls();
    public void hookupEvent();
    public void controlFocus();
}
