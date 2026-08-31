package com.senin.leadgen.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * Bir işletme kaydının pipeline içindeki durumunu takip eder.
 * TODO: alanları ihtiyaca göre genişlet (status enum: FOUND, SITE_GENERATED,
 * EMAIL_SENT, FAILED vb. - state machine gibi düşünebilirsin).
 */
@Entity
@Table(name = "leads")
public class Lead {

    @Id
    @GeneratedValue
    private Long id;

    private String placeId;
    private String displayName;
    private String status;
    private Instant createdAt;
    private Instant updatedAt;

    protected Lead() {
        // JPA için
    }

    public Lead(String placeId, String displayName, String status) {
        this.placeId = placeId;
        this.displayName = displayName;
        this.status = status;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    // TODO: getter/setter (Lombok @Getter/@Setter kullanmak istersen ekle)
}
