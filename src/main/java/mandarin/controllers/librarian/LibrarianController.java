package mandarin.controllers.librarian;

import mandarin.auth.AuthenticationNeeded;
import mandarin.auth.NoAuthentication;
import mandarin.auth.SessionHelper;
import mandarin.auth.UserType;
import mandarin.auth.exceptions.AuthenticationException;
import mandarin.auth.exceptions.UnauthorizedException;
import mandarin.dao.CategoryRepository;
import mandarin.dao.LendingLogRepository;
import mandarin.dao.UserRepository;
import mandarin.entities.Category;
import mandarin.entities.LendingLogItem;
import mandarin.entities.User;
import mandarin.exceptions.ForbiddenException;
import mandarin.utils.BasicResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@RequestMapping("/librarian")
@AuthenticationNeeded(UserType.Librarian)
@Controller
public class LibrarianController {

    @Resource
    UserRepository userRepository;

    @Resource
    SessionHelper sessionHelper;

    @Resource
    CategoryRepository categoryRepository;

    @Resource
    LendingLogRepository lendingLogRepository;

    @Resource
    HistoryResult historyResult;

    @GetMapping({"", "/", "/users", "/books"})
    public String index() {
        return "librarian-new/main";
    }

    //登录
    @GetMapping("/login")
    @NoAuthentication
    public String loginPage(HttpServletRequest request) throws RuntimeException {
        HttpSession session = request.getSession(false);
        User user = sessionHelper.getCurrentUser();
        if (user == null) {
            return "librarian-new/login";
        }
        return "redirect:/librarian";
    }

    @ResponseBody
    @PostMapping("/login")
    @NoAuthentication
    public ResponseEntity<BasicResponse> login(@RequestParam String username,
                                               @RequestParam String password,
                                               HttpSession session) {
        try {
            sessionHelper.login(username, password, UserType.Librarian);
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
            sessionHelper.logout(UserType.Librarian);
            return ResponseEntity.ok().body(BasicResponse.ok().message("Logged out"));
        } catch (AuthenticationException e) {
            return ResponseEntity.badRequest().body(BasicResponse.fail().message(e.getMessage()));
        }
    }


    //展示借阅、归还情况
    @ResponseBody
    @GetMapping("/history")
    @AuthenticationNeeded(UserType.Librarian)
    public ResponseEntity viewHistory(@RequestParam("userId") Integer userId,
                                      @RequestParam(value = "page", defaultValue = "0") Integer page,
                                      @RequestParam(value = "size", defaultValue = "10") Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("startTime"));
        List<LendingLogItem> list = lendingLogRepository.findByUserId(userId, pageable).getContent();
        BasicResponse<List<LendingLogItem>> response = BasicResponse.ok();
        response.data(list);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}/history")
    public ResponseEntity viewHistory1(@PathVariable Integer userId,
                                       @RequestParam(defaultValue = "0") Integer page,
                                       @RequestParam(defaultValue = "10") Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("startTime"));
        List<HistoryResult> results = historyResult.listHistory(userId, pageable);
        return ResponseEntity.ok(BasicResponse.ok().data(results));
    }


    @PostMapping("/categories")
    public ResponseEntity getCategories() {
        List<Category> categories = categoryRepository.findAll();
        return ResponseEntity.ok().body(BasicResponse.ok().data(categories));
    }


    @GetMapping("/show-history")
    public String getHistoryPage() {
        return "/librarian/compose";
    }

    @GetMapping("/add-book")
    public String getAddBookPage() {
        return "/librarian/add_book";
    }

    @GetMapping("/delete-book")
    public String getDeleteBookPage() {
        return "/librarian/delete_book";
    }

    @GetMapping("/register-reader")
    public String getRegisterReaderPage() {
        return "/librarian/register_reader";
    }

    @GetMapping("/lend-return-book")
    public String getLendBookPage() {
        return "/librarian/lend_return_book";
    }

}