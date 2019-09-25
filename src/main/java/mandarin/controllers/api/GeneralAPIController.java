package mandarin.controllers.api;

import mandarin.controllers.api.dto.BookDetailDTO;
import mandarin.controllers.api.dto.CategoryDTO;
import mandarin.dao.BookRepository;
import mandarin.entities.Book;
import mandarin.entities.Category;
import mandarin.exceptions.APIException;
import mandarin.utils.BasicResponse;
import mandarin.utils.ObjectUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class GeneralAPIController {
    @Resource
    BookRepository bookRepository;

    @GetMapping("/book/{bookId}")
    public ResponseEntity getBook(@PathVariable Integer bookId) {
        Book book = bookRepository.findById(bookId).orElse(null);
        if (book == null) {
            throw new APIException("No such book");
        }
        return ResponseEntity.ok(BasicResponse.ok().data(BookDetailDTO.toDTO(book)));
    }
}
