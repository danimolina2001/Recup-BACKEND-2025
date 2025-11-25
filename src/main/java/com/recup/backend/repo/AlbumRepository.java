package com.recup.backend.repo;

import com.recup.backend.domain.Album;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

public class AlbumRepository {
    private final EntityManager em;

    public AlbumRepository(EntityManager em) {
        this.em = em;
    }

    public Album save(Album album) {
        if (album.getId() == null) {
            em.persist(album);
            return album;
        } else {
            return em.merge(album);
        }
    }

    public Optional<Album> findById(Long id) {
        return Optional.ofNullable(em.find(Album.class, id));
    }

    public List<Album> findAll() {
        return em.createQuery("SELECT a FROM Album a", Album.class).getResultList();
    }

    public Optional<Album> findByTitle(String title) {
        return em.createQuery("SELECT a FROM Album a WHERE a.title = :title", Album.class)
                .setParameter("title", title)
                .getResultStream()
                .findFirst();
    }

    public void delete(Album album) {
        em.remove(em.contains(album) ? album : em.merge(album));
    }
}
