package cs.ut.domain.service;

@SuppressWarnings("serial")
public class InvalidHirePeriodException extends Exception {
	public InvalidHirePeriodException(String message) {
		super(message);
	}
}