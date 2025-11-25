package com.recup.backend.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Entity
@Table(name = "GENRES")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Genre {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "genre_seq")
    @SequenceGenerator(name = "genre_seq", sequenceName = "SEQ_GENRE_ID", allocationSize = 1)
    @Column(name = "GENRE_ID")
    private Long id;

    @NotBlank
    @Column(name = "NAME", length = 120, nullable = false)
    private String name;

    @OneToMany(mappedBy = "genre")
    private List<Track> tracks;
}
