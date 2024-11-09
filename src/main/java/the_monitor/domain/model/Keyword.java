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
@Table(name = "keywords")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Keyword extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "keyword_id")
    private Long id;

    @Column(name = "keyword", nullable = false)
    private String keyword;

    @Column(name = "keyword_type", nullable = false)
    private String keywordType;

    @Column(name = "keyword_naver_count", nullable = false)
    private int naverCount;

    @Column(name = "keyword_google_count", nullable = false)
    private int googleCount;

    @Column(name = "keyword_daum_count", nullable = false)
    private int daumCount;

    @OneToMany(mappedBy = "keyword", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Article> articles;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Builder
    public Keyword(String keyword,
                   String keywordType,
                   Category category,
                   int naverCount,
                   int googleCount,
                   int daumCount) {

        this.keyword = keyword;
        this.keywordType = keywordType;
        this.category = category;
        this.naverCount = naverCount;
        this.googleCount = googleCount;
        this.daumCount = daumCount;

    }

    public void setCategory(Category category) {
        this.category = category;
    }

}
