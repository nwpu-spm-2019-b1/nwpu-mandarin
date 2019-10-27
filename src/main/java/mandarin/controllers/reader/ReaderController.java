package mandarin.controllers.reader;

import mandarin.auth.SessionHelper;
import mandarin.auth.UserType;
import mandarin.auth.exceptions.AuthenticationException;
import mandarin.auth.exceptions.UnauthorizedException;
import mandarin.dao.*;
import mandarin.entities.*;
import mandarin.services.BookService;
import mandarin.services.UserService;
import mandarin.utils.BasicResponse;
import mandarin.utils.CryptoUtils;
import mandarin.utils.MiscUtils;
import mandarin.utils.URLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Controller
public class ReaderController {
    private Logger logger = LoggerFactory.getLogger(ReaderController.class);

    @Resource
    private UserRepository userRepository;

    @Resource
    BookRepository bookRepository;

    @Resource
    BookService bookService;

    @Resource
    UserService userService;

    @Resource
    LendingLogRepository lendingLogRepository;

    @Resource
    ReservationRepository reservationRepository;

    @Resource
    NewsRepository newsRepository;

    @Resource
    SessionHelper sessionHelper;

    @Resource
    JavaMailSender mailSender;

    @ModelAttribute("sessionHelper")
    public SessionHelper getSessionHelper() {
        return sessionHelper;
    }

    @ModelAttribute("news")
    public List<NewsItem> getNews() {
        return newsRepository.findAll();
    }

    @ModelAttribute("bookService")
    public BookService getBookService() {
        return bookService;
    }

    @ModelAttribute("userService")
    public UserService getUserService() {
        return userService;
    }

    @ExceptionHandler(UnauthorizedException.class)
    public String loginRedirect() {
        return "redirect:/#login";
    }

    @GetMapping("/")
    public String index() {
        return "reader/index";
    }

    @GetMapping("/history")
    public String showHistory(Model model) {
        User user = sessionHelper.getCurrentUser();
        if (user != null) {
            List<Reservation> reservations = reservationRepository.findAllByUser(user);
            Page<LendingLogItem> items = lendingLogRepository.findByUserId(user.getId(), Pageable.unpaged());
            model.addAttribute("reservations", reservations);
            model.addAttribute("items", items.getContent());
        }
        return "reader/history";
    }

    @GetMapping("/search")
    public String searchBook(@RequestParam String query,
                             @RequestParam(defaultValue = "1") Integer page,
                             @RequestParam(defaultValue = "10") Integer size,
                             @RequestParam("type") String type,
                             Model model) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id"));
        Page<Book> books = Page.empty();
        switch (type) {
            case "isbn":
                books = bookRepository.findAllByISBN(query, pageable);
                break;
            case "title":
                books = bookRepository.findAllByTitleContainsIgnoreCase(query, pageable);
                break;
            case "author":
                books = bookRepository.findALlByAuthorContainingIgnoreCase(query, pageable);
                break;
        }
        model.addAttribute("books", books.getContent());
        model.addAttribute("type", type);
        model.addAttribute("query", query);
        model.addAttribute("page", books);
        return "reader/search";
    }

    @GetMapping("/recover")
    public String recoverPassword(Model model) {
        model.addAttribute("finished", false);
        return "reader/recover_password";
    }

    @PostMapping("/recover")
    public String recoverPassword(@RequestParam String email, Model model) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user != null) {
            String password = CryptoUtils.randomString(20);
            user.setPassword(password);
            MiscUtils.sendMail(user.getEmail(),"Password reset - Mandarin", "Your new password: " + password);
            logger.info("New password: {}", password);
            userRepository.save(user);
        } else {
            logger.warn("Invalid email submitted for password reset");
        }
        model.addAttribute("finished", true);
        return "reader/recover_password";
    }

    @GetMapping("/profile")
    public String profilePage() {
        return "reader/profile";
    }

    @PostMapping(value = "/login", produces = "application/json")
    @ResponseBody
    public ResponseEntity login(@RequestParam String username,
                                @RequestParam String password,
                                HttpSession session) {
        try {
            sessionHelper.login(username, password, UserType.Reader);
            return ResponseEntity.ok().body(BasicResponse.ok().message("logged in"));
        } catch (AuthenticationException e) {
            return ResponseEntity.badRequest().body(BasicResponse.fail().message(e.getMessage()));
        }
    }

    @PostMapping(value = "/logout", produces = "application/json")
    @ResponseBody
    public ResponseEntity logout(HttpSession session) {
        try {
            sessionHelper.logout();
            return ResponseEntity.ok().body(BasicResponse.ok().message("logged out"));
        } catch (AuthenticationException e) {
            return ResponseEntity.badRequest().body(BasicResponse.fail().message(e.getMessage()));
        }
    }
}
