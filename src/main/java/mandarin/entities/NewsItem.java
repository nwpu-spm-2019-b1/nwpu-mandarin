package mandarin.entities;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "news")
public class NewsItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String title;
    private String content;
    private Instant time;
    @ManyToOne
    @JoinColumn(name = "poster_id")
    private User poster;

    public NewsItem() {
        this(null, null, null);
    }

    public NewsItem(String title, String content, User poster) {
        this.title = title;
        this.content = content;
        this.poster = poster;
        this.time = Instant.now();
    }

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public Instant getTime() {
        return time;
    }

    public User getPoster() {
        return poster;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTime(Instant time) {
        this.time = time;
    }
}
