package com.example.contracts.domain.repositories;

import java.util.Date;
import java.util.List;

import com.example.core.contracts.domain.repositories.ProjectionsAndSpecificationJpaRepository;
import com.example.domain.entities.Film;

public interface FilmRepository extends ProjectionsAndSpecificationJpaRepository<Film, Integer> {
	List<Film> findByLastUpdateGreaterThanEqualOrderByLastUpdate(Date fecha);
}
