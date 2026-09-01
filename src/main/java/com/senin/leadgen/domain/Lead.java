package com.senin.leadgen.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;

/**
 * Bir işletme kaydının pipeline içindeki durumunu takip eder.
 * TODO: alanları ihtiyaca göre genişlet (status enum: FOUND, SITE_GENERATED,
 * EMAIL_SENT, FAILED vb. - state machine gibi düşünebilirsin).
 */
@Entity
@Getter
@Table(name = "leads", uniqueConstraints = @UniqueConstraint(columnNames = "placeId"))
public class Lead {

    @Id
    @GeneratedValue
    private Long id;

    private String placeId;
    private String displayName;

    @Enumerated(EnumType.STRING)
    private LeadStatus status;
    private Instant createdAt;
    private Instant updatedAt;

    protected Lead() {
        // JPA için
    }

    public Lead(String placeId, String displayName, LeadStatus status) {
        this.placeId = placeId;
        this.displayName = displayName;
        this.status = status;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public void updateStatus(LeadStatus newStatus) {
        this.status = newStatus;
        this.updatedAt = Instant.now();
    }

}
