package the_monitor.application.serviceImpl;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import the_monitor.application.dto.ReportArticleDto;
import the_monitor.application.service.ExcelService;
import the_monitor.application.service.S3Service;
import the_monitor.domain.enums.CategoryType;
import the_monitor.domain.model.*;
import the_monitor.domain.repository.KeywordRepository;
import the_monitor.domain.repository.ReportRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExcelServiceImpl implements ExcelService {

    private final ReportRepository reportRepository;
    private final KeywordRepository keywordRepository;
    private final S3Service s3Service;

    private static final String TEMPLATE_PATH = "/templates/template.xlsx";

    @Override
    public void generateExcel(Long reportId, HttpServletResponse response) {
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
                // 1. 해당 카테고리의 기사 가져오기
                List<ReportCategory> categories = reportCategories.stream()
                        .filter(category -> category.getCategoryType() == categoryType)
                        .collect(Collectors.toList());

                if (categories.isEmpty()) {
                    continue; // 해당 카테고리 기사 없으면 스킵
                }

                // 2. 카테고리 타이틀 삽입 (셀 병합)
                if (categoryType != CategoryType.SELF) { // SELF는 첫 행에 있으므로 스킵
                    currentRow++; // 한 줄 띄우기
                    mergeCells(sheet, currentRow, currentRow, 1, 5); // B~F 병합
                    setCellValue(sheet, currentRow, 1, categoryType.name()); // 카테고리명 삽입
                    currentRow++;
                }

                // 3. 카테고리 기사 데이터 삽입
                for (ReportCategory category : categories) {
                    for (ReportArticle article : category.getReportArticles()) {
                        setCellValue(sheet, currentRow, 1, String.valueOf(currentRow - 5)); // 번호
                        setCellValue(sheet, currentRow, 2, article.getPublishDate()); // 날짜
                        setCellValue(sheet, currentRow, 3, article.getKeyword()); // 키워드
                        setCellValue(sheet, currentRow, 4, article.getTitle()); // 기사 제목
                        setCellValue(sheet, currentRow, 5, article.getUrl()); // URL
                        setCellValue(sheet, currentRow, 6, article.getPublisherName()); // Publisher
                        setCellValue(sheet, currentRow, 7, article.getReporterName()); // Reporter
                        currentRow++;
                    }
                    // 카테고리 섹션 끝에 셀 병합 및 제목 추가
                    if (category.getCategoryType() == CategoryType.COMPETITOR) {
                        mergeCells(sheet, currentRow, currentRow, 1, 6); // B~G 병합
                        setCellValue(sheet, currentRow, 1, "경쟁사");
                    } else if (category.getCategoryType() == CategoryType.INDUSTRY) {
                        mergeCells(sheet, currentRow, currentRow, 1, 6); // B~G 병합
                        setCellValue(sheet, currentRow, 1, "산업");
                    }
                    currentRow++;
                }
            }

            // 엑셀 파일 로컬 저장
            File tempFile = File.createTempFile("report_" + reportId, ".xlsx");
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                workbook.write(fos);
            }

            // S3에 업로드
            String prefix = "reports/" + report.getClient().getId() + "/";
            String s3FileUrl = s3Service.uploadFileWithKey(prefix, tempFile);

            // 로컬 임시 파일 삭제
            if (tempFile.exists()) {
                tempFile.delete();
            }

            // 응답에 엑셀 파일 작성
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment;filename=report.xlsx");

            try (OutputStream outputStream = response.getOutputStream()) {
                workbook.write(outputStream);
            }

        } catch (Exception e) {
            throw new RuntimeException("엑셀 파일 생성 중 오류 발생", e);
        }
    }

    // 셀 병합 유틸리티
    private void mergeCells(Sheet sheet, int firstRow, int lastRow, int firstCol, int lastCol) {
        CellRangeAddress cellRangeAddress = new CellRangeAddress(firstRow, lastRow, firstCol, lastCol);
        sheet.addMergedRegion(cellRangeAddress);

        // 병합된 셀 스타일 설정 (중앙 정렬)
        Workbook workbook = sheet.getWorkbook();
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        // 병합된 영역의 첫 번째 셀에 스타일 적용
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