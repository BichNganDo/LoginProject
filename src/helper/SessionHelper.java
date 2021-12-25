package helper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class SessionHelper {

    public static SessionHelper INSTANCE = new SessionHelper();

    public void addSession(HttpServletRequest request, String name, String message) {
        HttpSession session = request.getSession();
        session.setAttribute(name, message);

    }

    public String getSession(HttpServletRequest request, String name) {
        HttpSession session = request.getSession();
        if (session.getAttribute(name) != null) {
            return session.getAttribute(name).toString();
        }
        return "";
    }

    public void clearSession(HttpServletRequest request, String name) {
        HttpSession session = request.getSession();
        session.setAttribute(name, "");
    }

}
