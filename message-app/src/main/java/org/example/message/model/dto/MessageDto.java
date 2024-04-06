package org.example.message.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.common.dto.ThemeDto;
import org.example.common.dto.UserDto;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageDto {

    private UUID id;

    private String text;

    private ThemeDto theme;

    private Instant createDateTime;

    private Instant updateDateTime;

    private UserDto creator;

}
