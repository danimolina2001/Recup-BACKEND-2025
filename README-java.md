# Recup Backend - Aplicación de Consola (Preparación Parcial)

**Aplicación Java de consola** con Spring Boot + JPA/Hibernate + H2 en memoria que implementa el DER completo de comercialización de música.

## 🎯 Objetivo
Dejar listo todo el **andamiaje técnico** antes del parcial:
- ✅ Base de datos H2 en memoria (embedded)
- ✅ DDL externo: `database-ddl.sql` con tablas y secuencias
- ✅ Mapeo completo de todas las entidades del DER
- ✅ Relaciones configuradas (OneToMany, ManyToOne, ManyToMany)
- ✅ **Lombok** integrado
- ✅ **Convenciones de nombres:** Tablas MAYÚSCULAS SNAKE_CASE, clases UpperCamelCase
- ✅ **Secuencias explícitas** `SEQ_XYZ_ID` para cada tabla
- ✅ **Carga de CSV** lista para usar
- ✅ **Validación** de estructura y datos
- ✅ Seed inicial de prueba

**El día del parcial** solo necesitas agregar los datos del CSV y los procesos específicos solicitados.

## 📦 Stack Técnico
- ✅ Java 17
- ✅ Maven
- ✅ Spring Boot 3.3.4
- ✅ Spring Data JPA
- ✅ Hibernate ORM
- ✅ H2 Database (in-memory / embedded)
- ✅ **Lombok**
- ✅ Bean Validation

## 🗂️ Entidades Implementadas
Según el DER:
- **Artists** (artistas)
- **Albums** (álbumes)
- **Tracks** (canciones/pistas)
- **Genres** (géneros musicales)
- **MediaTypes** (tipos de archivo)
- **Playlists** (listas de reproducción)
- **PlaylistTrack** (relación N:N entre playlists y tracks)
- **Customers** (clientes)
- **Employees** (empleados)
- **Invoices** (facturas)
- **InvoiceItems** (ítems de factura)

## 🚀 Ejecución

```powershell
mvn clean compile exec:java
```

O compilar y ejecutar manualmente:
```powershell
mvn clean package
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

La aplicación iniciará en **modo consola interactivo** con un menú para:
1. Cargar CSVs de cada entidad
2. Validar integridad de datos
3. Consultar entidades

## 📊 Formato CSV Esperado

### artists.csv
```
ArtistId,Name
1,Queen
2,The Beatles
```

### genres.csv
```
GenreId,Name
1,Rock
2,Jazz
```

### albums.csv
```
AlbumId,Title,ArtistId
1,A Night at the Opera,1
```

### tracks.csv
```
TrackId,Name,AlbumId,MediaTypeId,GenreId,Composer,Milliseconds,Bytes,UnitPrice
1,Bohemian Rhapsody,1,1,1,Freddie Mercury,355000,5000000,1.29
```

### employees.csv
```
EmployeeId,LastName,FirstName,Title,ReportsTo,BirthDate,HireDate,...
1,Doe,Jane,Manager,,1980-01-15,2010-05-20,...
```

### customers.csv
```
CustomerId,FirstName,LastName,Company,Address,City,Email,...
1,John,Smith,Acme Inc,123 Main St,NYC,john@example.com,...
```

### invoices.csv
```
InvoiceId,CustomerId,InvoiceDate,BillingAddress,BillingCity,BillingState,BillingCountry,BillingPostalCode,Total
1,1,2024-01-15,123 Main St,NYC,NY,USA,10001,5.99
```

### invoice_items.csv
```
InvoiceLineId,InvoiceId,TrackId,UnitPrice,Quantity
1,1,1,1.29,2
```

## 🔍 Funcionalidades

### 1. Carga de CSV
El servicio `CsvLoaderService` permite cargar datos desde archivos CSV para todas las entidades.
- Valida referencias (FK) automáticamente
- Reporta cantidad de registros cargados

### 2. Validación de Datos
El servicio `ValidationService` verifica:
- Artistas sin nombre
- Álbumes sin artista asociado
- Tracks sin precio
- Facturas sin cliente
- Items sin referencias válidas
- Genera reporte con resumen y errores

### 3. Seed Inicial
La aplicación incluye un seed mínimo de prueba que se carga automáticamente al iniciar para verificar que JPA funciona correctamente.

## 🛠️ Arquitectura

**Patrón de inicialización:**
1. `App.java` → llama a `DbInitializer.initDatabase()`
2. `DbInitializer` → ejecuta `sql/database-ddl.sql` via **JDBC puro**
3. `LocalEntityManagerProvider` → carga `persistence.xml` y obtiene **EntityManager JPA**
4. La app usa JPA para operaciones, **NO genera DDL** (tablas ya creadas por el script)

## 📝 Estructura del Proyecto (según criterio de cátedra)
```
src/main/
├── java/
│   └── com/recup/backend/
│       ├── infra/
│       │   ├── DataSourceProvider.java      # Proveedor JDBC (H2)
│       │   ├── LocalEntityManagerProvider.java  # Proveedor EntityManager (JPA)
│       │   └── DbInitializer.java           # Ejecuta database-ddl.sql via JDBC
│       ├── domain/                          # Entidades JPA (modelo)
│       │   ├── Artist.java
│       │   ├── Album.java
│       │   ├── Track.java
│       │   ├── Genre.java
│       │   ├── MediaType.java
│       │   ├── Playlist.java
│       │   ├── Employee.java
│       │   ├── Customer.java
│       │   ├── Invoice.java
│       │   └── InvoiceItem.java
│       ├── repo/                            # Repositorios JPA (opcional)
│       └── App.java                         # Main: orquesta init + validación
└── resources/
    ├── META-INF/
    │   └── persistence.xml                  # Unidad de persistencia JPA
    └── sql/
        └── database-ddl.sql                 # DDL H2 + secuencias
