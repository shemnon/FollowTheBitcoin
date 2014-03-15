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

package com.shemnon.btc.blockchaininfo

import groovy.json.JsonSlurper;

/**
 * Created by shemnon on 3 Mar 2014.
 */
public class OfflineData {
    
    public static void loadOfflineData() {
        URL url = OfflineData.class.getResource("offline.json")
        def data = new JsonSlurper().parseText(url.text)

        data.each {e ->
            switch (e.type) {
                case 'AddressInfo' :
                    AddressInfo.fromJson(e.value);
                    break;
                case 'BlockInfo' :
                    BlockInfo.fromJson(e.value);
                    break;
                case 'TXInfo' :
                    TXInfo.fromJson(e.value);
                    break;
                default:
                    println "Unknown type $e.type - data not added"
            }
        }
    }
}
