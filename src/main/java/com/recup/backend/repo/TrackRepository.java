package com.recup.backend.repo;

import com.recup.backend.domain.Track;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

public class TrackRepository {
    private final EntityManager em;

    public TrackRepository(EntityManager em) {
        this.em = em;
    }

    public Track save(Track track) {
        if (track.getId() == null) {
            em.persist(track);
            return track;
        } else {
            return em.merge(track);
        }
    }

    public Optional<Track> findById(Long id) {
        return Optional.ofNullable(em.find(Track.class, id));
    }

    public List<Track> findAll() {
        return em.createQuery("SELECT t FROM Track t", Track.class).getResultList();
    }

    public Optional<Track> findByName(String name) {
        return em.createQuery("SELECT t FROM Track t WHERE t.name = :name", Track.class)
                .setParameter("name", name)
                .getResultStream()
                .findFirst();
    }

    public Optional<Track> findByNameAndAlbum(String name, Long albumId) {
        return em.createQuery("SELECT t FROM Track t WHERE t.name = :name AND t.album.id = :albumId", Track.class)
                .setParameter("name", name)
                .setParameter("albumId", albumId)
                .getResultStream()
                .findFirst();
    }

    public void delete(Track track) {
        em.remove(em.contains(track) ? track : em.merge(track));
    }
}
