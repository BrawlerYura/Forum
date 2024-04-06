package org.example.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDto {

    private UUID id;

    private String categoryName;

    private List<CategoryDto> childCategories;

    private Instant createDateTime;

    private Instant updateDateTime;

    private UserDto creator;

}
