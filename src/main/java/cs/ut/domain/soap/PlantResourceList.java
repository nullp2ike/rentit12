package cs.ut.domain.soap;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

import cs.ut.domain.soap.PlantResource;

@RooJavaBean
@RooToString
@XmlRootElement
public class PlantResourceList {

	List<PlantResource> listOfPlantResources;
	
	public PlantResourceList(){
		
	}
	
	public PlantResourceList(List<PlantResource> listOfPlantResources) {
		this.listOfPlantResources = listOfPlantResources;
	}
	
	@XmlElement
	public List<PlantResource> getListOfPlantResources() {
        return this.listOfPlantResources;
    }

	public void setListOfPlantResources(List<PlantResource> listOfPlantResources) {
        this.listOfPlantResources = listOfPlantResources;
    }
}
