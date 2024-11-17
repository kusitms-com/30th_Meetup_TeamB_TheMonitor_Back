package the_monitor.domain.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
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

    @Column(name = "keyword_result_count")
    private int resultCount;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    @JsonBackReference
    private Category category;

    @Builder
    public Keyword(String keyword,
                   Category category,
                   int resultCount) {

        this.keyword = keyword;
        this.category = category;
        this.resultCount = resultCount;

    }

    public void setCategory(Category category) {
        this.category = category;
    }

}
