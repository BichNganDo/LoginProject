package servlets;

import com.google.gson.Gson;
import common.APIResult;
import common.Config;
import entity.User;
import helper.HttpHelper;
import helper.SecurityHelper;
import helper.ServletUtil;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.JWTModel;
import model.RegisterModel;
import templater.PageGenerator;

public class LoginServlet extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Map<String, Object> pageVariables = new HashMap<>();
        pageVariables.put("app_domain", Config.APP_DOMAIN);
        pageVariables.put("static_domain", Config.STATIC_DOMAIN);

        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().println(PageGenerator.instance().getPage("login.html", pageVariables));

        response.setStatus(HttpServletResponse.SC_OK);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        APIResult result = new APIResult(0, "Success");

        String action = request.getParameter("action");
        switch (action) {
            case "login": {
                String email = request.getParameter("email");
                String password = SecurityHelper.getMD5Hash(request.getParameter("password"));
                User checkLogin = RegisterModel.INSTANCE.checkLogin(email, password);
                if (checkLogin != null && checkLogin.getId() > 0) {
                    String jwtToken = JWTModel.INSTANCE.genJWT(checkLogin.getEmail(), checkLogin.getId());
                    HttpHelper.setCookie(response, "authen", jwtToken, 86400);
                    result.setErrorCode(0);
                    result.setMessage("Đăng nhập thành công!");
                } else {
                    result.setErrorCode(-3);
                    result.setMessage("Email hoặc mật khẩu không đúng");
                }
                break;
            }

            default:
                throw new AssertionError();
        }

        ServletUtil.printJson(request, response, gson.toJson(result));

    }
}
