package com.recup.backend.repo;

import com.recup.backend.domain.Genre;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

public class GenreRepository {
    private final EntityManager em;

    public GenreRepository(EntityManager em) {
        this.em = em;
    }

    public Genre save(Genre genre) {
        if (genre.getId() == null) {
            em.persist(genre);
            return genre;
        } else {
            return em.merge(genre);
        }
    }

    public Optional<Genre> findById(Long id) {
        return Optional.ofNullable(em.find(Genre.class, id));
    }

    public List<Genre> findAll() {
        return em.createQuery("SELECT g FROM Genre g", Genre.class).getResultList();
    }

    public Optional<Genre> findByName(String name) {
        return em.createQuery("SELECT g FROM Genre g WHERE g.name = :name", Genre.class)
                .setParameter("name", name)
                .getResultStream()
                .findFirst();
    }

    public void delete(Genre genre) {
        em.remove(em.contains(genre) ? genre : em.merge(genre));
    }
}
