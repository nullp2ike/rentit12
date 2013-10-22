package cs.ut.domain.rest;
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

import cs.ut.domain.LoadTestProperties;
import cs.ut.domain.Plant;

@ContextConfiguration(locations = { "/META-INF/spring/applicationContext.xml" })
@RooIntegrationTest(entity = PlantResource.class)
public class PlantResourceIntegrationTest extends AbstractJUnit4SpringContextTests{

	private String app_url;
	
	Client client;
	
    
    @Before
    public void setUp() {
    	client = Client.create();	
    	LoadTestProperties props = new LoadTestProperties();
    	app_url = props.loadProperty("webappurl");
    }
    
    private long createPlant(){
    	Plant p = new Plant();
    	p.setName("Truck2");
    	p.setDescription("Dodge 2012");
    	p.setPricePerDay(new BigDecimal(400));
    	p.persist();
    	return p.getId();
    	
    }
    
    @Test
    public void testGetPlant(){
    	long id = createPlant();
    	WebResource webResource = client.resource(app_url + "/rest/plant/" + id);
    	ClientResponse response = webResource.type(MediaType.APPLICATION_XML)
    			.accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
    	assertTrue(response.getStatus() == Status.OK.getStatusCode());
    	PlantResource plant = response.getEntity(PlantResource.class);
    	assertTrue(plant.getPlantName().equals("Truck2"));
    	assertTrue(plant.getDescription().equals("Dodge 2012"));
    	assertTrue(new BigDecimal(400).compareTo(plant.getPricePerDay()) == 0);
    }
}
