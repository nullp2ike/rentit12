package cs.ut.domain.soap.service;

import java.util.Date;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlElement;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

import cs.ut.domain.Plant;
import cs.ut.domain.soap.PlantResourceAssembler;
import cs.ut.domain.soap.PlantResourceList;

@WebService
public class PlantSOAPService {
	
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
		
		List<Plant> plantList = Plant.findAllPlants();
		PlantResourceAssembler assembler = new PlantResourceAssembler();
		PlantResourceList resList = assembler.getPlantResourceList(plantList);
		return resList;
	}
	
	

}
