package com.tdev.backreport.web.controller;

import com.tdev.backreport.service.ReportRunner;
import com.tdev.backreport.web.dto.ReportRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reports")
public class ReportController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReportController.class);

    @Autowired
    private ReportRunner reportRunner;

    @PostMapping
    public ResponseEntity<byte[]> getBIRTReport(@RequestBody ReportRequest reportRequest) {
        ResponseEntity<byte[]> responseEntity = null;

        return responseEntity;
    }
}
