/* package com.grumps.GrumpsWeb.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name="web_users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    private String username;
    private String password;
    private String email;
    @Column(name = "permission_level")
    private Integer permissionLevel;
    @Column(name = "registration_date")
    private Instant registrationDate;
}
*/