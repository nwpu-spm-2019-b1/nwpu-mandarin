package mandarin.auth;

import mandarin.auth.exceptions.InvalidCredentialsException;
import mandarin.auth.exceptions.InvalidStateException;
import mandarin.auth.exceptions.WrongUserTypeException;
import mandarin.dao.UserRepository;
import mandarin.entities.User;
import mandarin.utils.CryptoUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@Component
public class SessionHelper {
    @Resource
    UserRepository userRepository;

    private HttpSession getSession() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        return attrs.getRequest().getSession(false);
    }

    public void login(String username, String password, UserType userType) {
        HttpSession session = getSession();
        if (session.getAttribute("userId") != null) {
            throw new InvalidStateException("already logged in");
        }
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null || !CryptoUtils.verifyPassword(password, user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }
        if (!user.getType().equals(userType)) {
            throw new WrongUserTypeException();
        }
        session.setAttribute("userId", user.getId());
        session.setAttribute("userType", user.getType());
        session.setAttribute("username", user.getUsername());
    }

    public User getCurrentUser() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        return getCurrentUser(attrs.getRequest().getSession(false));
    }

    public User getCurrentUser(HttpSession session) {
        if (session == null || session.getAttribute("userId") == null) {
            return null;
        } else {
            return userRepository.findById((Integer) session.getAttribute("userId")).orElse(null);
        }
    }

    public void logout(UserType userType) {
        HttpSession session = getSession();
        if (session.getAttribute("userType").equals(userType)) {
            session.invalidate();
        } else {
            throw new WrongUserTypeException();
        }
    }

    public void logout() {
        HttpSession session = getSession();
        session.invalidate();
    }
}
