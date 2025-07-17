package com.example.domain.entities.models;

import com.example.domain.entities.Film;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;

@Schema(name = "Pelicula (Corto)", description = "Version corta de las peliculas")
public record FilmShort(
		@JsonProperty("id") 
		@Schema(description = "Identificador de la pelicula", accessMode = AccessMode.READ_ONLY)
		int filmId, 
		@JsonProperty("titulo") 
		@Schema(description = "Titulo de la pelicula")
		String title) {
	public static FilmShort from(Film source) {
		return new FilmShort(source.getFilmId(), source.getTitle());
	}
}
