package mandarin.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String isbn;
    private String title;
    private String author;
    private String location;
    private BigDecimal price;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "book_category_rel",joinColumns = {@JoinColumn(name = "isbn",referencedColumnName = "isbn")},inverseJoinColumns = {@JoinColumn(name = "category_id")})
    private List<Category> categories;

    public Book() {
        this.categories=new ArrayList<>();
    }

    public Book(String isbn, String title, String author, String location, BigDecimal price, List<Category> categories) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
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
}
