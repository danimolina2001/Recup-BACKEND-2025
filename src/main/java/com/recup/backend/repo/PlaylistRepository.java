package com.recup.backend.repo;

import com.recup.backend.domain.Playlist;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

public class PlaylistRepository {
    private final EntityManager em;

    public PlaylistRepository(EntityManager em) {
        this.em = em;
    }

    public Playlist save(Playlist playlist) {
        if (playlist.getId() == null) {
            em.persist(playlist);
            return playlist;
        } else {
            return em.merge(playlist);
        }
    }

    public Optional<Playlist> findById(Long id) {
        return Optional.ofNullable(em.find(Playlist.class, id));
    }

    public List<Playlist> findAll() {
        return em.createQuery("SELECT p FROM Playlist p", Playlist.class).getResultList();
    }

    public Optional<Playlist> findByName(String name) {
        return em.createQuery("SELECT p FROM Playlist p WHERE p.name = :name", Playlist.class)
                .setParameter("name", name)
                .getResultStream()
                .findFirst();
    }

    public void delete(Playlist playlist) {
        em.remove(em.contains(playlist) ? playlist : em.merge(playlist));
    }
}
