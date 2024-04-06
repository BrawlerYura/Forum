package org.example.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.common.dto.UserDto;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryWithoutChildDto {

    private UUID id;

    private String categoryName;

    private Instant createDateTime;

    private Instant updateDateTime;

    private UserDto creator;

}
