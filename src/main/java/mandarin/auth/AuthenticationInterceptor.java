package mandarin.auth;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Method;

public class AuthenticationInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        AuthenticationNeeded annotation = method.getAnnotation(AuthenticationNeeded.class);
        if (annotation != null) {
            HttpSession session = request.getSession(false);
            if (session != null && session.getAttribute("userId") != null) {
                return true;
            } else {
                switch (annotation.value()) {
                    case Admin:
                        response.sendRedirect("/admin/login");
                        break;
                    case Reader:
                        response.sendRedirect("/login");
                        break;
                    case Librarian:
                        response.sendRedirect("/manage/login");
                        break;
                }
                return false;
            }
        } else {
            return true;
        }
      /*  if (!(request.getRequestURI().startsWith("/admin") || request.getRequestURI().startsWith("/manage"))) {
            return true;
        }
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("/login");
            return false;
        }
        return super.preHandle(request, response, handler);*/
    }
}
