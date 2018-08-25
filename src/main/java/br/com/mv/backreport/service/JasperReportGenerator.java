package br.com.mv.backreport.service;

import br.com.mv.backreport.web.controller.ReportController;
import br.com.mv.backreport.web.dto.ReportRequest;
import br.com.mv.backreport.web.dto.ReportResponse;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.util.JRSaver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@Service
public class JasperReportGenerator implements ReportGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(JasperReportGenerator.class);

    private static final String REPORTS_DIR = "/reports/";
    private static final String JRXML_EXTENSION = ".jrxml";
    private static final String JASPER_EXTENSION = ".jasper";
    private static final String PDF_EXTENSION = ".pdf";
    private static final String METHOD_CONTROLLER_GET_REPORT = "serveFile";

    @Autowired
    private StorageProperties storageProperties;

    @Autowired
    private DataSource dataSource;

    @Override
    public ReportResponse generate(ReportRequest reportRequest) throws JRException, SQLException {
        ReportResponse reportResponse = new ReportResponse();
        try {
            Map<String, Object> parameters = reportRequest.getParameters() == null ? parameters = new HashMap<>() : reportRequest.getParameters();

            String report = reportRequest.getName().concat("/").concat(reportRequest.getName()).concat(JRXML_EXTENSION);

            //InputStream inputstream = this.getClass().getResourceAsStream("/reports/agendamentos-periodo/agendamentos-periodo.jrxml");

            String path = REPORTS_DIR.concat(report);
            InputStream inputstream = this.getClass().getResourceAsStream(path);

            //InputStream inputstream = this.getClass().getResourceAsStream("/reports/comprovante-agendamento/comprovante-agendamento.jrxml");


            //InputStream inputstream  = new FileInputStream(storageProperties.getReportDir().concat(report));

            JasperReport jasperReport = JasperCompileManager.compileReport(inputstream);

            //JRSaver.saveObject(jasperReport, report.replace(JRXML_EXTENSION, JASPER_EXTENSION));

            //JasperReport jasperReport = (JasperReport) JRLoader.loadObject(inputstream);

            // Passa para o JasperPrint o relatório, os parâmetros e a fonte dos dados, no caso uma conexão ao banco de dados
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource.getConnection());

            String fileName = buildReportGeneratedName(reportRequest.getName()).concat(PDF_EXTENSION);
            JasperExportManager.exportReportToPdfFile(jasperPrint
                                                    , Paths.get(storageProperties.getOutputDir())
                                                                            .resolve(fileName).toString());

            String urlDownload = MvcUriComponentsBuilder.fromMethodName(ReportController.class,
                    METHOD_CONTROLLER_GET_REPORT, load(fileName).getFileName().toString()).build().toString();

            reportResponse.setUrlDownload(urlDownload);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return reportResponse;
    }

    private String buildReportGeneratedName(String reportName) {
        String name;

        name = reportName.concat("_".concat(String.valueOf(LocalDateTime.now().getYear()))
                                    .concat(String.valueOf(LocalDateTime.now().getMonthValue()))
                                    .concat(String.valueOf(LocalDateTime.now().getDayOfMonth()))
                                    .concat(String.valueOf(LocalDateTime.now().getHour()))
                                    .concat(String.valueOf(LocalDateTime.now().getMinute()))
                                    .concat(String.valueOf(LocalDateTime.now().getSecond())));

        return name;
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            }
            else {
                throw new RuntimeException(
                        "Could not read file: " + filename);

            }
        }
        catch (MalformedURLException e) {
            throw new RuntimeException("Could not read file: " + filename, e);
        }
    }

    public Path load(String filename) {
        return Paths.get(storageProperties.getOutputDir()).resolve(filename);
    }

}
