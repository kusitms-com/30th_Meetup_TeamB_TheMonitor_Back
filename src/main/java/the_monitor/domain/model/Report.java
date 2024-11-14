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
@Table(name = "accounts")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Report extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long id;

    @Column(name = "report_title", nullable = false)
    private String title;

    @Column(name = "report_logo", nullable = false)
    private String logo;

    @Column(name = "report_color", nullable = false)
    private String color;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ReportArticle> reportArticles;

    @Builder
    public Report(String title,
                  Client client,
                  String logo,
                  String color,
                  List<ReportArticle> reportArticles) {

        this.title = title;
        this.client = client;
        this.logo = logo;
        this.color = color;
        this.reportArticles = reportArticles;

    }

    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateLogo(String logo) {
        this.logo = logo;
    }

    public void updateColor(String color) {
        this.color = color;
    }

}
