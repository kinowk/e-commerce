package com.support.aspect;

import com.loopers.domain.saga.SagaCommand;
import com.loopers.domain.saga.SagaResult;
import com.loopers.domain.saga.SagaService;
import com.loopers.domain.saga.event.SagaEvent;
import com.support.annotation.Inboxing;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class InboxingAspect {

    private final SagaService sagaService;

    @Qualifier(TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME)
    private final TaskExecutor taskExecutor;

    @Around(value = "inboxingPointcut(inboxing, event)", argNames = "proceedingJoinPoint, inboxing, event")
    public Object handleAdvice(ProceedingJoinPoint proceedingJoinPoint, Inboxing inboxing, SagaEvent event) throws Throwable {
        SagaCommand.Inbound command = new SagaCommand.Inbound(event.eventId(), event.eventName(), event);

        if (inboxing.async() && !inboxing.idempotent()) {
            taskExecutor.execute(() -> sagaService.inbound(command));
            return proceedingJoinPoint.proceed();
        }

        SagaResult.Inbound inbound = sagaService.inbound(command);

        if (inboxing.idempotent() && !inbound.saved()) {
            return null;
        }

        return proceedingJoinPoint.proceed();
    }

    @Pointcut("@annotation(inboxing) && execution(void *..*.*(..) && args(event)")
    private void inboxingPointcut(Inboxing inboxing, SagaEvent event) {

    }
}
