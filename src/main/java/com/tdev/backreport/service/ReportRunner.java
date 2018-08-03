package com.tdev.backreport.service;

import com.tdev.backreport.model.Report;

import java.io.ByteArrayOutputStream;

public interface ReportRunner {

    ByteArrayOutputStream runReport(Report report);
}
