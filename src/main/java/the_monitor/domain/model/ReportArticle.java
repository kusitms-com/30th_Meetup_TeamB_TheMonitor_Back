package the_monitor.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import the_monitor.common.BaseTimeEntity;
import the_monitor.domain.enums.CategoryType;

import java.util.List;

@Entity
@Getter
@Table(name = "report_articles")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReportArticle extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_article_id")
    private Long id;

    @Column(name = "report_article_title", nullable = false)
    private String title;

    @Column(name = "report_article_url", nullable = false)
    private String url;

    @Column(name = "report_article_publisher_name", nullable = false)
    private String publisherName;

    @Column(name = "report_article_reporter_name", nullable = false)
    private String reporterName;

    @Column(name = "report_article_publish_date", nullable = false)
    private String publishDate;

    @Column(name = "report_article_category", nullable = false)
    private CategoryType category;

    @Column(name = "report_article_keyword", nullable = false)
    private String keyword;

    @ManyToOne
    @JoinColumn(name = "report_id", nullable = false)
    private Report report;

    @Builder
    public ReportArticle(String title,
                         String url,
                         String publisherName,
                         String reporterName,
                         String publishDate,
                         CategoryType category,
                         String keyword,
                         Report report) {

        this.title = title;
        this.url = url;
        this.publisherName = publisherName;
        this.reporterName = reporterName;
        this.publishDate = publishDate;
        this.category = category;
        this.keyword = keyword;
        this.report = report;

    }

}
