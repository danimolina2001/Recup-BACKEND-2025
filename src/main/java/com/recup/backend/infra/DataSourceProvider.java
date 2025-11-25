package com.recup.backend.infra;

import org.h2.jdbcx.JdbcDataSource;
import javax.sql.DataSource;

/**
 * Proveedor de DataSource para H2 en memoria (embedded).
 * 
 * Configuración:
 * - URL: jdbc:h2:mem:recup
 * - DB_CLOSE_DELAY=-1 (mantener BD en memoria hasta que JVM termine)
 * - DB_CLOSE_ON_EXIT=FALSE (no cerrar al salir del último connection)
 * - Usuario: sa (sin contraseña)
 * 
 * Usado por DbInitializer para ejecutar el DDL via JDBC puro.
 */
public class DataSourceProvider {
    private static DataSource dataSource;
    
    // URL H2 según requisitos: jdbc:h2:mem:database;DB_CLOSE_DELAY=-1
    private static final String H2_URL = "jdbc:h2:mem:database;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE";
    private static final String H2_USER = "sa";
    private static final String H2_PASSWORD = "";

    /**
     * Obtiene el DataSource singleton para H2 en memoria.
     * 
     * @return DataSource configurado para H2 embedded
     */
    public static DataSource getDataSource() {
        if (dataSource == null) {
            JdbcDataSource ds = new JdbcDataSource();
            ds.setURL(H2_URL);
            ds.setUser(H2_USER);
            ds.setPassword(H2_PASSWORD);
            dataSource = ds;
        }
        return dataSource;
    }
    
    /**
     * Obtiene la URL de conexión H2 (útil para debugging).
     */
    public static String getUrl() {
        return H2_URL;
    }
}
