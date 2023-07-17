package lt.paymentprocessing.exception;

public class PaymentCancellationException extends RuntimeException {

    public PaymentCancellationException(Long paymentId) {
        super("Payment cannot be cancelled or is already cancelled, id = " + paymentId);
    }
}