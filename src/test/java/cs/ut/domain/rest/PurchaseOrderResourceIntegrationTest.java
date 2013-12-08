package cs.ut.domain.rest;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.util.Date;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.roo.addon.test.RooIntegrationTest;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import cs.ut.domain.HireRequestStatus;
import cs.ut.domain.Plant;
import cs.ut.domain.PurchaseOrder;
import cs.ut.domain.PurchaseOrderUpdate;
import cs.ut.security.Assignments;
import cs.ut.security.Authorities;
import cs.ut.security.Users;
import cs.ut.util.RestHelper;

@RooIntegrationTest(entity = PurchaseOrderResource.class)
public class PurchaseOrderResourceIntegrationTest {

	RestTemplate template;

	@Value("${webappurl}")
	String webappurl;

	long plantId;

	@BeforeClass
	public static void doStuff() {
		removeStuff();
		setUsers();
	}
	

	@Before
	public void setUp() {
		createPlant();	
		template = new RestTemplate();
	}

	private static void removeStuff() {
		List<Assignments> assignments = Assignments.findAllAssignmentses();
		for (Assignments assignments2 : assignments) {
			assignments2.remove();
		}

		List<Users> allUsers = Users.findAllUserses();
		for (Users users : allUsers) {
			users.remove();
		}

		List<Authorities> auth = Authorities.findAllAuthoritieses();
		for (Authorities authorities : auth) {
			authorities.remove();
		}
	}

	private static void setUsers() {
		Users user = new Users();
		user.setEnabled(true);
		user.setPassword("5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8");
		user.setUsername("user@rentit.com");
		user.persist();

		Users admin = new Users();
		admin.setEnabled(true);
		admin.setPassword("5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8");
		admin.setUsername("admin@rentit.com");
		admin.persist();

		Authorities authUser = new Authorities();
		authUser.setAuthority("ROLE_USER");
		authUser.persist();

		Authorities authAdmin = new Authorities();
		authAdmin.setAuthority("ROLE_ADMIN");
		authAdmin.persist();

		Assignments assignSiteEng = new Assignments();
		assignSiteEng.setAuthority(authUser);
		assignSiteEng.setUserRentit(user);
		assignSiteEng.persist();

		Assignments assignAdmin = new Assignments();
		assignAdmin.setAuthority(authAdmin);
		assignAdmin.setUserRentit(admin);
		assignAdmin.persist();
	}

	private void createPlant() {
		Plant p = new Plant();
		p.setDescription("Dodges 2013");
		p.setName("Truck");
		p.setPricePerDay(new BigDecimal(200));
		p.persist();
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
		po.setPlantHireRequestId(4);
		po.persist();
		return po.getId();
	}

	private long createPOUpdate(long id, int totalCost, PurchaseOrder po) {
		PurchaseOrderUpdate poUpdate = new PurchaseOrderUpdate();
		poUpdate.setTotalCost(new BigDecimal(totalCost));
		poUpdate.setEndDate(new Date());
		poUpdate.setPlant(Plant.findPlant(plantId));
		poUpdate.setStartDate(new Date());
		poUpdate.setPurchaseOrderId(id);
		poUpdate.setStatus(po.getStatus());
		poUpdate.persist();
		return poUpdate.getId();
	}

	// OK
	@Test
	public void testCreatePO() {
		PlantResourceAssembler assembler = new PlantResourceAssembler();
		PlantResource plantResource = assembler.getPlantResource(Plant
				.findPlant(plantId));
		PurchaseOrderResource poResource = new PurchaseOrderResource();
		poResource.setEndDate(new Date());
		poResource.setStartDate(new Date());
		poResource.setPlantResource(plantResource);
		poResource.setStatus(HireRequestStatus.OPEN); // Creating a PO should
														// always result in
														// status
														// PENDING_CONFIRMATION
		poResource.setTotalCost(new BigDecimal(50));

		String json = resourceToJson(poResource);
		HttpEntity<String> requestEntity = new HttpEntity<String>(json,
				RestHelper.getHeaders("user@rentit.com", "password"));

		ResponseEntity<PurchaseOrderResource> clientResponse = template
				.postForEntity(webappurl + "/rest/pos/", requestEntity,
						PurchaseOrderResource.class);

		assertTrue(clientResponse.getStatusCode().value() == 201);
		String id = getIdFromLocation(clientResponse.getHeaders().getLocation());

		PurchaseOrder po = PurchaseOrder.findPurchaseOrder(Long.parseLong(id));
		assertTrue(po.getStatus()
				.equals(HireRequestStatus.PENDING_CONFIRMATION));
		assertTrue(clientResponse.getBody().get_links().size() == 3);
	}

