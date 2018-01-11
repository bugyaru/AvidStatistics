/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bug;

import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

/**
 *
 * @author bkantor
 */
public class AvidStatistics {

    /**
     * @param args the command line arguments
     */
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

    public static void main(String[] args) throws UnsupportedEncodingException, ScriptException, TransformerConfigurationException, TransformerException, FileNotFoundException {
        Date lastCheck = new Date();
        SOAPquery Q = null;
        command cmd = new command();
        notify ntf = new notify();
        ArrayList<Integer> reportIndexes = new ArrayList<Integer>();
        ScriptEngine engineJS = null;
        JSONObject config = getGfg(args);
        JSONObject gen = config.getJSONObject("General");
        StreamSource source;
        StreamSource stylesource;

        JSONObject act = null;
        JSONArray Actions = null;
        int actionCount = 1;
        if ("org.json.JSONObject".equals(config.get("Action").getClass().getCanonicalName())) {
            act = config.getJSONObject("Action");
        } else {
            Actions = config.getJSONArray("Action");
            actionCount = Actions.length();
        }
        for (int i = 0; i < actionCount; i++) {
            if ("org.json.JSONArray".equals(config.get("Action").getClass().getCanonicalName())) {
                act = Actions.getJSONObject(i);
            }
            Q = new SOAPquery();
            Q.setUrl(gen.getString("AvidWFE_Url"));
            Q.setAcceptEncoding(gen.getString("AvidWFE_AcceptEncoding"));
            Q.setContentType(gen.getString("AvidWFE_ContentType"));
            querylogic(gen, act, Q);
        }
    }

    public static JSONObject getGfg(String[] args) {
        JSONObject out = null;
        File config = new File("config.xml");
        try {
            if (!"".equals(args[0]) && new File(args[0]).exists()) {
                config = new File(args[0]);
            }
        } catch (Exception e) {
        }
        if (config.exists()) {
            try {
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(config);
                DOMSource source = new DOMSource(doc);
                StringWriter strb = new StringWriter();
                StreamResult result = new StreamResult(strb);
                TransformerFactory transFactory = TransformerFactory.newInstance();
                Transformer transformer = transFactory.newTransformer();
                transformer.transform(source, result);
                out = XML.toJSONObject(strb.toString()).getJSONObject("Config");
            } catch (Exception e) {
                System.err.println("Error: 0x03-Error create xml structure." );
            }
        } else {
            System.err.println("Error: 0x04-Error read config.");
        }
        return out;
    }
    