```

## ⚙️ Configuración (persistence.xml)
```xml
<persistence-unit name="recup-pu">
  <provider>org.hibernate.jpa.HibernatePersistenceProvider</provider>
  <properties>
    <property name="jakarta.persistence.jdbc.url" value="jdbc:h2:mem:recup"/>
    <property name="hibernate.hbm2ddl.auto" value="none"/>  <!-- NO generar DDL -->
    <property name="hibernate.show_sql" value="true"/>
  </properties>
</persistence-unit>
```

El DDL se ejecuta **manualmente** via `DbInitializer` antes de crear el EntityManager.

## 📋 Requisitos Técnicos Cumplidos

✅ **Java 17 o superior, Maven**  
✅ **Librerías:** Lombok, JDBC (via JPA), JPA/Hibernate  
✅ **H2 en memoria (embedded)** obligatorio  
✅ **DDL:** archivo `database-ddl.sql` con estructura de tablas y secuencias adaptada a H2  
✅ **Convenciones de nombres:**  
   - Tablas y columnas: **MAYÚSCULAS SNAKE_CASE** (ej: `ARTISTS`, `CUSTOMER_ID`)
   - Clases Java: **UpperCamelCase** (ej: `Artist`, `Customer`)
   - Campos Java: **lowerCamelCase** (ej: `firstName`, `customerId`)
   - Mapeo con `@Column(name = "COLUMN_NAME")`  
✅ **Secuencias en BD:**  
   - Cada tabla con PK numérica usa su propia secuencia
   - Patrón: `ID_XYZ INTEGER NOT NULL DEFAULT NEXT VALUE FOR SEQ_XYZ_ID`
   - Ejemplo: `SEQ_ARTIST_ID`, `SEQ_CUSTOMER_ID`, etc.

## 🎓 Para el Día del Parcial
1. ✅ El proyecto ya está listo
2. ✅ La BD H2 y JPA están configurados
3. ✅ Todas las entidades mapeadas
4. ✅ CSV loader disponible
5. **Solo agregar:**
   - Cargar el CSV específico del examen
   - Implementar los procesos de negocio solicitados en la consigna

## 🧪 Testing Rápido
Al ejecutar con los datos de seed:
```
Artists:        2
Albums:         2
Tracks:         1
Customers:      1
Invoices:       1
Invoice Items:  1
```

¡Todo listo para el parcial! 🚀
