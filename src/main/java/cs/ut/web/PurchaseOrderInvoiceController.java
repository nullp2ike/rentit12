package cs.ut.web;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cs.ut.domain.HireRequestStatus;
import cs.ut.domain.PurchaseOrder;
import cs.ut.domain.bean.PurchaseOrderListDTO;
import cs.ut.repository.PurchaseOrderRepository;
import cs.ut.util.MailMail;

@RequestMapping("/purchaseorders/approved/**")
@Controller
public class PurchaseOrderInvoiceController {
	
	@Autowired
	private transient MailSender mailTemplate;

	@Autowired
	PurchaseOrderRepository repository;

	@RequestMapping(method = RequestMethod.GET)
	public String displayApprovedPurchaseOrders(HttpServletRequest request,
			ModelMap modelMap) {
		PurchaseOrderListDTO po = new PurchaseOrderListDTO();
		List<PurchaseOrder> orderList = repository
				.findForSendingInvoice(HireRequestStatus.CLOSED);
		po.setOrderList(orderList);
		modelMap.put("purchaseOrders", po);
		return "purchaseorders/approved/list";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String sendInvoice(@Valid PurchaseOrderListDTO poDTO,
			HttpServletRequest request, ModelMap modelMap) {
		String selectedPurchaseOrder = request.getParameter("radio");
		PurchaseOrder po = PurchaseOrder.findPurchaseOrder(Long
				.parseLong(selectedPurchaseOrder));
		sendInvoice();
		po.setInvoiceSent(true);
		List<PurchaseOrder> orderList = repository
				.findForSendingInvoice(HireRequestStatus.CLOSED);
		poDTO.setOrderList(orderList);
		modelMap.put("purchaseOrders", poDTO);
		return "purchaseorders/approved/list";
	}

	private void sendInvoice() {
		
		SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
				simpleMailMessage.setFrom("rentitteam12@gmail.com");
				simpleMailMessage.setSubject("Testing123");
				simpleMailMessage.setTo("builditteam12@gmail.com");
				simpleMailMessage.setText("Testing only \n\n Hello Spring Email Sender");
				mailTemplate.send(simpleMailMessage);
	}
}
