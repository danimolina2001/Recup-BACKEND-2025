package com.recup.backend.infra;

import com.recup.backend.domain.*;
import com.recup.backend.repo.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Carga datos desde playlists.csv cumpliendo:
 * - Evitar duplicados por nombre (playlist, track, album, artist, genre, mediaType)
 * - Ignorar líneas con algún campo vacío
 */
public class CsvPlaylistLoader {

    public static void load(EntityManager em, String resourcePath) {
        InputStream is = CsvPlaylistLoader.class.getClassLoader().getResourceAsStream(resourcePath);
        if (is == null) {
            System.out.println("❌ Recurso no encontrado en classpath: " + resourcePath);
            return;
        }

        PlaylistRepository playlistRepo = new PlaylistRepository(em);
        TrackRepository trackRepo = new TrackRepository(em);
        AlbumRepository albumRepo = new AlbumRepository(em);
        ArtistRepository artistRepo = new ArtistRepository(em);
        GenreRepository genreRepo = new GenreRepository(em);
        MediaTypeRepository mediaTypeRepo = new MediaTypeRepository(em);
        PlaylistTrackRepository playlistTrackRepo = new PlaylistTrackRepository(em);

        EntityTransaction tx = em.getTransaction();
        tx.begin();
        final int BATCH_SIZE = 1000; // lote más grande para menos commits
        int sinceLastCommit = 0;
        long startTime = System.nanoTime();
        AtomicInteger processed = new AtomicInteger();
        AtomicInteger skipped = new AtomicInteger();
        AtomicInteger newPlaylists = new AtomicInteger();
        AtomicInteger newTracks = new AtomicInteger();
        AtomicInteger newAlbums = new AtomicInteger();
        AtomicInteger newArtists = new AtomicInteger();
        AtomicInteger newGenres = new AtomicInteger();
        AtomicInteger newMediaTypes = new AtomicInteger();
        AtomicInteger newRelations = new AtomicInteger();

        // Caches en memoria para evitar hits repetidos a la base (por nombre)
        Map<String, Playlist> playlistCache = new ConcurrentHashMap<>();
        Map<String, Artist> artistCache = new ConcurrentHashMap<>();
        Map<String, Album> albumCache = new ConcurrentHashMap<>(); // clave: titulo
        Map<String, Genre> genreCache = new ConcurrentHashMap<>();
        Map<String, MediaType> mediaTypeCache = new ConcurrentHashMap<>();
        Map<String, Track> trackCache = new ConcurrentHashMap<>(); // clave: trackName|albumTitle
        Set<String> playlistTrackRelCache = new HashSet<>(); // clave: playlistName|trackName|albumTitle

        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String header = br.readLine(); // encabezado
            if (header == null) {
                System.out.println("⚠️ Archivo vacío");
                tx.rollback();
                return;
            }
                // Precargar entidades existentes para evitar SELECT por línea
                em.createQuery("SELECT p FROM Playlist p", Playlist.class).getResultList()
                    .forEach(p -> playlistCache.put(p.getName(), p));
                em.createQuery("SELECT a FROM Artist a", Artist.class).getResultList()
                    .forEach(a -> artistCache.put(a.getName(), a));
                em.createQuery("SELECT al FROM Album al", Album.class).getResultList()
                    .forEach(al -> albumCache.put(al.getTitle(), al));
                em.createQuery("SELECT g FROM Genre g", Genre.class).getResultList()
                    .forEach(g -> genreCache.put(g.getName(), g));
                em.createQuery("SELECT m FROM MediaType m", MediaType.class).getResultList()
                    .forEach(m -> mediaTypeCache.put(m.getName(), m));
                em.createQuery("SELECT t FROM Track t", Track.class).getResultList()
                    .forEach(t -> trackCache.put(t.getName() + "|" + (t.getAlbum()!=null ? t.getAlbum().getTitle():""), t));
                em.createQuery("SELECT pt.playlist.name, pt.track.name, pt.track.album.title FROM PlaylistTrack pt", Object[].class)
                    .getResultList()
                    .forEach(row -> playlistTrackRelCache.add(((String)row[0]) + "|" + ((String)row[1]) + "|" + ((String)row[2])));

                String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] cols = line.split(",", -1);
                if (cols.length < 10) { // columnas esperadas
                    skipped.incrementAndGet();
                    continue;
                }
                // Extraer columnas
                String playlistName = cols[0].trim();
                String trackName = cols[1].trim();
                String composer = cols[2].trim();
                String millisecondsStr = cols[3].trim();
                String bytesStr = cols[4].trim();
                String unitPriceStr = cols[5].trim();
                String albumTitle = cols[6].trim();
                String artistName = cols[7].trim();
                String genreName = cols[8].trim();
                String mediaTypeName = cols[9].trim();

