package br.com.phc.brasileiraoapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.phc.brasileiraoapi.entity.Equipe;
import br.com.phc.brasileiraoapi.services.EquipeService;
import io.swagger.annotations.Api;

@Api("API de partidas")
@RestController
@RequestMapping("/api/v1/partidas")
public class PartidaController {
	
	

}
