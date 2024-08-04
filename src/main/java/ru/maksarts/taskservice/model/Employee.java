package ru.maksarts.taskservice.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import jakarta.persistence.*;

@Entity
@Table(name = "employee")
@Data
public class Employee {

    @Id
    @NotNull
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @NotNull
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @NotNull
    @Size(min = 8)
    @Column(name = "password", nullable = false)
    private String password;
}
