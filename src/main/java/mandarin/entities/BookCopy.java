package mandarin.entities;

import javax.persistence.*;

@Table(name = "book_copies")
@Entity
public class BookCopy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String location;
    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;
}
