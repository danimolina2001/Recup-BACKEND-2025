package com.recup.backend.infra;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.stream.Collectors;

/**
 * Inicializador de base de datos.
 * Ejecuta el DDL completo (database-ddl.sql) via JDBC antes de usar JPA.
 * 
 * Incluye:
 * - Creación de secuencias (CREATE SEQUENCE ...)
 * - Creación de tablas (todas las tablas del modelo)
 * - Definición de claves primarias y foráneas
 * - Creación de índices (si aplica)
 */
public class DbInitializer {
    
    public static void initDatabase() {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║  INICIALIZACIÓN DE BASE DE DATOS H2                       ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println();
        
        // Usar try-with-resources para cerrar automáticamente todos los recursos
        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             Statement stmt = conn.createStatement()) {
            
            System.out.println("🔗 Conexión H2 establecida: " + conn.getMetaData().getURL());
            
            // Leer database-ddl.sql desde classpath (con try-with-resources)
            InputStream is = DbInitializer.class.getClassLoader()
                    .getResourceAsStream("sql/database-ddl.sql");
            
            if (is == null) {
                throw new RuntimeException("❌ No se encontró sql/database-ddl.sql en el classpath");
            }
            
            String ddl;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                ddl = reader.lines().collect(Collectors.joining("\n"));
            }
            
            System.out.println("📄 Archivo DDL cargado correctamente");
            System.out.println();
            
            // Contadores para feedback
            int sequencesCreated = 0;
            int tablesCreated = 0;
            int constraintsCreated = 0;
            int statementsExecuted = 0;
            
            // Pre-procesar: eliminar comentarios de línea y normalizar espacios
            String normalized = ddl.replaceAll("--.*", "")
                                   .replaceAll("/\\*.*?\\*/", "")
                                   .replaceAll("\\r", "")
                                   .trim();

            // Separar por punto y coma manteniendo CREATE multi-línea
            String[] statements = normalized.split(";");

            for (String raw : statements) {
                String sql = raw.trim();
                if (sql.isEmpty()) {
                    continue;
                }
                // Ejecutar statement
                stmt.execute(sql);
                statementsExecuted++;

                String upperSql = sql.toUpperCase();
                if (upperSql.startsWith("CREATE SEQUENCE")) {
                    sequencesCreated++;
                    System.out.println("   ✓ Secuencia creada");
                } else if (upperSql.startsWith("CREATE TABLE")) {
                    tablesCreated++;
                    String tableName = extractTableName(sql);
                    System.out.println("   ✓ Tabla creada: " + tableName);
                } else if (upperSql.startsWith("ALTER TABLE") || upperSql.contains("FOREIGN KEY")) {
                    constraintsCreated++;
                } else if (upperSql.startsWith("CREATE INDEX")) {
                    System.out.println("   ✓ Índice creado");
                }
            }
            
            System.out.println();
            System.out.println("═══════════════════════════════════════════════════════════");
            System.out.println("📊 RESUMEN DE INICIALIZACIÓN:");
            System.out.println("   Statements ejecutados: " + statementsExecuted);
            System.out.println("   Secuencias creadas:    " + sequencesCreated);
            System.out.println("   Tablas creadas:        " + tablesCreated);
            if (constraintsCreated > 0) {
                System.out.println("   Constraints (FK):      " + constraintsCreated);
            }
            System.out.println("═══════════════════════════════════════════════════════════");
            
            // Validar que las tablas principales existen
            validateDatabaseStructure(conn);
            
            System.out.println();
            System.out.println("✅ Base de datos H2 inicializada correctamente");
            System.out.println();
            
        } catch (Exception e) {
            System.err.println();
            System.err.println("❌ ERROR FATAL inicializando base de datos:");
            System.err.println("   " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Fallo en inicialización de BD", e);
        }
    }
    
    /**
     * Extrae el nombre de la tabla de un CREATE TABLE statement
     */
    private static String extractTableName(String createTableSql) {
        try {
            String upper = createTableSql.toUpperCase();
            int start = upper.indexOf("CREATE TABLE") + 12;
            int ifNotExists = upper.indexOf("IF NOT EXISTS");
            if (ifNotExists > 0) {
                start = ifNotExists + 13;
            }
            String rest = createTableSql.substring(start).trim();
            int end = rest.indexOf("(");
            if (end > 0) {
                return rest.substring(0, end).trim();
            }
            return "???";
        } catch (Exception e) {
            return "???";
        }
    }
    
    /**
     * Valida que las tablas principales y secuencias existen en la BD
     */
    private static void validateDatabaseStructure(Connection conn) throws Exception {
        System.out.println();
        System.out.println("🔍 Validando estructura de la base de datos...");
        
        try (Statement stmt = conn.createStatement()) {
            // Verificar algunas tablas clave
            String[] expectedTables = {"ARTISTS", "ALBUMS", "TRACKS", "CUSTOMERS", "INVOICES"};
            
            for (String table : expectedTables) {
                ResultSet rs = stmt.executeQuery(
                    "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = '" + table + "'"
                );
                if (rs.next() && rs.getInt(1) == 1) {
                    System.out.println("   ✓ Tabla " + table + " existe");
                } else {
                    throw new RuntimeException("Tabla " + table + " no fue creada");
                }
                rs.close();
            }
            
            // Verificar algunas secuencias
            ResultSet rs = stmt.executeQuery(
                "SELECT COUNT(*) FROM INFORMATION_SCHEMA.SEQUENCES WHERE SEQUENCE_NAME LIKE 'SEQ_%'"
            );
            if (rs.next()) {
                int seqCount = rs.getInt(1);
                System.out.println("   ✓ Secuencias detectadas: " + seqCount);
            }
            rs.close();
        }
    }
}
