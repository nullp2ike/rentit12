package cs.ut.domain.bean;

import java.util.List;

import org.springframework.roo.addon.javabean.RooJavaBean;

import cs.ut.domain.PurchaseOrder;

@RooJavaBean
public class PurchaseOrderListDTO {
	
	private List<PurchaseOrder> orderList;
	private int radio;
	
}
