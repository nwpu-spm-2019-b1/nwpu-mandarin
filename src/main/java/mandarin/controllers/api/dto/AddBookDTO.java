package mandarin.controllers.api.dto;

import java.math.BigDecimal;
import java.util.List;

public class AddBookDTO {
    public String isbn;
    public String title;
    public String author;
    public String location;
    public BigDecimal price;
    public List<Integer> category_ids;
}
