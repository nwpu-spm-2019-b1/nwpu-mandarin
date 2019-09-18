package mandarin.controllers;

import mandarin.dao.BookRepository;
import mandarin.dao.LendingLogRepository;
import mandarin.dao.UserRepository;
import mandarin.entities.Book;
import mandarin.entities.LendingLog;
import mandarin.entities.User;
import mandarin.utils.CryptoUtils;
import mandarin.utils.StatusUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.time.Instant;
import java.util.List;

@RequestMapping("/manage")
@Controller
public class ManageController {

    @Resource
    UserRepository userRepository;

    @Resource
    LendingLogRepository lendingLogRepository;

    @Resource
    BookRepository bookRepository;

    @GetMapping({"/", ""})
    public String index(Model model) {
        model.addAttribute("now", Instant.now());
        return "manage";
    }

    //登录
    @ResponseBody
    @PostMapping("/login")
    public ResponseEntity<StatusUtils> login(@RequestParam("username") String username,
                                            @RequestParam("password") String password,
                                            HttpSession session, HttpServletResponse response) throws RuntimeException {

        User user = userRepository.findByUsername(username);
        if (user == null)
            return ResponseEntity.badRequest().body(StatusUtils.fail("User not exists"));

        if (!(CryptoUtils.verifyPassword(password, user.getPasswordHash())))
            return ResponseEntity.badRequest().body(StatusUtils.fail("Password is not correct"));

        if (session.getAttribute("userId") == null)
            session.setAttribute("userId", user.getId());
        response.addCookie(new Cookie("userId", user.getId().toString()));

        return ResponseEntity.status(HttpStatus.CREATED).body(StatusUtils.ok());
    }

    //登出
    @PostMapping("/logout")
    public ResponseEntity<StatusUtils> logout(HttpSession session) {
        session.removeAttribute("userId");
        return ResponseEntity.ok(StatusUtils.ok());
    }

    //借书
    @ResponseBody
    @PostMapping("/book/lend")
    public ResponseEntity<StatusUtils> lendBook(@RequestParam("userId") Integer userId,
                                @RequestParam("bookId") Integer bookId) {
        User user=userRepository.findById(userId).orElse(null);
        Book book=bookRepository.findById(bookId).orElse(null);
        lendingLogRepository.save(new LendingLog(book, user));
        return ResponseEntity.ok(StatusUtils.ok());
    }

    //还书
    @ResponseBody
    @PostMapping("/book/return")
    public ResponseEntity<StatusUtils> returnBook(@RequestParam("userId") Integer userId,
                                  @RequestParam("bookId") Integer bookId) {

        LendingLog lendingLog = lendingLogRepository.findByUserIdAndBookId(userId, bookId);
        lendingLog.setEndTime(Instant.now());
        return ResponseEntity.accepted().body(StatusUtils.ok());
    }

    //添加书
    @ResponseBody
    @PostMapping("/book/add")
    public ResponseEntity<StatusUtils> addBook(@RequestBody Book book){

        bookRepository.save(book);
        return ResponseEntity.status(HttpStatus.CREATED).body(StatusUtils.ok());
    }

    //删除书
    @ResponseBody
    @DeleteMapping("/book/delete")
    public ResponseEntity<StatusUtils> deleteBook(@RequestParam("bookId") Integer bookId){

        bookRepository.deleteById(bookId);
        return ResponseEntity.ok(StatusUtils.ok());
    }

    //展示借阅、归还情况
    @ResponseBody
    @GetMapping("/history")
    public ResponseEntity viewHistory(@RequestParam("userId") Integer userId,
                                      @RequestParam(value = "page", defaultValue = "0") Integer page,
                                      @RequestParam(value = "size", defaultValue = "10") Integer size){
        Pageable pageable = PageRequest.of(page, size, Sort.by("startTime"));
        List<LendingLog> list = lendingLogRepository.findByUserId(userId, pageable).getContent();
        StatusUtils<List<LendingLog>> response = StatusUtils.ok();
        response.setData(list);
        return ResponseEntity.ok(response);

    }

}
