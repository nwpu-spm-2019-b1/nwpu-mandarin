package mandarin.entities;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "reservations")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    private Instant time;

    public Reservation() {
        this.book = null;
        this.user = null;
        this.time = Instant.now();
    }

    public Reservation(Book book, User user) {
        this.book = book;
        this.user = user;
        this.time = Instant.now();
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

    public Instant getTime() {
        return time;
    }

    public void setTime(Instant time) {
        this.time = time;
    }
}
