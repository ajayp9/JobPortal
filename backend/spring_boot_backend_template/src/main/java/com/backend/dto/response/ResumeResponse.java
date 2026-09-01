package com.backend.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResumeResponse {
    private Long id;
    private String publicId;
    private String resumeUrl;
}
