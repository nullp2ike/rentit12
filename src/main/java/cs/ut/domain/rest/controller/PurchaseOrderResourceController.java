package cs.ut.domain.rest.controller;

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

import cs.ut.domain.Plant;
import cs.ut.domain.PurchaseOrder;
import cs.ut.domain.rest.PurchaseOrderResource;
import cs.ut.domain.rest.PurchaseOrderResourceAssembler;
import cs.ut.domain.rest.PurchaseOrderStatusResource;

@Controller
@RequestMapping("/rest")
public class PurchaseOrderResourceController {
	
	@RequestMapping(method = RequestMethod.POST, value="/pos")
	public ResponseEntity<Void> createPurchaseOrder(@RequestBody PurchaseOrderResource res) {
			PurchaseOrder p = new PurchaseOrder();
			p.setEndDate(res.getEndDate());
			p.setPlant(Plant.findPlant(res.getPlantId()));
			p.setStartDate(res.getStartDate());
			p.setStatus(res.getStatus());
			p.setTotalCost(res.getTotalCost());
			p.persist();
			HttpHeaders headers = new HttpHeaders();
			URI location = ServletUriComponentsBuilder.fromCurrentRequestUri().pathSegment(p.getId().toString()).build().toUri();
			headers.setLocation(location);
			ResponseEntity<Void> response = new ResponseEntity<>(headers, HttpStatus.CREATED);
			return response;
	}
	
	@RequestMapping(method = RequestMethod.PUT, value="/pos/{id}")
	public ResponseEntity<Void> updatePurchaseOrder(@PathVariable("id") Long id, @RequestBody PurchaseOrderResource res) {
			PurchaseOrder p = PurchaseOrder.findPurchaseOrder(id);
			p.setEndDate(res.getEndDate());
			p.setPlant(Plant.findPlant(res.getPlantId()));
			p.setStartDate(res.getStartDate());
			p.setStatus(res.getStatus());
			p.setTotalCost(res.getTotalCost());
			p.persist();
			HttpHeaders headers = new HttpHeaders();
			URI location = ServletUriComponentsBuilder.fromCurrentRequestUri().pathSegment(p.getId().toString()).build().toUri();
			headers.setLocation(location);
			ResponseEntity<Void> response = new ResponseEntity<>(headers, HttpStatus.OK);
			return response;
	}
	
	@RequestMapping(method = RequestMethod.PUT, value="/pos/{id}/cancel")
	public ResponseEntity<Void> cancelPurchaseOrder(@PathVariable("id") Long id, @RequestBody PurchaseOrderStatusResource res) {
			PurchaseOrder p = PurchaseOrder.findPurchaseOrder(id);
			p.setStatus(res.getStatus());
			p.persist();
			HttpHeaders headers = new HttpHeaders();
			URI location = ServletUriComponentsBuilder.fromCurrentRequestUri().pathSegment(p.getId().toString()).build().toUri();
			headers.setLocation(location);
			ResponseEntity<Void> response = new ResponseEntity<>(headers, HttpStatus.OK);
			return response;
	}
	
	@RequestMapping(method = RequestMethod.GET, value="/pos/{id}")
	public ResponseEntity<PurchaseOrderResource> getPurchaseOrder(@PathVariable("id") Long id) {
			PurchaseOrder po = PurchaseOrder.findPurchaseOrder(id);
			PurchaseOrderResourceAssembler assembler = new PurchaseOrderResourceAssembler();
			PurchaseOrderResource poRes = assembler.getPurchaseOrderResource(po);
			ResponseEntity<PurchaseOrderResource> response = new ResponseEntity<>(poRes, HttpStatus.OK);
			return response;
	}
	
}
