package mandarin.entities;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "lending_log")
public class LendingLogItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(name = "start_time", nullable = false)
    private Instant startTime = Instant.now();
    @Column(name = "end_time")
    private Instant endTime = null;

    public LendingLogItem() {
    }

    public LendingLogItem(Book book, User user) {
        this.book = book;
        this.user = user;
    }

    public Integer getId() {
        return id;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public Integer getId(){
        return this.id;
    }
}
