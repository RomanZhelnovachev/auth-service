package EffectiveMobile.auth_service.kafka;

import EffectiveMobile.auth_service.dto.RegisterEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegisterProducer {

    private final KafkaTemplate<String, RegisterEvent> template;

    @Value("${app.kafka.topics.register}")
    private String topic;

    public void send(RegisterEvent event) {
        template.send(topic, event.email(), event)
                .whenComplete((result, exception) -> {
                    if (exception == null) {
                        log.info("Событие {} успешно отправлено",
                                event.eventId());
                    } else {
                        log.error("Ошибка при отправке события {}",
                                event.eventId(),
                                exception);
                    }
                });
    }
}
