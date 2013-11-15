package cs.ut.web;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

import cs.ut.domain.HireRequestStatus;
import cs.ut.domain.PurchaseOrder;
import cs.ut.domain.bean.PurchaseOrderListDTO;
import cs.ut.repository.PurchaseOrderRepository;
import cs.ut.util.LoadProperties;

@RequestMapping("/purchaseorders/new/**")
@Controller
public class PurchaseOrderApproveController {
	
	@Autowired
	PurchaseOrderRepository repository;

    @RequestMapping(method = RequestMethod.GET)
    public String displayNewPurchaseOrders(HttpServletRequest request, ModelMap modelMap) {
    	PurchaseOrderListDTO po = new PurchaseOrderListDTO();
    	List<PurchaseOrder> orderList = repository.findByStatus(HireRequestStatus.PENDING_CONFIRMATION);
    	po.setOrderList(orderList);
    	modelMap.put("purchaseOrders", po);
    	return "purchaseorders/new/list";
    }
    
    
    @RequestMapping(method = RequestMethod.POST)
    public String acceptOrRejectPO(@Valid PurchaseOrderListDTO poDTO, HttpServletRequest request, ModelMap modelMap) {
     	String selectedPurchaseOrder = request.getParameter("radio");
     	String rejectComment = request.getParameter("rejectionReason");
    	String decision = request.getParameter("submit");
		LoadProperties props = new LoadProperties();
		String app_url = props.loadProperty("webappurl");
		String buildItUrl = props.loadProperty("builditurl");
		RestTemplate template = new RestTemplate();
    	if(decision.equals("Approve")){
    		String acceptUrl = app_url + "/rest/pos/" + selectedPurchaseOrder
			+ "/accept";
    		template.put(acceptUrl, PurchaseOrder.class);
    		
    	}else if(decision.equals("Reject")){
    		String rejectUrl = app_url + "/rest/pos/" + selectedPurchaseOrder
			+ "/reject";
    		template.delete(rejectUrl, PurchaseOrder.class);
    		long poId = Long.parseLong(selectedPurchaseOrder);
    		long phrId = PurchaseOrder.findPurchaseOrder(poId).getPlantHireRequestId();
    		String builditRejectUrl = buildItUrl + "/rest/phr/" + phrId + "/reject?comment=" + rejectComment;
    		template.delete(builditRejectUrl);
    	}else{
    		System.out.println("ERROR, this should not happen");
    	}
   
    	PurchaseOrderListDTO po = new PurchaseOrderListDTO();
    	List<PurchaseOrder> orderList = repository.findByStatus(HireRequestStatus.PENDING_CONFIRMATION);
    	po.setOrderList(orderList);
    	modelMap.put("purchaseOrders", po);
    	return "purchaseorders/new/list";
    }

}
