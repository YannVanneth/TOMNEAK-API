package org.yannvanneth.tomneak.userservice.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "addresses")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, columnDefinition = "varchar(50)")
    private String label;

    @Column(nullable = false, columnDefinition = "varchar(255)")
    private String recipientName;

    @Column(nullable = false, columnDefinition = "varchar(255)")
    private String street;

    @Column(nullable = false, columnDefinition = "varchar(255)")
    private String city;

    @Column(nullable = false, columnDefinition = "varchar(255)")
    private String country;

    @Column(nullable = false, columnDefinition = "varchar(255)")
    private String state;

    @Column(nullable = false, columnDefinition = "varchar(255)")
    private String postalCode;

    @Column(nullable = false, columnDefinition = "varchar(255)")
    private String phoneNumber;

    private Boolean isDefault;

    @Column(updatable = false)
    private Instant createdAt;

    @Column(insertable = false)
    private Instant updatedAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;
}
