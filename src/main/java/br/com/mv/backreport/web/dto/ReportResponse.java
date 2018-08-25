package br.com.mv.backreport.web.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.ResourceSupport;

@Getter
@Setter
public class ReportResponse extends ResourceSupport {

    private String urlDownload;
}
