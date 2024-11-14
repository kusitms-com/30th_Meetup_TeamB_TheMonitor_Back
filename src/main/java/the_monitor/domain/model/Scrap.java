package the_monitor.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import the_monitor.common.BaseTimeEntity;
import the_monitor.domain.enums.CategoryType;

@Entity
@Getter
@Table(name = "scraps")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Scrap extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "scrap_id")
    private Long id;

    @Column(name = "scrap_title", nullable = false)
    private String title;

    @Column(name = "scrap_url", nullable = false)
    private String url;

    @Column(name = "scrap_publisher_name", nullable = false)
    private String publisherName;

    @Column(name = "scrap_reporter_name", nullable = false)
    private String reporterName;

    @Column(name = "scrap_publish_date", nullable = false)
    private String publishDate;

    @Column(name = "scrap_category", nullable = false)
    private CategoryType category;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Builder
    public Scrap(String title,
                 String url,
                 String publisherName,
                 String reporterName,
                 String publishDate,
                 CategoryType category,
                 Client client) {

        this.title = title;
        this.url = url;
        this.publisherName = publisherName;
        this.reporterName = reporterName;
        this.publishDate = publishDate;
        this.category = category;
        this.client = client;

    }


}