                // Validar que ninguno esté vacío
                if (playlistName.isEmpty() || trackName.isEmpty() || composer.isEmpty() || millisecondsStr.isEmpty() ||
                        bytesStr.isEmpty() || unitPriceStr.isEmpty() || albumTitle.isEmpty() || artistName.isEmpty() ||
                        genreName.isEmpty() || mediaTypeName.isEmpty()) {
                    skipped.incrementAndGet();
                    continue;
                }

                // Recuperar/crear entidades usando caches sin SELECT por línea
                Playlist playlist = playlistCache.get(playlistName);
                if (playlist == null) {
                    playlist = new Playlist();
                    playlist.setName(playlistName);
                    playlistRepo.save(playlist);
                    playlistCache.put(playlistName, playlist);
                    newPlaylists.incrementAndGet();
                }

                Artist artist = artistCache.get(artistName);
                if (artist == null) {
                    artist = new Artist();
                    artist.setName(artistName);
                    artistRepo.save(artist);
                    artistCache.put(artistName, artist);
                    newArtists.incrementAndGet();
                }

                Album album = albumCache.get(albumTitle);
                if (album == null) {
                    album = new Album();
                    album.setTitle(albumTitle);
                    album.setArtist(artist);
                    albumRepo.save(album);
                    albumCache.put(albumTitle, album);
                    newAlbums.incrementAndGet();
                }

                Genre genre = genreCache.get(genreName);
                if (genre == null) {
                    genre = new Genre();
                    genre.setName(genreName);
                    genreRepo.save(genre);
                    genreCache.put(genreName, genre);
                    newGenres.incrementAndGet();
                }

                MediaType mediaType = mediaTypeCache.get(mediaTypeName);
                if (mediaType == null) {
                    mediaType = new MediaType();
                    mediaType.setName(mediaTypeName);
                    mediaTypeRepo.save(mediaType);
                    mediaTypeCache.put(mediaTypeName, mediaType);
                    newMediaTypes.incrementAndGet();
                }

                String trackKey = trackName + "|" + albumTitle;
                Track track = trackCache.get(trackKey);
                if (track == null) {
                    track = new Track();
                    track.setName(trackName);
                    track.setComposer(composer);
                    try { track.setMilliseconds(Integer.parseInt(millisecondsStr)); } catch (NumberFormatException ignored) {}
                    try { track.setBytes(Integer.parseInt(bytesStr)); } catch (NumberFormatException ignored) {}
                    try { track.setUnitPrice(new BigDecimal(unitPriceStr)); } catch (Exception ignored) {}
                    track.setAlbum(album);
                    track.setGenre(genre);
                    track.setMediaType(mediaType);
                    trackRepo.save(track);
                    trackCache.put(trackKey, track);
                    newTracks.incrementAndGet();
                }

                String relKey = playlistName + "|" + trackName + "|" + albumTitle;
                if (!playlistTrackRelCache.contains(relKey)) {
                    PlaylistTrack pt = new PlaylistTrack();
                    pt.setPlaylist(playlist);
                    pt.setTrack(track);
                    playlistTrackRepo.save(pt);
                    playlistTrackRelCache.add(relKey);
                    newRelations.incrementAndGet();
                }

                processed.incrementAndGet();
                sinceLastCommit++;

                // Logging de progreso periódico para dar feedback al usuario
                if (processed.get() % 500 == 0) {
                    long elapsedMs = (System.nanoTime() - startTime) / 1_000_000;
                    double lps = processed.get() / ((elapsedMs / 1000.0) + 0.0001);
                    System.out.println("   Progreso: " + processed.get() + " líneas (omitidas=" + skipped.get() + ", " + String.format("%.1f", lps) + " líneas/seg)");
                }

