package mandarin.entities;

import javax.persistence.*;
import java.math.BigDecimal;
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
    @ManyToMany
    @JoinTable(name = "book_category_rel",joinColumns = {@JoinColumn(name = "isbn",referencedColumnName = "isbn")},inverseJoinColumns = {@JoinColumn(name = "category_id")})
    private List<Category> categories;
}
