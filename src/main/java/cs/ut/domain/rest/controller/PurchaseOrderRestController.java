package cs.ut.domain.rest.controller;

import java.lang.reflect.Method;
import java.net.URI;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;
import cs.ut.domain.HireRequestStatus;
import cs.ut.domain.Plant;
import cs.ut.domain.PurchaseOrder;
import cs.ut.domain.PurchaseOrderUpdate;
import cs.ut.domain.rest.PurchaseOrderResource;
import cs.ut.domain.rest.PurchaseOrderResourceAssembler;
import cs.ut.domain.rest.PurchaseOrderStatusResource;
import cs.ut.util.ExtendedLink;

@Controller
@RequestMapping("/rest/pos/")
public class PurchaseOrderRestController {

	//OK
	@RequestMapping(method = RequestMethod.PUT, value = "{id}")
	public ResponseEntity<PurchaseOrderResource> updatePO(@PathVariable("id") Long id,
			@RequestBody PurchaseOrderResource res){
		
		PurchaseOrder po = PurchaseOrder.findPurchaseOrder(id);
		ResponseEntity<PurchaseOrderResource> response;
		
		if (po.getStatus().equals(HireRequestStatus.REJECTED)
				|| po.getStatus()
						.equals(HireRequestStatus.PENDING_CONFIRMATION)) {
			po.setStatus(HireRequestStatus.PENDING_CONFIRMATION);
			po.setPlant(Plant.findPlant(res.getPlantResource().getIdentifier()));
			po.setStartDate(res.getStartDate());
			po.setEndDate(res.getEndDate());
			po.setTotalCost(res.getTotalCost());
			po.persist();
			
			PurchaseOrderResourceAssembler assembler = new PurchaseOrderResourceAssembler();
			PurchaseOrderResource resource = assembler.toResource(po);
			
			try {
				addMethodLink(po, resource, "acceptPO", "POST");
				addMethodLink(po, resource, "rejectPO", "DELETE");
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			response = new ResponseEntity<>(resource, HttpStatus.OK);
		} else
			response = new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);

		return response;
	}

	//OK
	@RequestMapping(method = RequestMethod.DELETE, value = "{id}")
	public ResponseEntity<Void> closePO(
			@PathVariable("id") Long id) {
		PurchaseOrder p = PurchaseOrder.findPurchaseOrder(id);
		p.setStatus(HireRequestStatus.CLOSED);
		p.persist();
		HttpHeaders headers = new HttpHeaders();
		URI location = ServletUriComponentsBuilder.fromCurrentRequestUri()
				.pathSegment(p.getId().toString()).build().toUri();
		headers.setLocation(location);
		ResponseEntity<Void> response = new ResponseEntity<>(headers,
				HttpStatus.OK);
		return response;
	}

