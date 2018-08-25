package br.com.mv.backreport.web.controller;

import br.com.mv.backreport.service.ReportGenerator;
import br.com.mv.backreport.web.dto.ReportResponse;
import br.com.mv.backreport.web.dto.ReportRequest;
import net.sf.jasperreports.engine.JRException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;

@RestController
@RequestMapping("/report")
public class ReportController {

    @Autowired
    private ReportGenerator reportGenerator;

    @Autowired
    private DataSource dataSource;

    private static final Logger LOGGER = LoggerFactory.getLogger(ReportController.class);

    @MessageMapping("/generation")
    @SendTo("/mv-report/generated")
    public ReportResponse getUser(ReportRequest reportRequest) throws InterruptedException
                                                                    , SQLException
                                                                    , IOException
                                                                    , JRException {
        ReportResponse reportResponse = reportGenerator.generate(reportRequest);
        return new ReportResponse();
    }

    @PostMapping
    public ResponseEntity<ReportResponse> imprimir(@RequestBody ReportRequest reportRequest) throws JRException
                                                           , SQLException
                                                           , IOException {
        ReportResponse reportResponse = reportGenerator.generate(reportRequest);

        return new ResponseEntity<>(reportResponse, HttpStatus.CREATED);
    }


    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        Resource file = reportGenerator.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "inline; filename=\"" + file.getFilename() + "\"").contentType(MediaType.APPLICATION_PDF)
                .body(file);
    }
}
