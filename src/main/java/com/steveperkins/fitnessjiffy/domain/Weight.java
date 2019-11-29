package com.steveperkins.fitnessjiffy.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.sql.Date;
import java.util.Optional;
import java.util.UUID;

@Entity
@Table(
        name = "weight",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "date"})
)
public final class Weight {

    @Id
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "date", nullable = false)
    private Date date;

    @Column(name = "pounds", nullable = false)
    private Double pounds;

    public Weight(
            final UUID id,
            final User user,
            final Date date,
            final double pounds
    ) {
        this.id = Optional.ofNullable(id).orElse(UUID.randomUUID());
        this.user = user;
        this.date = (Date) date.clone();
        this.pounds = pounds;
    }

    public Weight() {
    }


    public UUID getId() {
        return id;
    }

    public void setId(final UUID id) {
        this.id = id;
    }


    public User getUser() {
        return user;
    }

    public void setUser(final User user) {
        this.user = user;
    }


    public Date getDate() {
        return (Date) date.clone();
    }

    public void setDate(final Date date) {
        this.date = (Date) date.clone();
    }


    public Double getPounds() {
        return pounds;
    }

    public void setPounds(final Double pounds) {
        this.pounds = pounds;
    }

}
