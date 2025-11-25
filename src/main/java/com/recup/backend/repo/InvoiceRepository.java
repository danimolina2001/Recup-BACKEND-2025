package com.recup.backend.repo;

import com.recup.backend.domain.Invoice;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

public class InvoiceRepository {
    private final EntityManager em;

    public InvoiceRepository(EntityManager em) {
        this.em = em;
    }

    public Invoice save(Invoice invoice) {
        if (invoice.getId() == null) {
            em.persist(invoice);
            return invoice;
        } else {
            return em.merge(invoice);
        }
    }

    public Optional<Invoice> findById(Long id) {
        return Optional.ofNullable(em.find(Invoice.class, id));
    }

    public List<Invoice> findAll() {
        return em.createQuery("SELECT i FROM Invoice i", Invoice.class).getResultList();
    }

    public void delete(Invoice invoice) {
        em.remove(em.contains(invoice) ? invoice : em.merge(invoice));
    }
}
