package cs.ut.domain.rest;

import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

import cs.ut.domain.Plant;
import cs.ut.domain.PurchaseOrder;
import cs.ut.domain.rest.controller.PurchaseOrderRestController;

public class PurchaseOrderResourceAssembler extends
		ResourceAssemblerSupport<PurchaseOrder, PurchaseOrderResource> {

	public PurchaseOrderResourceAssembler() {
		super(PurchaseOrderRestController.class, PurchaseOrderResource.class);
	}

	@Override
	public PurchaseOrderResource toResource(PurchaseOrder po) {
		PurchaseOrderResource por = createResourceWithId(po.getId(), po);
		por.setEndDate(po.getEndDate());
		por.setStartDate(po.getStartDate());
		por.setTotalCost(po.getTotalCost());
		por.setStatus(po.getStatus());
		PlantResource plantResource;
		
		if (po.getPlant() != null) {
			PlantResourceAssembler assembler = new PlantResourceAssembler();
			plantResource = assembler.getPlantResource(po.getPlant());
			por.setPlantResource(plantResource);
		}
		return por;
	}
	
	public PurchaseOrderResource getPurchaseOrderResource(
			PurchaseOrder purchaseOrder) {
		PurchaseOrderResource purchaseOrderResource = new PurchaseOrderResource();
		PlantResourceAssembler assembler = new PlantResourceAssembler();
		purchaseOrderResource.setEndDate(purchaseOrder.getEndDate());
		purchaseOrderResource.setPlantResource(assembler.getPlantResource(purchaseOrder.getPlant()));
		purchaseOrderResource.setStartDate(purchaseOrder.getStartDate());
		purchaseOrderResource.setStatus(purchaseOrder.getStatus());
		purchaseOrderResource.setTotalCost(purchaseOrder.getTotalCost());
		return purchaseOrderResource;
	}

}
