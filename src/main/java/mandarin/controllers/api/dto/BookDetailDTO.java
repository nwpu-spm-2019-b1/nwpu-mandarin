package mandarin.controllers.api.dto;

import mandarin.entities.Book;
import mandarin.entities.Category;
import mandarin.utils.ObjectUtils;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class BookDetailDTO {
    @Nullable
    public Integer id;
    public String isbn;
    public String title;
    public String author;
    public String description;
    public String location;
    public BigDecimal price;
    public List<CategoryDTO> categories;

    public static BookDetailDTO toDTO(Book book) {
        BookDetailDTO dto = new BookDetailDTO();
        ObjectUtils.copyFields(book, dto, "id", "isbn", "title", "author", "location", "price", "description");
        dto.categories = book.getCategories().stream().map((Category c) -> {
            CategoryDTO cdto = new CategoryDTO();
            cdto.id = c.getId();
            cdto.name = c.getName();
            return cdto;
        }).collect(Collectors.toList());
        return dto;
    }
}
