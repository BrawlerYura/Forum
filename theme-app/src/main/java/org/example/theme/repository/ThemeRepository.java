package org.example.theme.repository;

import com.sun.istack.NotNull;
import org.example.theme.model.entity.ThemeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.UUID;

public interface ThemeRepository extends JpaRepository<ThemeEntity, UUID> {
    Page<ThemeEntity> findAllByCategoryId(Pageable pageable, UUID categoryId);

    @Query("SELECT t FROM ThemeEntity t WHERE lower(t.themeName) LIKE lower(concat('%', :substring, '%'))")
    List<ThemeEntity> findAllByThemeNameContainingIgnoreCase(@Param("substring") String substring);

    boolean existsById(@NonNull UUID id);
}
