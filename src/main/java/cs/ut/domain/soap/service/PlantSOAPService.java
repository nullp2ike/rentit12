package cs.ut.domain.soap.service;

import java.util.Date;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlElement;

import org.springframework.beans.factory.annotation.Autowired;

import cs.ut.domain.Plant;
import cs.ut.domain.soap.PlantResourceAssembler;
import cs.ut.domain.soap.PlantResourceList;
import cs.ut.repository.PlantRepository;

@WebService
public class PlantSOAPService {
	
	@Autowired
	PlantRepository repository;
	
	@WebMethod
	@XmlElement
	public PlantResourceList getAllPlants(){
		List<Plant> plantList = Plant.findAllPlants();
		PlantResourceAssembler assembler = new PlantResourceAssembler();
		PlantResourceList resList = assembler.getPlantResourceList(plantList);
		return resList;
	}
	
	@WebMethod
	@XmlElement
	public PlantResourceList getAvailablePlants(Date startDate, Date endDate){
		
		List<Plant> plantList = repository.findByDateRange(startDate, endDate);
		PlantResourceAssembler assembler = new PlantResourceAssembler();
		PlantResourceList resList = assembler.getPlantResourceList(plantList);
		return resList;
	}
	
	

}
