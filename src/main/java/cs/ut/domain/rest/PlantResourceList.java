package cs.ut.domain.rest;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@XmlRootElement(name = "plants")
public class PlantResourceList {
	
	List<PlantResource> listOfPlantResources;
	
	public PlantResourceList(){
		
	}
	
	public PlantResourceList(List<PlantResource> listOfPlantResources) {
		this.listOfPlantResources = listOfPlantResources;
	}
}