	//
	@RequestMapping(method = RequestMethod.PUT, value = "{id}/accept")
	public ResponseEntity<PurchaseOrderResource> acceptPO(@PathVariable("id") Long id) {
		PurchaseOrder po = PurchaseOrder.findPurchaseOrder(id);
		ResponseEntity<PurchaseOrderResource> response;
		
		if (po.getStatus().equals(HireRequestStatus.PENDING_CONFIRMATION)) {
			po.setStatus(HireRequestStatus.OPEN);
			po.persist();
			
			PurchaseOrderResourceAssembler assembler = new PurchaseOrderResourceAssembler();
			PurchaseOrderResource resource = assembler.toResource(po);

			try {
				Method methodLink = PurchaseOrderRestController.class.getMethod(
						"requestPOUpdate", Long.class, PurchaseOrderResource.class);
				String link = linkTo(methodLink, po.getId()).toUri()
						.toString();
				resource.add(new ExtendedLink(link, "requestPOUpdate", "POST"));
				
				Method methodLink2 = PurchaseOrderRestController.class.getMethod(
						"closePO", Long.class);
				String link2 = linkTo(methodLink2, po.getId()).toUri()
						.toString();
				resource.add(new ExtendedLink(link2, "closePO", "DELETE"));
			
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			response = new ResponseEntity<>(resource, HttpStatus.OK);
		} else
			response = new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
		return response;
	}
	
	
	@RequestMapping(method = RequestMethod.POST, value = "{id}/updates")
	public ResponseEntity<PurchaseOrderResource> requestPOUpdate(@PathVariable("id") Long id, @RequestBody PurchaseOrderResource res){
		PurchaseOrder po = PurchaseOrder.findPurchaseOrder(id);
		ResponseEntity<PurchaseOrderResource> response;
		
		if (po.getStatus().equals(HireRequestStatus.OPEN)) {
			po.setStatus(HireRequestStatus.PENDING_UPDATE);
			po.persist();
			
			
			//TODO Might have to check here for an existing update first		
			PurchaseOrderUpdate poUpdate = new PurchaseOrderUpdate();
			poUpdate.setPurchaseOrderId(id);
			poUpdate.setEndDate(res.getEndDate());
			poUpdate.setPlant(Plant.findPlant(res.getPlantResource().getIdentifier()));
			poUpdate.setStartDate(res.getStartDate());
			poUpdate.setTotalCost(res.getTotalCost());
			poUpdate.persist();
			
			
			//TODO Add links to the response
			/*
			String acceptLink = linkTo(_acceptPOUpdate, po.getId()).toUri()
					.toString();
			resource.add(new ExtendedLink(acceptLink, "acceptPOUpdate", "POST"));
			String rejectLink = linkTo(_rejectPOUpdate, po.getId()).toUri()
					.toString();
			resource.add(new ExtendedLink(rejectLink, "rejectPOUpdate", "DELETE"));
			*/
			
			
			PurchaseOrderResourceAssembler assembler = new PurchaseOrderResourceAssembler();
			PurchaseOrderResource resource = assembler.toResource(po);
		
			response = new ResponseEntity<>(resource,
					HttpStatus.OK);
		} else
			response = new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
		return response;
	}
	
	// OK
	@RequestMapping(method = RequestMethod.GET, value = "{id}")
	public ResponseEntity<PurchaseOrderResource> getPO(
			@PathVariable("id") Long id) throws NoSuchMethodException, SecurityException {
		PurchaseOrder po = PurchaseOrder.findPurchaseOrder(id);
		PurchaseOrderResourceAssembler assembler = new PurchaseOrderResourceAssembler();
		PurchaseOrderResource resource = assembler.toResource(po);
		switch (po.getStatus()) {
		case PENDING_CONFIRMATION:
			addMethodLink(po, resource, "rejectPO", "DELETE");
			addMethodLink(po, resource, "acceptPO", "POST");
			break;
		case OPEN:
			addMethodLink(po, resource, "requestPOUpdate", "POST");
			addMethodLink(po, resource, "closePO", "DELETE");
			break;
		case PENDING_UPDATE:
			addMethodLink(po, resource, "rejectPOUpdate", "DELETE");
			addMethodLink(po, resource, "acceptPOUpdate", "POST");
			break;
		case REJECTED:
			addMethodLink(po, resource, "updatePO", "PUT");
			break;
		default:
			break;
		}
		ResponseEntity<PurchaseOrderResource> response = new ResponseEntity<>(
				resource, HttpStatus.OK);
		return response;
	}
	

	@RequestMapping(method = RequestMethod.POST, value = "{id}/reject")
	public ResponseEntity<PurchaseOrderResource> rejectPO(@PathVariable("id") Long id) {
		PurchaseOrder po = PurchaseOrder.findPurchaseOrder(id);
		ResponseEntity<PurchaseOrderResource> response;
		if (po.getStatus().equals(HireRequestStatus.PENDING_CONFIRMATION)) {
			po.setStatus(HireRequestStatus.REJECTED);
			po.persist();
			PurchaseOrderResourceAssembler assembler = new PurchaseOrderResourceAssembler();
			PurchaseOrderResource resource = assembler.toResource(po);
			try {
				addMethodLinkWithResource(po, resource, "updatePO", "PUT");
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			response = new ResponseEntity<>(resource, HttpStatus.OK);
		} else
			response = new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
		return response;
	}

	@RequestMapping(method = RequestMethod.POST, value = "")
	public ResponseEntity<Void> createPO(@RequestBody PurchaseOrderResource res)
			throws NoSuchMethodException, SecurityException {
		PurchaseOrder po = new PurchaseOrder();
		po.setEndDate(res.getEndDate());
		po.setPlant(Plant.findPlant(res.getPlantResource().getIdentifier()));
		po.setStartDate(res.getStartDate());
		po.setStatus(HireRequestStatus.PENDING_CONFIRMATION);
		po.setTotalCost(res.getTotalCost());
		po.persist();

		HttpHeaders headers = new HttpHeaders();
		URI location = ServletUriComponentsBuilder.fromCurrentRequestUri()
				.pathSegment(po.getId().toString()).build().toUri();
		headers.setLocation(location);
		ResponseEntity<Void> response = new ResponseEntity<>(headers,
				HttpStatus.CREATED);
		return response;
	}

	
	private void addMethodLinkWithResource(PurchaseOrder po,
			PurchaseOrderResource resource, String action,  String method) throws NoSuchMethodException {
		Method methodLink = PurchaseOrderRestController.class.getMethod(
				action, Long.class, PurchaseOrderResource.class);
		String link = linkTo(methodLink, po.getId()).toUri()
				.toString();
		resource.add(new ExtendedLink(link, action, method));
	}
	
	private void addMethodLink(PurchaseOrder po,
			PurchaseOrderResource resource, String action,  String method) throws NoSuchMethodException {
		Method methodLink = PurchaseOrderRestController.class.getMethod(
				action, Long.class);
		String link = linkTo(methodLink, po.getId()).toUri()
				.toString();
		resource.add(new ExtendedLink(link, action, method));
	}
}
