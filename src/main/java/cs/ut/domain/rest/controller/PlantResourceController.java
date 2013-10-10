package cs.ut.domain.rest.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cs.ut.domain.Plant;
import cs.ut.domain.rest.PlantResource;
import cs.ut.domain.rest.PlantResourceAssembler;

@Controller
@RequestMapping("/rest/plant")
public class PlantResourceController {
	
	@RequestMapping("{id}")
	public ResponseEntity<PlantResource> getPlantResource(@PathVariable("id") Long id){
		Plant p = Plant.findPlant(id);
		PlantResourceAssembler assembler = new PlantResourceAssembler();
		PlantResource res = assembler.getPlantResource(p);
		ResponseEntity<PlantResource> response = new ResponseEntity<>(res, HttpStatus.OK);
		return response;
	}
	
}
