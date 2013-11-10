package cs.ut.domain;

import java.math.BigDecimal;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.roo.addon.test.RooIntegrationTest;

import cs.ut.repository.PlantRepository;

//@RooIntegrationTest(entity = Plant.class)
public class PlantRepositoryTest {
/*
	long plantId;
	@Autowired
	PlantRepository repository;
	
	@Before
	public void setUp(){
		createPlant();
	}
	
	private void createPlant() {
		Plant p = new Plant();
		p.setDescription("RepoDesc");
		p.setName("RepoName");
		p.setPricePerDay(new BigDecimal(200));
		repository.save(p);
		plantId = p.getId();
	}
	
	@Test
	public void testGetAvailablePlantsByDateRange(){

		PurchaseOrder po = new PurchaseOrder();
		DateTime startDate = new DateTime();
		startDate.minusDays(14);
		DateTime endDate = startDate.minusDays(7);
		po.setEndDate(endDate.toDate());
		po.setPlant(Plant.findPlant(plantId));
		po.setStartDate(startDate.toDate());
		po.setStatus(HireRequestStatus.PENDING_CONFIRMATION);
		po.setTotalCost(new BigDecimal(2));
		po.persist();
		po.flush();
		
		/*
		List<Plant> availablePlants = repository.findByDateRange(startDate.toDate(), endDate.toDate());
		assertTrue(availablePlants != null);
		*/
	
	
	
}
