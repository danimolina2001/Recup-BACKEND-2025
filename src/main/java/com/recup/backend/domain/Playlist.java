package com.recup.backend.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Entity
@Table(name = "PLAYLISTS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Playlist {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "playlist_seq")
    @SequenceGenerator(name = "playlist_seq", sequenceName = "SEQ_PLAYLIST_ID", allocationSize = 1)
    @Column(name = "PLAYLIST_ID")
    private Long id;

    @NotBlank
    @Column(name = "NAME", length = 120, nullable = false)
    private String name;

    @OneToMany(mappedBy = "playlist")
    private List<PlaylistTrack> playlistTracks;
}
