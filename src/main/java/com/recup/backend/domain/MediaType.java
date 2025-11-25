package com.recup.backend.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Entity
@Table(name = "MEDIA_TYPES")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MediaType {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "media_type_seq")
    @SequenceGenerator(name = "media_type_seq", sequenceName = "SEQ_MEDIA_TYPE_ID", allocationSize = 1)
    @Column(name = "MEDIA_TYPE_ID")
    private Long id;

    @NotBlank
    @Column(name = "NAME", length = 120, nullable = false)
    private String name;

    @OneToMany(mappedBy = "mediaType")
    private List<Track> tracks;
}
