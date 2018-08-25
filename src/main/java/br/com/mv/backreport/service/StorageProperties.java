package br.com.mv.backreport.service;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("backreport")
public class StorageProperties {

    private String outputDir;
    private String reportDir;

}
