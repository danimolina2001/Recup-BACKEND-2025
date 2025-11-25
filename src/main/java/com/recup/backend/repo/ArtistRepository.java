package com.recup.backend.repo;

import com.recup.backend.domain.Artist;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

public class ArtistRepository {
    private final EntityManager em;

    public ArtistRepository(EntityManager em) {
        this.em = em;
    }

    public Artist save(Artist artist) {
        if (artist.getId() == null) {
            em.persist(artist);
            return artist;
        } else {
            return em.merge(artist);
        }
    }

    public Optional<Artist> findById(Long id) {
        return Optional.ofNullable(em.find(Artist.class, id));
    }

    public List<Artist> findAll() {
        return em.createQuery("SELECT a FROM Artist a", Artist.class).getResultList();
    }

    public Optional<Artist> findByName(String name) {
        return em.createQuery("SELECT a FROM Artist a WHERE a.name = :name", Artist.class)
                .setParameter("name", name)
                .getResultStream()
                .findFirst();
    }

    public void delete(Artist artist) {
        em.remove(em.contains(artist) ? artist : em.merge(artist));
    }
}
