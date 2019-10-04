package mandarin.auth;

import mandarin.auth.exceptions.UnauthorizedException;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Properties;

public class AuthenticationInterceptor extends HandlerInterceptorAdapter {
    private boolean disabled = false;

    public AuthenticationInterceptor() {
        Properties properties = new Properties();
        try {
            properties.load(this.getClass().getClassLoader().getResourceAsStream("application.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        disabled = !Boolean.parseBoolean(properties.getProperty("mandarin.auth", "true"));
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        if (disabled) {
            return true;
        }
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("userId") == null) {
            session.invalidate();
            session = null;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        Class<?> clazz = method.getDeclaringClass();
        if (method.getAnnotation(NoAuthentication.class) != null || clazz.getAnnotation(NoAuthentication.class) != null) {
            return true;
        }
        AuthenticationNeeded annotation = method.getAnnotation(AuthenticationNeeded.class);
        if (annotation == null) {
            annotation = clazz.getAnnotation(AuthenticationNeeded.class);
        }
        if (annotation != null) {
            if (session != null && session.getAttribute("userId") != null) {
                for (UserType userType : annotation.value()) {
                    if (userType.equals(session.getAttribute("userType"))) {
                        return true;
                    }
                }
            }
            throw new UnauthorizedException();
        }
        return true;
    }
}
