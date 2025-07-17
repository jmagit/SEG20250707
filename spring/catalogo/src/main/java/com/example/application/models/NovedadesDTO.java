package com.example.application.models;

import java.util.List;

import com.example.domain.entities.Category;
import com.example.domain.entities.Film;
import com.example.domain.entities.Language;
import com.example.domain.entities.models.ActorDTO;
import com.example.domain.entities.models.FilmShort;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class NovedadesDTO {
	private List<FilmShort> films;
	private List<ActorDTO> actors;
	private List<Category> categories;
	private List<Language> languages;
	
}
