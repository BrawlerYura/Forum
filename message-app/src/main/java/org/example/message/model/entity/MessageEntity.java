package org.example.message.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "message")
public class MessageEntity {

    @Id
    @Column(nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String text;

    @Column(nullable = false)
    private UUID themeId;

    @Column(nullable = false)
    private Instant createDateTime;

    private Instant updateDateTime;

    @Column(nullable = false)
    private UUID creatorId;

    @Column(nullable = false)
    private UUID categoryId;
}
