package com.makeitshort.url.dto;

import com.makeitshort.url.enums.Expiry;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CreateUrlRequest {

    @NotBlank(message = "URL cannot be blank")
    @Size(max = 2048, message = "URL cannot exceed 2048 characters")
    private String longUrl;

    @Size(
            min = 3,
            max = 20,
            message = "Custom code must be between 3 and 20 characters"
    )
    private String customCode;

    private Expiry expiry;
}