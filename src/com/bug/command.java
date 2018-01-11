/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bug;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 *
 * @author bkantor
 */
public class command {

    private String type = "";
    private String cmd = "";
    private String param = "";
    private String name = "";

    public command() {
    }

    public command(String type, String cmd, String param) {
        super();
        this.type = type;
        this.cmd = cmd;
        this.param = param;
    }

    public command(String type, String name, String cmd, String param) {
        super();
        this.type = type;
        this.cmd = cmd;
        this.param = param;
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public String getCmd() {
        return cmd;
    }

    public String getParam() {
        return param;
    }

    public String getName() {
        return name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String runCmd() throws IOException {
        if ("exec".equals(type)) {
            ProcessBuilder pb = new ProcessBuilder("myshellScript.sh", "myArg1", "myArg2");
            Map<String, String> env = pb.environment();
            env.put("VAR1", "myValue");
            env.remove("OTHERVAR");
            env.put("VAR2", env.get("VAR1") + "suffix");
            pb.directory(new File("myDir"));
            Process p = pb.start();
            return "exec";
        } else if ("script".equals(type)) {

            return "script";
        } else {
            return "";
        }
    }

}
