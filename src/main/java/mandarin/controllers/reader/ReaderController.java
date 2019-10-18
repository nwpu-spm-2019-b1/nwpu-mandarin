package mandarin.controllers.reader;

import mandarin.auth.SessionHelper;
import mandarin.auth.UserType;
import mandarin.auth.exceptions.AuthenticationException;
import mandarin.auth.exceptions.UnauthorizedException;
import mandarin.dao.BookRepository;
import mandarin.dao.LendingLogRepository;
import mandarin.dao.ReservationRepository;
import mandarin.dao.UserRepository;
import mandarin.entities.Book;
import mandarin.entities.LendingLogItem;
import mandarin.entities.Reservation;
import mandarin.entities.User;
import mandarin.services.BookService;
import mandarin.services.UserService;
import mandarin.utils.BasicResponse;
import mandarin.utils.CryptoUtils;
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
    SessionHelper sessionHelper;

    @Resource
    JavaMailSender mailSender;

    @ModelAttribute("sessionHelper")
    public SessionHelper getSessionHelper() {
        return sessionHelper;
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
                             @RequestParam(defaultValue = "0") Integer page,
                             @RequestParam(defaultValue = "10") Integer size,
                             @RequestParam("type") String type,
                             Model model) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id"));
        List<Book> books = new ArrayList<>();
        String fullQuery = "%" + query + "%";
        switch (type) {
            case "title":
                books = bookRepository.findByTitleLike(fullQuery, pageable).getContent();
                break;
            case "author":
                books = bookRepository.findByAuthorLike(fullQuery, pageable).getContent();
                break;
        }
        model.addAttribute("books", books);
        model.addAttribute("type", type);
        model.addAttribute("query", query);
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
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(user.getEmail());
            message.setFrom(((JavaMailSenderImpl) mailSender).getUsername());
            message.setSubject("Password reset - Mandarin");
            message.setSentDate(new Date());
            String password = CryptoUtils.randomString(20);
            message.setText("Your new password: " + password);
            logger.info("New password: {}", password);
            mailSender.send(message);
            model.addAttribute("finished", true);
        } else {
            logger.warn("Invalid email submitted for password reset");
            model.addAttribute("finished", true);
        }
        return "reader/recover_password";
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
            sessionHelper.logout(UserType.Reader);
            return ResponseEntity.ok().body(BasicResponse.ok().message("logged out"));
        } catch (AuthenticationException e) {
            return ResponseEntity.badRequest().body(BasicResponse.fail().message(e.getMessage()));
        }
    }
}
