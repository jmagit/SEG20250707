package com.example.contracts.domain.services;

import java.util.Date;
import java.util.List;

import com.example.core.contracts.domain.services.ProjectionDomainService;
import com.example.domain.entities.Actor;

public interface ActorService extends ProjectionDomainService<Actor, Integer> {
	void repartePremios();
	List<Actor> novedades(Date fecha);
}
