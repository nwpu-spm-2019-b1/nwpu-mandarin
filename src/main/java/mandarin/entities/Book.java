package mandarin.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.annotation.Resource;
import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "books")
public class Book implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String isbn;
    private String title;
    private String author;
    private String description;
    private String location;
    private BigDecimal price;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "book_category_rel", joinColumns = {@JoinColumn(name = "book_id")}, inverseJoinColumns = {@JoinColumn(name = "category_id")})
    private List<Category> categories;
    @JsonIgnore
    @OneToOne(cascade = {CascadeType.ALL}, orphanRemoval = true, mappedBy = "book")
    private Reservation reservation;
    @JsonIgnore
    @OneToMany(cascade = {CascadeType.REMOVE}, orphanRemoval = true, mappedBy = "book")
    private List<LendingLogItem> lendingLog;

    public Book() {
        this.price = BigDecimal.ZERO;
        this.categories = new ArrayList<>();
    }

    public Book(String isbn, String title, String author, String description, String location, BigDecimal price, List<Category> categories) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.description = description;
        this.location = location;
        this.price = price;
        this.categories = categories;
    }

    public Integer getId() {
        return id;
    }

    public String getISBN() {
        return isbn;
    }

    public void setISBN(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public List<LendingLogItem> getLendingLog() {
        return lendingLog;
    }
}
