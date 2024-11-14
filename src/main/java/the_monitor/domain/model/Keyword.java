package the_monitor.domain.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
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

    @Column(name = "keyword_result_count", nullable = false)
    private int resultCount;

    @OneToMany(mappedBy = "keyword", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Article> articles;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    @JsonBackReference
    private Category category;

    @Builder
    public Keyword(String keyword,
                   String keywordType,
                   Category category,
                   int resultCount) {

        this.keyword = keyword;
        this.keywordType = keywordType;
        this.category = category;
        this.resultCount = resultCount;

    }

    public void setCategory(Category category) {
        this.category = category;
    }

}
