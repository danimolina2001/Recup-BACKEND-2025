package com.recup.backend.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "EMPLOYEES")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "employee_seq")
    @SequenceGenerator(name = "employee_seq", sequenceName = "SEQ_EMPLOYEE_ID", allocationSize = 1)
    @Column(name = "EMPLOYEE_ID")
    private Long id;

    @Column(name = "LAST_NAME", length = 20)
    private String lastName;

    @Column(name = "FIRST_NAME", length = 20)
    private String firstName;

    @Column(name = "TITLE", length = 30)
    private String title;

    @ManyToOne
    @JoinColumn(name = "REPORTS_TO")
    private Employee reportsTo;

    @Column(name = "BIRTH_DATE")
    private LocalDate birthDate;

    @Column(name = "HIRE_DATE")
    private LocalDate hireDate;

    @Column(name = "ADDRESS", length = 70)
    private String address;

    @Column(name = "CITY", length = 40)
    private String city;

    @Column(name = "STATE", length = 40)
    private String state;

    @Column(name = "COUNTRY", length = 40)
    private String country;

    @Column(name = "POSTAL_CODE", length = 10)
    private String postalCode;

    @Column(name = "PHONE", length = 24)
    private String phone;

    @Column(name = "FAX", length = 24)
    private String fax;

    @Column(name = "EMAIL", length = 60)
    private String email;

    @OneToMany(mappedBy = "supportRep")
    private List<Customer> customers;
}
