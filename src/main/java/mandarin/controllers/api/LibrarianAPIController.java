package mandarin.controllers.api;

import mandarin.auth.AuthenticationNeeded;
import mandarin.auth.UserType;
import mandarin.dao.BookRepository;
import mandarin.dao.CategoryRepository;
import mandarin.dao.LendingLogRepository;
import mandarin.dao.UserRepository;
import mandarin.entities.Book;
import mandarin.entities.Category;
import mandarin.entities.LendingLogItem;
import mandarin.entities.User;
import mandarin.utils.BasicResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/librarian")
@AuthenticationNeeded(UserType.Librarian)
public class LibrarianAPIController {
    @Resource
    UserRepository userRepository;

    @Resource
    LendingLogRepository lendingLogRepository;

    @Resource
    BookRepository bookRepository;

    @Resource
    CategoryRepository categoryRepository;

    //借书
    @PostMapping("/book/lend")
    public ResponseEntity<BasicResponse> lendBook(@RequestParam("userId") Integer userId,
                                                  @RequestParam("bookId") Integer bookId) {
        User user = userRepository.findById(userId).orElse(null);
        Book book = bookRepository.findById(bookId).orElse(null);
        lendingLogRepository.save(new LendingLogItem(book, user));
        return ResponseEntity.ok(BasicResponse.ok());
    }

    //还书
    @PostMapping("/book/return")
    public ResponseEntity<BasicResponse> returnBook(@RequestParam("userId") Integer userId,
                                                    @RequestParam("bookId") Integer bookId) {
        LendingLogItem lendingLogItem = lendingLogRepository.findByUserIdAndBookId(userId, bookId);
        lendingLogItem.setEndTime(Instant.now());
        lendingLogRepository.save(lendingLogItem);
        return ResponseEntity.ok().body(BasicResponse.ok());
    }

    //添加书
    @PostMapping("/book/add")
    public ResponseEntity<BasicResponse> addBook(@RequestBody Book book) {
        bookRepository.save(book);
        return ResponseEntity.status(HttpStatus.CREATED).body(BasicResponse.ok());
    }

    //删除书
    @DeleteMapping("/book/delete/{bookId}")
    public ResponseEntity<BasicResponse> deleteBook(@PathVariable("bookId") Integer bookId) {
        bookRepository.deleteById(bookId);
        return ResponseEntity.ok(BasicResponse.ok());
    }

    //展示借阅、归还情况
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
    @PostMapping("/register")
    public ResponseEntity register(@RequestParam("username") String username,
                                   @RequestParam("password") String password) {

        User user = new User(username, password, UserType.Reader);
        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(BasicResponse.ok());
    }

    //编辑书
    @PutMapping("/book/edit")
    public ResponseEntity editBook(@RequestBody Book book) {
        Book oldBook = bookRepository.findById(book.getId()).orElse(null);
        BeanUtils.copyProperties(book, oldBook);
        bookRepository.save(oldBook);
        return ResponseEntity.accepted().body(BasicResponse.ok());
    }

    //搜索书(By title/author/categories)
    @GetMapping("/book/search/{cond}")
    public ResponseEntity searchBook(@RequestParam("param") String param,
                                     @RequestParam(value = "page", defaultValue = "0") Integer page,
                                     @RequestParam(value = "size", defaultValue = "10") Integer size,
                                     @PathVariable("cond") String condition) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id"));
        List<Book> books = new ArrayList<>();
        param = "%" + param + "%";
        switch (condition) {
            case "title":
                books = bookRepository.findByTitleLike(param, pageable).getContent();
                break;
            case "author":
                books = bookRepository.findByAuthorLike(param, pageable).getContent();
                break;
            case "categories":
                books = bookRepository.findByCategoriesIsContaining(param, pageable).getContent();
                break;
        }
        BasicResponse response = BasicResponse.ok().data(books);
        return ResponseEntity.ok(response);
    }

    //增加种类(修改)
    @PostMapping("/category")
    public ResponseEntity addCategory(@RequestParam("name") String name,
                                      @RequestParam("pName") String pName) {

        Category category = categoryRepository.findByName(name);
        if (category == null)
            categoryRepository.save(new Category(name, categoryRepository.findByName(pName)));
        else {
            category.setParentCategory(categoryRepository.findByName(pName));
            categoryRepository.save(category);
        }
        return ResponseEntity.ok(BasicResponse.ok());
    }

    //删除种类
    @DeleteMapping("/category/{id}")
    public ResponseEntity deleteCategory(@PathVariable("id") Integer id) {
        if (categoryRepository.findById(id).orElse(null) == null)
            return ResponseEntity.ok(BasicResponse.fail().message("Category not exists"));
        else {
            categoryRepository.deleteById(id);
            return ResponseEntity.ok(BasicResponse.ok());
        }
    }
}
