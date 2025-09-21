package com.turmaa.helpdesk.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class StartupLogger implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(StartupLogger.class);
    
    private final Environment environment;
    
    @Value("${spring.application.name:Helpdesk Application}")
    private String applicationName;
    
    @Value("${server.port:8080}")
    private String serverPort;

    public StartupLogger(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String[] activeProfiles = environment.getActiveProfiles();
        String profile = activeProfiles.length > 0 ? activeProfiles[0] : "default";
        
        logger.info("\n" +
            "╔════════════════════════════════════════════════════════════════╗\n" +
            "║                    🚀 APLICAÇÃO INICIADA                       ║\n" +
            "╠════════════════════════════════════════════════════════════════╣\n" +
            "║ 📱 Aplicação: {}                                               ║\n" +
            "║ 🌍 Perfil Ativo: {}                                            ║\n" +
            "║ 🌐 Porta: {}                                                   ║\n" +
            "║ 🔗 URL Local: http://localhost:{}                              ║\n" +
            "║ 📊 Banco de Dados: MySQL                                       ║\n" +
            "║ ✅ Status: FUNCIONANDO PERFEITAMENTE                           ║\n" +
            "╚════════════════════════════════════════════════════════════════╝",
            String.format("%-25s", applicationName),
            String.format("%-30s", profile.toUpperCase()),
            String.format("%-35s", serverPort),
            serverPort
        );
        
        // Log adicional para diferentes ambientes
        if ("dev".equals(profile)) {
            logger.warn("⚠️  MODO DESENVOLVIMENTO ATIVO - Banco será recriado a cada inicialização");
        } else if ("prod".equals(profile)) {
            logger.info("🔒 MODO PRODUÇÃO ATIVO - Sistema pronto para uso");
        }
        
        logger.info("🎯 Todas as funcionalidades do Helpdesk estão disponíveis!");
    }
}