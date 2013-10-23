package cs.ut.domain.service;

import java.math.BigDecimal;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.springframework.stereotype.Service;

import cs.ut.domain.HireRequestStatus;
import cs.ut.domain.Plant;
import cs.ut.domain.PurchaseOrder;
import cs.ut.domain.rest.PurchaseOrderResource;

@Service
public class PurchaseOrderService {

	public PurchaseOrder createPO(PurchaseOrderResource por)
			throws PlantUnavailableException, InvalidHirePeriodException {
		if (por.getStartDate().compareTo(por.getEndDate()) >= 0)
			throw new InvalidHirePeriodException("Invalid date range");
		Plant plant = Plant.findPlant(por.getPlantResource().getIdentifier());
		if (plant != null) {
			PurchaseOrder po = new PurchaseOrder();
			// Set state in the state diagram
			po.setStatus(HireRequestStatus.PENDING_CONFIRMATION);
			po.setEndDate(por.getEndDate());
			po.setStartDate(por.getStartDate());
			po.setPlant(plant);
			DateTime startDate = new DateTime(por.getStartDate());
			DateTime endDate = new DateTime(por.getEndDate());
			int days = Days.daysBetween(startDate, endDate).getDays();
			po.setTotalCost(po.getPlant().getPricePerDay()
					.multiply(new BigDecimal(days)));
			po.persist();
			return po;
		} else
			throw new PlantUnavailableException(
					"The requested plant is not available");
	}
	
	public PurchaseOrder getPO(Long id) throws NoSuchMethodException, SecurityException, PurchaseOrderNotFound {
		PurchaseOrder po = PurchaseOrder.findPurchaseOrder(id);
		if(po != null){
			return po;
		}else{
			throw new PurchaseOrderNotFound("Purchase order with id: " + id + " was not found");
		}
	}
	
}