package com.example.contracts.domain.services;

import java.util.Date;
import java.util.List;

import com.example.core.contracts.domain.services.ProjectionDomainService;
import com.example.core.contracts.domain.services.SpecificationDomainService;
import com.example.domain.entities.Film;

public interface FilmService extends ProjectionDomainService<Film, Integer>, SpecificationDomainService<Film, Integer> {
	List<Film> novedades(Date fecha);
}
