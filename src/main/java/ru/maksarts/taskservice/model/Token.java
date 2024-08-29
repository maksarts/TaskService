package ru.maksarts.taskservice.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "token")
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @OneToOne
    @JoinColumn(name = "employee_email", referencedColumnName = "email", unique = true)
    private Employee employee;

    @Column(name = "token", nullable = false, unique = true)
    private String token;
}
