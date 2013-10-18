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
import cs.ut.domain.rest.PurchaseOrderResource;
import cs.ut.domain.rest.PurchaseOrderResourceAssembler;
import cs.ut.domain.rest.PurchaseOrderStatusResource;
import cs.ut.util.ExtendedLink;

@Controller
@RequestMapping("/rest")
public class PurchaseOrderRestController {

	@RequestMapping(method = RequestMethod.PUT, value = "/pos/{id}")
	public ResponseEntity<Void> updatePurchaseOrder(
			@PathVariable("id") Long id, @RequestBody PurchaseOrderResource res) {
		PurchaseOrder p = PurchaseOrder.findPurchaseOrder(id);
		p.setEndDate(res.getEndDate());
		p.setPlant(Plant.findPlant(res.getPlantResource().getIdentifier()));
		p.setStartDate(res.getStartDate());
		p.setStatus(res.getStatus());
		p.setTotalCost(res.getTotalCost());
		p.persist();
		HttpHeaders headers = new HttpHeaders();
		URI location = ServletUriComponentsBuilder.fromCurrentRequestUri()
				.pathSegment(p.getId().toString()).build().toUri();
		headers.setLocation(location);
		ResponseEntity<Void> response = new ResponseEntity<>(headers,
				HttpStatus.OK);
		return response;
	}

	@RequestMapping(method = RequestMethod.PUT, value = "/pos/{id}/cancel")
	public ResponseEntity<Void> cancelPurchaseOrder(
			@PathVariable("id") Long id,
			@RequestBody PurchaseOrderStatusResource res) {
		PurchaseOrder p = PurchaseOrder.findPurchaseOrder(id);
		p.setStatus(res.getStatus());
		p.persist();
		HttpHeaders headers = new HttpHeaders();
		URI location = ServletUriComponentsBuilder.fromCurrentRequestUri()
				.pathSegment(p.getId().toString()).build().toUri();
		headers.setLocation(location);
		ResponseEntity<Void> response = new ResponseEntity<>(headers,
				HttpStatus.OK);
		return response;
	}


	@RequestMapping(method = RequestMethod.POST, value = "/pos/{id}/accept")
	public ResponseEntity<Void> acceptPO(@PathVariable("id") Long id) {
		PurchaseOrder po = PurchaseOrder.findPurchaseOrder(id);
		ResponseEntity<Void> response;
		if (po.getStatus().equals(HireRequestStatus.PENDING_CONFIRMATION)) {
			po.setStatus(HireRequestStatus.OPEN);
			po.persist();
			response = new ResponseEntity<>(HttpStatus.OK);
		} else
			response = new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
		return response;
	}

	@RequestMapping(method = RequestMethod.POST, value = "/pos/{id}/reject")
	public ResponseEntity<Void> rejectPO(@PathVariable("id") Long id) {
		PurchaseOrder po = PurchaseOrder.findPurchaseOrder(id);
		ResponseEntity<Void> response;
		if (po.getStatus().equals(HireRequestStatus.PENDING_CONFIRMATION)) {
			po.setStatus(HireRequestStatus.REJECTED);
			po.persist();
			response = new ResponseEntity<>(HttpStatus.OK);
		} else
			response = new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
		return response;
	}
	

	@RequestMapping(method = RequestMethod.POST, value = "/pos")
	public ResponseEntity<Void> createPO(
			@RequestBody PurchaseOrderResource res) {
		PurchaseOrder po = new PurchaseOrder();
		po.setEndDate(res.getEndDate());
		po.setPlant(Plant.findPlant(res.getPlantResource().getIdentifier()));
		po.setStartDate(res.getStartDate());
		po.setStatus(res.getStatus());
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

	@RequestMapping(method = RequestMethod.GET, value = "/pos/{id}")
	public ResponseEntity<PurchaseOrderResource> getPO(
			@PathVariable("id") Long id) {
		PurchaseOrder po = PurchaseOrder.findPurchaseOrder(id);
		PurchaseOrderResourceAssembler assembler = new PurchaseOrderResourceAssembler();
		PurchaseOrderResource resource = assembler.toResource(po);
		switch (po.getStatus()) {
		case PENDING_CONFIRMATION:
			Method _rejectPO = null;
			Method _acceptPO = null;
			try {
				_rejectPO = PurchaseOrderRestController.class.getMethod(
						"rejectPO", Long.class);
				_acceptPO = PurchaseOrderRestController.class.getMethod(
						"acceptPO", Long.class);
			} catch (Exception e) {
				e.printStackTrace();
			}

			String acceptLink = linkTo(_acceptPO, po.getId()).toUri()
					.toString();
			resource.add(new ExtendedLink(acceptLink, "acceptPO", "POST"));
			String rejectLink = linkTo(_rejectPO, po.getId()).toUri()
					.toString();
			resource.add(new ExtendedLink(rejectLink, "rejectPO", "DELETE"));
			break;
		default:
			break;
		}
		ResponseEntity<PurchaseOrderResource> response = new ResponseEntity<>(
				resource, HttpStatus.OK);
		return response;
	}
}
