package mandarin.controllers.admin;

import mandarin.auth.AuthenticationNeeded;
import mandarin.auth.NoAuthentication;
import mandarin.auth.SessionHelper;
import mandarin.auth.UserType;
import mandarin.auth.exceptions.AuthenticationException;
import mandarin.auth.exceptions.UnauthorizedException;
import mandarin.dao.UserRepository;
import mandarin.entities.User;
import mandarin.exceptions.APIException;
import mandarin.exceptions.ForbiddenException;
import mandarin.utils.BasicResponse;
import mandarin.utils.CryptoUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/admin")
@AuthenticationNeeded(UserType.Admin)
public class AdminController {
    @Resource
    private UserRepository userRepository;

    @Resource
    private SessionHelper sessionHelper;

    @ExceptionHandler(UnauthorizedException.class)
    @NoAuthentication
    public String loginRedirect() {
        return "redirect:/admin/login";
    }

    @GetMapping({"/", ""})
    public String index(HttpServletRequest request, Model model, HttpSession session) {
        List<User> librarianUsers = userRepository.findAllByType(UserType.Librarian);
        model.addAttribute("librarianList", librarianUsers);
        return "admin/admin";
    }

    @GetMapping("/login")
    @NoAuthentication
    public String loginPage(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return "admin/login";
        } else {
            User user = userRepository.findById((Integer) session.getAttribute("userId")).orElse(null);
            if (user == null) {
                session.invalidate();
                return "redirect:/admin/login";
            } else if (!user.getType().equals(UserType.Admin)) {
                throw new ForbiddenException();
            } else {
                return "redirect:/admin";
            }
        }
    }

    @ResponseBody
    @PostMapping("/login")
    @NoAuthentication
    public ResponseEntity login(@RequestParam("username") String username,
                                @RequestParam("password") String password,
                                HttpSession session) {
        try {
            sessionHelper.login(session, username, password, UserType.Admin);
            return ResponseEntity.status(HttpStatus.CREATED).body(BasicResponse.ok());
        } catch (AuthenticationException e) {
            return ResponseEntity.badRequest().body(BasicResponse.fail().message(e.getMessage()));
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        sessionHelper.logout(session, UserType.Admin);
        return "redirect:/admin/login";
    }

    @GetMapping(value = "/profile")
    public String showProfile(HttpSession session, Model model) {
        User user = userRepository.findById((Integer) session.getAttribute("userId")).orElse(null);
        if (user == null) {
            throw new ForbiddenException();
        }
        model.addAttribute("user", user);
        return "admin/profile";
    }
}