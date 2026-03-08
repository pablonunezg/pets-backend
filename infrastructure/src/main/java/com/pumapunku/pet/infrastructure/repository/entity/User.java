package com.pumapunku.pet.infrastructure.repository.entity;

import com.pumapunku.pet.domain.Role;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "app_user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User
{
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true, length = 100)
    private String username;

    @Column(nullable = false, length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Role role;

    /**
     * Indicates whether the user account is locked.
     * The field is named {@code locked} (not {@code isLocked}) so that Lombok correctly
     * generates the {@code isLocked()} getter and MapStruct can resolve the property.
     * The database column is explicitly mapped with {@code name = "is_locked"}.
     */
    @Column(name = "is_locked", nullable = false)
    private boolean locked;
}
