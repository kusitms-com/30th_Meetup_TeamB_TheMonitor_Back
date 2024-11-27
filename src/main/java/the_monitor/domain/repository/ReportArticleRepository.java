package the_monitor.domain.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import the_monitor.domain.model.ReportArticle;
import the_monitor.infrastructure.persistence.JpaReportArticleRepository;

import java.util.List;

@Repository
public interface ReportArticleRepository extends JpaReportArticleRepository {

    @Query("SELECT ra FROM ReportArticle ra " +
            "JOIN ra.reportCategory rc " +
            "JOIN rc.report r " +
            "WHERE r.id = :reportId")
    List<ReportArticle> findByReportId(@Param("reportId") Long reportId);

}