	@Test
	public void testCreatePOWithSamePlantAndDatesFails() {
		PlantResourceAssembler assembler = new PlantResourceAssembler();
		PlantResource plantResource = assembler.getPlantResource(Plant
				.findPlant(plantId));

		PurchaseOrderResource poResource = new PurchaseOrderResource();
		poResource.setEndDate(new Date());
		poResource.setStartDate(new Date());
		poResource.setPlantResource(plantResource);
		poResource.setTotalCost(new BigDecimal(50));

		String json = resourceToJson(poResource);
		HttpEntity<String> requestEntity = new HttpEntity<String>(json,
				RestHelper.getHeaders("user@rentit.com", "password"));

		ResponseEntity<PurchaseOrderResource> clientResponse = template
				.postForEntity(webappurl + "/rest/pos/", requestEntity,
						PurchaseOrderResource.class);
		assertTrue(clientResponse.getStatusCode().value() == 201);
		
		Link acceptLink = clientResponse.getBody().get_link("acceptPO");
		String acceptUrl = acceptLink.getHref();
		
		ResponseEntity<PurchaseOrderResource> response = template.exchange(
				acceptUrl, HttpMethod.PUT,
				requestEntity, PurchaseOrderResource.class);

		try{
		ResponseEntity<PurchaseOrderResource> clientResponse2 = template
				.postForEntity(webappurl + "/rest/pos/", requestEntity,
						PurchaseOrderResource.class);
		}catch(HttpClientErrorException e){
			assertTrue(e.getMessage().contains("409 Conflict"));
			return;
		}
		fail();
	}

	//
	@Test
	public void testRejectPO() {
		long id = createPO(HireRequestStatus.PENDING_CONFIRMATION);
		HttpEntity<String> requestEntity = new HttpEntity<String>(
				RestHelper.getHeaders("user@rentit.com", "password"));

		ResponseEntity<PurchaseOrderResource> response = template.exchange(
				webappurl + "/rest/pos/" + id + "/reject", HttpMethod.DELETE,
				requestEntity, PurchaseOrderResource.class);

		assertTrue(response.getStatusCode().value() == 200);
		HireRequestStatus status = PurchaseOrder.findPurchaseOrder(id)
				.getStatus();
		assertTrue(status.equals(HireRequestStatus.REJECTED));
		assertTrue(response.getBody().get_links().size() == 1);
	}

	// OK
	@Test
	public void testAcceptPO() {
		long id = createPO(HireRequestStatus.PENDING_CONFIRMATION);
		HttpEntity<String> requestEntity = new HttpEntity<String>(
				RestHelper.getHeaders("user@rentit.com", "password"));
		ResponseEntity<PurchaseOrderResource> response = template.exchange(
				webappurl + "/rest/pos/" + id + "/accept", HttpMethod.PUT,
				requestEntity, PurchaseOrderResource.class);

		assertTrue(response.getStatusCode().value() == 200);

		HireRequestStatus status = PurchaseOrder.findPurchaseOrder(id)
				.getStatus();
		assertTrue(status.equals(HireRequestStatus.OPEN));
		assertTrue(response.getBody().get_links().size() == 2);
		System.out.println(response.getBody());
	}

	// OK
	@Test
	public void testUpdatePO() {
		long poId = createPO(HireRequestStatus.REJECTED);
		PurchaseOrderResourceAssembler poResourceAssembler = new PurchaseOrderResourceAssembler();
		PurchaseOrder po = PurchaseOrder.findPurchaseOrder(poId);
		PurchaseOrderResource poResource = poResourceAssembler
				.getPurchaseOrderResource(po);
		poResource.setTotalCost(new BigDecimal(50));

		String json = resourceToJson(poResource);
		HttpEntity<String> requestEntity = new HttpEntity<String>(json,
				RestHelper.getHeaders("user@rentit.com", "password"));

		ResponseEntity<PurchaseOrderResource> response = template.exchange(
				webappurl + "/rest/pos/" + poId, HttpMethod.PUT, requestEntity,
				PurchaseOrderResource.class);

		assertTrue(response.getStatusCode().value() == 200);
		HireRequestStatus status = PurchaseOrder.findPurchaseOrder(poId)
				.getStatus();
		assertTrue(status.equals(HireRequestStatus.PENDING_CONFIRMATION));
		assertTrue(response.getBody().get_links().size() == 2);
	}