                // Commit por lotes para reducir consumo de memoria y tiempo al final
                if (sinceLastCommit >= BATCH_SIZE) {
                    em.flush(); // sincronizar con BD
                    tx.commit();
                    System.out.println("   ✓ Lote de " + sinceLastCommit + " líneas confirmado. Total acumulado: " + processed.get());
                    sinceLastCommit = 0;
                    tx = em.getTransaction();
                    tx.begin();
                }
            }
            // Commit final si quedó algo pendiente en el último lote
            if (tx.isActive()) {
                em.flush();
                tx.commit();
            }
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            System.out.println("❌ Error procesando CSV: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        System.out.println("\n📥 RESUMEN CARGA playlists.csv");
        System.out.println("   Líneas procesadas: " + processed.get());
        System.out.println("   Líneas omitidas (incompletas): " + skipped.get());
        System.out.println("   Playlists nuevas: " + newPlaylists.get());
        System.out.println("   Tracks nuevos: " + newTracks.get());
        System.out.println("   Álbumes nuevos: " + newAlbums.get());
        System.out.println("   Artistas nuevos: " + newArtists.get());
        System.out.println("   Géneros nuevos: " + newGenres.get());
        System.out.println("   MediaTypes nuevos: " + newMediaTypes.get());
        System.out.println("   Relaciones Playlist-Track nuevas: " + newRelations.get());
        System.out.println("✅ Carga finalizada\n");
        
        // Mostrar resultados requeridos
        mostrarResultados(em);
    }
    
    private static void mostrarResultados(EntityManager em) {
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("2. MOSTRAR LOS SIGUIENTES RESULTADOS:");
        System.out.println("═══════════════════════════════════════════════════════════\n");
        
        // 1. Resultados de la importación
        System.out.println("1. Resultados de la importación:\n");
        
        Long totalTracks = em.createQuery("SELECT COUNT(t) FROM Track t", Long.class).getSingleResult();
        Long totalPlaylists = em.createQuery("SELECT COUNT(p) FROM Playlist p", Long.class).getSingleResult();
        Long totalAlbums = em.createQuery("SELECT COUNT(a) FROM Album a", Long.class).getSingleResult();
        
        System.out.println("   1. Cantidad de Tracks insertados en la base de datos: " + totalTracks);
        System.out.println("   2. Cantidad de Playlists insertados en la base de datos: " + totalPlaylists);
        System.out.println("   3. Cantidad de Albums insertados en la base de datos: " + totalAlbums);
        
        // 2. Top 5 playlists con mayor promedio costo/minuto
        System.out.println("\n2. El nombre (y el valor) de las 5 Playlists con mayor promedio");
        System.out.println("   costo/minuto de sus Tracks:\n");
        
        var top5Playlists = em.createQuery(
            "SELECT p.name, " +
            "SUM(t.unitPrice) / (SUM(t.milliseconds) / 60000.0) as costoMinuto " +
            "FROM Playlist p " +
            "JOIN p.playlistTracks pt " +
            "JOIN pt.track t " +
            "GROUP BY p.id, p.name " +
            "HAVING SUM(t.milliseconds) > 0 " +
            "ORDER BY costoMinuto DESC",
            Object[].class)
            .setMaxResults(5)
            .getResultList();
        
        int rank = 1;
        for (Object[] row : top5Playlists) {
            String playlistName = (String) row[0];
            Double costoMinuto = ((Number) row[1]).doubleValue();
            System.out.printf("   %d. %s: %.2f u$s/min%n", rank++, playlistName, costoMinuto);
        }
        
        // 3. Playlists que únicamente contengan tracks del género Jazz
        System.out.println("\n3. La cantidad de playlist que únicamente contengan tracks del género");
        System.out.println("   2 (Jazz):\n");
        
        // Buscar el ID del género Jazz
        Long jazzGenreId = em.createQuery(
            "SELECT g.id FROM Genre g WHERE LOWER(g.name) = 'jazz'", Long.class)
            .getResultStream()
            .findFirst()
            .orElse(null);
        
        if (jazzGenreId != null) {
            Long playlistsOnlyJazz = em.createQuery(
                "SELECT COUNT(DISTINCT p.id) FROM Playlist p " +
                "WHERE EXISTS (SELECT 1 FROM PlaylistTrack pt1 WHERE pt1.playlist = p) " +
                "AND NOT EXISTS (" +
                "  SELECT 1 FROM PlaylistTrack pt2 JOIN pt2.track t2 " +
                "  WHERE pt2.playlist = p AND (t2.genre IS NULL OR t2.genre.id <> :jazzId)" +
                ")",
                Long.class)
                .setParameter("jazzId", jazzGenreId)
                .getSingleResult();
            
            System.out.println("   Cantidad: " + playlistsOnlyJazz);
        } else {
            System.out.println("   Cantidad: 0 (género Jazz no encontrado)");
        }
        
        System.out.println("\n═══════════════════════════════════════════════════════════\n");
    }
}
