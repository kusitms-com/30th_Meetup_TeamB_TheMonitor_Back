package the_monitor.application.service;

import jakarta.servlet.http.HttpServletResponse;

public interface ExcelService {
    void generateExcel(Long reportId, HttpServletResponse response);
}
