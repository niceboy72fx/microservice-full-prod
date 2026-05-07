package com.payment.app.command;

import com.payment.app.bean.payment.CreatePaymentRequest;
import com.payment.app.service.CreatePaymentService;
import com.payment.app.bean.payment.PaymentResponse;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class CreatePaymentHandler implements CommandHandler<CreatePaymentCommand, PaymentResponse> {

    private final CommandRegistry commandRegistry;
    private final CreatePaymentService createPaymentService;

    public CreatePaymentHandler(CommandRegistry commandRegistry, CreatePaymentService createPaymentService) {
        this.commandRegistry = commandRegistry;
        this.createPaymentService = createPaymentService;
    }

    @PostConstruct
    public void register() {
        commandRegistry.register(this);
    }

    @Override
    public Class<CreatePaymentCommand> commandType() {
        return CreatePaymentCommand.class;
    }

    @Override
    public PaymentResponse handle(CreatePaymentCommand command) {
        CreatePaymentRequest request = new CreatePaymentRequest();
        request.setUserId(command.getUserId());
        request.setType(command.getType());
        request.setAmount(command.getAmount());
        request.setCurrency(command.getCurrency());
        request.setMethod(command.getMethod());
        request.setIdempotencyKey(command.getIdempotencyKey());
        return createPaymentService.execute(request, command.getIdempotencyKeyHeader(), command.getCorrelationIdHeader());
    }
}
