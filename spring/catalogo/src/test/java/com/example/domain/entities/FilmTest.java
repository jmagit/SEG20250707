package com.example.domain.entities;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.example.domain.entities.Film.Rating;

class FilmTest {

	@Test
	void validTest() {
		Film film = new Film(1, "Peli", new Language(1), (byte) 5, new BigDecimal("0.99"), new BigDecimal("20.95"));
		assertEquals("", film.getErrorsMessage());
	}

	@Test
	void invalidTest() {
		Film film = new Film(1, "  ", new Language(1), (byte) 0, new BigDecimal("0.99"), new BigDecimal("20.95"));
		assertEquals("ERRORES: rentalDuration: debe ser mayor que 0. title: no debe estar vacío.",
				film.getErrorsMessage());
	}

	@Test
	void mergeTest() {
		Film source = new Film(1, "uno", "uno", (short) 1, new Language(1), new Language(1), (byte) 1,
				new BigDecimal("1.0"), 1, new BigDecimal("1.0"), Rating.GENERAL_AUDIENCES);
		source.addActor(1);
		source.addActor(2);
		source.addCategory(1);
		Film targer = new Film(2, "dos", "dos", (short) 2, new Language(2), new Language(2), (byte) 2,
				new BigDecimal("2.0"), 2, new BigDecimal("2.0"), Rating.ADULTS_ONLY);
		targer.addActor(2);
		targer.addActor(3);
		targer.addCategory(2);
		Film actual = source.merge(targer);
		assertNotNull(actual);
		assertAll("Propiedades", 
				() -> assertEquals(2, actual.getFilmId()),
				() -> assertEquals(source.getTitle(), actual.getTitle()),
				() -> assertEquals(source.getDescription(), actual.getDescription()),
				() -> assertEquals(source.getReleaseYear(), actual.getReleaseYear()),
				() -> assertEquals(source.getLanguage(), actual.getLanguage()),
				() -> assertEquals(source.getLanguageVO(), actual.getLanguageVO()),
				() -> assertEquals(source.getRentalDuration(), actual.getRentalDuration()),
				() -> assertEquals(source.getRentalRate(), actual.getRentalRate()),
				() -> assertEquals(source.getLength(), actual.getLength()),
				() -> assertEquals(source.getReplacementCost(), actual.getReplacementCost()),
				() -> assertEquals(source.getRating(), actual.getRating())
				);
		assertArrayEquals(source.getActors().stream().map(a -> a.getActorId()).toArray(), actual.getActors().stream().map(a -> a.getActorId()).toArray(), "Actors");
		assertArrayEquals(source.getCategories().stream().map(a -> a.getCategoryId()).toArray(), actual.getCategories().stream().map(a -> a.getCategoryId()).toArray(), "Categories");
	}

}
