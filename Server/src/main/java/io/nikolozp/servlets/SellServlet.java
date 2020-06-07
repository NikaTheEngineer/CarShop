package io.nikolozp.servlets;

import io.nikolozp.constants.MyConstants;
import org.apache.commons.lang.math.NumberUtils;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Level;

@WebServlet("/SellServlet")
public class SellServlet extends ParentServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logMsg(Level.INFO, "Starting to sell a model.");
        String bodyStr = getBodyAsJSON(request);
        if (bodyStr.equals(MyConstants.ERROR) || !isJSONValid(bodyStr)) {
            logMsg(Level.INFO, "Request was poorly formatted for selling. Sending BadRequest response.");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        logMsg(Level.INFO, "Read body from the request.");
        JSONObject bodyObj = new JSONObject(bodyStr);
        logMsg(Level.INFO, "Checking if all the parameters are provided and formatted correctly for selling.");
        if(wrongParamsForSelling(bodyObj)) {
            logMsg(Level.INFO, "Request was poorly formatted for selling. Sending BadRequest response.");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        } else {
            String msg = DbC.sellModel(bodyObj);
            if (msg.equals(MyConstants.PRODUCT_SOLD)) {
                logMsg(Level.INFO, "Sending Accepted response.");
                response.setStatus(HttpServletResponse.SC_ACCEPTED);
            } else {
                logMsg(Level.INFO, "Sending NotAcceptable response.");
                response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            }
            response.getWriter().println(msg);
        }
    }


}
