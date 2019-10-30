package mandarin.controllers.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import mandarin.auth.AuthenticationNeeded;
import mandarin.auth.SessionHelper;
import mandarin.controllers.api.dto.BookDetailDTO;
import mandarin.dao.BookRepository;
import mandarin.dao.CategoryRepository;
import mandarin.dao.LendingLogRepository;
import mandarin.dao.UserRepository;
import mandarin.entities.Book;
import mandarin.entities.Category;
import mandarin.entities.User;
import mandarin.exceptions.APIException;
import mandarin.utils.BasicResponse;
import mandarin.utils.CryptoUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class GeneralAPIController {
    @Resource
    BookRepository bookRepository;
    @Resource
    CategoryRepository categoryRepository;
    @Resource
    LendingLogRepository lendingLogRepository;
    @Resource
    UserRepository userRepository;
    @Resource
    SessionHelper sessionHelper;

    @GetMapping("/book/{bookId}")
    public ResponseEntity getBook(@PathVariable Integer bookId) {
        Book book = bookRepository.findById(bookId).orElse(null);
        if (book == null) {
            throw new APIException("No such book");
        }
        return ResponseEntity.ok(BasicResponse.ok().data(BookDetailDTO.toDTO(book)));
    }

    /*
    //搜索书(By title/author/categories)
    @GetMapping("/book/search/{cond}")
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ResponseEntity searchBook(@RequestParam String param,
                                     @RequestParam(defaultValue = "0") Integer page,
                                     @RequestParam(defaultValue = "10") Integer size,
                                     @PathVariable("cond") String condition) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id"));
        List<Book> books = new ArrayList<>();
        switch (condition) {
            case "title":
                books = bookRepository.findAllByTitleContainsIgnoreCase(param, pageable).getContent();
                break;
            case "author":
                books = bookRepository.findAllByAuthorContaining(param, pageable).getContent();
                break;
            case "categories":
                Category category = categoryRepository.findByName(param);
                books = bookRepository.findAllByCategoriesContaining(category, pageable).getContent();
                break;
            default:
                throw new APIException("Invalid search type");
        }
        BasicResponse response = BasicResponse.ok().data(books.stream().map(BookDetailDTO::toDTO).collect(Collectors.toList()));
        return ResponseEntity.ok(response);
    }
    */

    static class ChangeProfileRequest {
        public String username;
        public String email;
        @JsonProperty("old_password")
        public String oldPassword;
        public String password;
    }

    @AuthenticationNeeded
    @PutMapping("/profile")
    public ResponseEntity changeProfile(@RequestBody ChangeProfileRequest request) {
        User user = sessionHelper.getCurrentUser();
        if (user == null) {
            throw new APIException("User does not exist");
        }
        if (StringUtils.isNotBlank(request.username)) {
            user.setUsername(request.username);
        }
        if (StringUtils.isNotBlank(request.email)) {
            user.setEmail(request.email);
        }
        if (StringUtils.isNotBlank(request.oldPassword) && StringUtils.isNotBlank(request.password)) {
            if (!CryptoUtils.verifyPassword(request.oldPassword, user.getPasswordHash())) {
                throw new APIException("Wrong password");
            }
            if (request.password.length() < 8) {
                throw new APIException("Password must longer than 8 characters");
            }
            user.setPassword(request.password);
        }
        userRepository.save(user);
        return ResponseEntity.ok(BasicResponse.ok().message("Profile successfully changed"));
    }
}