    static private void querylogic(JSONObject gen, JSONObject act, SOAPquery Q) throws UnsupportedEncodingException {
        String SOAPVarNameact = "";
        String SOAPVarNames = "";
        String SOAPVarValueact = "";
        String SOAPVarValues = "";
        String SOAPFilteract = "";
        String SOAPFilter = "";

        try {
            SOAPVarNameact = act.getJSONObject("SOAPNames").getString("action");
        } catch (JSONException e) {
        }
        try {
            SOAPVarNames = act.getJSONObject("SOAPNames").getString("content");
        } catch (JSONException e) {
        }
        try {
            SOAPVarValueact = act.getJSONObject("SOAPValues").getString("action");
        } catch (JSONException e) {
        }
        try {
            SOAPVarValues = act.getJSONObject("SOAPValues").getString("content");
        } catch (JSONException e) {
        }
        try {
            SOAPFilteract = act.getJSONObject("SOAPFilter").getString("action");
        } catch (JSONException e) {
        }
        try {
            SOAPFilter = act.getJSONObject("SOAPFilter").getString("content");
        } catch (JSONException e) {
        }

        if (!SOAPFilter.isEmpty() && !SOAPFilteract.isEmpty()) {
            Q.setSOAPAction(SOAPFilteract);
            Date currentDate = new Date();
            Q.setFilterBody(SOAPFilter.replace("{currentTime}", dateFormat.format(currentDate)).replace("{checkTime}", dateFormat.format(new Date(currentDate.getTime() - (long) gen.getInt("CheckTime")))));
            try {
                Q.setSearchTag(act.getJSONObject("SOAPFilter").getString("SearchTag"));
            } catch (Exception e) {
                Q.setSearchTag(gen.getJSONObject("SOAPFilter").getString("SearchTag"));
            }
            try {
                Q.setSearchTagChild(act.getJSONObject("SOAPFilter").getString("SearchTagChild"));
            } catch (Exception e) {
                Q.setSearchTagChild(gen.getJSONObject("SOAPFilter").getString("SearchTagChild"));
            }
            try {
                Q.setTagPrefix(act.getJSONObject("SOAPFilter").getString("TagPrefix"));
            } catch (Exception e) {
                Q.setTagPrefix(gen.getJSONObject("SOAPFilter").getString("TagPrefix"));
            }
            try {
                Q.getFilterDataArray();
            } catch (Exception ex) {
                Logger.getLogger(AvidStatistics.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (!SOAPVarNameact.isEmpty() && !SOAPVarValueact.isEmpty() && !SOAPVarNames.isEmpty() && !SOAPVarValues.isEmpty()) {
            HashMap<String, String> buf;
            //for (HashMap<String, String> map : Q.wfData) {
            for (int in = 0; in < Q.wfData.size(); in++) {
                Q.setSOAPAction(SOAPVarNameact);
                try {
                    Q.setSearchTag(act.getJSONObject("SOAPNames").getString("SearchTag"));
                } catch (Exception e) {
                    Q.setSearchTag(gen.getJSONObject("SOAPNames").getString("SearchTag"));
                }
                try {
                    Q.setSearchTagChild(act.getJSONObject("SOAPNames").getString("SearchTagChild"));
                } catch (Exception e) {
                    Q.setSearchTagChild(gen.getJSONObject("SOAPNames").getString("SearchTagChild"));
                }
                try {
                    Q.setTagPrefix(act.getJSONObject("SOAPNames").getString("TagPrefix"));
                } catch (Exception e) {
                    Q.setTagPrefix(gen.getJSONObject("SOAPNames").getString("TagPrefix"));
                }
                Q.setFilterBody(replaceBodyTag(SOAPVarNames, Q.wfData.get(in)));
                buf = Q.getFilterDataMap();
                Q.setSOAPAction(SOAPVarValueact);
                try {
                    Q.setSearchTag(act.getJSONObject("SOAPValues").getString("SearchTag"));
                } catch (Exception e) {
                    Q.setSearchTag(gen.getJSONObject("SOAPValues").getString("SearchTag"));
                }
                try {
                    Q.setSearchTagChild(act.getJSONObject("SOAPValues").getString("SearchTagChild"));
                } catch (Exception e) {
                    Q.setSearchTagChild(gen.getJSONObject("SOAPValues").getString("SearchTagChild"));
                }
                try {
                    Q.setTagPrefix(act.getJSONObject("SOAPValues").getString("TagPrefix"));
                } catch (Exception e) {
                    Q.setTagPrefix(gen.getJSONObject("SOAPValues").getString("TagPrefix"));
                }
                Q.setFilterBody(replaceBodyTag(SOAPVarValues, Q.wfData.get(in)));
                buf = Q.joinDataMaps(buf, Q.getFilterDataMap());
                Q.wfData.get(in).putAll(buf);
                //System.out.println("com.bug.AvidStatistics.main()");
            }
        }
    }

    static public String replaceBodyTag(String body, HashMap<String, String> map) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            String patern = "";
            try {
                patern = "{" + key + "}";
                body = body.replace(patern, value);
            } catch (Exception e) {
                //System.out.println(key + "=>" + patern);
            }
        }
        return body;
    }

    static public String replaceLogic(String body) {
        body = body.replace("{and}", " && ");
        body = body.replace("{or}", " || ");
        body = body.replace("{not}", "!");
        body = body.replace("{like}", "");
        int count = (body.length() - body.replace("(", "").length()) - (body.length() - body.replace(")", "").length());
        for (int i = 0; i < count; i++) {
            body += ")";
        }
        return body;
    }

    static public String replaceEscapeChar(String body) {
        body = body.replace("'", "\'");
        body = body.replace("\"", "\\\"");
        body = body.replace("\n", "\\n");
        return body;
    }

    static public String createXmlfromObject(HashMap<String, String> hashMap) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        XMLEncoder xmlEncoder = new XMLEncoder(bos);
        xmlEncoder.writeObject(hashMap);
        xmlEncoder.close();
        return bos.toString();
    }

    static public String createXmlReport(ArrayList<HashMap> wfDt, ArrayList<Integer> reportIndexes) {
        ArrayList<HashMap> wfDtx = new ArrayList<HashMap>();
        for (Integer i : reportIndexes) {
            wfDtx.add(wfDt.get(i));
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        XMLEncoder xmlEncoder = new XMLEncoder(bos);
        xmlEncoder.writeObject(wfDtx);
        xmlEncoder.close();
        return bos.toString();
    }
}
