package mandarin.controllers.api.dto;

import org.springframework.lang.Nullable;

import java.math.BigDecimal;
import java.util.List;

public class BookDTO {
    @Nullable
    public Integer id;
    public String isbn;
    public String title;
    public String author;
    public String location;
    public BigDecimal price;
    public List<Integer> category_ids;
}
