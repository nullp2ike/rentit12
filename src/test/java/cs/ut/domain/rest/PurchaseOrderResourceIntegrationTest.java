package cs.ut.domain.rest;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URI;
import java.util.Date;
import java.util.Properties;

import javax.ws.rs.core.MediaType;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.roo.addon.test.RooIntegrationTest;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.WebResource;

import cs.ut.domain.HireRequestStatus;
import cs.ut.domain.LoadTestProperties;
import cs.ut.domain.Plant;
import cs.ut.domain.PurchaseOrder;

@RooIntegrationTest(entity = PurchaseOrderResource.class)
public class PurchaseOrderResourceIntegrationTest {

	Client client;

	private String app_url;
	long plantId;

	@Before
	public void setUp() {
		
		client = Client.create();
		createPlant();
		LoadTestProperties props = new LoadTestProperties();
		app_url = props.loadProperty("webappurl");
	}

	private void createPlant() {
		Plant p = new Plant();
		p.setDescription("Dodge 2013");
		p.setName("Truck");
		p.setPricePerDay(new BigDecimal(200));
		p.persist();
		p.flush();
		plantId = p.getId();
	}

	private String getIdFromLocation(URI location) {
		String locationStr = location.toString();
		String id = locationStr.substring(locationStr.lastIndexOf("/"));
		return id;
	}
	
	private long createPO(HireRequestStatus status){
		PurchaseOrder po = new PurchaseOrder();
		po.setEndDate(new Date());
		po.setPlant(Plant.findPlant(plantId));
		po.setStartDate(new Date());
		po.setStatus(status);
		po.setTotalCost(new BigDecimal(2));
		po.persist();
		po.flush();
		long id = po.getId();
		return id;
	}

	private ClientResponse createPurchaseOrder(int totalPrice) {
		WebResource webResource = client.resource(app_url + "/rest/pos");
		PurchaseOrderResource poResource = new PurchaseOrderResource();
		poResource.setEndDate(new Date());
		PlantResourceAssembler assembler = new PlantResourceAssembler();
		poResource.setPlantResource(assembler.getPlantResource(Plant.findPlant(plantId)));
		poResource.setStartDate(new Date());
		poResource.setTotalCost(new BigDecimal(totalPrice));
		poResource.setStatus(HireRequestStatus.PENDING_CONFIRMATION);

		ClientResponse clientResponse = webResource
				.type(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.post(ClientResponse.class, poResource);
		return clientResponse;
	}

	@Test
	public void testCreateNewPurchaseOrderViaRest() {
		ClientResponse postResponse = createPurchaseOrder(1);
		assertTrue(postResponse.getStatus() == Status.CREATED.getStatusCode());
		String poId = getIdFromLocation(postResponse.getLocation());
		WebResource webResource = client.resource(app_url + "/rest/pos/" + poId);
    	ClientResponse response = webResource.type(MediaType.APPLICATION_XML)
    			.accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
    	PurchaseOrderResource poResource = response.getEntity(PurchaseOrderResource.class);
    	assertTrue(poResource.getTotalCost().intValue() == 1);
	}


	@Test
	public void testCancelPurchaseOderViaRest() {
		long id = createPO(HireRequestStatus.PENDING_CONFIRMATION);
		WebResource webResource = client.resource(app_url + "/rest/pos/" + id + "/cancel");
		
		PurchaseOrderStatusResource poResource = new PurchaseOrderStatusResource();
		poResource.setStatus(HireRequestStatus.REJECTED);

		ClientResponse clientResponse = webResource
				.type(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.put(ClientResponse.class, poResource);
		assertTrue(clientResponse.getStatus() == Status.OK.getStatusCode());
		
		assertTrue(PurchaseOrder.findPurchaseOrder(id).getStatus().equals(HireRequestStatus.REJECTED));

	}

	@Test
	public void testUpdatePurchaseOrderViaRest() {
		ClientResponse clientResp = createPurchaseOrder(4);
		String id = getIdFromLocation(clientResp.getLocation());
		WebResource webResource = client.resource(app_url + "/rest/pos/" + id);
		
		PurchaseOrderResource poResource = new PurchaseOrderResource();
		poResource.setEndDate(new Date());
		PlantResourceAssembler assembler = new PlantResourceAssembler();
		poResource.setPlantResource(assembler.getPlantResource(Plant.findPlant(plantId)));
		poResource.setStartDate(new Date());
		poResource.setTotalCost(new BigDecimal(4));
		poResource.setStatus(HireRequestStatus.PENDING_CONFIRMATION);

		ClientResponse postResponse = webResource
				.type(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.put(ClientResponse.class, poResource);
		assertTrue(postResponse.getStatus() == Status.OK.getStatusCode());
		
		webResource = client.resource(app_url + "/rest/pos/" + id);
		
		ClientResponse clientResponseAfterUpdate = webResource
				.type(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		
		PurchaseOrderResource poResourceUpdated = clientResponseAfterUpdate
				.getEntity(PurchaseOrderResource.class);
		assertTrue(poResourceUpdated.getTotalCost().intValue() == 4);
	}
	
	@Test
	public void testGetPO(){
		long id = createPO(HireRequestStatus.PENDING_CONFIRMATION);
		WebResource webResource = client.resource(app_url + "/rest/pos/" + id);
		ClientResponse clientResponse = webResource
				.type(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		assertTrue(clientResponse.getStatus() == Status.OK.getStatusCode());
		PurchaseOrderResource poResource = clientResponse
				.getEntity(PurchaseOrderResource.class);
		assertTrue(poResource.getTotalCost().intValue() == 2);
	}
	
	@Test 
	public void testAcceptPO(){
		long id = createPO(HireRequestStatus.PENDING_CONFIRMATION);
		WebResource webResource = client.resource(app_url + "/rest/pos/" + id + "/accept");
		ClientResponse clientResponse = webResource
				.type(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML).post(ClientResponse.class);
		assertTrue(clientResponse.getStatus() == Status.OK.getStatusCode());
		HireRequestStatus status = PurchaseOrder.findPurchaseOrder(id).getStatus();
		assertTrue(status.equals(HireRequestStatus.OPEN));
	}
	
	@Test 
	public void testRejectPO(){
		long id = createPO(HireRequestStatus.PENDING_CONFIRMATION);
		WebResource webResource = client.resource(app_url + "/rest/pos/" + id + "/reject");
		ClientResponse clientResponse = webResource
				.type(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML).post(ClientResponse.class);
		assertTrue(clientResponse.getStatus() == Status.OK.getStatusCode());
		HireRequestStatus status = PurchaseOrder.findPurchaseOrder(id).getStatus();
		assertTrue(status.equals(HireRequestStatus.REJECTED));
	}
}
