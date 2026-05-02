package EffectiveMobile.auth_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "confirmed", nullable = false)
    private boolean confirmed;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "time_created", updatable = false)
    @CreationTimestamp
    private Instant timeCreated;

    @Column(name = "time_updated")
    @UpdateTimestamp
    private Instant timeUpdated;
}
