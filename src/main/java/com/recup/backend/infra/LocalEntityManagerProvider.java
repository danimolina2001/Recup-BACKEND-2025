package com.recup.backend.infra;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * Proveedor de EntityManager (JPA).
 * 
 * Configuración:
 * - Usa EntityManagerFactory con Hibernate y H2
 * - hibernate.hbm2ddl.auto=none (el DDL lo ejecuta DbInitializer)
 * - show_sql y format_sql habilitados para debug
 * - Usa el MISMO DataSource que DataSourceProvider (consistencia)
 * 
 * IMPORTANTE: Mantener nombre coherente con materiales de referencia
 * para reutilización en el día del parcial.
 */
public class LocalEntityManagerProvider {
    private static EntityManagerFactory emf;

    /**
     * Obtiene un EntityManager configurado con Hibernate + H2.
     * Usa el mismo DataSource que DataSourceProvider.
     * 
     * @return EntityManager nuevo
     */
    public static EntityManager getEntityManager() {
        if (emf == null) {
            // Obtener el DataSource centralizado
            DataSource ds = DataSourceProvider.getDataSource();
            
            // Configurar propiedades de Hibernate programáticamente
            Map<String, Object> properties = new HashMap<>();
            
            // Usar el DataSource de DataSourceProvider (mismo para JDBC y JPA)
            properties.put("javax.sql.DataSource", ds);
            properties.put("jakarta.persistence.nonJtaDataSource", ds);
            
            // Hibernate: NO generar DDL (ya ejecutado por DbInitializer)
            properties.put("hibernate.hbm2ddl.auto", "none");
            
            // Debug SQL (opcional, útil para desarrollo)
            properties.put("hibernate.show_sql", "true");
            properties.put("hibernate.format_sql", "true");
            
            // Dialecto H2
            properties.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
            
            // Crear EntityManagerFactory con estas propiedades
            emf = Persistence.createEntityManagerFactory("recup-pu", properties);
        }
        return emf.createEntityManager();
    }

    /**
     * Obtiene la EntityManagerFactory (útil para operaciones avanzadas).
     */
    public static EntityManagerFactory getEntityManagerFactory() {
        if (emf == null) {
            getEntityManager(); // Inicializar si no existe
        }
        return emf;
    }

    /**
     * Cierra la EntityManagerFactory (al finalizar la aplicación).
     */
    public static void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}
