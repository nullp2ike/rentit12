package cs.ut.domain.rest;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ws.rs.core.MediaType;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.roo.addon.test.RooIntegrationTest;
import org.springframework.test.context.ContextConfiguration;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.WebResource;

import cs.ut.domain.HireRequestStatus;
import cs.ut.domain.Plant;
import cs.ut.domain.PurchaseOrder;

@ContextConfiguration(locations = { "/META-INF/spring/applicationContext*.xml" })
@RooIntegrationTest(entity = PlantResourceList.class)
public class PlantResourceListIntegrationTest {
	
	Client client;
	
	@Value("${webappurl}")
	String webappurl;
    
    @Before
    public void setUp() {
    	client = Client.create();
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
    
	private long createPlant() {
		Plant p = new Plant();
		p.setDescription("TestAvailable");
		p.setName("Truck");
		p.setPricePerDay(new BigDecimal(200));
		p.persist();
		return p.getId();
	}

	private long createPO(long plantId, Date startDate, Date endDate) {
		PurchaseOrder po = new PurchaseOrder();
		po.setEndDate(endDate);
		po.setPlant(Plant.findPlant(plantId));
		po.setStartDate(startDate);
		po.setStatus(HireRequestStatus.PENDING_CONFIRMATION);
		po.setTotalCost(new BigDecimal(2));
		po.persist();
		return po.getId();
	}
    
    @Test
    public void testGetPlants(){
    	
    	createPlant("PlantResourceListTruck");
    	createPlant("PlantResourceListTruck2");
    	
    	WebResource webResource = client.resource(webappurl + "/rest/plant/");
 
    	ClientResponse getResponse = webResource.type(MediaType.APPLICATION_XML)
    			.accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
    	assertTrue(getResponse.getStatus() == Status.OK.getStatusCode());
    	
    	PlantResourceList plantList = getResponse.getEntity(PlantResourceList.class);
    	assertTrue(plantList.getListOfPlantResources().size() > 1);
    }

    @Test
    public void testGetAvailablePlants(){
    	long id = createPlant();
    	createPlant("PlantResourceListTruck");
    	
    	//First get the number of all plants
    	WebResource webResource = client.resource(webappurl + "/rest/plant/");
    	 
    	ClientResponse getResponse = webResource.type(MediaType.APPLICATION_XML)
    			.accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
    	assertTrue(getResponse.getStatus() == Status.OK.getStatusCode());
    	
    	PlantResourceList plantList = getResponse.getEntity(PlantResourceList.class);
    	long allPlantsSize = plantList.getListOfPlantResources().size();
    	
    	//Then createPO for one plant and make sure it is not available
    	DateTime today = new DateTime().toDateMidnight().toDateTime();
    	DateTime tomorrow = today.plusDays(1);
		createPO(id, today.toDate(), tomorrow.toDate());
    	
    	String startDateString = new SimpleDateFormat("dd-MM-yy").format(today.toDate());
    	String endDateString = new SimpleDateFormat("dd-MM-yy").format(tomorrow.toDate());

    	WebResource webResourceDates = client.resource(webappurl + "/rest/plant/" + "?startDate=" + startDateString + "&endDate=" + endDateString);
 
    	ClientResponse getResponseDates = webResourceDates.type(MediaType.APPLICATION_XML)
    			.accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
    	assertTrue(getResponse.getStatus() == Status.OK.getStatusCode());
    	
    	PlantResourceList plantListAvailable = getResponseDates.getEntity(PlantResourceList.class);
    	assertTrue(plantListAvailable.getListOfPlantResources().size() == allPlantsSize - 1);
    	
    }
}
