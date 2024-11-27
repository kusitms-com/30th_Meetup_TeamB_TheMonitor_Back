package the_monitor.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import the_monitor.domain.model.ReportCategory;

import java.util.List;

@Repository
public interface ReportCategoryRepository extends JpaRepository<ReportCategory, Long> {

    List<ReportCategory> findByReportId(@Param("reportId") Long reportId);

    ReportCategory findByIdAndReportId(@Param("reportCategoryId") Long reportCategoryId, @Param("reportId") Long reportId);


}
