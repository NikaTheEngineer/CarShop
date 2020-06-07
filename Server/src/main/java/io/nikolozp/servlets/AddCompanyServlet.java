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

@WebServlet("/AddCompanyServlet")
public class AddCompanyServlet extends ParentServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logMsg(Level.INFO, "Starting to add a company.");
        String bodyStr = getBodyAsJSON(request);
        if (bodyStr.equals(MyConstants.ERROR) || !isJSONValid(bodyStr)) {
            logMsg(Level.INFO, "Request was poorly formatted. Sending BadRequest response.");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        logMsg(Level.INFO, "Read body from the request.");
        JSONObject bodyObj = new JSONObject(bodyStr);
        logMsg(Level.INFO, "Checking if all the parameters are provided and formatted correctly.");
        if(wrongParams(bodyObj)) {
            logMsg(Level.INFO, "Request was poorly formatted. Sending BadRequest response.");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        } else {
            String msg = DbC.addNewCompany(bodyObj);
            if(msg.equals(MyConstants.COMPANY_ADDED)) {
                logMsg(Level.INFO, "Sending Accepted response.");
                response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            }
            else {
                logMsg(Level.INFO, "Sending NotAcceptable response.");
                response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            }
            response.getWriter().println(msg);
        }
    }

}
