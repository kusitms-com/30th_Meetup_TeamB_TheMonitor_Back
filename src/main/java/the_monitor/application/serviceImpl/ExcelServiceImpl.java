package the_monitor.application.serviceImpl;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import the_monitor.application.service.ExcelService;
import the_monitor.domain.enums.CategoryType;
import the_monitor.domain.model.*;
import the_monitor.domain.repository.ReportRepository;

import java.io.*;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExcelServiceImpl implements ExcelService {

    private final ReportRepository reportRepository;

    private static final String TEMPLATE_PATH = "/templates/template.xlsx";

    /**
     * 엑셀 파일 생성 (로컬에 저장)
     * @param reportId 보고서 ID
     * @return 생성된 파일 객체
     */
    @Override
    public File createExcelFile(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Report not found"));

        List<ReportCategory> reportCategories = report.getReportCategories();

        try (InputStream templateStream = getClass().getResourceAsStream(TEMPLATE_PATH)) {
            if (templateStream == null) {
                throw new IllegalStateException("Excel template not found");
            }

            Workbook workbook = new XSSFWorkbook(templateStream);
            Sheet sheet = workbook.getSheetAt(0);

            // B2: Client 이름
            setCellValue(sheet, 1, 1, report.getClient().getName());

            // B3: Report Title
            setCellValue(sheet, 2, 1, report.getTitle());

            int currentRow = 6; // SELF 기사 시작 위치 (B7)

            // 각 카테고리 기사 추가 및 병합
            for (CategoryType categoryType : CategoryType.values()) {
                List<ReportCategory> categories = reportCategories.stream()
                        .filter(category -> category.getCategoryType() == categoryType)
                        .toList();

                if (categories.isEmpty()) continue;

                // 카테고리 타이틀 삽입 (셀 병합)
                if (categoryType != CategoryType.SELF) {
                    currentRow++;
                    mergeCells(sheet, currentRow, currentRow, 1, 5);
                    setCellValue(sheet, currentRow, 1, categoryType.name());
                    currentRow++;
                }

                // 카테고리 기사 데이터 삽입
                for (ReportCategory category : categories) {
                    for (ReportArticle article : category.getReportArticles()) {
                        setCellValue(sheet, currentRow, 1, String.valueOf(currentRow - 5)); // 번호
                        setCellValue(sheet, currentRow, 2, article.getPublishDate());      // 날짜
                        setCellValue(sheet, currentRow, 3, article.getKeyword());         // 키워드
                        setCellValue(sheet, currentRow, 4, article.getTitle());           // 제목
                        setCellValue(sheet, currentRow, 5, article.getUrl());             // URL
                        setCellValue(sheet, currentRow, 6, article.getPublisherName());   // Publisher
                        currentRow++;
                    }
                }
            }

            // 로컬 임시 파일 생성
            File tempFile = File.createTempFile("report_" + reportId, ".xlsx");
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                workbook.write(fos);
            }

            return tempFile;

        } catch (Exception e) {
            throw new RuntimeException("엑셀 파일 생성 중 오류 발생", e);
        }
    }

    /**
     * 엑셀 파일 다운로드 (HTTP 응답)
     * @param reportId 보고서 ID
     * @param response HTTP 응답 객체
     */
    @Override
    public void generateExcel(Long reportId, HttpServletResponse response) {
        try {
            File excelFile = createExcelFile(reportId); // 파일 생성

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment;filename=report.xlsx");

            // 파일을 HTTP 응답으로 전송
            try (OutputStream outputStream = response.getOutputStream();
                 FileInputStream fis = new FileInputStream(excelFile)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }

            // 로컬 파일 삭제
            if (excelFile.exists()) {
                excelFile.delete();
            }

        } catch (Exception e) {
            throw new RuntimeException("엑셀 파일 다운로드 중 오류 발생", e);
        }
    }

    // 셀 병합 유틸리티
    private void mergeCells(Sheet sheet, int firstRow, int lastRow, int firstCol, int lastCol) {
        CellRangeAddress cellRangeAddress = new CellRangeAddress(firstRow, lastRow, firstCol, lastCol);
        sheet.addMergedRegion(cellRangeAddress);

        Workbook workbook = sheet.getWorkbook();
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        Row row = sheet.getRow(firstRow);
        if (row == null) {
            row = sheet.createRow(firstRow);
        }
        Cell cell = row.getCell(firstCol);
        if (cell == null) {
            cell = row.createCell(firstCol);
        }
        cell.setCellStyle(style);
    }

    // 셀 값 설정 유틸리티
    private void setCellValue(Sheet sheet, int rowNumber, int columnNumber, String value) {
        Row row = sheet.getRow(rowNumber);
        if (row == null) {
            row = sheet.createRow(rowNumber);
        }
        Cell cell = row.getCell(columnNumber);
        if (cell == null) {
            cell = row.createCell(columnNumber);
        }
        cell.setCellValue(value);
    }
}