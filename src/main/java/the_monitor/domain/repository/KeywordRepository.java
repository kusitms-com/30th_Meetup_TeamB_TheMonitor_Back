package the_monitor.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import the_monitor.domain.enums.CategoryType;
import the_monitor.domain.model.Keyword;

import java.util.List;
import java.util.Optional;

@Repository
public interface KeywordRepository extends JpaRepository<Keyword, Long> {

    Keyword findKeywordById(Long id);

    @Query("SELECT k FROM Keyword k " +
            "JOIN k.category c " +
            "JOIN c.client cl " +
            "JOIN cl.account ac " +
            "WHERE ac.id = :accountId " +
            "AND cl.id = :clientId")
    List<Keyword> findKeywordByAccountIdAndClientId(@Param("accountId") Long accountId, @Param("clientId") Long clientId);

    @Query("SELECT k FROM Keyword k " +
            "JOIN k.category c " +
            "JOIN c.client cl " +
            "JOIN cl.account ac " +
            "WHERE ac.id = :accountId " +
            "AND cl.id = :clientId " +
            "AND c.categoryType = :categoryType")
    List<Keyword> findKeywordByAccountIdAndClientIdAndCategoryType(@Param("accountId") Long accountId,
                                                                   @Param("clientId") Long clientId,
                                                                   @Param("categoryType") CategoryType categoryType);

    @Query("SELECT k FROM Keyword k " +
            "JOIN k.category c " +
            "JOIN c.client cl " +
            "JOIN cl.account ac " +
            "WHERE k.id = :keywordId " +
            "AND ac.id = :accountId " +
            "AND cl.id = :clientId " +
            "AND c.categoryType = :categoryType")
    Optional<Keyword> findByIdAndAccountIdAndClientIdAndCategoryType(@Param("keywordId") Long keywordId,
                                                                     @Param("accountId") Long accountId,
                                                                     @Param("clientId") Long clientId,
                                                                     @Param("categoryType") CategoryType categoryType);

    @Modifying
    @Query("DELETE FROM Keyword k " +
            "WHERE k.category.client.id = :clientId")
    void deleteAllByClientId(@Param("clientId") Long clientId);

    @Query("SELECT k FROM Keyword k " +
            "JOIN k.category c " +
            "WHERE c.categoryType = :categoryType")
    List<Keyword> findKeywordsByCategoryType(@Param("categoryType") CategoryType categoryType);

}