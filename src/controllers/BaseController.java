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
public abstract class BaseController {
    public abstract void initDependencies();
    public abstract void bindControls();
    public abstract void listenControls();
}
