package com.makeitshort.url.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

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

    private LocalDateTime expiresAt;
}