package cs.ut.domain.rest;
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

@RooIntegrationTest(entity = PlantResourceList.class)
public class PlantResourceListIntegrationTest {
	
	Client client;
    
    @Before
    public void setUp() {
    	client = Client.create();	
    }
	
    @Test
    public void testListAllPlantstViaRest(){
    	
    	WebResource webResource = client.resource("http://localhost:8080/Rentit/rest/plant");
    	
    	PlantResource newPlantResource = new PlantResource();
    	newPlantResource.setDescription("Dodge 2013");
    	newPlantResource.setPlantName("Truck");
    	newPlantResource.setPricePerDay(new BigDecimal(200));
    	
    	ClientResponse postResponse = webResource.type(MediaType.APPLICATION_XML)
    			.accept(MediaType.APPLICATION_XML).post(ClientResponse.class, newPlantResource);
    	assertTrue(postResponse.getStatus() == Status.CREATED.getStatusCode());
 
    	ClientResponse getResponse = webResource.type(MediaType.APPLICATION_XML)
    			.accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
    	assertTrue(getResponse.getStatus() == Status.OK.getStatusCode());
    	
    	PlantResourceList plantList = getResponse.getEntity(PlantResourceList.class);
    	assertTrue(plantList.getListOfPlantResources().size() > 0);
    }

}
