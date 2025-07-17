package com.example.contracts.domain.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.example.core.contracts.domain.repositories.ProjectionsAndSpecificationJpaRepository;
import com.example.core.domain.exceptions.DuplicateKeyException;
import com.example.core.domain.exceptions.NotFoundException;
import com.example.domain.entities.Actor;

public interface ActorRepository extends ProjectionsAndSpecificationJpaRepository<Actor, Integer> {
	List<Actor> findByLastUpdateGreaterThanEqualOrderByLastUpdate(Date fecha);
	
	default Actor insert(Actor item) throws DuplicateKeyException {
		if(existsById(item.getActorId()))
			throw new DuplicateKeyException();
		return save(item);
	}
	
	default Actor update(Actor item) throws NotFoundException {
		// Con Domain events
		var stored = findById(item.getActorId())
				.orElseThrow(NotFoundException::new);
		BeanUtils.copyProperties(item, stored);
		return save(stored);
		// Sin Domain events
//		if(!existsById(item.getActorId()))
//			throw new NotFoundException();
//		return save(item);
				
	}
}
