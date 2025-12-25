package fr.insa.toto.webui;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import fr.insa.beuvron.utils.database.ConnectionPool;
import fr.insa.beuvron.utils.database.ConnectionSimpleSGBD;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import fr.insa.toto.model.Jeu.*;
import fr.insa.toto.model.GestionRH.*;
import java.sql.Connection;
import java.sql.SQLException;

/*
==========================
pour régler l'erreur de lancement (sur windows) "Web server failed to start. Port 8080 was already in use." : 
1) ouvrir l'invite de commandes et entrer la commande "netstat -ano | findstr :8080"
2) une liste de tâhes s'affiche : ce sont les processus utilisant :8080. les nombres à la fin sont les id des processus (PID).
3) entrer la commande "taskkill /PID <id du processus> /F"
--> https://www.ggorantala.dev/how-to-fix-port-8080-already-in-use-error-on-windows-and-macos/ 
voir cet article aussi pour régler le problème sur mac
=================================

*/

@SpringBootApplication
@Theme("default")
public class Application extends SpringBootServletInitializer implements AppShellConfigurator {

    public static void main(String[] args) {
        // seulement si bdd h2 en memoire
        
//        Connection con = null;
//        try {
//            con = ConnectionSimpleSGBD.defaultCon();
//            System.out.println("Connection OK");
//        } catch (SQLException ex) {
//            System.out.println("Problème de connection : " + ex.getLocalizedMessage());
//            throw new Error(ex);
//        }

     /* try (Connection con = ConnectionPool.getConnection()) {
            GestionBdD.razBdd(con);
            BdDTest.createBdDTestV4(con);
        } catch (SQLException ex) {
            throw new Error(ex);
       }*/
 
//        try{
//            GestionBdD.razBdd(con);
//            System.out.print("the raz was done");
//            BdDTest.createBdDTestV4(con);
//        } catch (SQLException ex) {
//            throw new Error(ex);
//        }
        SpringApplication.run(Application.class, args);
    }

}
