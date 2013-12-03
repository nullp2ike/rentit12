package cs.ut.domain.rest;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.roo.addon.test.RooIntegrationTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.client.RestTemplate;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.Base64;

import cs.ut.domain.Plant;
import cs.ut.repository.PlantRepository;
import cs.ut.security.Assignments;
import cs.ut.security.Authorities;
import cs.ut.security.Users;

@ContextConfiguration(locations = { "/META-INF/spring/applicationContext*.xml" })
@RooIntegrationTest(entity = PlantResource.class)
public class PlantResourceIntegrationTest{
	
	Client client;
	
	@Autowired PlantRepository plantRepository;
	
	@Value("${webappurl}")
	String webappurl;
	
	@BeforeClass
	public static void doStuff() {
		removeStuff();
		setUsers();
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
    
    @Before
    public void setUp() {
    	client = Client.create();	
    }
    
    private static void setUsers() {
		Users user = new Users();
		user.setEnabled(true);
		user.setPassword("5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8");
		user.setUsername("user");
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
    	
    	HttpEntity<String> requestEntity = new HttpEntity<String>(
				getHeaders("user" + ":" + "password"));
    	
    	RestTemplate template = new RestTemplate();
		ResponseEntity<PlantResource> response = template.exchange(
				webappurl + "/rest/plant/" + id, HttpMethod.GET,
				requestEntity, PlantResource.class);
		
		assertTrue(response.getStatusCode().value() == Status.OK.getStatusCode());
		PlantResource plant = response.getBody();
    	
    	assertTrue(plant.getPlantName().equals("Truck2"));
    	assertTrue(plant.getDescription().equals("Dodge 2012"));
    	assertTrue(new BigDecimal(400).compareTo(plant.getPricePerDay()) == 0);
    }
    
	private static HttpHeaders getHeaders(String auth) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
		headers.setAccept(Arrays
				.asList(org.springframework.http.MediaType.APPLICATION_JSON));
		byte[] encodedAuthorisation = Base64.encode(auth.getBytes());
		headers.add("Authorization", "Basic "
				+ new String(encodedAuthorisation));
		return headers;
	}
}
