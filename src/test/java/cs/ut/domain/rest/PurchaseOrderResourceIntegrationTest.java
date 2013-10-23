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
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
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
import cs.ut.domain.PurchaseOrderUpdate;
import cs.ut.domain.service.PurchaseOrderNotFound;

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
		String id = locationStr.substring(locationStr.lastIndexOf("/") + 1);
		return id;
	}

	private long createPO(HireRequestStatus status) {
		PurchaseOrder po = new PurchaseOrder();
		po.setEndDate(new Date());
		po.setPlant(Plant.findPlant(plantId));
		po.setStartDate(new Date());
		po.setStatus(status);
		po.setTotalCost(new BigDecimal(2));
		po.persist();
		po.flush();
		return po.getId();
	}
	
	private long createPOUpdate(long id, int totalCost){
		PurchaseOrderUpdate poUpdate = new PurchaseOrderUpdate();
		poUpdate.setTotalCost(new BigDecimal(totalCost));
		poUpdate.setEndDate(new Date());
		poUpdate.setPlant(Plant.findPlant(plantId));
		poUpdate.setStartDate(new Date());
		poUpdate.setPurchaseOrderId(id);
		poUpdate.persist();
		poUpdate.flush();
		return poUpdate.getId();
	}

	//OK
	@Test
	public void testCreatePO() {
		PlantResourceAssembler assembler = new PlantResourceAssembler();
		PlantResource plantResource = assembler.getPlantResource(Plant
				.findPlant(plantId));
		WebResource webResource = client.resource(app_url + "/rest/pos/");
		PurchaseOrderResource poResource = new PurchaseOrderResource();
		poResource.setEndDate(new Date());
		poResource.setStartDate(new Date());
		poResource.setPlantResource(plantResource);
		poResource.setStatus(HireRequestStatus.OPEN); // Creating a PO should
														// always result in
														// status
														// PENDING_CONFIRMATION
		poResource.setTotalCost(new BigDecimal(50));

		ClientResponse clientResponse = webResource
				.type(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.post(ClientResponse.class, poResource);

		assertTrue(clientResponse.getStatus() == Status.CREATED.getStatusCode());
		String id = getIdFromLocation(clientResponse.getLocation());
		PurchaseOrder po = PurchaseOrder.findPurchaseOrder(Long.parseLong(id));
		assertTrue(po.getStatus()
				.equals(HireRequestStatus.PENDING_CONFIRMATION));
		PurchaseOrderResource poResourceAfter = clientResponse
				.getEntity(PurchaseOrderResource.class);
		assertTrue(poResourceAfter.get_links().size() == 2);
	}
	
	
	//
	@Test
	public void testRejectPO() {
		long id = createPO(HireRequestStatus.PENDING_CONFIRMATION);
		WebResource webResource = client.resource(app_url + "/rest/pos/" + id
				+ "/reject");
		ClientResponse clientResponse = webResource
				.type(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML).delete(ClientResponse.class);
		assertTrue(clientResponse.getStatus() == Status.OK.getStatusCode());
		HireRequestStatus status = PurchaseOrder.findPurchaseOrder(id)
				.getStatus();
		assertTrue(status.equals(HireRequestStatus.REJECTED));
		PurchaseOrderResource poResource = clientResponse
				.getEntity(PurchaseOrderResource.class);
		assertTrue(poResource.get_links().size() == 1);
	}
	

	// OK
	@Test
	public void testUpdatePO() {
		long poId = createPO(HireRequestStatus.REJECTED);
		WebResource webResource = client
				.resource(app_url + "/rest/pos/" + poId);
		PurchaseOrderResourceAssembler poResourceAssembler = new PurchaseOrderResourceAssembler();
		PurchaseOrder po = PurchaseOrder.findPurchaseOrder(poId);
		PurchaseOrderResource poResource = poResourceAssembler
				.getPurchaseOrderResource(po);
		poResource.setTotalCost(new BigDecimal(50));

		ClientResponse clientResponse = webResource
				.type(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.put(ClientResponse.class, poResource);
		assertTrue(clientResponse.getStatus() == Status.OK.getStatusCode());
		PurchaseOrderResource poResourceAfterUpdate = clientResponse
				.getEntity(PurchaseOrderResource.class);
		HireRequestStatus status = PurchaseOrder.findPurchaseOrder(poId)
				.getStatus();
		assertTrue(status.equals(HireRequestStatus.PENDING_CONFIRMATION));
		assertTrue(poResourceAfterUpdate.get_links().size() == 2);
	}

	
	//OK
	@Test
	public void testAcceptPO() {
		long id = createPO(HireRequestStatus.PENDING_CONFIRMATION);
		WebResource webResource = client.resource(app_url + "/rest/pos/" + id
				+ "/accept");
		
		ClientResponse clientResponse = webResource
				.type(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML).put(ClientResponse.class);
		assertTrue(clientResponse.getStatus() == Status.OK.getStatusCode());

		PurchaseOrderResource poResource = clientResponse
				.getEntity(PurchaseOrderResource.class);
		HireRequestStatus status = PurchaseOrder.findPurchaseOrder(id)
				.getStatus();
		assertTrue(status.equals(HireRequestStatus.OPEN));
		assertTrue(poResource.get_links().size() == 2);
	}
	
	//
	@Test
	public void testRequestPOUpdate() {
		long poId = createPO(HireRequestStatus.OPEN);
		WebResource webResource = client.resource(app_url + "/rest/pos/" + poId
				+ "/updates");
		PurchaseOrder po = PurchaseOrder.findPurchaseOrder(poId);
		PurchaseOrderResourceAssembler assembler = new PurchaseOrderResourceAssembler();
		PurchaseOrderResource poResource = assembler.getPurchaseOrderResource(po);
		assertTrue(PurchaseOrderUpdate.countPurchaseOrderUpdates() == 1);

		ClientResponse clientResponse = webResource
				.type(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.post(ClientResponse.class, poResource);

		assertTrue(clientResponse.getStatus() == Status.OK.getStatusCode());
		HireRequestStatus status = PurchaseOrder.findPurchaseOrder(poId)
				.getStatus();
		assertTrue(status.equals(HireRequestStatus.PENDING_UPDATE));
		PurchaseOrderResource poResourceAfterUpdate = clientResponse
				.getEntity(PurchaseOrderResource.class);
		assertTrue(poResourceAfterUpdate.get_links().size() == 2);
		assertTrue(PurchaseOrderUpdate.countPurchaseOrderUpdates() == 2);
		// TODO Test here that the cost is present in db
	}
	
	//OK
	@Test
	public void testRejectPOUpdate() {
		long id = createPO(HireRequestStatus.PENDING_UPDATE);
		long uid = createPOUpdate(id, 1);
		WebResource webResource = client.resource(app_url + "/rest/pos/" + id + "/updates/" + uid 
				+ "/reject");
		ClientResponse clientResponse = webResource
				.type(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML).delete(ClientResponse.class);
		assertTrue(clientResponse.getStatus() == Status.OK.getStatusCode());
		HireRequestStatus status = PurchaseOrder.findPurchaseOrder(id)
				.getStatus();
		assertTrue(status.equals(HireRequestStatus.OPEN));
		PurchaseOrderResource poResource = clientResponse
				.getEntity(PurchaseOrderResource.class);
		assertTrue(poResource.get_links().size() == 2);
	}
	
	
	//
	@Test
	public void testAcceptPOUpdate() {
		long id = createPO(HireRequestStatus.PENDING_UPDATE);
		long uid = createPOUpdate(id, 5432100);
		WebResource webResource = client.resource(app_url + "/rest/pos/" + id + "/updates/" + uid 
				+ "/accept");
		ClientResponse clientResponse = webResource
				.type(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML).post(ClientResponse.class);
		assertTrue(clientResponse.getStatus() == Status.OK.getStatusCode());
		HireRequestStatus status = PurchaseOrder.findPurchaseOrder(id)
				.getStatus();
		assertTrue(status.equals(HireRequestStatus.OPEN));
		PurchaseOrderResource poResource = clientResponse
				.getEntity(PurchaseOrderResource.class);
		assertTrue(poResource.get_links().size() == 2);
		PurchaseOrder po = PurchaseOrder.findPurchaseOrder(id);
		assertTrue(po.getTotalCost().intValue() == 5432100);
	}
	
	// OK
	@Test
	public void testClosePO() {
		long id = createPO(HireRequestStatus.PENDING_CONFIRMATION);
		WebResource webResource = client.resource(app_url + "/rest/pos/" + id);

		ClientResponse clientResponse = webResource
				.type(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML).delete(ClientResponse.class);
		assertTrue(clientResponse.getStatus() == Status.OK.getStatusCode());
		assertTrue(PurchaseOrder.findPurchaseOrder(id).getStatus()
				.equals(HireRequestStatus.CLOSED));

	}
	

	//TODO add more asserts for other status link checks
	@Test
	public void testGetPO() {
		long id = createPO(HireRequestStatus.PENDING_CONFIRMATION);
		WebResource webResource = client.resource(app_url + "/rest/pos/" + id);
		ClientResponse clientResponse = webResource
				.type(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		assertTrue(clientResponse.getStatus() == Status.OK.getStatusCode());
		PurchaseOrderResource poResource = clientResponse
				.getEntity(PurchaseOrderResource.class);
		assertTrue(poResource.getTotalCost().intValue() == 2);
		assertTrue(poResource.get_links().size() == 2);
	}

}
