/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author Sawmtea
 */
public class PasswordGenerator {
    public static String generateToMD5(String pwd) {
        try{
            MessageDigest digest=MessageDigest.getInstance("MD5");
            byte[] cipher = digest.digest(pwd.getBytes());
            StringBuffer bf=new StringBuffer();
            for (int i = 0; i < cipher.length; i++) {
                bf.append(Integer.toHexString((cipher[i] & 0xFF) | 0x100).substring(1,3));
            }
            return bf.toString();
        }catch(NoSuchAlgorithmException e){
            return null;
        }
    }
}
