package mandarin.controllers;

import mandarin.auth.UserType;
import mandarin.dao.BookRepository;
import mandarin.dao.LendingLogRepository;
import mandarin.dao.UserRepository;
import mandarin.entities.Book;
import mandarin.entities.LendingLogItem;
import mandarin.entities.User;
import mandarin.exceptions.ForbiddenException;
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
import javax.servlet.http.HttpServletRequest;
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
    @GetMapping("/login")
    public String loginPage(HttpServletRequest request) throws RuntimeException {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return "manage/login";
        }
        User user = userRepository.findById((Integer) session.getAttribute("userId")).orElse(null);
        if(user==null)
        {
            session.invalidate();
            return "redirect:/manage/login";
        }
        if (user.getType() != UserType.Librarian) {
            throw new ForbiddenException();
        } else {
            return "redirect:/manage";
        }
    }

    @PostMapping("/login")
    public ResponseEntity<BasicResponse> login(@RequestParam("username") String username,
                                               @RequestParam("password") String password,
                                               HttpSession session) throws RuntimeException {
        if (session.getAttribute("userId") != null) {
            return ResponseEntity.badRequest().body(BasicResponse.fail().message("You are already logged in"));
        }
        User user = userRepository.findByUsername(username);
        if (user == null || !CryptoUtils.verifyPassword(password, user.getPasswordHash())) {
            return ResponseEntity.badRequest().body(BasicResponse.fail().message("Username and/or password incorrect"));
        }
        session.setAttribute("userId", user.getId());
        session.setAttribute("userType", user.getType().toString());

        return ResponseEntity.ok().body(BasicResponse.ok());
    }

    //登出
    @PostMapping("/logout")
    public ResponseEntity<BasicResponse> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(BasicResponse.ok());
    }

    //借书
    @ResponseBody
    @PostMapping("/book/lend")
    public ResponseEntity<BasicResponse> lendBook(@RequestParam("userId") Integer userId,
                                                  @RequestParam("bookId") Integer bookId) {
        User user = userRepository.findById(userId).orElse(null);
        Book book = bookRepository.findById(bookId).orElse(null);
        lendingLogRepository.save(new LendingLogItem(book, user));
        return ResponseEntity.ok(BasicResponse.ok());
    }

    //还书
    @ResponseBody
    @PostMapping("/book/return")
    public ResponseEntity<BasicResponse> returnBook(@RequestParam("userId") Integer userId,
                                                    @RequestParam("bookId") Integer bookId) {
        LendingLogItem lendingLogItem = lendingLogRepository.findByUserIdAndBookId(userId, bookId);
        lendingLogItem.setEndTime(Instant.now());
        lendingLogRepository.save(lendingLogItem);
        return ResponseEntity.ok().body(BasicResponse.ok());
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
        List<LendingLogItem> list = lendingLogRepository.findByUserId(userId, pageable).getContent();
        BasicResponse<List<LendingLogItem>> response = BasicResponse.ok();
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
