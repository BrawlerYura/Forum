package org.example.category.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "category")
public class CategoryEntity {

    @Id
    @Column(nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String categoryName;

    @ManyToOne
    @JoinColumn(name = "parent_id", referencedColumnName = "id")
    private CategoryEntity parentCategory;

    @Column
    @OneToMany(mappedBy = "parentCategory")
    private List<CategoryEntity> childCategories;

    @Column(nullable = false)
    private Instant createDateTime;

    @Column
    private Instant updateDateTime;

    @Column(name = "creator_id", nullable = false)
    private UUID creatorId;
}
