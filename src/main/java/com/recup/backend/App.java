package com.recup.backend;

import com.recup.backend.infra.DbInitializer;
import com.recup.backend.infra.LocalEntityManagerProvider;
import com.recup.backend.infra.CsvPlaylistLoader;
import jakarta.persistence.EntityManager;

/**
 * Aplicación principal de consola.
 * Orquesta: init DB (DDL via JDBC) + EntityManager (JPA) + validación.
 */
public class App {
    public static void main(String[] args) {
        try {
            System.out.println("\n");
            System.out.println("╔═══════════════════════════════════════════════════════════╗");
            System.out.println("║    SISTEMA DE GESTIÓN MUSICAL - RECUP BACKEND            ║");
            System.out.println("║    H2 + JPA/Hibernate (estructura según criterio)        ║");
            System.out.println("╚═══════════════════════════════════════════════════════════╝");
            System.out.println();

            // 1. Inicializar BD (ejecutar DDL via JDBC)
            DbInitializer.initDatabase();

            // 2. Obtener EntityManager (JPA)
            EntityManager em = LocalEntityManagerProvider.getEntityManager();
            System.out.println("✅ EntityManager JPA listo");
            System.out.println();

            // 3. Ejecutar importación y mostrar resultados (sin interacción)
            System.out.println("📂 Iniciando carga desde playlists.csv ...");
            CsvPlaylistLoader.load(em, "sample-data/playlists.csv");

            // 4. Cerrar recursos y finalizar
            em.close();
            LocalEntityManagerProvider.close();
            
        } catch (Exception e) {
            System.err.println("❌ Error fatal: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

}
