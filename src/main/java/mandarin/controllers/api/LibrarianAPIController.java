package mandarin.controllers.api;

import mandarin.auth.AuthenticationNeeded;
import mandarin.auth.UserType;
import mandarin.controllers.api.dto.BookDTO;
import mandarin.controllers.api.dto.BookDetailDTO;
import mandarin.controllers.api.dto.CategoryDTO;
import mandarin.dao.BookRepository;
import mandarin.dao.CategoryRepository;
import mandarin.dao.LendingLogRepository;
import mandarin.dao.UserRepository;
import mandarin.entities.Book;
import mandarin.entities.Category;
import mandarin.entities.LendingLogItem;
import mandarin.entities.User;
import mandarin.exceptions.APIException;
import mandarin.utils.BasicResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public ResponseEntity<BasicResponse> lendBook(@RequestParam Integer userId, @RequestParam Integer bookId) {
        User user = userRepository.findById(userId).orElse(null);
        Book book = bookRepository.findById(bookId).orElse(null);
        if (user == null || book == null) {
            throw new APIException("Invalid ID(s)");
        }
        lendingLogRepository.save(new LendingLogItem(book, user));
        return ResponseEntity.ok(BasicResponse.ok());
    }

    //还书
    @PostMapping("/book/return")
    public ResponseEntity<BasicResponse> returnBook(@RequestParam Integer userId,
                                                    @RequestParam Integer bookId) {
        LendingLogItem lendingLogItem = lendingLogRepository.findByUserIdAndBookId(userId, bookId);
        if (lendingLogItem == null) {
            throw new APIException("Could not find the specified lending record");
        }
        lendingLogItem.setEndTime(Instant.now());
        lendingLogRepository.save(lendingLogItem);
        return ResponseEntity.ok().body(BasicResponse.ok());
    }

    //添加书
    @PostMapping(value = "/book/add", consumes = "application/json")
    public ResponseEntity<BasicResponse> addBook(@RequestBody BookDTO dto) {
        List<Category> categories = new ArrayList<>();
        for (Integer category_id : dto.category_ids) {
            Optional<Category> category = categoryRepository.findById(category_id);
            if (!category.isPresent()) {
                throw new APIException("Invalid category id");
            }
            categories.add(category.get());
        }
        if (dto.isbn.length() == 0 || dto.title.length() == 0 || dto.author.length() == 0 || dto.location.length() == 0 || dto.price.compareTo(BigDecimal.ZERO) < 0) {
            throw new APIException("Invalid input");
        }
        Book book = new Book(dto.isbn, dto.title, dto.author, dto.location, dto.price, categories);
        bookRepository.save(book);
        return ResponseEntity.status(HttpStatus.CREATED).body(BasicResponse.ok());
    }

    //删除书
    @DeleteMapping("/book/delete/{bookId}")
    public ResponseEntity<BasicResponse> deleteBook(@PathVariable Integer bookId) {
        bookRepository.deleteById(bookId);
        return ResponseEntity.ok(BasicResponse.ok());
    }

    //展示借阅、归还情况
    @GetMapping("/history")
    public ResponseEntity viewHistory(@RequestParam Integer userId,
                                      @RequestParam(defaultValue = "0") Integer page,
                                      @RequestParam(defaultValue = "10") Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("startTime"));
        List<LendingLogItem> list = lendingLogRepository.findByUserId(userId, pageable).getContent();
        BasicResponse<List<LendingLogItem>> response = BasicResponse.ok();
        response.data(list);
        return ResponseEntity.ok(response);
    }

    //添加READER
    @PostMapping("/reader/register")
    public ResponseEntity register(@RequestParam String username,
                                   @RequestParam String password) {
        User user = new User(username, password, UserType.Reader);
        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(BasicResponse.ok());
    }

    //编辑书
    @PutMapping(value = "/book/edit", consumes = "application/json")
    public ResponseEntity editBook(@RequestBody BookDTO dto) {
        Book book = bookRepository.findById(dto.id).orElse(null);
        if (book == null) {
            throw new APIException("No such book");
        }
        book.setISBN(dto.isbn);
        book.setTitle(dto.title);
        book.setAuthor(dto.author);
        book.setLocation(dto.location);
        book.setPrice(dto.price);
        book.getCategories().clear();
        book.getCategories().addAll(dto.category_ids.stream().map((Integer cid) -> {
            Optional<Category> category = categoryRepository.findById(cid);
            if (!category.isPresent()) {
                throw new APIException("Invalid category ID");
            }
            return category.get();
        }).collect(Collectors.toList()));
        bookRepository.save(book);
        return ResponseEntity.accepted().body(BasicResponse.ok());
    }

    //搜索书(By title/author/categories)
    @GetMapping("/book/search/{cond}")
    public ResponseEntity searchBook(@RequestParam String param,
                                     @RequestParam(defaultValue = "0") Integer page,
                                     @RequestParam(defaultValue = "10") Integer size,
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
        BasicResponse response = BasicResponse.ok().data(books.stream().map(BookDetailDTO::toDTO).collect(Collectors.toList()));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/category")
    public ResponseEntity listCategories() {
        List<CategoryDTO> result = categoryRepository.findAll().stream().map((Category c) -> {
            CategoryDTO dto = new CategoryDTO();
            dto.id = c.getId();
            dto.name = c.getName();
            Category parent = c.getParentCategory();
            if (parent != null) {
                dto.parent_category_id = parent.getId();
            } else {
                dto.parent_category_id = null;
            }
            return dto;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(BasicResponse.ok().data(result));
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
            return ResponseEntity.ok(BasicResponse.fail().message("Category does not exist"));
        else {
            categoryRepository.deleteById(id);
            return ResponseEntity.ok(BasicResponse.ok());
        }
    }
}
