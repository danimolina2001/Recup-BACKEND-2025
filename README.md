# RECUP BACKEND (Java + JPA + H2)

Proyecto de preparación para el recuperatorio: estructura en capas, ejecución de DDL externa, uso de JPA/Hibernate sin Spring Boot y DataSource único.

## Arquitectura
- `infra/`: DataSource, EntityManagerProvider, inicializador DDL, seeder.
- `domain/`: Entidades JPA (11 tablas del DER).
- `repo/`: Repositorios JPA simples (EntityManager directo).
- `sql/database-ddl.sql`: DDL con secuencias y tablas.

## Tecnologías
- Java 17
- JPA / Hibernate
- H2 (memoria)
- Lombok

## Flujo de arranque
1. `DbInitializer` ejecuta `sql/database-ddl.sql` vía JDBC.
2. Se crea `EntityManagerFactory` usando el mismo `DataSource` (consistencia).
3. `DataSeeder` inserta datos mínimos si la BD está vacía.
4. Menú interactivo para validación y consultas.

## Ejecución

```powershell
mvn clean compile
mvn exec:java
```

## Menú
| Opción | Acción |
|--------|--------|
| 1 | Placeholder carga CSV (extensible) |
| 2 | Validar conteos de entidades |
| 3 | Consultar primeras entidades (seed auto) |
| S | Salir |

## Seed automático
Si no hay artistas al iniciar, se insertan: 3 artistas, 2 álbumes, 2 tracks, 2 géneros, 1 media type, 1 playlist + relaciones.

## Próximos pasos sugeridos
- Implementar carga CSV real (leer archivos y persistir).
- Agregar validaciones de negocio (longitud, nulos).
- Añadir consultas específicas según consigna del examen.
- Exportar reportes (por ejemplo, totales de facturación, etc.).

## Notas
- `hibernate.hbm2ddl.auto=none` evita generación automática: control total mediante DDL externo.
- Tablas y columnas en MAYÚSCULAS SNAKE_CASE, Java en camelCase.
- Secuencias dedicadas para cada PK.

## Comandos útiles
```powershell
# Ejecutar sólo la app
mvn exec:java

# Limpiar y recompilar
mvn clean compile
```

## Estructura validada
Las 11 tablas: ARTISTS, ALBUMS, TRACKS, GENRES, MEDIA_TYPES, PLAYLISTS, PLAYLIST_TRACK, EMPLOYEES, CUSTOMERS, INVOICES, INVOICE_ITEMS.

## Repositorios
Ejemplo de uso rápido dentro de `App` (futuro):
```java
var em = LocalEntityManagerProvider.getEntityManager();
var artistRepo = new ArtistRepository(em);
artistRepo.findAll().forEach(a -> System.out.println(a.getName()));
```

---
Este README reemplaza el scaffold anterior Node.js que ya no aplica.
