package com.example.application.services;

import java.util.Date;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.application.models.NovedadesDTO;
import com.example.contracts.application.CatalogoService;
import com.example.contracts.domain.services.ActorService;
import com.example.contracts.domain.services.CategoryService;
import com.example.contracts.domain.services.FilmService;
import com.example.contracts.domain.services.LanguageService;
import com.example.domain.entities.models.ActorDTO;
import com.example.domain.entities.models.FilmShort;

@Service
public class CatalogoServiceImpl implements CatalogoService {
	@Autowired
	private FilmService filmSrv;
	@Autowired
	private ActorService artorSrv;
	@Autowired
	private CategoryService categorySrv;
	@Autowired
	private LanguageService languageSrv;

	@Override
	public NovedadesDTO novedades(Date fecha) {
		// Date fecha = Date.valueOf("2019-01-01 00:00:00");
		if(fecha == null)
			fecha = Date.from(Instant.now().minusSeconds(36000));
		return new NovedadesDTO(
				filmSrv.novedades(fecha).stream().map(item -> new FilmShort(item.getFilmId(), item.getTitle())).toList(), 
				artorSrv.novedades(fecha).stream().map(item -> ActorDTO.from(item)).toList(), 
				categorySrv.novedades(fecha), 
				languageSrv.novedades(fecha)
				);
	}

}
