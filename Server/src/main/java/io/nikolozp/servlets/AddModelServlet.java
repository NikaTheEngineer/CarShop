package io.nikolozp.servlets;

import io.nikolozp.constants.MyConstants;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Level;

@WebServlet("/AddModelServlet")
public class AddModelServlet extends ParentServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logMsg(Level.INFO, "Starting to add a model.");
        String bodyStr = getBodyAsJSON(request);
        if (bodyStr.equals(MyConstants.ERROR) || !isJSONValid(bodyStr)) {
            logMsg(Level.INFO, "Request was poorly formatted for adding a model. Sending BadRequest response.");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        logMsg(Level.INFO, "Read body from the request.");
        JSONObject bodyObj = new JSONObject(bodyStr);
        logMsg(Level.INFO, "Checking if all the parameters are provided and formatted correctly.");
        if(wrongParamsForAddingModel(bodyObj)) {
            logMsg(Level.INFO, "Request was poorly formatted. Sending BadRequest response.");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        } else {
            String msg = DbC.addNewModel(bodyObj);
            if(msg.equals(MyConstants.MODEL_ADDED) || msg.equals(MyConstants.MODEL_ALREADY_EXISTS)) {
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
