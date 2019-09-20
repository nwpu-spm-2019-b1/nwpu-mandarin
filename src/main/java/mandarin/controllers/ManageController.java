package mandarin.controllers;

import mandarin.auth.UserType;
import mandarin.dao.BookRepository;
import mandarin.dao.LendingLogRepository;
import mandarin.dao.UserRepository;
import mandarin.entities.Book;
import mandarin.entities.LendingLog;
import mandarin.entities.User;
import mandarin.utils.CryptoUtils;
import mandarin.utils.BasicResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.time.Instant;
import java.util.ArrayList;
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
    public String index() {
        return "manage";
    }

    //登录
    @ResponseBody
    @PostMapping("/login")
    public ResponseEntity<BasicResponse> login(@RequestParam("username") String username,
                                               @RequestParam("password") String password,
                                               HttpSession session) throws RuntimeException {
        User user = userRepository.findByUsername(username);
        if (user == null)
            return ResponseEntity.badRequest().body(BasicResponse.fail().message("User not exists"));
        if (!(CryptoUtils.verifyPassword(password, user.getPasswordHash())))
            return ResponseEntity.badRequest().body(BasicResponse.fail().message("Password is not correct"));

        if (session.getAttribute("userId") == null) {
            session.setAttribute("userId", user.getId());
            session.setAttribute("userType", user.getType().toString());
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(BasicResponse.ok());
    }

    //登出
    @PostMapping("/logout")
    public ResponseEntity<BasicResponse> logout(HttpSession session) {
        session.removeAttribute("userId");
        return ResponseEntity.ok(BasicResponse.ok());
    }

    //借书
    @ResponseBody
    @PostMapping("/book/lend")
    public ResponseEntity<BasicResponse> lendBook(@RequestParam("userId") Integer userId,
                                                  @RequestParam("bookId") Integer bookId) {
        User user = userRepository.findById(userId).orElse(null);
        Book book = bookRepository.findById(bookId).orElse(null);
        lendingLogRepository.save(new LendingLog(book, user));
        return ResponseEntity.ok(BasicResponse.ok());
    }

    //还书
    @ResponseBody
    @PostMapping("/book/return")
    public ResponseEntity<BasicResponse> returnBook(@RequestParam("userId") Integer userId,
                                                    @RequestParam("bookId") Integer bookId) {

        LendingLog lendingLog = lendingLogRepository.findByUserIdAndBookId(userId, bookId);
        lendingLog.setEndTime(Instant.now());
        lendingLogRepository.save(lendingLog);
        return ResponseEntity.accepted().body(BasicResponse.ok());
    }

    //添加书
    @ResponseBody
    @PostMapping("/book/add")
    public ResponseEntity<BasicResponse> addBook(@RequestBody Book book) {


        bookRepository.save(book);
        return ResponseEntity.status(HttpStatus.CREATED).body(BasicResponse.ok());
    }

    //删除书
    @ResponseBody
    @DeleteMapping("/book/delete/{bookId}")
    public ResponseEntity<BasicResponse> deleteBook(@PathVariable("bookId") Integer bookId) {

        bookRepository.deleteById(bookId);
        return ResponseEntity.ok(BasicResponse.ok());
    }

    //展示借阅、归还情况
    @ResponseBody
    @GetMapping("/history")
    public ResponseEntity viewHistory(@RequestParam("userId") Integer userId,
                                      @RequestParam(value = "page", defaultValue = "0") Integer page,
                                      @RequestParam(value = "size", defaultValue = "10") Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("startTime"));
        List<LendingLog> list = lendingLogRepository.findByUserId(userId, pageable).getContent();
        BasicResponse<List<LendingLog>> response = BasicResponse.ok();
        response.data(list);
        return ResponseEntity.ok(response);
    }

    //添加READER
    @ResponseBody
    @PostMapping("/register")
    public ResponseEntity register(@RequestParam("username") String username,
                                   @RequestParam("password") String password) {

        User user = new User(username, password, UserType.Reader);
        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(BasicResponse.ok());
    }

    //编辑书
    @PutMapping("/book/edit")
    @ResponseBody
    public ResponseEntity editBook(@RequestBody Book book) {
        Book oldBook = bookRepository.findById(book.getId()).orElse(null);
        BeanUtils.copyProperties(book, oldBook);
        bookRepository.save(oldBook);
        return ResponseEntity.accepted().body(BasicResponse.ok());

    }

    //搜索书(By title/author/categories)
    @GetMapping("/book/search/{By}")
    @ResponseBody
    public ResponseEntity searchBook(@RequestParam("param") String param,
                                     @RequestParam(value = "page", defaultValue = "0") Integer page,
                                     @RequestParam(value = "size", defaultValue = "10") Integer size,
                                     @PathVariable("By") String condition) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id"));
        List<Book> books = new ArrayList<>();
        param = "%" + param + "%";
        if (condition.equals("title")) {
            books = bookRepository.findByTitleLike(param, pageable).getContent();
        } else if (condition.equals("author")) {
            books = bookRepository.findByAuthorLike(param, pageable).getContent();
        } else if (condition.equals("categories")) {
            books = bookRepository.findByCategoriesIsContaining(param, pageable).getContent();
        }

        BasicResponse response = BasicResponse.ok().data(books);
        return ResponseEntity.ok(response);
    }
}
