package mandarin.controllers.reader;

import mandarin.auth.SessionHelper;
import mandarin.auth.UserType;
import mandarin.auth.exceptions.AuthenticationException;
import mandarin.dao.UserRepository;
import mandarin.entities.User;
import mandarin.utils.BasicResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@Controller
public class ReaderController {
    @GetMapping("/")
    public String index() {
        return "index";
    }
    @Resource
    UserRepository userRepository;
    @Resource
    SessionHelper sessionHelper;

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping(value = "/register", produces = "application/json")
    @ResponseBody
    public ResponseEntity register(@RequestParam String username,
                                   @RequestParam String password,
                                   @RequestParam String type) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setType(Enum.valueOf(UserType.class, type));
        userRepository.save(user);
        return ResponseEntity.ok().body(BasicResponse.ok().message("registered"));
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
        try {
            sessionHelper.login(session, username, password, UserType.Reader);
            return ResponseEntity.ok().body(BasicResponse.ok().message("logged in"));
        } catch (AuthenticationException e) {
            return ResponseEntity.badRequest().body(BasicResponse.fail().message(e.getMessage()));
        }
    }

    @PostMapping(value = "/logout", produces = "application/json")
    @ResponseBody
    public ResponseEntity logout(HttpSession session) {
        try {
            sessionHelper.logout(session, UserType.Reader);
            return ResponseEntity.ok().body(BasicResponse.ok().message("logged out"));
        } catch (AuthenticationException e) {
            return ResponseEntity.badRequest().body(BasicResponse.fail().message(e.getMessage()));
        }
    }
}
