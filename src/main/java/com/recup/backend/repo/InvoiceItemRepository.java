package com.recup.backend.repo;

import com.recup.backend.domain.InvoiceItem;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

public class InvoiceItemRepository {
    private final EntityManager em;

    public InvoiceItemRepository(EntityManager em) {
        this.em = em;
    }

    public InvoiceItem save(InvoiceItem invoiceItem) {
        if (invoiceItem.getId() == null) {
            em.persist(invoiceItem);
            return invoiceItem;
        } else {
            return em.merge(invoiceItem);
        }
    }

    public Optional<InvoiceItem> findById(Long id) {
        return Optional.ofNullable(em.find(InvoiceItem.class, id));
    }

    public List<InvoiceItem> findAll() {
        return em.createQuery("SELECT ii FROM InvoiceItem ii", InvoiceItem.class).getResultList();
    }

    public void delete(InvoiceItem invoiceItem) {
        em.remove(em.contains(invoiceItem) ? invoiceItem : em.merge(invoiceItem));
    }
}
