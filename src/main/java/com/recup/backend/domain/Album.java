package com.recup.backend.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Entity
@Table(name = "ALBUMS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Album {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "album_seq")
    @SequenceGenerator(name = "album_seq", sequenceName = "SEQ_ALBUM_ID", allocationSize = 1)
    @Column(name = "ALBUM_ID")
    private Long id;

    @NotBlank
    @Column(name = "TITLE", length = 160, nullable = false)
    private String title;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ARTIST_ID")
    private Artist artist;

    @OneToMany(mappedBy = "album")
    private List<Track> tracks;
}
