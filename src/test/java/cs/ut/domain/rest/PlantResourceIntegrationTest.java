package cs.ut.domain.rest;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import javax.ws.rs.core.MediaType;

import org.junit.Before;
import org.junit.Test;
import org.springframework.roo.addon.test.RooIntegrationTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.WebResource;

import cs.ut.domain.Plant;

@ContextConfiguration(locations = { "/META-INF/spring/applicationContext.xml" })
@RooIntegrationTest(entity = PlantResource.class)
public class PlantResourceIntegrationTest extends AbstractJUnit4SpringContextTests{

	Client client;
	private static final String DOMAIN = "http://rentit12.herokuapp.com";
	//private static final String DOMAIN = "http://localhost:8080/Rentit";
    
    @Before
    public void setUp() {
    	client = Client.create();	
    }
    
    private long createPlant(){
    	Plant p = new Plant();
    	p.setName("Truck2");
    	p.setDescription("Dodge 2012");
    	p.setPricePerDay(new BigDecimal(400));
    	p.persist();
    	p.flush();
    	return p.getId();
    	
    }
    
    @Test
    public void testCreateNewPlantViaRest(){
    	
    	WebResource webResource = client.resource(DOMAIN + "/rest/plant");
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
    	long id = createPlant();
    	WebResource webResource = client.resource(DOMAIN + "/rest/plant/" + id);
    	ClientResponse response = webResource.type(MediaType.APPLICATION_XML)
    			.accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
    	assertTrue(response.getStatus() == Status.OK.getStatusCode());
    	PlantResource plant = response.getEntity(PlantResource.class);
    	assertTrue(plant.getPlantName().equals("Truck2"));
    	assertTrue(plant.getDescription().equals("Dodge 2012"));
    	assertTrue(new BigDecimal(400).compareTo(plant.getPricePerDay()) == 0);
    }
}
