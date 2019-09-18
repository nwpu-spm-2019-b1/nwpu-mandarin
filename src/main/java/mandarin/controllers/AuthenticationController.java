package mandarin.controllers;

import com.fasterxml.jackson.annotation.JacksonAnnotation;
import com.fasterxml.jackson.annotation.JsonProperty;
import mandarin.dao.UserRepository;
import mandarin.entities.User;
import mandarin.utils.CryptoUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
public class AuthenticationController {
    @Resource
    UserRepository userRepository;

    class SimpleResponse {
        @JsonProperty
        boolean success;
        @JsonProperty
        String message;

        public SimpleResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping(value = "/register", produces = "application/json")
    @ResponseBody
    public ResponseEntity register(@RequestParam String username,
                                   @RequestParam String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        userRepository.save(user);
        return ResponseEntity.ok().body(new SimpleResponse(true, "registered"));
    }

    @GetMapping("/login")
    public String loginPage(HttpSession session) {
        if (session.getAttribute("userId") != null) {
            return "redirect:/";
        }
        return "login";
    }

    @PostMapping(value = "/login", produces = "application/json")
    @ResponseBody
    public ResponseEntity login(@RequestParam String username,
                                @RequestParam String password,
                                HttpSession session) {
        Map<String, Object> map = new HashMap<>();
        if (session.getAttribute("userId") != null) {
            return ResponseEntity.badRequest().body(new SimpleResponse(false, "already logged in"));
        } else {
            User user = userRepository.findByUsername(username);
            if (user == null || !CryptoUtils.verifyPassword(password, user.getPasswordHash())) {
                return ResponseEntity.badRequest().body(new SimpleResponse(false, "username or password incorrect"));
            }
            session.setAttribute("userId", user.getId());
            session.setAttribute("userType", user.getType());
            return ResponseEntity.ok().body(new SimpleResponse(true, "logged in"));
        }
    }

    @PostMapping(value = "/logout", produces = "application/json")
    @ResponseBody
    public ResponseEntity logout(HttpSession session) {
        if (session.getAttribute("userId") == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new SimpleResponse(false, "not logged in"));
        } else {
            session.removeAttribute("userId");
            session.removeAttribute("userType");
            return ResponseEntity.ok().body(new SimpleResponse(true, "logged out"));
        }
    }
}
