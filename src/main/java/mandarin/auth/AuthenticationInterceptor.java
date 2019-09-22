package mandarin.auth;

import mandarin.exceptions.ForbiddenException;
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
            assert annotation.value().length != 0;
            HttpSession session = request.getSession(false);
            if (session != null && session.getAttribute("userId") != null) {
                for (UserType userType : annotation.value()) {
                    if (userType.equals(session.getAttribute("userType"))) {
                        return true;
                    }
                }
                throw new ForbiddenException();
            } else {
                switch (annotation.value()[0]) {
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
    }
}
