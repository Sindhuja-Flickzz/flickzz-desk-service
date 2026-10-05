package com.flickzz.desk.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketTemplateDetailVO implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long fieldId;
    private String fieldName;
    private String value;
}