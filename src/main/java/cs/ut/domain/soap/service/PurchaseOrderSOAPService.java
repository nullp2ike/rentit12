package cs.ut.domain.soap.service;

import java.math.BigDecimal;
import java.util.Date;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlElement;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;

import cs.ut.domain.HireRequestStatus;
import cs.ut.domain.Plant;
import cs.ut.domain.PurchaseOrder;
import cs.ut.repository.PlantRepository;

@WebService
public class PurchaseOrderSOAPService {
	
	
	@WebMethod
	@XmlElement
	public void createPurchaseOrder(long plantId, Date startDate, Date endDate){
		LocalDate startD = new LocalDate(startDate);
		LocalDate endD = new LocalDate(endDate);
		int days = Days.daysBetween(startD, endD).getDays();
		PurchaseOrder po = new PurchaseOrder();
		po.setEndDate(endDate);
		po.setStartDate(startDate);
		Plant p = Plant.findPlant(plantId);
		po.setPlant(p);
		po.setTotalCost(p.getPricePerDay().multiply(BigDecimal.valueOf(days)));
		po.setStatus(HireRequestStatus.PENDING_CONFIRMATION);
		po.persist();
	}

}
