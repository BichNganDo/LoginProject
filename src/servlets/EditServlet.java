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
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.JWTModel;
import model.RegisterModel;
import templater.PageGenerator;

public class EditServlet extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Map<String, Object> pageVariables = new HashMap<>();
        pageVariables.put("app_domain", Config.APP_DOMAIN);
        pageVariables.put("static_domain", Config.STATIC_DOMAIN);

        String name = "authen";
        String cookie = HttpHelper.getCookie(request, name);
        Jws<Claims> parseJwt = JWTModel.INSTANCE.parseJwt(cookie);
        Object idObject = parseJwt.getBody().get("id");
        int idUser = Integer.parseInt(idObject.toString());
        pageVariables.put("id_user", idUser);

        User userRegisterByID = RegisterModel.INSTANCE.getUserRegisterByID(idUser);
        pageVariables.put("user_by_id", userRegisterByID);

        String avatar = userRegisterByID.getAvatar();
        pageVariables.put("avatar", avatar);

        String dob = userRegisterByID.getDob();
        long time = Long.parseLong(dob);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String date = sdf.format(time).replace("/", "-"); 
        pageVariables.put("birthday", date);

        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().println(PageGenerator.instance().getPage("edit.html", pageVariables));

        response.setStatus(HttpServletResponse.SC_OK);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
