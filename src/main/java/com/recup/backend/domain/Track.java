package com.recup.backend.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "TRACKS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Track {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "track_seq")
    @SequenceGenerator(name = "track_seq", sequenceName = "SEQ_TRACK_ID", allocationSize = 1)
    @Column(name = "TRACK_ID")
    private Long id;

    @NotBlank
    @Column(name = "NAME", length = 200, nullable = false)
    private String name;

    @ManyToOne(optional = true)
    @JoinColumn(name = "ALBUM_ID")
    private Album album;

    @ManyToOne(optional = false)
    @JoinColumn(name = "MEDIA_TYPE_ID", nullable = false)
    private MediaType mediaType;

    @ManyToOne(optional = true)
    @JoinColumn(name = "GENRE_ID")
    private Genre genre;

    @Column(name = "COMPOSER", length = 220)
    private String composer;

    @Column(name = "MILLISECONDS")
    private Integer milliseconds;

    @Column(name = "BYTES")
    private Integer bytes;

    @Column(name = "UNIT_PRICE", precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @OneToMany(mappedBy = "track")
    private List<PlaylistTrack> playlistTracks;

    /**
     * Devuelve la duración del track en minutos.
     * @return duración en minutos (con decimales) o 0.0 si no hay información
     */
    public double getDurationInMinutes() {
        return milliseconds != null ? milliseconds / 60000.0 : 0.0;
    }

    /**
     * Valida si el precio unitario es mayor que cero.
     * @return true si el precio es válido (> 0), false en caso contrario
     */
    public boolean hasValidPrice() {
        return unitPrice != null && unitPrice.doubleValue() > 0.0;
    }
}
