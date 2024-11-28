package the_monitor.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import the_monitor.domain.enums.CategoryType;
import the_monitor.domain.model.Article;
import the_monitor.domain.model.Keyword;
import the_monitor.infrastructure.persistence.JpaArticleRepository;

import java.util.List;

public interface ArticleRepository extends JpaArticleRepository {

    Page<Article> findByKeyword(Keyword keyword, Pageable pageable);

    @Query("SELECT a FROM Article a " +
            "JOIN a.keyword k " +
            "JOIN k.category c " +
            "WHERE c.client.id = :clientId " +
            "AND c.categoryType = :categoryType")
    Page<Article> findByClientIdAndCategoryType(@Param("clientId") Long clientId,
                                                @Param("categoryType") CategoryType categoryType,
                                                Pageable pageable);

//    List<Article> findAllByKeyword_Category_Client_IdAndScrappedTrue(Long clientId);

    @Modifying
    @Query("DELETE FROM Article a WHERE a.keyword.category.client.id = :clientId")
    void deleteByClientId(@Param("clientId") Long clientId);

    @Modifying
    @Query("UPDATE Article a SET a.scrapped = false WHERE a.id IN :articleIds")
    void updateScrappedStatusToFalse(@Param("articleIds") List<Long> articleIds);

}