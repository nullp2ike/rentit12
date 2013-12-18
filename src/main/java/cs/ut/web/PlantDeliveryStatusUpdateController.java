package cs.ut.web;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cs.ut.domain.PlantStatus;
import cs.ut.domain.PurchaseOrder;
import cs.ut.domain.bean.PurchaseOrderListDTO;

@RequestMapping("/purchaseorders/delivery/**")
@Controller
public class PlantDeliveryStatusUpdateController {
	
    @RequestMapping(method = RequestMethod.GET)
    public String displayNewPurchaseOrders(HttpServletRequest request, ModelMap modelMap) {
    	PurchaseOrderListDTO po = new PurchaseOrderListDTO();
    	List<PurchaseOrder> orderList = PurchaseOrder.findAllPurchaseOrders();
    	po.setOrderList(orderList);
    	modelMap.put("purchaseOrders", po);
    	modelMap.put("plantstatuses", Arrays.asList(PlantStatus.values()));
    	return "purchaseorders/delivery/list";
    }
    
    @RequestMapping(method = RequestMethod.POST)
    public String acceptOrRejectPO(@Valid PurchaseOrderListDTO poDTO, HttpServletRequest request, ModelMap modelMap) {
     	String selectedPurchaseOrder = request.getParameter("radio");
     	
     	PurchaseOrder po = PurchaseOrder.findPurchaseOrder(Long.parseLong(selectedPurchaseOrder));
     	po.setPlantStatus(poDTO.getPlantStatus());
     	po.persist();
     	
     	List<PurchaseOrder> orderList = PurchaseOrder.findAllPurchaseOrders();
     	poDTO.setOrderList(orderList);
    	modelMap.put("purchaseOrders", poDTO);
    	modelMap.put("plantstatuses", Arrays.asList(PlantStatus.values()));
    	return "purchaseorders/delivery/list";
    }
}
