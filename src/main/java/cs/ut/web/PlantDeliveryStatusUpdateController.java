package cs.ut.web;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cs.ut.domain.HireRequestStatus;
import cs.ut.domain.PlantStatus;
import cs.ut.domain.PurchaseOrder;
import cs.ut.domain.bean.PurchaseOrderListDTO;
import cs.ut.repository.PurchaseOrderRepository;



@RequestMapping("/purchaseorders/delivery/**")
@Controller
public class PlantDeliveryStatusUpdateController {
	
	@Autowired
	PurchaseOrderRepository repository;
	
    @RequestMapping(method = RequestMethod.GET)
    public String showPlantStatuses(HttpServletRequest request, ModelMap modelMap) {
    	PurchaseOrderListDTO po = new PurchaseOrderListDTO();
    	List<PurchaseOrder> orderList = repository.findByStatus(HireRequestStatus.OPEN);
    	List<PurchaseOrder> pendingUpdateRequests = repository.findByStatus(HireRequestStatus.PENDING_UPDATE);
    	orderList.addAll(pendingUpdateRequests);
    	po.setOrderList(orderList);
    	modelMap.put("purchaseOrders", po);
    	modelMap.put("plantstatuses", Arrays.asList(PlantStatus.values()));
    	return "purchaseorders/delivery/list";
    }
    
    @RequestMapping(method = RequestMethod.POST)
    public String updatePlantStatus(@Valid PurchaseOrderListDTO poDTO, HttpServletRequest request, ModelMap modelMap) {
     	String selectedPurchaseOrder = request.getParameter("radio");
     	
     	PurchaseOrder po = PurchaseOrder.findPurchaseOrder(Long.parseLong(selectedPurchaseOrder));
     	po.setPlantStatus(poDTO.getPlantStatus());
     	po.persist();
     	
    	List<PurchaseOrder> orderList = repository.findByStatus(HireRequestStatus.OPEN);
    	List<PurchaseOrder> pendingUpdateRequests = repository.findByStatus(HireRequestStatus.PENDING_UPDATE);
    	orderList.addAll(pendingUpdateRequests);
    	
     	poDTO.setOrderList(orderList);
    	modelMap.put("purchaseOrders", poDTO);
    	modelMap.put("plantstatuses", Arrays.asList(PlantStatus.values()));
    	return "purchaseorders/delivery/list";
    }
}
