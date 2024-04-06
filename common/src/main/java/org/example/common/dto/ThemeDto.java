package org.example.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.common.dto.CategoryWithoutChildDto;
import org.example.common.dto.UserDto;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThemeDto {

    private UUID id;

    private String themeName;

    private CategoryWithoutChildDto category;

    private Instant createDateTime;

    private Instant updateDateTime;

    private UserDto creator;

}
