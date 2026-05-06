package EffectiveMobile.auth_service.service.integration;

import EffectiveMobile.auth_service.dto.ConfirmDto;
import EffectiveMobile.auth_service.dto.RegisterDto;
import EffectiveMobile.auth_service.dto.RegisterEvent;
import EffectiveMobile.auth_service.entity.User;
import EffectiveMobile.auth_service.exception.UserNotFoundByEmailException;
import EffectiveMobile.auth_service.kafka.RegisterProducer;
import EffectiveMobile.auth_service.repository.jpa.UserRepository;
import EffectiveMobile.auth_service.service.AuthService;
import EffectiveMobile.auth_service.service.TokenService;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Properties;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@Testcontainers
class AuthServiceIntegrationTest {

    private static final String EMAIL = "test@test.com";
    private static final String CODE = "1234";
    private static final Instant TIME = Instant.parse("2026-05-06T00:00:00Z");
    private static final String TOPIC = "test-registered";

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository repository;

    @Autowired
    private RegisterProducer producer;

    @Autowired
    private TokenService service;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("auth_db")
            .withUsername("postgres")
            .withPassword("postgres");

    @Container
    static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("apache/kafka:3.7.0"));

    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine")
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
        registry.add("app.kafka.topics.register", () -> TOPIC);
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
    }

    @Test
    @DisplayName("Регистрация пользователя")
    @Transactional
    void registerUser() {
        authService.registerUser(registerDto());
        assertThat(repository.findByEmail(EMAIL)).isPresent();
        try (KafkaConsumer<String, RegisterEvent> consumer = createConsumer()) {
            consumer.subscribe(Collections.singletonList(TOPIC));
            ConsumerRecords<String, RegisterEvent> records = consumer.poll(Duration.ofSeconds(5));
            assertThat(records.count()).isEqualTo(1);
            ConsumerRecord<String, RegisterEvent> record = records.iterator()
                    .next();
            RegisterEvent event = record.value();
            assertThat(event.email()).isEqualTo(EMAIL);
            assertThat(event.code()).isNotBlank();
        }
    }

    @Test
    @DisplayName("При совпадении кода - пометить пользователя, как подтверждённого")
    @Transactional
    void userConfirmation() {
       User user = user(EMAIL);
       repository.saveAndFlush(user);
        authService.userConfirmation(confirmDto());
        User updatedUser = repository.findByEmail(EMAIL).orElseThrow(()-> new UserNotFoundByEmailException(EMAIL));
        assertThat(true).isEqualTo(updatedUser.isConfirmed());
    }

    private RegisterDto registerDto() {
        return new RegisterDto(EMAIL);
    }

    private ConfirmDto confirmDto(){
        return new ConfirmDto(EMAIL, CODE);
    }

    private User user(String email){
        return User.builder()
                .id(UUID.randomUUID())
                .email(email)
                .confirmed(false)
                .code(CODE)
                .timeCreated(TIME)
                .build();
    }

    private KafkaConsumer<String, RegisterEvent> createConsumer() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group-" + UUID.randomUUID());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "EffectiveMobile.auth_service.dto");
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, RegisterEvent.class.getName());
        return new KafkaConsumer<>(props);
    }
}