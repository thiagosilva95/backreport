package br.com.mv.backreport.web.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class ReportRequest {

    private String name;
    Map<String, Object> parameters;
}
