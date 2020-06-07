package io.nikolozp.servlets;

import io.nikolozp.constants.MyConstants;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

@WebServlet("/GetCompanyServlet")
public class GetCompanyServlet extends ParentServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logMsg(Level.INFO, "Starting to search for model.");
        String bodyStr = getBodyAsJSON(request);
        if (bodyStr.equals(MyConstants.ERROR)) {
            logMsg(Level.INFO, "Request was poorly formatted. Sending BadRequest response.");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        if (!isJSONValid(bodyStr)) {
            bodyStr = "{ '" + MyConstants.COMPANY + "' : ''}";
        }
        logMsg(Level.INFO, "Read body from the request.");
        JSONObject bodyObj = new JSONObject(bodyStr);
        logMsg(Level.INFO, "Checking if all the parameters are provided and formatted correctly.");
        if(wrongParamsForSearching(bodyObj)) {
            logMsg(Level.INFO, "Request was poorly formatted. Sending BadRequest response.");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        } else {
            List<JSONObject> found = DbC.searchCompany(bodyObj);
            if(found.size()!=0) {
                logMsg(Level.INFO, "Sending Accepted response.");
                response.setStatus(HttpServletResponse.SC_ACCEPTED);
            }
            else {
                logMsg(Level.INFO, "Sending BadRequest response.");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
            response.setContentType("application/json");
            JSONObject toSend = new JSONObject();
            for (JSONObject jsonObject : found) {
                toSend.append("foundResults", jsonObject);
            }
            response.getWriter().println(toSend.toString());
        }
    }

    private boolean wrongParamsForSearching(JSONObject obj) {
        if(obj.has(MyConstants.COMPANY) && !obj.get(MyConstants.COMPANY).getClass().getName().equals("java.lang.String")) return true;
        if(!obj.has(MyConstants.COMPANY)) {
            obj.put(MyConstants.COMPANY, "");
        }
        return false;
    }
}
