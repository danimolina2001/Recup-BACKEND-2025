package com.recup.backend.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Entity
@Table(name = "ARTISTS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Artist {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "artist_seq")
    @SequenceGenerator(name = "artist_seq", sequenceName = "SEQ_ARTIST_ID", allocationSize = 1)
    @Column(name = "ARTIST_ID")
    private Long id;

    @NotBlank
    @Column(name = "NAME", length = 120, nullable = false)
    private String name;

    @OneToMany(mappedBy = "artist")
    private List<Album> albums;
}
