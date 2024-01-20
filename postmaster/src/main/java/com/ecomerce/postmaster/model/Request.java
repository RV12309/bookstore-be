package com.ecomerce.postmaster.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(content = JsonInclude.Include.NON_NULL)
public class Request {

    @NotBlank
    private String sender;

    @NotBlank
    private String receiver;

    private String subject;

    private String content;

    private Map<String,String> attachment;

    private Boolean isUseCustomEmail;

    private String requestId;

}
