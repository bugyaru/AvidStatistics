/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bug;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

/**
 *
 *
 * @author bkantor
 */
public class notify {

    private String type = "";
    private String outpath = "";
    private String xslt = "";
    private String xml = "";
    private String sendto = "";
    private String recepient = "";
    private String subject = "";
    private String body = "";
    private String host = "";
    private int port = 25;
    private Boolean tls = false;
    private Boolean auth = false;
    private String user = "";
    private String pass = "";
    private Properties prop;

    public notify() {
    }

    public String getXml() {
        return xml;
    }

    public Boolean getTls() {
        return tls;
    }

    public Boolean getAuth() {
        return auth;
    }

    public String getType() {
        return type;
    }

    public String getOutpath() {
        return outpath;
    }

    public String getXslt() {
        return xslt;
    }

    public String getSendto() {
        return sendto;
    }

    public String getRecepient() {
        return recepient;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getUser() {
        return user;
    }

    public String getPass() {
        return pass;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setOutpath(String outpath) {
        this.outpath = outpath;
    }

    public void setXslt(String xslt) {
        this.xslt = xslt;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }

    public void setSMTP(String host, int port, Boolean auth, String user, String pass, Boolean tls) {
        this.host = host;
        this.port = port;
        this.user = user;
        this.pass = pass;
        this.auth = auth;
        this.tls = tls;
        prop = new Properties();
        prop.put("mail.smtps.auth", String.valueOf(auth));
        prop.put("mail.smtps.host", host);
        prop.put("mail.smtps.port", String.valueOf(port));
        prop.put("mail.smtps.starttls.enable", String.valueOf(tls));
        prop.put("mail.smtp.auth", String.valueOf(auth));
        prop.put("mail.smtp.host", host);
        prop.put("mail.smtp.port", String.valueOf(port));
        prop.put("mail.smtp.starttls.enable", String.valueOf(tls));
        prop.setProperty("mail.user", user);
        prop.setProperty("mail.password", pass);
    }

    public void setMail(String sendto, String recepient, String subject, String body) {
        this.sendto = sendto;
        this.recepient = recepient;
        this.subject = subject;
        this.body = body;
    }

    public String sendNotify() {
        if ("xml".equals(type)) {

            return "xml";
        } else if ("mail".equals(type)) {
            Session session = null;
            Transport trnsport;
            try {
                if (auth) {
                    session = Session.getDefaultInstance(prop);
                    trnsport = session.getTransport("smtps");

                } else {
                    session = Session.getDefaultInstance(prop);
                    trnsport = session.getTransport("smtp");
                }

                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(recepient));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(sendto));
                message.setSubject(subject);
                message.setText(body);
                trnsport.connect(null, pass);
                message.saveChanges();
                trnsport.sendMessage(message, message.getAllRecipients());
                trnsport.close();
                System.out.println("Sent message successfully....");

            } catch (Exception e) {
                e.printStackTrace();
            }
            return "mail";
        } else {
            return "";
        }
    }

}
