package the_monitor.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import the_monitor.common.BaseTimeEntity;

import java.util.List;

@Entity
@Getter
@Table(name = "articles")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Article extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "article_id")
    private Long id;

    @Column(name = "article_title", nullable = false)
    private String title;

    @Column(name = "article_body", nullable = false)
    private String body;

    @Column(name = "article_image", nullable = false)
    private String image;

    @Column(name = "article_url", nullable = true)
    private String url;

    @Column(name = "article_publisher_name", nullable = false)
    private String publisherName;

    @Column(name = "article_reporter_name", nullable = false)
    private String reporterName;

    @Column(name = "article_publish_date", nullable = false)
    private String publishDate;

    @Column(name = "article_is_read", nullable = false)
    private boolean isRead;

    @Column(name = "article_is_duplicate", nullable = false)
    private boolean isDuplicate;

    @Column(name = "article_portal_naver", nullable = true)
    private boolean portalNaver;

    @Column(name = "article_portal_google", nullable = true)
    private boolean portalGoogle;

    @Column(name = "article_portal_daum", nullable = true)
    private boolean portalDaum;

    @Column(name = "article_portal_zum", nullable = true)
    private boolean portalZum;

    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Scrap> scraps;

    @ManyToOne
    @JoinColumn(name = "keyword_id", nullable = false)
    private Keyword keyword;

    @Builder
    public Article(String title,
                   String body,
                   String image,
                   String url,
                   String publisherName,
                   String reporterName,
                   String publishDate,
                   boolean isRead,
                   boolean isDuplicate,
                   boolean portalNaver,
                   boolean portalGoogle,
                   boolean portalDaum,
                   boolean portalZum,
                   Keyword keyword) {

        this.title = title;
        this.body = body;
        this.image = image;
        this.url = url;
        this.publisherName = publisherName;
        this.reporterName = reporterName;
        this.publishDate = publishDate;
        this.isRead = isRead;
        this.isDuplicate = isDuplicate;
        this.portalNaver = portalNaver;
        this.portalGoogle = portalGoogle;
        this.portalDaum = portalDaum;
        this.portalZum = portalZum;
        this.keyword = keyword;

    }

}
