package cs.ut.web;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

import cs.ut.domain.HireRequestStatus;
import cs.ut.domain.PurchaseOrder;
import cs.ut.domain.bean.PurchaseOrderListDTO;
import cs.ut.domain.rest.PurchaseOrderResource;
import cs.ut.repository.PurchaseOrderRepository;
import cs.ut.util.RestHelper;

@RequestMapping("/purchaseorders/pending/**")
@Controller
public class PurchaseOrderApproveController {
	
	@Autowired
	PurchaseOrderRepository repository;
	
	@Value("${webappurl}")
	String webappurl;
	
	@Value("${builditurl}")
	String builditurl;
	
	@Value("${rentit.role.user}")
	String rentitUser;
	
	@Value("${rentit.role.user.password}")
	String rentitUserPassword;

    @RequestMapping(method = RequestMethod.GET)
    public String displayNewPurchaseOrders(HttpServletRequest request, ModelMap modelMap) {
    	PurchaseOrderListDTO po = new PurchaseOrderListDTO();
    	List<PurchaseOrder> orderList = repository.findByStatus(HireRequestStatus.PENDING_CONFIRMATION);
    	po.setOrderList(orderList);
    	modelMap.put("purchaseOrders", po);
    	return "purchaseorders/pending/list";
    }
    
    
    @RequestMapping(method = RequestMethod.POST)
    public String acceptOrRejectPO(@Valid PurchaseOrderListDTO poDTO, HttpServletRequest request, ModelMap modelMap) {
     	String selectedPurchaseOrder = request.getParameter("radio");
     	String rejectComment = request.getParameter("rejectionReason");
    	String decision = request.getParameter("submit");;
		RestTemplate template = new RestTemplate();
 		
		HttpEntity<String> requestEntity = new HttpEntity<String>(
				RestHelper.getHeaders(rentitUser, rentitUserPassword));
    	if(decision.equals("Approve")){
    		String acceptUrl = webappurl + "/rest/pos/" + selectedPurchaseOrder
			+ "/accept";
    		ResponseEntity<PurchaseOrderResource> resp = template.exchange(acceptUrl, HttpMethod.PUT, requestEntity, PurchaseOrderResource.class);
    	}else if(decision.equals("Reject")){
    		String rejectUrl = webappurl + "/rest/pos/" + selectedPurchaseOrder
			+ "/reject";
    		template.exchange(rejectUrl, HttpMethod.DELETE, requestEntity, PurchaseOrderResource.class);
    		System.out.println("untsakad");
    		long poId = Long.parseLong(selectedPurchaseOrder);
    		long phrId = PurchaseOrder.findPurchaseOrder(poId).getPlantHireRequestId();
    		
    		String builditRejectUrl = builditurl + "/rest/phr/" + phrId + "/reject?comment=" + rejectComment;
    		ResponseEntity<String> resp = template.exchange(builditRejectUrl, HttpMethod.DELETE, requestEntity, String.class);
    		System.out.println(resp);
    	}else{
    		System.out.println("ERROR, this should not happen");
    	}
   
    	PurchaseOrderListDTO po = new PurchaseOrderListDTO();
    	List<PurchaseOrder> orderList = repository.findByStatus(HireRequestStatus.PENDING_CONFIRMATION);
    	po.setOrderList(orderList);
    	modelMap.put("purchaseOrders", po);
    	return "purchaseorders/pending/list";
    }

}
