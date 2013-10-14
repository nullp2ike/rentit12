package cs.ut.domain.rest;

import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

import cs.ut.domain.HireRequestStatus;

@RooJavaBean
@RooToString
@XmlRootElement(name = "purchaseOrder")
public class PurchaseOrderStatusResource {
	
    private HireRequestStatus status;

}
