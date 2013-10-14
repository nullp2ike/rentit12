package cs.ut.domain.rest;

import cs.ut.domain.Plant;
import cs.ut.domain.PurchaseOrder;

public class PurchaseOrderResourceAssembler {
	
	public PurchaseOrderResource getPurchaseOrderResource(PurchaseOrder purchaseOrder){
		PurchaseOrderResource purchaseOrderResource = new PurchaseOrderResource();
		purchaseOrderResource.setConstructionSite(purchaseOrder.getConstructionSite());
		purchaseOrderResource.setEndDate(purchaseOrder.getEndDate());
		purchaseOrderResource.setPlantId(purchaseOrder.getPlant().getId());
		purchaseOrderResource.setSiteEngineer(purchaseOrder.getSiteEngineer());
		purchaseOrderResource.setStartDate(purchaseOrder.getStartDate());
		purchaseOrderResource.setStatus(purchaseOrder.getStatus());
		purchaseOrderResource.setTotalCost(purchaseOrder.getTotalCost());
		return purchaseOrderResource;
	}

}
