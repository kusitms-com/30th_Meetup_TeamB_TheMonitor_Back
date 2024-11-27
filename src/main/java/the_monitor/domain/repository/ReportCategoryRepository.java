package the_monitor.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import the_monitor.domain.enums.CategoryType;
import the_monitor.domain.model.ReportCategory;

import java.util.List;

@Repository
public interface ReportCategoryRepository extends JpaRepository<ReportCategory, Long> {

    List<ReportCategory> findByReportId(Long reportId);

    ReportCategory findByIdAndReportId(Long reportCategoryId, Long reportId);

    ReportCategory findByCategoryTypeAndIsDefault(CategoryType categoryType, boolean isDefault);

}
