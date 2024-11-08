package the_monitor.domain.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.repository.query.Query;
import the_monitor.domain.model.Article;
import the_monitor.infrastructure.persistence.JpaArticleRepository;

import java.util.List;

public interface ArticleRepository extends JpaArticleRepository {

    @Query("SELECT a FROM Article a " +
            "JOIN a.keyword k " +
            "JOIN k.category c " +
            "JOIN c.client cl " +
            "JOIN cl.account ac " +
            "WHERE ac.accountId = :accountId " +
            "AND (:categoryId IS NULL OR c.categoryId = :categoryId)")
    Page<Article> findAllByAccountIdAndAndCategoryId(
            @Param("accountId") Long accountId,
            @Param("categoryId") Long categoryId,
            Pageable pageable);

}
