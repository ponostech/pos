/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.prefs.Preferences;

/**
 *
 * @author Sawmtea
 */
public class PonosPreference {
     private  Preferences pref=Preferences.userRoot().node(this.getClass().getName());
     public static String TAX_VALUE="TAX_VALUE";
     public static String PERCENT_OPTION="PERCENT_OPTION";
     
     public void setTax(float value){
         pref.putFloat(TAX_VALUE, value);
     }
     public float getTax(){
         return pref.getFloat(TAX_VALUE, 0);
     }
     
     public  boolean getDiscountOption(){
         return pref.getBoolean(PERCENT_OPTION, false);
     }
     public  void setDiscountOption(boolean value){
          pref.putBoolean(PERCENT_OPTION, value);
     }
     
    
}
