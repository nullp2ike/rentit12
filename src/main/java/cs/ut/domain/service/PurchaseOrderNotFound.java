package cs.ut.domain.service;

@SuppressWarnings("serial")
public class PurchaseOrderNotFound extends Exception {
	public PurchaseOrderNotFound(String message) {
		super(message);
	}
}