/*
 * Follow the  Bitcoin
 * Copyright (C) 2014  Danno Ferrin
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

package com.shemnon.btc.ftm

import groovy.json.JsonOutput

import java.text.DateFormat
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat

/**
 * Created by shemnon on 1 Mar 2014.
 */
class JsonBase {

    public static final DateFormat JSON_DATE = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
    public static final NumberFormat BTC_FORMAT = new DecimalFormat("\u0e3f ###,##0.### ### ###");
    public static final NumberFormat USD_FORMAT = new DecimalFormat("\$###,###,##0.00");
    
    def jsonSeed
    
    String toString() {
        return ""
    }
    
    static String shortHash(String hash) {
        return "${hash[0..6]}...${hash[-4..-1]}"
    }

    String dumpJson() {
        return "{\"type\":\"${this.class.simpleName}\", \"value\":${JsonOutput.toJson(jsonSeed)}}"
    }

}
