package com.gastrogeniusai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for GastroGenius AI.
 * 
 * A smart cooking assistant that manages recipes and provides
 * AI-powered features like recipe generation, nutritional analysis,
 * and wine pairing suggestions.
 */
@SpringBootApplication
public class GastroGeniusAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(GastroGeniusAiApplication.class, args);
    }
}
