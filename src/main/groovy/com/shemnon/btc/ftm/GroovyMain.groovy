package com.shemnon.btc.ftm

import groovy.json.JsonSlurper

URL tx = new URL("https://blockchain.info/rawtx/3a1b9e330d32fef1ee42f8e86420d2be978bbe0dc5862f17da9027cf9e11f8c4")
String txtxt = tx.text

def slurper = new JsonSlurper()
def result = slurper.parseText(txtxt)

println result.inputs*.prevout.addr


println slurper.parseText(new URL(""))

Object j = client.invoke("getinfo", null, Map.class);
System.out.println(j);

j = client.invoke("gettransaction", ["3a1b9e330d32fef1ee42f8e86420d2be978bbe0dc5862f17da9027cf9e11f8c4"], Map.class);
System.out.println(j);
