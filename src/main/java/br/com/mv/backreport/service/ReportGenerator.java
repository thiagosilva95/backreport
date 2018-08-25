package br.com.mv.backreport.service;

import br.com.mv.backreport.web.dto.ReportRequest;
import br.com.mv.backreport.web.dto.ReportResponse;
import net.sf.jasperreports.engine.JRException;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.sql.SQLException;

public interface ReportGenerator {
    ReportResponse generate(ReportRequest reportRequest) throws JRException, SQLException, IOException;
    Resource loadAsResource(String filename);
}
