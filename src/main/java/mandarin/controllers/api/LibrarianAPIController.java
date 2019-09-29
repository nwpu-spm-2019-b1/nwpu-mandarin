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
import mandarin.services.BookService;
import mandarin.utils.BasicResponse;
import mandarin.utils.ObjectUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
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

    @Resource
    BookService bookService;

    //展示借阅、归还情况
    @GetMapping("/user/{userId}/history")
    public ResponseEntity viewHistory(@PathVariable Integer userId,
                                      @RequestParam(defaultValue = "0") Integer page,
                                      @RequestParam(defaultValue = "10") Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("startTime"));
        List<?> items = lendingLogRepository.findByUserId(userId, pageable).getContent().stream().map((LendingLogItem item) -> {
            Map<String, Object> map = new HashMap<>();
            ObjectUtils.copyFieldsIntoMap(item, map, "id", "startTime", "endTime");
            map.put("book", BookDetailDTO.toDTO(item.getBook()));
            map.put("user", ObjectUtils.copyFieldsIntoMap(item.getUser(), null, "id", "username"));
            return map;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(BasicResponse.ok().data(items));
    }

    //借书
    @PostMapping("/book/lend")
    public ResponseEntity lendBook(@RequestParam Integer userId, @RequestParam Integer bookId) {
        User user = userRepository.findById(userId).orElse(null);
        Book book = bookRepository.findById(bookId).orElse(null);
        if (user == null || book == null) {
            throw new APIException("Invalid ID(s)");
        }
        if (!bookService.checkAvailability(bookId)) {
            throw new APIException("Book not available");
        }
        lendingLogRepository.save(new LendingLogItem(book, user));
        return ResponseEntity.ok(BasicResponse.ok());
    }

    //还书
    @PostMapping("/book/return")
    public ResponseEntity returnBook(@RequestParam Integer userId,
                                     @RequestParam Integer bookId) {
        LendingLogItem lendingLogItem = lendingLogRepository.findByUserIdAndBookId(userId, bookId);
        if (lendingLogItem == null) {
            throw new APIException("Could not find the specified lending record");
        }
        if (lendingLogItem.getEndTime() != null) {
            throw new APIException("Book already returned");
        }
        lendingLogItem.setEndTime(Instant.now());
        lendingLogRepository.save(lendingLogItem);
        return ResponseEntity.ok().body(BasicResponse.ok());
    }

    //添加书
    @PostMapping(value = "/book/add", consumes = "application/json")
    public ResponseEntity addBook(@RequestBody BookDTO dto) {
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
        Integer bookId = book.getId();
        return ResponseEntity.status(HttpStatus.CREATED).body(BasicResponse.ok().data(bookId));
    }

    //删除书
    @DeleteMapping("/book/delete/{bookId}")
    public ResponseEntity deleteBook(@PathVariable Integer bookId) {
        bookRepository.deleteById(bookId);
        return ResponseEntity.ok(BasicResponse.ok());
    }

    //添加READER
    @PostMapping("/reader/register")
    public ResponseEntity register(@RequestParam String username) {
        String defaultPassword = "12345678";
        User user = new User(username, defaultPassword, UserType.Reader);
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
        ObjectUtils.copyFields(dto, book, "isbn", "title", "author", "location", "price");
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

    @GetMapping("/category")
    public ResponseEntity listCategories() {
        List<CategoryDTO> result = categoryRepository.findAll().stream().map((Category c) -> {
            CategoryDTO dto = new CategoryDTO();
            ObjectUtils.copyFields(c, dto, "id", "name");
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

    //增加种类
    @PostMapping("/category")
    public ResponseEntity addCategory(@RequestParam String name,
                                      @RequestParam(required = false) Integer parentId) {
        Category category = new Category(name, null);
        if (parentId != null) {
            Category parent = categoryRepository.findById(parentId).orElse(null);
            if (parent == null) {
                throw new APIException("Could not found parent category by ID");
            }
            category.setParentCategory(parent);
        }
        categoryRepository.save(category);
        return ResponseEntity.ok(BasicResponse.ok().data(category.getId()));
    }

    //删除种类
    @DeleteMapping("/category/{id}")
    public ResponseEntity deleteCategory(@PathVariable Integer id) {
        Category target = categoryRepository.findById(id).orElse(null);
        if (target == null)
            return ResponseEntity.ok(BasicResponse.fail().message("Category does not exist"));
        else {
            categoryRepository.delete(target);
            return ResponseEntity.ok(BasicResponse.ok());
        }
    }
}
