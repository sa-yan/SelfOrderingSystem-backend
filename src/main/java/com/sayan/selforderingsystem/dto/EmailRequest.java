package com.sayan.selforderingsystem.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class EmailRequest {
    private String to;
    private String subject;
    private String body;
}
