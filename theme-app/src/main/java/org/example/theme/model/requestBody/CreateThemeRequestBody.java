package org.example.theme.model.requestBody;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateThemeRequestBody {

    @NotNull
    private String themeName;

}
