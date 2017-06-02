/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 *
 * @author Sawmtea
 */
public class DateConverter {
    public static Date toUtilDate(LocalDate date){
        Date d = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
        return d;
    }
}
