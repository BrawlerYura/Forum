package org.example.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.common.enums.FoundElementType;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FoundElementDto {

    private UUID id;

    private FoundElementType type;

    private String text;

    private Instant createDateTime;

    private UserDto creator;

}
