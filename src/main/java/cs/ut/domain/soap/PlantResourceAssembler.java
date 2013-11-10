package cs.ut.domain.soap;

import java.util.ArrayList;
import java.util.List;

import cs.ut.domain.Plant;
import cs.ut.domain.soap.PlantResource;
import cs.ut.domain.soap.PlantResourceList;


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
			plantResource.setIdentifier(plants.get(i).getId());
			plantResource.setDescription(plants.get(i).getDescription());
			plantResource.setPlantName(plants.get(i).getName());
			plantResource.setPricePerDay(plants.get(i).getPricePerDay());
			listOfPlantResources.add(plantResource);
		}
		PlantResourceList plantResourceList = new PlantResourceList(listOfPlantResources);
		return plantResourceList;
	}

}
