package com.recup.backend.infra;

import com.recup.backend.domain.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.math.BigDecimal;

/**
 * Inserta datos mínimos de prueba si la base está vacía.
 */
public class DataSeeder {
    public static void seedIfEmpty(EntityManager em) {
        Long artistCount = em.createQuery("SELECT COUNT(a) FROM Artist a", Long.class).getSingleResult();
        if (artistCount != 0) {
            return; // Ya hay datos
        }
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        try {
            Artist a1 = new Artist();
            a1.setName("AC/DC");
            em.persist(a1);

            Artist a2 = new Artist();
            a2.setName("Miles Davis");
            em.persist(a2);

            Artist a3 = new Artist();
            a3.setName("The Beatles");
            em.persist(a3);

            Album album1 = new Album();
            album1.setTitle("Back in Black");
            album1.setArtist(a1);
            em.persist(album1);

            Album album2 = new Album();
            album2.setTitle("Kind of Blue");
            album2.setArtist(a2);
            em.persist(album2);

            Genre rock = new Genre();
            rock.setName("Rock");
            em.persist(rock);

            Genre jazz = new Genre();
            jazz.setName("Jazz");
            em.persist(jazz);

            MediaType mp3 = new MediaType();
            mp3.setName("MP3");
            em.persist(mp3);

            Track t1 = new Track();
            t1.setName("Hells Bells");
            t1.setAlbum(album1);
            t1.setGenre(rock);
            t1.setMediaType(mp3);
            t1.setMilliseconds(312000);
            t1.setUnitPrice(new BigDecimal("0.99"));
            em.persist(t1);

            Track t2 = new Track();
            t2.setName("So What");
            t2.setAlbum(album2);
            t2.setGenre(jazz);
            t2.setMediaType(mp3);
            t2.setMilliseconds(545000);
            t2.setUnitPrice(new BigDecimal("1.29"));
            em.persist(t2);

            Playlist p = new Playlist();
            p.setName("Favoritos");
            em.persist(p);

            PlaylistTrack pt1 = new PlaylistTrack();
            pt1.setPlaylist(p);
            pt1.setTrack(t1);
            em.persist(pt1);

            PlaylistTrack pt2 = new PlaylistTrack();
            pt2.setPlaylist(p);
            pt2.setTrack(t2);
            em.persist(pt2);

            tx.commit();
            System.out.println("🌱 Datos de prueba insertados (3 artistas, 2 álbumes, 2 tracks, playlist)");
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }
}
