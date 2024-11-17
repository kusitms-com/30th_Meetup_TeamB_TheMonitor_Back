package the_monitor.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import the_monitor.domain.enums.CategoryType;
import the_monitor.domain.model.Keyword;

import java.util.List;

@Repository
public interface KeywordRepository extends JpaRepository<Keyword, Long> {

    Keyword findKeywordById(Long id);

    @Query("SELECT k FROM Keyword k " +
            "JOIN k.category c " +
            "JOIN c.client cl " +
            "JOIN cl.account ac " +
            "WHERE ac.id = :accountId " +
            "AND cl.id = :clientId")
    List<Keyword> findKeywordByAccountIdAndClientId(Long accountId, Long clientId);

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

}