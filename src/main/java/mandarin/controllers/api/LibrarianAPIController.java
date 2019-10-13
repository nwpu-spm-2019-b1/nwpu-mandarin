package mandarin.controllers.api;

import mandarin.auth.AuthenticationNeeded;
import mandarin.auth.UserType;
import mandarin.controllers.api.dto.BookDTO;
import mandarin.controllers.api.dto.BookDetailDTO;
import mandarin.controllers.api.dto.CategoryDTO;
import mandarin.dao.*;
import mandarin.entities.*;
import mandarin.exceptions.APIException;
import mandarin.services.BookService;
import mandarin.utils.BasicResponse;
import mandarin.utils.FormatUtils;
import mandarin.utils.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
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
    ReservationRepository reservationRepository;

    @Resource
    LendingLogRepository lendingLogRepository;

    @Resource
    BookRepository bookRepository;

    @Resource
    CategoryRepository categoryRepository;

    @Resource
    BookService bookService;

    @GetMapping("/book/search")
    public ResponseEntity searchBook(@RequestParam String type,
                                     @RequestParam String query,
                                     @RequestParam(defaultValue = "1") Integer page,
                                     @RequestParam(defaultValue = "20") Integer size) {
        Page<Book> books;
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "id"));
        if (query.length() > 0) {
            switch (type) {
                case "isbn":
                    books = bookRepository.findAllByISBN(query, pageable);
                    break;
                case "title":
                    books = bookRepository.findAllByTitleContainsIgnoreCase(query, pageable);
                    break;
                case "author":
                    books = bookRepository.findAllByAuthorContaining(query, pageable);
                    break;
                case "description":
                    books = bookRepository.findAllByDescriptionContaining(query, pageable);
                    break;
                default:
                    throw new APIException("Invalid search type");
            }
        } else {
            books = bookRepository.findAll(pageable);
        }
        Map<String, Object> data = new HashMap<>();
        data.put("books", books.getContent().stream().map((Book book) -> {
            Map<String, Object> result = new HashMap<>();
            ObjectUtils.copyFieldsIntoMap(book, result, "id", "isbn", "title", "description", "author", "location", "price");
            return result;
        }).collect(Collectors.toList()));
        data.put("total", books.getTotalPages());
        data.put("count", books.getTotalElements());
        return ResponseEntity.ok(BasicResponse.ok().data(data));
    }

    @GetMapping("/user/search")
    public ResponseEntity searchUser(@RequestParam String type,
                                     @RequestParam String query,
                                     @RequestParam(defaultValue = "1") Integer page,
                                     @RequestParam(defaultValue = "20") Integer size) {
        Map<String, Object> data = new HashMap<>();
        switch (type) {
            case "username":
                Page<User> userPage = userRepository.findAllByUsernameContaining(query, PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "id")));
                data.put("users", userPage.getContent());
                data.put("count", userPage.getTotalElements());
                data.put("total", userPage.getTotalPages());
                break;
            case "id":
                User user = userRepository.findById(Integer.parseInt(query)).orElse(null);
                if (user != null) {
                    data.put("users", Collections.singletonList(user));
                    data.put("count", 1);
                    data.put("total", 1);
                } else {
                    data.put("users", Collections.emptyList());
                    data.put("count", 0);
                    data.put("total", 0);
                }
                break;
        }
        return ResponseEntity.ok().body(BasicResponse.ok().data(data));
    }

    @GetMapping("/user/reservations")
    public ResponseEntity getReservations(@RequestParam("id") Integer userId) {
        List<Reservation> reservations = reservationRepository.findAllByUser(userRepository.findById(userId).orElse(null));
        return ResponseEntity.ok(BasicResponse.ok().data(reservations.stream().map((Reservation item) -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", item.getId());
            map.put("book", BookDetailDTO.toDTO(item.getBook()));
            map.put("time", FormatUtils.formatInstant(item.getTime()).orElse("-"));
            map.put("deadline", FormatUtils.formatInstant(item.getDeadline()).orElse("-"));
            return map;
        }).collect(Collectors.toList())));
    }

    //展示借阅、归还情况
    @GetMapping("/user/history")
    public ResponseEntity viewHistory(@RequestParam("id") Integer userId,
                                      @RequestParam(defaultValue = "1") Integer page,
                                      @RequestParam(defaultValue = "10") Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("startTime"));
        List<?> items = lendingLogRepository.findByUserId(userId, pageable).getContent().stream().map((LendingLogItem item) -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", item.getId());
            map.put("startTime", FormatUtils.formatInstant(item.getStartTime()).orElse("-"));
            map.put("endTime", FormatUtils.formatInstant(item.getEndTime()).orElse("-"));
            map.put("book", BookDetailDTO.toDTO(item.getBook()));
            map.put("user", ObjectUtils.copyFieldsIntoMap(item.getUser(), null, "id", "username"));
            return map;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(BasicResponse.ok().data(items));
    }

    //借书
    @PostMapping("/book/lend")
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ResponseEntity lendBook(@RequestParam String username,
                                   @RequestParam Integer bookId) {
        User user = userRepository.findByUsername(username).orElse(null);
        Book book = bookRepository.findById(bookId).orElse(null);
        if (user == null || book == null) {
            throw new APIException("Invalid ID(s)");
        }
        try {
            LendingLogItem item = bookService.lendBook(user, book);
            return ResponseEntity.ok(BasicResponse.ok().data(item.getId()));
        } catch (RuntimeException e) {
            throw new APIException(e.getMessage());
        }
    }

    //还书
    @PostMapping("/book/return")
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ResponseEntity returnBook(@RequestParam String username,
                                     @RequestParam Integer bookId) {
        User user = userRepository.findByUsername(username).orElse(null);
        Book book = bookRepository.findById(bookId).orElse(null);
        if (user == null) {
            throw new APIException("No such user");
        } else if (book == null) {
            throw new APIException("No such book");
        }
        LendingLogItem lendingLogItem = lendingLogRepository.findOutstandingByUserAndBook(user, book).orElse(null);
        if (lendingLogItem == null) {
            throw new APIException("No outstanding lending record");
        }
        lendingLogItem.setEndTime(Instant.now());
        lendingLogRepository.save(lendingLogItem);
        return ResponseEntity.ok().body(BasicResponse.ok().data(lendingLogItem.getEndTime()));
    }

    //添加书
    @PostMapping(value = "/book", consumes = "application/json")
    public ResponseEntity addBook(@RequestBody BookDTO dto) {
        List<Category> categories = new ArrayList<>();
        for (Integer category_id : dto.category_ids) {
            Optional<Category> category = categoryRepository.findById(category_id);
            if (!category.isPresent()) {
                throw new APIException("Invalid category id");
            }
            categories.add(category.get());
        }
        if (dto.isbn.length() == 0 || dto.title.length() == 0 || dto.author.length() == 0 || dto.location.length() == 0 || dto.price.compareTo(BigDecimal.ZERO) < 0 || dto.count == null || dto.count <= 0) {
            throw new APIException("Invalid input");
        }
        List<Book> allBooks = new ArrayList<>();
        for (int i = 0; i < dto.count; i++) {
            Book book = new Book(dto.isbn, dto.title, dto.author, dto.description, dto.location, dto.price, categories);
            bookRepository.save(book);
            allBooks.add(book);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(BasicResponse.ok().data(allBooks.stream().map((Book book) -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", book.getId());
            map.put("location", book.getLocation());
            return map;
        }).collect(Collectors.toList())));
    }

    //删除书
    @DeleteMapping("/book/{bookId}")
    public ResponseEntity deleteBook(@PathVariable Integer bookId) {
        bookRepository.deleteById(bookId);
        return ResponseEntity.ok(BasicResponse.ok());
    }

    @DeleteMapping("/book")
    public ResponseEntity deleteBooks(@RequestBody Map<String, Object> request) {
        if (!(request.getOrDefault("id_list", null) instanceof List)) {
            throw new APIException("Malformed request");
        }
        List<Integer> bookIds = ((List<Object>) request.get("id_list")).stream().map((Object o) -> {
            if (!(o instanceof Integer)) {
                return Integer.parseInt((String) o);
            } else {
                return (Integer) o;
            }
        }).collect(Collectors.toList());
        int deletedCount = 0;
        for (Integer bookId : bookIds) {
            deletedCount += bookService.deleteBook(bookId);
        }
        return ResponseEntity.ok(BasicResponse.ok().data(deletedCount));
    }

    //添加User
    @PostMapping("/user")
    public ResponseEntity register(@RequestParam String username, @RequestParam String password) {
        if (password.length() == 0) {
            password = "12345678";
        }
        User user = new User(username, password, UserType.Reader);
        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(BasicResponse.ok());
    }

    //编辑Reader
    @PutMapping(value = "/user/{userId}", consumes = "application/json")
    public ResponseEntity editReader(@PathVariable("userId") Integer id,
                                     @RequestParam(required = false) String username,
                                     @RequestParam(required = false) String password) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            throw new APIException("No such user");
        }
        if (username != null && username.length() > 0) {
            user.setUsername(username);
        }
        if (password != null && password.length() > 0) {
            user.setPassword(password);
        }
        userRepository.save(user);
        return ResponseEntity.ok(BasicResponse.ok());
    }

    //删除Reader
    @DeleteMapping("/user/{id}")
    public ResponseEntity deleteReader(@PathVariable Integer id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            throw new APIException("Reader does not exist");
        } else if (bookService.checkBorrowedBookNumber(user) > 0) {
            throw new APIException("There are still outstanding books");
        }
        //TODO: 罚金
        userRepository.deleteById(id);
        return ResponseEntity.ok(BasicResponse.ok());
    }

    //编辑书
    @PutMapping(value = "/book/{id}", consumes = "application/json")
    public ResponseEntity editBook(@PathVariable Integer id, @RequestBody BookDTO dto) {
        Book book = bookRepository.findById(id).orElse(null);
        if (book == null) {
            throw new APIException("No such book");
        }
        ObjectUtils.copyFields(dto, book, "isbn", "title", "author", "location", "price", "description");
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

    @GetMapping("/categories")
    public ResponseEntity listCategories() {
        List<?> result = categoryRepository.findAll().stream().map((Category c) -> {
            Map<String, Object> map = ObjectUtils.copyFieldsIntoMap(c, null, "id", "name");
            Category parent = c.getParentCategory();
            map.put("parent_category_id", parent != null ? parent.getId() : null);
            return map;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(BasicResponse.ok().data(result));
    }

    //增加种类
    @PostMapping("/categories")
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
    @DeleteMapping("/categories/{id}")
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
