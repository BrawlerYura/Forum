package org.example.message.repository;

import org.example.message.model.entity.MessageEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<MessageEntity, UUID>, JpaSpecificationExecutor<MessageEntity> {
    Page<MessageEntity> findAllByThemeId(Pageable pageable, UUID themeId);

    @Query("SELECT m FROM MessageEntity m WHERE lower(m.text) LIKE lower(concat('%', :substring, '%'))")
    List<MessageEntity> findAllByTextContainingIgnoreCase(@Param("substring") String substring);

//    @Query("SELECT m FROM MessageEntity m " +
//            "WHERE (:searchText IS NULL OR m.text LIKE concat('%', :searchText, '%')) " +
//            "AND (:startDate IS NULL OR m.createDateTime >= :startDate) " +
//            "AND (:endDate IS NULL OR m.createDateTime <= :endDate) " +
//            "AND (:authorLogin IS NULL OR m.creator.login = :authorLogin) " +
//            "AND (:themeId IS NULL OR m.themeId = :themeId) " +
//            "AND (:categoryId IS NULL OR m.theme.category.id = :categoryId)")
//    Page<MessageEntity> findByCriteria(
//            @Param("searchText") String searchText,
//            @Param("startDate") Instant startDate,
//            @Param("endDate") Instant endDate,
//            @Param("authorLogin") String authorLogin,
//            @Param("themeId") UUID themeId,
//            @Param("categoryId") UUID categoryId,
//            Pageable pageable
//    );

    List<MessageEntity> findAll(Specification<MessageEntity> s);
}
