package com.recup.backend.repo;

import com.recup.backend.domain.MediaType;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

public class MediaTypeRepository {
    private final EntityManager em;

    public MediaTypeRepository(EntityManager em) {
        this.em = em;
    }

    public MediaType save(MediaType mediaType) {
        if (mediaType.getId() == null) {
            em.persist(mediaType);
            return mediaType;
        } else {
            return em.merge(mediaType);
        }
    }

    public Optional<MediaType> findById(Long id) {
        return Optional.ofNullable(em.find(MediaType.class, id));
    }

    public List<MediaType> findAll() {
        return em.createQuery("SELECT m FROM MediaType m", MediaType.class).getResultList();
    }

    public Optional<MediaType> findByName(String name) {
        return em.createQuery("SELECT m FROM MediaType m WHERE m.name = :name", MediaType.class)
                .setParameter("name", name)
                .getResultStream()
                .findFirst();
    }

    public void delete(MediaType mediaType) {
        em.remove(em.contains(mediaType) ? mediaType : em.merge(mediaType));
    }
}
