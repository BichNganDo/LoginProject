package servlets;

import common.Config;
import entity.User;
import helper.HttpHelper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.JWTModel;
import model.RegisterModel;
import templater.PageGenerator;

public class HomeServlet extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Map<String, Object> pageVariables = new HashMap<>();
        pageVariables.put("app_domain", Config.APP_DOMAIN);
        pageVariables.put("static_domain", Config.STATIC_DOMAIN);

        int idUser = JWTModel.INSTANCE.getIdUser(request);
        if(idUser > 0){
            User userRegisterByID = RegisterModel.INSTANCE.getUserRegisterByID(idUser);
            pageVariables.put("user_by_id", userRegisterByID);

            String dob = userRegisterByID.getDob();
            long time = Long.parseLong(dob);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            String date = sdf.format(time).replace("/", "-"); 
            pageVariables.put("birthday", date);
        } else {
            response.sendRedirect(Config.APP_DOMAIN + "/login");
            return;
        }

        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().println(PageGenerator.instance().getPage("index.html", pageVariables));

        response.setStatus(HttpServletResponse.SC_OK);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
