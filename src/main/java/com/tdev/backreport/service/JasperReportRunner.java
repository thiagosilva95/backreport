package com.tdev.backreport.service;

import com.tdev.backreport.model.Report;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class JasperReportRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(JasperReportRunner.class);

    @Autowired
    private DataSource dataSource;

    @Autowired
    private Environment env;

    public ByteArrayOutputStream runReport(Report birtReport) {
        ByteArrayOutputStream byteArrayOutputStream;
        File rptDesignFile;
        try {
            Map<String, Object> parameters = parseParametersAsMap(birtReport.getParameters());
            //parameters = parameters == null ? parameters = new HashMap<>() : parameters;

            // Pega o arquivo .jasper localizado em resources
            InputStream jasperStream = this.getClass().getResourceAsStream("/relatorios/livros.jasper");

            // Cria o objeto JaperReport com o Stream do arquivo jasper
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);
            // Passa para o JasperPrint o relatório, os parâmetros e a fonte dos dados, no caso uma conexão ao banco de dados
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource.getConnection());

            // Configura a respota para o tipo PDF
            response.setContentType("application/pdf");
            // Define que o arquivo pode ser visualizado no navegador e também nome final do arquivo
            // para fazer download do relatório troque 'inline' por 'attachment'
            response.setHeader("Content-Disposition", "inline; filename=livros.pdf");

            // Faz a exportação do relatório para o HttpServletResponse
            final OutputStream outputStream = response.getOutputStream();
            JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);

        } catch (Exception e) {
            LOGGER.error("Error while running report task: {}.", e.getMessage());
            // TODO add custom message to thrown exception
            throw new RuntimeException();
        }

        return byteArrayOutputStream;
    }

    public Map<String, Object> parseParametersAsMap(String reportParameters) {
        Map<String, Object> parsedParameters = new HashMap<String, Object>();
        String[] paramArray;
        if (reportParameters.isEmpty()) {
            throw new IllegalArgumentException("Report parameters cannot be empty");
        } else if (!reportParameters.startsWith("?") && !reportParameters.contains("?")) {
            throw new IllegalArgumentException("Report parameters must start with a question mark '?'!");
        } else {
            String noQuestionMark = reportParameters.substring(1, reportParameters.length());
            paramArray = noQuestionMark.split("&");
            for (String param : paramArray) {
                String[] paramGroup = param.split("=");
                if (paramGroup.length == 2) {
                    parsedParameters.put(paramGroup[0], paramGroup[1]);
                } else {
                    parsedParameters.put(paramGroup[0], "");
                }

            }
        }
        return parsedParameters;
    }
}
