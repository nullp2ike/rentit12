package cs.ut.web;

import java.util.ArrayList;
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
import cs.ut.domain.PurchaseOrderUpdate;
import cs.ut.domain.bean.PurchaseOrderListDTO;
import cs.ut.domain.rest.PurchaseOrderResource;
import cs.ut.repository.PurchaseOrderRepository;
import cs.ut.util.RestHelper;

@RequestMapping("/purchaseorders/updates/**")
@Controller
public class PurchaseOrderUpdatesController {

	@Autowired
	PurchaseOrderRepository repository;

	@Value("${webappurl}")
	String webappurl;

	@Value("${rentit.role.user}")
	String rentitUser;

	@Value("${rentit.role.user.password}")
	String rentitUserPassword;

	@RequestMapping(method = RequestMethod.GET)
	public String displayUpdatedPOs(HttpServletRequest request,
			ModelMap modelMap) {
		PurchaseOrderListDTO po = new PurchaseOrderListDTO();
		List<PurchaseOrderUpdate> updateList = repository
				.findUpdatesByStatus(HireRequestStatus.PENDING_UPDATE);
		List<PurchaseOrder> poList = new ArrayList<PurchaseOrder>();
		for (PurchaseOrderUpdate purchaseOrderUpdate : updateList) {
			poList.add(PurchaseOrder.findPurchaseOrder(purchaseOrderUpdate
					.getPurchaseOrderId()));
		}

		po.setPoUpdateList(updateList);
		po.setOrderList(poList);
		modelMap.put("poListDTO", po);
		return "purchaseorders/updates/list";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String acceptOrRejectPOUpdate(@Valid PurchaseOrderListDTO poListDTO,
			HttpServletRequest request, ModelMap modelMap) {
		String uid = request.getParameter("radio");
		long poid = PurchaseOrderUpdate.findPurchaseOrderUpdate(
				Long.parseLong(uid)).getPurchaseOrderId();
		String decision = request.getParameter("submit");
		RestTemplate template = new RestTemplate();

		HttpEntity<String> requestEntity = new HttpEntity<String>(
				RestHelper.getHeaders(rentitUser, rentitUserPassword));
		if (decision.equals("Approve")) {
			String acceptUrl = webappurl + "/rest/pos/" + poid + "/updates/"
					+ uid + "/accept";
			ResponseEntity<PurchaseOrderResource> resp = template.exchange(
					acceptUrl, HttpMethod.POST, requestEntity,
					PurchaseOrderResource.class);
			System.out.println(resp);
		} else if (decision.equals("Reject")) {
			String rejectUrl = webappurl + "/rest/pos/" + poid + "/updates/"
					+ uid + "/reject";
			ResponseEntity<PurchaseOrderResource> resp = template.exchange(
					rejectUrl, HttpMethod.DELETE, requestEntity,
					PurchaseOrderResource.class);
			System.out.println(resp);
		}

		List<PurchaseOrderUpdate> updateList = repository
				.findUpdatesByStatus(HireRequestStatus.PENDING_UPDATE);
		List<PurchaseOrder> poList = new ArrayList<PurchaseOrder>();
		for (PurchaseOrderUpdate purchaseOrderUpdate : updateList) {
			poList.add(PurchaseOrder.findPurchaseOrder(purchaseOrderUpdate
					.getPurchaseOrderId()));
		}

		poListDTO.setPoUpdateList(updateList);
		poListDTO.setOrderList(poList);
		modelMap.put("poListDTO", poListDTO);
		return "purchaseorders/updates/list";
	}

}
