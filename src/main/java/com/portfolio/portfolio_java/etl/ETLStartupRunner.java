package com.portfolio.portfolio_java.etl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class ETLStartupRunner implements ApplicationRunner {

    @Autowired
    private ETLdatos etldatos;

    @Override
    public void run(ApplicationArguments args) {
        
        System.out.println("Hola, porfavor espera mientras se cargan los datos");
        
        etldatos.extraerDatos();
        
        System.out.println("ETL completado exitosamente, usa la API ahora ");
    }
}
