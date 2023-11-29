package com.poly;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paypal.api.payments.Amount;
import com.paypal.api.payments.Payer;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.PaymentExecution;
import com.paypal.api.payments.RedirectUrls;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;

@Service
public class PaypalService {

	@Autowired
	private APIContext apiContext;

	public Payment createPayment(
			Double total,
			String currency,
			String method,
			String intent,
			String description,
			String cancelUrl,
			String successUrl) throws PayPalRESTException {
		// Tạo đối tượng Amount và định dạng số tiền và loại tiền tệ
		try {
			// Amount amount = new Amount();
			// amount.setCurrency(currency); // đặt loại tiền tệ
			// total = new BigDecimal(total).setScale(2, RoundingMode.HALF_UP).doubleValue();
			// amount.setTotal(String.format("%.2f", total));
			Amount amount = new Amount();
			amount.setCurrency(currency); // Đặt loại tiền tệ
			
			BigDecimal roundedTotal = new BigDecimal(total).setScale(2, RoundingMode.HALF_UP);
			String formattedTotal = roundedTotal.toPlainString(); // Định dạng số tiền
			
			amount.setTotal(formattedTotal); // Đặt giá trị số tiền đã được định dạng
			
			// Tạo đối tượng Transaction và gán đối tượng Amount vào đó
			Transaction transaction = new Transaction();
			transaction.setDescription(description);
			transaction.setAmount(amount);

			// Tạo danh sách Transaction và thêm Transaction đã tạo vào danh sách
			List<Transaction> transactions = new ArrayList<>();
			transactions.add(transaction);

			// Tạo đối tượng Payer và đặt phương thức thanh toán
			Payer payer = new Payer();
			payer.setPaymentMethod(method);

			// Tạo đối tượng Payment và gán các thông tin đã tạo
			Payment payment = new Payment();
			payment.setIntent(intent);
			payment.setPayer(payer);
			payment.setTransactions(transactions);
			
			// Tạo đối tượng RedirectUrls và đặt URL hủy và URL thành công
			RedirectUrls redirectUrls = new RedirectUrls();
			redirectUrls.setCancelUrl(cancelUrl);
			redirectUrls.setReturnUrl(successUrl);
			payment.setRedirectUrls(redirectUrls);

			// Thực hiện yêu cầu thanh toán và trả về đối tượng Payment
			return payment.create(apiContext);
		} catch (PayPalRESTException e) {
			System.err.println("Lỗi khi tạo thanh toán: " + e.getLocalizedMessage());
			throw e;
		}
	}
	//thực hiện một giao dịch thanh toán qua PayPal
	public Payment executePayment(String paymentId, String payerId) throws PayPalRESTException {
		try {
			Payment payment = new Payment();
			payment.setId(paymentId);

			PaymentExecution paymentExecute = new PaymentExecution();
			paymentExecute.setPayerId(payerId);

			return payment.execute(apiContext, paymentExecute);
		} catch (PayPalRESTException e) {
			System.err.println("Lỗi khi thực hiện thanh toán: " + e.getLocalizedMessage());
			throw e;
		}
	}

}
