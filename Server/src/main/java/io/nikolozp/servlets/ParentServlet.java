package io.nikolozp.servlets;

import io.nikolozp.constants.MyConstants;
import io.nikolozp.database.DatabaseController;
import org.apache.commons.lang.math.NumberUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class ParentServlet extends HttpServlet {
    private final static Logger LOGGER = Logger.getLogger(ParentServlet.class.getName());

    protected static DatabaseController DbC = new DatabaseController();

    protected static void logMsg(Level level, String msg) {
        FileHandler fh = null;
        try {
            fh = new FileHandler("C:\\Users\\npest\\Desktop\\Server\\MyLogFile.log", true);
            LOGGER.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
            LOGGER.log(level, msg);
        }
        catch(Exception e) {
        }
        finally {
            if(fh!=null) {
                fh.close();
            }
        }
    }

    protected static String getBodyAsJSON(HttpServletRequest request) {
        StringBuilder result = new StringBuilder("");
        BufferedReader reader = null;
        String toRet = null;
        try {
            reader = request.getReader();
            while(true) {
                String line = reader.readLine();
                if(line==null) break;
                result.append(line).append('\n');
            }
            toRet = result.toString();
        }
        catch (Exception e){
            logMsg(Level.WARNING, "JSON object was not provided.");
            toRet = MyConstants.ERROR;
        }
        finally {
            safeClose(reader);
            return toRet;
        }
    }

    protected static void safeClose(AutoCloseable toClose) {
        if(toClose==null) return;
        try {
            toClose.close();
        } catch (Exception e) {
            logMsg(Level.WARNING, "Could not close " + toClose.getClass().getName());
        }
    }

    protected static boolean wrongParams(JSONObject obj) {
        if(!obj.has(MyConstants.COMPANY)) return true;
        if(!obj.get(MyConstants.COMPANY).getClass().getName().equals("java.lang.String")) return true;
        if(obj.get(MyConstants.COMPANY).equals("")) return true;
        return false;
    }

    protected static boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }

    protected static boolean wrongParamsForAddingModel(JSONObject obj) {
        if(wrongParams(obj)) return true;
        if(!obj.has(MyConstants.MODEL)) return true;
        if(!obj.get(MyConstants.MODEL).getClass().getName().equals("java.lang.String")) return true;
        if(obj.get(MyConstants.MODEL).equals("")) return true;
        return false;
    }

    protected static boolean wrongParamsForSelling(JSONObject obj) {
        if(wrongParamsForAddingModel(obj)) return true;
        if(!obj.has(MyConstants.MONEY)) return true;
        if(!obj.get(MyConstants.MONEY).getClass().getName().equals("java.lang.String")) return true;
        if(!NumberUtils.isNumber(obj.getString(MyConstants.MONEY))) return true;
        return false;
    }
}
