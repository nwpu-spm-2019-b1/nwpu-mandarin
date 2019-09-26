package mandarin.controllers.librarian;

import mandarin.auth.AuthenticationNeeded;
import mandarin.auth.SessionHelper;
import mandarin.auth.UserType;
import mandarin.auth.exceptions.AuthenticationException;
import mandarin.dao.BookRepository;
import mandarin.dao.CategoryRepository;
import mandarin.dao.LendingLogRepository;
import mandarin.dao.UserRepository;
import mandarin.entities.Category;
import mandarin.entities.User;
import mandarin.exceptions.ForbiddenException;
import mandarin.utils.BasicResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@RequestMapping("/librarian")
@Controller
public class LibrarianController {

    @Resource
    UserRepository userRepository;

    @Resource
    SessionHelper sessionHelper;

    @Resource
    CategoryRepository categoryRepository;

    @GetMapping({"/", ""})
    public String index()
    {

        return "librarian/index";
    }

    //登录
    @GetMapping("/login")
    public String loginPage(HttpServletRequest request) throws RuntimeException {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return "librarian/login";
        }
        User user = userRepository.findById((Integer) session.getAttribute("userId")).orElse(null);
        if (user == null) {
            session.invalidate();
            return "redirect:/librarian/login";
        }
        if (user.getType() != UserType.Librarian) {
            throw new ForbiddenException();
        }
        return "redirect:/librarian";
    }

    @ResponseBody
    @PostMapping("/login")
    public ResponseEntity<BasicResponse> login(@RequestParam String username,
                                               @RequestParam String password,
                                               HttpSession session) {
        try {
            sessionHelper.login(session, username, password, UserType.Librarian);
            return ResponseEntity.ok().body(BasicResponse.ok().message("Logged in"));
        } catch (AuthenticationException e) {
            return ResponseEntity.badRequest().body(BasicResponse.fail().message(e.getMessage()));
        }
    }

    //登出
    @ResponseBody
    @PostMapping("/logout")
    @AuthenticationNeeded(UserType.Librarian)
    public ResponseEntity<BasicResponse> logout(HttpSession session) {
        try {
            sessionHelper.logout(session, UserType.Librarian);
            return ResponseEntity.ok().body(BasicResponse.ok().message("Logged out"));
        } catch (AuthenticationException e) {
            return ResponseEntity.badRequest().body(BasicResponse.fail().message(e.getMessage()));
        }
    }

    @PostMapping("/categories")
    public ResponseEntity getCategories(){
        List<Category> categories = categoryRepository.findAll();
        return ResponseEntity.ok().body(BasicResponse.ok().data(categories));
    }

    @GetMapping("/add-book")
    public String getAddBookPage()
    {
        return "/librarian/add_book";
    }

    @GetMapping("/delete-book")
    public String getDeleteBookPage()
    {
        return "/librarian/delete_book";
    }

    @GetMapping("/register-reader")
    public String getRegisterReaderPage()
    {
        return "/librarian/register_reader";
    }

    @GetMapping("/lend-return-book")
    public String getLendBookPage()
    {
        return "/librarian/lend_return_book";
    }

}