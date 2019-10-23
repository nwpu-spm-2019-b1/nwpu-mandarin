package mandarin.controllers.api.dto;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

public class EditBookDTO {
    @NotBlank(message = "ISBN must not be empty")
    public String isbn;
    @NotBlank(message = "Title must not be empty")
    public String title;
    @NotBlank(message = "Author must not be empty")
    public String author;
    @NotBlank(message = "Location must not be empty")
    public String location;
    @NotNull
    public String description;
    @NotNull(message = "Price must not be empty")
    @DecimalMin(value = "0", message = "Price must be a non-negative number")
    public BigDecimal price;
    @NotNull
    public List<String> categories;
}
