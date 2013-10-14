package cs.ut.domain.rest;

import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Date;

import javax.ws.rs.core.MediaType;

import org.junit.Before;
import org.junit.Test;
import org.springframework.roo.addon.test.RooIntegrationTest;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.WebResource;

import cs.ut.domain.HireRequestStatus;

@RooIntegrationTest(entity = PurchaseOrderResource.class)
public class PurchaseOrderResourceIntegrationTest {

	Client client;
	String resourcePath;

	@Before
	public void setUp() {
		resourcePath = "http://localhost:8080/Rentit/rest";
		client = Client.create();
		createPlant();
	}

	private void createPlant() {
		WebResource webResource = client.resource(resourcePath + "/plant");
		PlantResource plantResource = new PlantResource();
		plantResource.setDescription("Dodge 2013");
		plantResource.setPlantName("Truck");
		plantResource.setPricePerDay(new BigDecimal(200));

		webResource.type(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.post(ClientResponse.class, plantResource);
	}

	private String getIdFromLocation(URI location) {
		String locationStr = location.toString();
		String id = locationStr.substring(locationStr.lastIndexOf("/"));
		return id;
	}

	private ClientResponse createPurchaseOrder(int totalPrice) {
		WebResource webResource = client.resource(resourcePath + "/pos");
		PurchaseOrderResource poResource = new PurchaseOrderResource();
		poResource.setEndDate(new Date());
		poResource.setPlantId(1);
		poResource.setStartDate(new Date());
		poResource.setTotalCost(new BigDecimal(totalPrice));
		poResource.setStatus(HireRequestStatus.PENDING);

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
		WebResource webResource = client.resource(resourcePath + "/pos/" + poId);
    	ClientResponse response = webResource.type(MediaType.APPLICATION_XML)
    			.accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
    	PurchaseOrderResource poResource = response.getEntity(PurchaseOrderResource.class);
    	assertTrue(poResource.getTotalCost().intValue() == 1);
	}

	@Test
	public void testGetPurchaseOrderByIdViaRest() {
		ClientResponse clientResp = createPurchaseOrder(2);
		String id = getIdFromLocation(clientResp.getLocation());
		WebResource webResource = client.resource(resourcePath + "/pos/" + id);
		ClientResponse clientResponse = webResource
				.type(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		assertTrue(clientResponse.getStatus() == Status.OK.getStatusCode());
		PurchaseOrderResource poResource = clientResponse
				.getEntity(PurchaseOrderResource.class);
		assertTrue(poResource.getTotalCost().intValue() == 2);
	}

	@Test
	public void testCancelPurchaseOderViaRest() {
		ClientResponse clientResp = createPurchaseOrder(3);
		String id = getIdFromLocation(clientResp.getLocation());
		WebResource webResource = client.resource(resourcePath + "/pos/" + id + "/status");
		
		PurchaseOrderResource poResource = new PurchaseOrderResource();
		poResource.setStatus(HireRequestStatus.REJECTED);

		ClientResponse clientResponse = webResource
				.type(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.put(ClientResponse.class, poResource);
		assertTrue(clientResponse.getStatus() == Status.OK.getStatusCode());
		
		webResource = client.resource(resourcePath + "/pos/" + id);
		
		ClientResponse clientResponseAfterCancel = webResource
				.type(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		
		PurchaseOrderResource poResourceCanceled = clientResponseAfterCancel
				.getEntity(PurchaseOrderResource.class);
		assertTrue(poResourceCanceled.getStatus().equals(HireRequestStatus.REJECTED));

	}

	@Test
	public void testUpdatePurchaseOrderViaRest() {
		ClientResponse clientResp = createPurchaseOrder(4);
		String id = getIdFromLocation(clientResp.getLocation());
		WebResource webResource = client.resource(resourcePath + "/pos/" + id);
		
		PurchaseOrderResource poResource = new PurchaseOrderResource();
		poResource.setEndDate(new Date());
		poResource.setPlantId(1);
		poResource.setStartDate(new Date());
		poResource.setTotalCost(new BigDecimal(4));
		poResource.setStatus(HireRequestStatus.PENDING);

		ClientResponse postResponse = webResource
				.type(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.put(ClientResponse.class, poResource);
		assertTrue(postResponse.getStatus() == Status.OK.getStatusCode());
		
		webResource = client.resource(resourcePath + "/pos/" + id);
		
		ClientResponse clientResponseAfterUpdate = webResource
				.type(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		
		PurchaseOrderResource poResourceUpdated = clientResponseAfterUpdate
				.getEntity(PurchaseOrderResource.class);
		assertTrue(poResourceUpdated.getTotalCost().intValue() == 4);
		
	}
}
