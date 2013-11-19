package cs.ut.web;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.List;

import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cs.ut.domain.HireRequestStatus;
import cs.ut.domain.PurchaseOrder;
import cs.ut.domain.bean.PurchaseOrderListDTO;
import cs.ut.repository.PurchaseOrderRepository;

@RequestMapping("/purchaseorders/approved/**")
@Controller
public class PurchaseOrderInvoiceController {

	@Autowired
	JavaMailSenderImpl sender;
	
	@Value("${webappurl}")
	String webappurl;
	
	@Value("${email.username}")
	String fromEmail;
	
	@Value("${email.buildit}")
	String toEmail;
	
	@Autowired
	PurchaseOrderRepository repository;

	@RequestMapping(method = RequestMethod.GET)
	public String displayApprovedPurchaseOrders(HttpServletRequest request,
			ModelMap modelMap) {
		PurchaseOrderListDTO po = new PurchaseOrderListDTO();
		List<PurchaseOrder> orderList = repository
				.findForSendingInvoice(HireRequestStatus.OPEN);
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
		try {
			sendInvoice(createInvoice(po.getId().toString(), po.getTotalCost()));
		} catch (ParserConfigurationException | TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		po.setInvoiceSent(true);
		List<PurchaseOrder> orderList = repository
				.findForSendingInvoice(HireRequestStatus.CLOSED);
		poDTO.setOrderList(orderList);
		modelMap.put("purchaseOrders", poDTO);
		return "purchaseorders/approved/list";
	}

	private void sendInvoice(String invoiceContent) {
		MimeMessage message = sender.createMimeMessage();
		SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
		simpleMailMessage.setFrom(fromEmail);
		simpleMailMessage.setSubject("Your invoice for purcase order");
		simpleMailMessage.setTo(toEmail);
		simpleMailMessage.setText("Hello, your invoice is attached to the email");
		try {
			MimeMessageHelper helper = new MimeMessageHelper(message, true);
			helper.setFrom(simpleMailMessage.getFrom());
			helper.setTo(simpleMailMessage.getTo());
			helper.setSubject(simpleMailMessage.getSubject());
			helper.setText(simpleMailMessage.getText());
			DataSource ds = null;
			try {
				ds = new ByteArrayDataSource(invoiceContent, "application/xml");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			helper.addAttachment("invoice.xml", ds);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
				sender.send(message);
	}
 
	private String createInvoice(String poId, BigDecimal totalSum) throws ParserConfigurationException, TransformerException{
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		
		Document doc = docBuilder.newDocument();
		doc.setXmlStandalone(false);
		Element rootElement = doc.createElement("invoice");
		doc.appendChild(rootElement);
 
		
		Element link = doc.createElement("purchaseOrderHRef");
		link.setTextContent(webappurl + "/rest/pos/" + poId);
		rootElement.appendChild(link);
		
		Element total = doc.createElement("total");
		total.setTextContent(totalSum.toString());
		rootElement.appendChild(total);
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		StringWriter writer = new StringWriter();
		transformer.transform(new DOMSource(doc), new StreamResult(writer));
		String output = writer.getBuffer().toString();
		return output;
	}
}
