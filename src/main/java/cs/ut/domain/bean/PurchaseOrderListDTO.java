package cs.ut.domain.bean;

import java.util.List;

import org.springframework.roo.addon.javabean.RooJavaBean;

import cs.ut.domain.PlantStatus;
import cs.ut.domain.PurchaseOrder;
import cs.ut.domain.PurchaseOrderUpdate;

@RooJavaBean
public class PurchaseOrderListDTO {
	
	private List<PurchaseOrder> orderList;
	private List<PurchaseOrderUpdate> poUpdateList;
	private int radio;
	private PlantStatus plantStatus;
	
}
