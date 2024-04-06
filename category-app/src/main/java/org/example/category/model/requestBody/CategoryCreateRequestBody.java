package org.example.category.model.requestBody;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryCreateRequestBody {

    @NotNull
    private String categoryName;

    private UUID parentId;

}
