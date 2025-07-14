package com.example.contracts.application;

import java.util.Date;

import com.example.application.models.NovedadesDTO;


public interface CatalogoService {

	NovedadesDTO novedades(Date fecha);

}