package mandarin.auth;

import mandarin.auth.exceptions.InvalidCredentialsException;
import mandarin.auth.exceptions.InvalidStateException;
import mandarin.auth.exceptions.WrongUserTypeException;
import mandarin.dao.UserRepository;
import mandarin.entities.User;
import mandarin.utils.CryptoUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@Component
public class SessionHelper {
    @Resource
    UserRepository userRepository;

    public void login(HttpSession session, String username, String password, UserType userType) {
        if (session.getAttribute("userId") != null) {
            throw new InvalidStateException("already logged in");
        }
        User user = userRepository.findByUsername(username);
        if (user == null || !CryptoUtils.verifyPassword(password, user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }
        if (!user.getType().equals(userType)) {
            throw new WrongUserTypeException();
        }
        session.setAttribute("userId", user.getId());
        session.setAttribute("userType", user.getType());
        session.setAttribute("username",user.getUsername());
    }

    public void logout(HttpSession session, UserType userType) {
        if (session.getAttribute("userType").equals(userType)) {
            session.invalidate();
        } else {
            throw new WrongUserTypeException();
        }
    }
}
