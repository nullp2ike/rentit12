package cs.ut.domain.rest;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Properties;

import javax.ws.rs.core.MediaType;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
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
@RooIntegrationTest(entity = PlantResourceList.class)
public class PlantResourceListIntegrationTest extends AbstractJUnit4SpringContextTests {
	
	Client client;
	
	private String app_url;
    
    @Before
    public void setUp() {
    	client = Client.create();
    	LoadTestProperties props = new LoadTestProperties();
    	app_url = props.loadProperty("webappurl");
    	
    }
	
    private long createPlant(String plantName){
    	Plant p = new Plant();
    	p.setName("PlantResourceListTruck");
    	p.setDescription("Dodge 2012");
    	p.setPricePerDay(new BigDecimal(400));
    	p.persist();
    	p.flush();
    	return p.getId();
    	
    }
    
    @Test
    public void testGetPlants(){
    	
    	createPlant("PlantResourceListTruck");
    	createPlant("PlantResourceListTruck2");
    	
    	WebResource webResource = client.resource(app_url + "/rest/plant/");
 
    	ClientResponse getResponse = webResource.type(MediaType.APPLICATION_XML)
    			.accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
    	assertTrue(getResponse.getStatus() == Status.OK.getStatusCode());
    	
    	PlantResourceList plantList = getResponse.getEntity(PlantResourceList.class);
    	assertTrue(plantList.getListOfPlantResources().size() > 1);
    }

}
