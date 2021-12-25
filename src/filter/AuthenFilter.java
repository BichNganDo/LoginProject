package filter;


import common.Config;
import freemarker.template.Configuration;
import helper.HttpHelper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.JWTModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import servlets.api.APIRegisterServlet;

public class AuthenFilter implements Filter {
    final static Logger logger = Logger.getLogger(APIRegisterServlet.class);

    public AuthenFilter() {
        PropertyConfigurator.configure("log4j.properties");
    }


    @Override
    public void init(FilterConfig fc) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain fc) throws IOException, ServletException {
        try {
            HttpServletRequest req = (HttpServletRequest) request;
            HttpServletResponse resp = (HttpServletResponse) response;

            String path = req.getServletPath();

            ArrayList<String> array = new ArrayList<>();
            array.add("/login");
            array.add("/register");

            if (array.contains(path)) {
                fc.doFilter(request, response);
                return;
            } else {
                String authenCookie = HttpHelper.getCookie(req, "authen");
                if (StringUtils.isNotEmpty(authenCookie)) {
                    Jws<Claims> user = JWTModel.INSTANCE.parseJwt(authenCookie);
                    if (user != null) {
                        Object idObject = user.getBody().get("id");
                        int idUser = Integer.parseInt(idObject.toString());
                        if (idUser > 0) {
                            fc.doFilter(request, response);
                            return;
                        }
                    }
                }
                resp.sendRedirect(Config.APP_DOMAIN + "/login");
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public void destroy() {
    }

}
