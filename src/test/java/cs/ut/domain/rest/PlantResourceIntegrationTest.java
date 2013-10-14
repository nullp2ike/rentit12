package cs.ut.domain.rest;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import javax.ws.rs.core.MediaType;

import org.junit.Before;
import org.junit.Test;
import org.springframework.roo.addon.test.RooIntegrationTest;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.WebResource;

@RooIntegrationTest(entity = PlantResource.class)
public class PlantResourceIntegrationTest {

	Client client;
    
    @Before
    public void setUp() {
    	client = Client.create();	
    }
    
    @Test
    public void testCreateNewPlantViaRest(){
    	
    	WebResource webResource = client.resource("http://localhost:8080/Rentit/rest/plant");
    	ClientResponse response = webResource.type(MediaType.APPLICATION_XML)
    			.accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
    	PlantResourceList oldPlantList = response.getEntity(PlantResourceList.class);
    	int oldPlantListSize = oldPlantList.getListOfPlantResources().size();
    	
    	PlantResource newPlantResource = new PlantResource();
    	newPlantResource.setDescription("Dodge 2013");
    	newPlantResource.setPlantName("Truck");
    	newPlantResource.setPricePerDay(new BigDecimal(200));
    	
    	ClientResponse postResponse = webResource.type(MediaType.APPLICATION_XML)
    			.accept(MediaType.APPLICATION_XML).post(ClientResponse.class, newPlantResource);
    	assertTrue(postResponse.getStatus() == Status.CREATED.getStatusCode());
 
    	ClientResponse getResponse = webResource.type(MediaType.APPLICATION_XML)
    			.accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
    	
    	PlantResourceList plantList = getResponse.getEntity(PlantResourceList.class);
    	assertEquals(plantList.getListOfPlantResources().size(), oldPlantListSize + 1);
    }
    
    @Test
    public void testQueryPlantViaRestById(){
    	WebResource webResource = client.resource("http://localhost:8080/Rentit/rest/plant/1");
    	ClientResponse response = webResource.type(MediaType.APPLICATION_XML)
    			.accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
    	assertTrue(response.getStatus() == Status.OK.getStatusCode());
    	PlantResource plant = response.getEntity(PlantResource.class);
    	assertTrue(plant.getPlantName().equals("Truck"));
    	assertTrue(plant.getDescription().equals("Dodge 2013"));
    	assertTrue(new BigDecimal(200).compareTo(plant.getPricePerDay()) == 0);
    }
}
