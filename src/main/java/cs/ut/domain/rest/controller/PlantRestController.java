package cs.ut.domain.rest.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import cs.ut.domain.Plant;
import cs.ut.domain.rest.PlantResource;
import cs.ut.domain.rest.PlantResourceAssembler;
import cs.ut.domain.rest.PlantResourceList;

@Controller
@RequestMapping("/rest/plant")
public class PlantRestController {
	
	@RequestMapping(method = RequestMethod.GET, value = "/{id}")
	public ResponseEntity<PlantResource> getPlant(@PathVariable("id") Long id){
		Plant p = Plant.findPlant(id);
		PlantResourceAssembler assembler = new PlantResourceAssembler();
		PlantResource res = assembler.getPlantResource(p);
		ResponseEntity<PlantResource> response = new ResponseEntity<>(res, HttpStatus.OK);
		return response;
	}
	
	
	@RequestMapping(method = RequestMethod.GET, value = "/")
	public ResponseEntity<PlantResourceList> getPlantList(){
		List<Plant> plantList = Plant.findAllPlants();
		PlantResourceAssembler assembler = new PlantResourceAssembler();
		PlantResourceList resList = assembler.getPlantResourceList(plantList);
		ResponseEntity<PlantResourceList> response = new ResponseEntity<>(resList, HttpStatus.OK);
		return response;
	}
}
