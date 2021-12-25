package servlets;

import common.Config;
import helper.HttpHelper;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LogoutServlet extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpHelper.clearCookie(response, "authen");
        response.sendRedirect(Config.APP_DOMAIN + "/login");
    }
}
