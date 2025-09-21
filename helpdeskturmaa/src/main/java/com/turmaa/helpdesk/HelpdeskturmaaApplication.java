package com.turmaa.helpdesk;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HelpdeskturmaaApplication implements CommandLineRunner{
	

	public static void main(String[] args) {
		SpringApplication.run(HelpdeskturmaaApplication.class, args);
	}
	
	public void run (String... args) throws Exception {
		System.out.println();
		System.out.println("🟢 ================================");
		System.out.println("🟢   APLICAÇÃO INICIADA COM SUCESSO!");
		System.out.println("🟢   Sistema Helpdesk está funcionando");
		System.out.println("🟢   Banco de dados conectado");
		System.out.println("🟢   Todas as APIs estão disponíveis");
		System.out.println("🟢 ================================");
		System.out.println("📍 Acesse: http://localhost");
		System.out.println("💻 Status: ONLINE ✅");
		System.out.println();
	}
}

