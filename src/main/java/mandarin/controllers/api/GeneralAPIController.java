package mandarin.controllers.api;

import mandarin.controllers.api.dto.BookDetailDTO;
import mandarin.controllers.api.dto.CategoryDTO;
import mandarin.controllers.librarian.HistoryResult;
import mandarin.dao.BookRepository;
import mandarin.dao.LendingLogRepository;
import mandarin.entities.Book;
import mandarin.entities.Category;
import mandarin.entities.LendingLogItem;
import mandarin.exceptions.APIException;
import mandarin.utils.BasicResponse;
import mandarin.utils.ObjectUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
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
    HistoryResult historyResult;

    @GetMapping("/book/{bookId}")
    public ResponseEntity getBook(@PathVariable Integer bookId) {
        Book book = bookRepository.findById(bookId).orElse(null);
        if (book == null) {
            throw new APIException("No such book");
        }
        return ResponseEntity.ok(BasicResponse.ok().data(BookDetailDTO.toDTO(book)));
    }

    //展示借阅、归还情况
    @GetMapping("/user/{userId}/history")
    public ResponseEntity viewHistory(@PathVariable Integer userId,
                                      @RequestParam(defaultValue = "0") Integer page,
                                      @RequestParam(defaultValue = "10") Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("startTime"));
        List<HistoryResult> results = new HistoryResult().listHistory(userId, pageable);
        return ResponseEntity.ok(BasicResponse.ok().data(results));
    }
}
