package lt.paymentprocessing.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lt.paymentprocessing.dto.PaymentResponseDto;
import lt.paymentprocessing.dto.PaymentType1RequestDto;
import lt.paymentprocessing.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentController.class)
//@AutoConfigureMockMvc
class PaymentControllerTest {

    @MockBean
    PaymentService paymentService;

    @Autowired
    MockMvc mockMvc;

    @Test
    void givenNewPayment_whenCreatePayment_thenResponseContainsSuccessMessage() throws Exception {
        // given
        PaymentType1RequestDto paymentRequestDto = new PaymentType1RequestDto();
        paymentRequestDto.setType("TYPE1");
        paymentRequestDto.setAmount(new BigDecimal("10"));
        paymentRequestDto.setCurrency("EUR");
        paymentRequestDto.setDeptorIban("LT1234");
        paymentRequestDto.setCreditorIban("LT4567");
        paymentRequestDto.setDetails("details");

        var objectMapper = new ObjectMapper();
        String paymentRequestJson = objectMapper.writeValueAsString(paymentRequestDto);

        PaymentResponseDto paymentResponseDto = PaymentResponseDto
                .builder()
                .id(1L)
                .message("successfully created")
                .build();

        when(paymentService.savePayment(paymentRequestDto))
                .thenReturn(paymentResponseDto);

        // when, then
        mockMvc.perform(
                MockMvcRequestBuilders.post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(paymentRequestJson))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("successfully created")));
    }

    @Test
    void givenNoPayment_whenGetAllValidPayments_thenResponseContainsEmptyList() throws Exception {
        // given
        PaymentResponseDto paymentResponseDto = PaymentResponseDto
                .builder()
                .idList(new ArrayList<>())
                .build();

        when(paymentService.getAllValidPaymentsBetweenAmount(null, null))
                .thenReturn(paymentResponseDto);

        // when, then
        mockMvc.perform(MockMvcRequestBuilders.get("/payments"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("id_list")));
    }

    @Test
    void givenExistingPayment_whenGetPaymentById_thenResponseContainsCancellationFee() throws Exception {
        // given
        PaymentResponseDto paymentResponseDto = PaymentResponseDto
                .builder()
                .id(1L)
                .cancellationFee(new BigDecimal("0.00"))
                .build();

        when(paymentService.getPaymentCancellationFee(paymentResponseDto.getId()))
                .thenReturn(paymentResponseDto);

        // when, then
        mockMvc.perform(MockMvcRequestBuilders.get("/payments/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("cancellation_fee")));
    }

    @Test
    void givenExistingPayment_whenCancelPaymentById_thenResponseContainsSuccessMessage() throws Exception {
        // given
        PaymentResponseDto paymentResponseDto = PaymentResponseDto
                .builder()
                .id(1L)
                .message("successfully cancelled")
                .build();

        when(paymentService.cancelPayment(paymentResponseDto.getId()))
                .thenReturn(paymentResponseDto);

        // when, then
        mockMvc.perform(MockMvcRequestBuilders.delete("/payments/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("successfully cancelled")));
    }
}