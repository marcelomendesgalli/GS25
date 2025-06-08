package com.greenlight.monitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Classe principal da aplicação Spring Boot para monitoramento climático em escolas.
 * 
 * Esta aplicação fornece:
 * - CRUD para escolas, sensores, leituras e alertas
 * - Autenticação OAuth2 (Google, GitHub)
 * - Integração com RabbitMQ para recebimento de dados de sensores
 * - Interface web com Thymeleaf
 * - Spring AI opcional para alertas personalizados
 */
@SpringBootApplication
@EnableAsync
@EnableScheduling
public class EscolaClimaMonitorApplication {

    public static void main(String[] args) {
        SpringApplication.run(EscolaClimaMonitorApplication.class, args);
    }
}

