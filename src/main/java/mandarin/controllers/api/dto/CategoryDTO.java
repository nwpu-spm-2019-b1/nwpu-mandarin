package mandarin.controllers.api.dto;

import java.io.Serializable;

public class CategoryDTO implements Serializable {
    public Integer id;
    public String name;
    public Integer parent_category_id;
}
