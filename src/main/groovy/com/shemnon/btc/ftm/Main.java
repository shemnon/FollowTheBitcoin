package com.shemnon.btc.ftm;

import com.shemnon.btc.blockchaininfo.TXInfo;

/**
 * Created by shemnon on 27 Feb 2014.
 */
public class Main {
    
    public static void main(String... args) throws Throwable {
//        JsonRpcHttpClient client = new JsonRpcHttpClient(
//                new URL("http://localhost:8332/"));
//
//        Authenticator.setDefault(new Authenticator() {
//            protected PasswordAuthentication getPasswordAuthentication() {
//                return new PasswordAuthentication("user", "passphrase".toCharArray());
//            }
//        });

//        Object j = client.invoke("getinfo", null, Map.class);
//        System.out.println(j);
//
//        j = client.invoke("gettransaction", new Object[] {"3a1b9e330d32fef1ee42f8e86420d2be978bbe0dc5862f17da9027cf9e11f8c4"}, Map.class);
//        System.out.println(j);

//        TXInfo o = TXInfo.query("3a1b9e330d32fef1ee42f8e86420d2be978bbe0dc5862f17da9027cf9e11f8c4");
//        System.out.println(o.getInputAddresses());
//        System.out.println(o.getInputValue().doubleValue());
//        System.out.println(o.getOutputAddresses());
//        System.out.println(o.getOutputValue().doubleValue());
//        System.out.println(o.getFeePaid().doubleValue());
//        
//        BlockInfo b = o.getBlock();
//        System.out.println(b);
        //System.out.println(b.getTXs());
        
        TXInfo o = TXInfo.query("103bb04aa6da1bc6878295f4f520202fee3c68328e8a984cc572d67e2b9ab1f0");
//        System.out.println(o.getInputAddresses());
//        System.out.println(o.getInputValue().doubleValue());
//        System.out.println(o.getOutputAddresses());
//        System.out.println(o.getOutputValue().doubleValue());
//        System.out.println(o.getFeePaid().doubleValue());
//        
//        System.out.println(o.getBlock().keys());
        o.getBlock().getTXs().forEach(tx -> System.out.println(tx.getFeePaid()));
    }
}
