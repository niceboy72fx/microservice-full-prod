package com.payment.app.command;

import com.payment.app.service.GetPaymentService;
import com.payment.app.bean.payment.PaymentResponse;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class GetPaymentHandler implements CommandHandler<GetPaymentCommand, PaymentResponse> {

    private final CommandRegistry commandRegistry;
    private final GetPaymentService getPaymentService;

    public GetPaymentHandler(CommandRegistry commandRegistry, GetPaymentService getPaymentService) {
        this.commandRegistry = commandRegistry;
        this.getPaymentService = getPaymentService;
    }

    @PostConstruct
    public void register() {
        commandRegistry.register(this);
    }

    @Override
    public Class<GetPaymentCommand> commandType() {
        return GetPaymentCommand.class;
    }

    @Override
    public PaymentResponse handle(GetPaymentCommand command) {
        return getPaymentService.execute(command.getPaymentId());
    }
}
