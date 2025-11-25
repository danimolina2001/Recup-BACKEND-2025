package com.recup.backend.repo;

import com.recup.backend.domain.Customer;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

public class CustomerRepository {
    private final EntityManager em;

    public CustomerRepository(EntityManager em) {
        this.em = em;
    }

    public Customer save(Customer customer) {
        if (customer.getId() == null) {
            em.persist(customer);
            return customer;
        } else {
            return em.merge(customer);
        }
    }

    public Optional<Customer> findById(Long id) {
        return Optional.ofNullable(em.find(Customer.class, id));
    }

    public List<Customer> findAll() {
        return em.createQuery("SELECT c FROM Customer c", Customer.class).getResultList();
    }

    public void delete(Customer customer) {
        em.remove(em.contains(customer) ? customer : em.merge(customer));
    }
}