	//
	@Test
	public void testRequestPOUpdate() {
		long poId = createPO(HireRequestStatus.OPEN);
		long numOfPOUpdates = PurchaseOrderUpdate.countPurchaseOrderUpdates();
		assertTrue( numOfPOUpdates == 1);
		
		PurchaseOrder po = PurchaseOrder.findPurchaseOrder(poId);
		PurchaseOrderResourceAssembler assembler = new PurchaseOrderResourceAssembler();
		PurchaseOrderResource poResource = assembler
				.getPurchaseOrderResource(po);
		poResource.setStatus(HireRequestStatus.REJECTED);
		poResource.setTotalCost(new BigDecimal(23232));
		
		String json = resourceToJson(poResource);
		HttpEntity<String> requestEntity = new HttpEntity<String>(json,
				RestHelper.getHeaders("user@rentit.com", "password"));

		ResponseEntity<PurchaseOrderResource> response = template.exchange(
				webappurl + "/rest/pos/" + poId + "/updates", HttpMethod.POST,
				requestEntity, PurchaseOrderResource.class);

		assertTrue(response.getStatusCode().value() == 200);
		HireRequestStatus status = PurchaseOrder.findPurchaseOrder(poId)
				.getStatus();
		assertTrue(status.equals(HireRequestStatus.PENDING_UPDATE));
		assertTrue(response.getBody().get_links().size() == 2);
		assertTrue(PurchaseOrderUpdate.countPurchaseOrderUpdates() == 2);
		// TODO Test here that the cost is present in db
		
//		HireRequestStatus updateStatus = PurchaseOrderUpdate.findPurchaseOrderUpdate(id);
	}

	// OK
	@Test
	public void testRejectPOUpdate() {
		long id = createPO(HireRequestStatus.PENDING_UPDATE);
		long uid = createPOUpdate(id, 1,PurchaseOrder.findPurchaseOrder(id));		
		HttpEntity<String> requestEntity = new HttpEntity<String>(
				RestHelper.getHeaders("user@rentit.com", "password"));

		ResponseEntity<PurchaseOrderResource> response = template.exchange(
				webappurl + "/rest/pos/" + id
				+ "/updates/" + uid + "/reject", HttpMethod.DELETE,
				requestEntity, PurchaseOrderResource.class);
		
		assertTrue(response.getStatusCode().value() == 200);
		HireRequestStatus status = PurchaseOrder.findPurchaseOrder(id)
				.getStatus();
		assertTrue(status.equals(HireRequestStatus.OPEN));
		assertTrue(response.getBody().get_links().size() == 2);
	}

	//
	@Test
	public void testAcceptPOUpdate() {
		long id = createPO(HireRequestStatus.PENDING_UPDATE);
		long uid = createPOUpdate(id, 5432100, PurchaseOrder.findPurchaseOrder(id));
		
		HttpEntity<String> requestEntity = new HttpEntity<String>(
				RestHelper.getHeaders("user@rentit.com", "password"));
		
		ResponseEntity<PurchaseOrderResource> response = template.exchange(
				webappurl + "/rest/pos/" + id
				+ "/updates/" + uid + "/accept", HttpMethod.POST,
				requestEntity, PurchaseOrderResource.class);
		
		assertTrue(response.getStatusCode().value() == 200);
		HireRequestStatus status = PurchaseOrder.findPurchaseOrder(id)
				.getStatus();
		assertTrue(status.equals(HireRequestStatus.OPEN));
		assertTrue(response.getBody().get_links().size() == 2);
		PurchaseOrder po = PurchaseOrder.findPurchaseOrder(id);
		assertTrue(po.getTotalCost().intValue() == 5432100);
	}

	// OK
	@Test
	public void testClosePO() {
		long id = createPO(HireRequestStatus.PENDING_CONFIRMATION);

		HttpEntity<String> requestEntity = new HttpEntity<String>(
				RestHelper.getHeaders("user@rentit.com", "password"));

		ResponseEntity<PurchaseOrderResource> response = template.exchange(
				webappurl + "/rest/pos/" + id, HttpMethod.DELETE,
				requestEntity, PurchaseOrderResource.class);
		assertTrue(response.getStatusCode().value() == 200);
		assertTrue(PurchaseOrder.findPurchaseOrder(id).getStatus()
				.equals(HireRequestStatus.CLOSED));

	}

	// TODO add more asserts for other status link checks
	@Test
	public void testGetPO() {
		long id = createPO(HireRequestStatus.PENDING_CONFIRMATION);

		HttpEntity<String> requestEntity = new HttpEntity<String>(
				RestHelper.getHeaders("user@rentit.com", "password"));

		ResponseEntity<PurchaseOrderResource> response = template.exchange(
				webappurl + "/rest/pos/" + id, HttpMethod.GET, requestEntity,
				PurchaseOrderResource.class);

		assertTrue(response.getStatusCode().value() == 200);
		PurchaseOrderResource poResource = response.getBody();
		assertTrue(poResource.getTotalCost().intValue() == 2);
		assertTrue(poResource.get_links().size() == 2);
	}

	private String resourceToJson(Object resource) {
		ObjectWriter ow = new ObjectMapper().writer()
				.withDefaultPrettyPrinter();
		String json = null;
		try {
			json = ow.writeValueAsString(resource);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return json;
	}

}
