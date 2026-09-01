package com.backend.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PersonalWebsiteResponse {

    private Long id;

    private String websiteTitle;

    private String websiteUrl;
}