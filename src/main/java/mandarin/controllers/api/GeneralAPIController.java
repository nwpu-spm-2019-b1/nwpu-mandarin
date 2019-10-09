package mandarin.controllers.api;

import mandarin.controllers.api.dto.BookDetailDTO;
import mandarin.dao.BookRepository;
import mandarin.dao.LendingLogRepository;
import mandarin.dao.UserRepository;
import mandarin.entities.Book;
import mandarin.entities.User;
import mandarin.exceptions.APIException;
import mandarin.utils.BasicResponse;
import mandarin.utils.CryptoUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class GeneralAPIController {
    @Resource
    BookRepository bookRepository;
    @Resource
    LendingLogRepository lendingLogRepository;
    @Resource
    UserRepository userRepository;

    @GetMapping("/book/{bookId}")
    public ResponseEntity getBook(@PathVariable Integer bookId) {
        Book book = bookRepository.findById(bookId).orElse(null);
        if (book == null) {
            throw new APIException("No such book");
        }
        return ResponseEntity.ok(BasicResponse.ok().data(BookDetailDTO.toDTO(book)));
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
                /*
            case "categories":
                books = bookRepository.findByCategoriesIsContaining(param, pageable).getContent();
                break;
                 */
        }
        BasicResponse response = BasicResponse.ok().data(books.stream().map(BookDetailDTO::toDTO).collect(Collectors.toList()));
        return ResponseEntity.ok(response);
    }

    //修改密码
    @PostMapping("/changePassword")
    public ResponseEntity changePassword(@RequestParam("username") String username,
                                         @RequestParam("password") String password){
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null ){
            return ResponseEntity.ok(BasicResponse.fail().message("User not exits"));
        }else if (CryptoUtils.verifyPassword(password, user.getPasswordHash())){
            return ResponseEntity.ok(BasicResponse.fail().message("Password and original password cannot be the same"));
        }else if(password.length()<8 || password.length()>16){
            return ResponseEntity.ok(BasicResponse.fail().message("Password must be 8-16 characters"));
        }

        user.setPassword(password);
        userRepository.save(user);
        return ResponseEntity.ok(BasicResponse.ok().message("Password successfully changed"));
    }
}
