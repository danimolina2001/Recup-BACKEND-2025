package com.recup.backend.repo;

import com.recup.backend.domain.PlaylistTrack;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

public class PlaylistTrackRepository {
    private final EntityManager em;

    public PlaylistTrackRepository(EntityManager em) {
        this.em = em;
    }

    public PlaylistTrack save(PlaylistTrack playlistTrack) {
        if (playlistTrack.getId() == null) {
            em.persist(playlistTrack);
            return playlistTrack;
        } else {
            return em.merge(playlistTrack);
        }
    }

    public Optional<PlaylistTrack> findById(Long id) {
        return Optional.ofNullable(em.find(PlaylistTrack.class, id));
    }

    public List<PlaylistTrack> findAll() {
        return em.createQuery("SELECT pt FROM PlaylistTrack pt", PlaylistTrack.class).getResultList();
    }

    public boolean existsByPlaylistAndTrack(Long playlistId, Long trackId) {
        Long count = em.createQuery(
                "SELECT COUNT(pt) FROM PlaylistTrack pt WHERE pt.playlist.id = :pid AND pt.track.id = :tid",
                Long.class)
                .setParameter("pid", playlistId)
                .setParameter("tid", trackId)
                .getSingleResult();
        return count != null && count > 0;
    }

    public void delete(PlaylistTrack playlistTrack) {
        em.remove(em.contains(playlistTrack) ? playlistTrack : em.merge(playlistTrack));
    }
}
