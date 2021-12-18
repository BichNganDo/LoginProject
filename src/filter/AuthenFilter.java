package filter;


import common.Config;
import freemarker.template.Configuration;
import helper.HttpHelper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import java.io.IOException;
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

            if (!"/login".equals(path)) {
                int idUser = JWTModel.INSTANCE.getIdUser(req);
                if (idUser > 0) {
                    fc.doFilter(request, response);
                    return;
                } else {
                    resp.sendRedirect(Config.APP_DOMAIN + "/login");
                    return;
                }
            } else {
                fc.doFilter(request, response);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public void destroy() {
    }

}
