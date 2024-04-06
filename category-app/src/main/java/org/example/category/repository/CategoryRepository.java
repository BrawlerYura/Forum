package org.example.category.repository;


import com.sun.istack.NotNull;
import lombok.NonNull;
import org.example.category.model.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import reactor.util.annotation.NonNullApi;

import java.util.List;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<CategoryEntity, UUID> {
    List<CategoryEntity> findAllByParentCategoryIsNull();

    @Query("SELECT c FROM CategoryEntity c WHERE lower(c.categoryName) LIKE lower(concat('%', :substring, '%'))")
    List<CategoryEntity> findAllByCategoryNameContainingIgnoreCase(@Param("substring") String substring);

    boolean existsById(@NonNull UUID id);
}
