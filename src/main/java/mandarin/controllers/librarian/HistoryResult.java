package mandarin.controllers.librarian;

import mandarin.dao.LendingLogRepository;
import mandarin.entities.LendingLogItem;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
public class HistoryResult {
    private Integer id;
    private Integer bookId;
    private Integer userId;
    private Instant startTime;
    private Instant endTime;

    @Resource
    LendingLogRepository lendingLogRepository;

    public HistoryResult() {}

    public HistoryResult(Integer id, Integer bookId, Integer userId, Instant startTime, Instant endTime) {
        this.id = id;
        this.bookId = bookId;
        this.userId = userId;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public List<HistoryResult> listHistory(Integer queryId, Pageable pageable){
        List<LendingLogItem> data = lendingLogRepository.findByUserId(queryId, pageable).getContent();
        List<HistoryResult> result = new ArrayList<>();
        for(LendingLogItem one: data){
             Integer id = one.getId();
             Integer bookId = one.getBook().getId();
             Integer userId = one.getUser().getId();
             Instant startTime = one.getStartTime();
             Instant endTime = one.getEndTime();
           result.add(new HistoryResult(id, bookId, userId, startTime, endTime));
        }
        return result;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }
}
