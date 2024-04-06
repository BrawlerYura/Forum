package org.example.theme.model.requestBody;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateThemeRequestBody {

    @NotNull
    private String themeName;

    private UUID categoryId;

}
