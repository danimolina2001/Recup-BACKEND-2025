package com.recup.backend.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Tabla intermedia PLAYLIST_TRACK con clave primaria propia.
 * Representa la relación N:N entre Playlists y Tracks.
 */
@Entity
@Table(name = "PLAYLIST_TRACK")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaylistTrack {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "playlist_track_seq")
    @SequenceGenerator(name = "playlist_track_seq", sequenceName = "SEQ_PLAYLIST_TRACK_ID", allocationSize = 1)
    @Column(name = "PLAYLIST_TRACK_ID")
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "PLAYLIST_ID")
    private Playlist playlist;

    @ManyToOne(optional = false)
    @JoinColumn(name = "TRACK_ID")
    private Track track;
}
