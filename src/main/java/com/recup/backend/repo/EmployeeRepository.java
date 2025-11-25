package com.recup.backend.repo;

import com.recup.backend.domain.Employee;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

public class EmployeeRepository {
    private final EntityManager em;

    public EmployeeRepository(EntityManager em) {
        this.em = em;
    }

    public Employee save(Employee employee) {
        if (employee.getId() == null) {
            em.persist(employee);
            return employee;
        } else {
            return em.merge(employee);
        }
    }

    public Optional<Employee> findById(Long id) {
        return Optional.ofNullable(em.find(Employee.class, id));
    }

    public List<Employee> findAll() {
        return em.createQuery("SELECT e FROM Employee e", Employee.class).getResultList();
    }

    public void delete(Employee employee) {
        em.remove(em.contains(employee) ? employee : em.merge(employee));
    }
}
