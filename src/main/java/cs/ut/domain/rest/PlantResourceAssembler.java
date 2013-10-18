package cs.ut.domain.rest;
import java.util.ArrayList;
import java.util.List;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

import cs.ut.domain.Plant;

@RooJavaBean
@RooToString
public class PlantResourceAssembler {
	
	public PlantResource getPlantResource(Plant plant){
		PlantResource plantResource = new PlantResource();
		plantResource.setDescription(plant.getDescription());
		plantResource.setPlantName(plant.getName());
		plantResource.setPricePerDay(plant.getPricePerDay());
		plantResource.setIdentifier(plant.getId());
		return plantResource;
	}
	
	public PlantResourceList getPlantResourceList(List<Plant> plants){
		List<PlantResource> listOfPlantResources = new ArrayList<PlantResource>();
		for (int i = 0; i < plants.size(); i++) {
			PlantResource plantResource = new PlantResource();
			plantResource.setDescription(plants.get(i).getDescription());
			plantResource.setPlantName(plants.get(i).getName());
			plantResource.setPricePerDay(plants.get(i).getPricePerDay());
			listOfPlantResources.add(plantResource);
		}
		PlantResourceList plantResourceList = new PlantResourceList(listOfPlantResources);
		return plantResourceList;
	}
}
