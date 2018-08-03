package com.tdev.backreport.web.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportRequest {

    private String reportName;
    private String reportParameters;
}
