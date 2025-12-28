package fr.insa.toto.webui.session;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import fr.insa.beuvron.utils.database.ConnectionPool;
import fr.insa.beuvron.vaadin.utils.dataGrid.ResultSetGrid;
import fr.insa.toto.model.GestionRH.Utilisateur;
import fr.insa.toto.webui.Acceuil;
import fr.insa.toto.webui.session.CreationUtilisateur;
import fr.insa.toto.webui.session.SessionInfo;
import fr.insa.toto.webui.session.LoginDialog;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Route("utilisateurs")
@PageTitle("Utilisateurs")
public class PageUtilisateur extends VerticalLayout {

    public PageUtilisateur() {

        // Bouton retour
        Button retour = new Button("<-- retour",
                e -> getUI().ifPresent(ui -> ui.navigate(Acceuil.class)));
        add(retour);

        /* ================================
               1) SI PAS CONNECTÉ → POPUP LOGIN
           ================================= */
        if (!SessionInfo.userConnected()) {
            new LoginDialog().open();
            return;  
        }

        /* ================================
               2) UTILISATEUR CONNECTÉ
           ================================= */
        Utilisateur u = SessionInfo.curUser().get();

        add(new H2("Espace utilisateur"));

        /* ================================
               3) SI ADMIN → LISTE + CRÉATION
           ================================= */
        if (SessionInfo.adminConnected()) {

            add(new H2("Liste des utilisateurs"));

            try (Connection con = ConnectionPool.getConnection()) {
                PreparedStatement pst = con.prepareStatement(
                        "SELECT surnom, role FROM utilisateur ORDER BY surnom"
                );

                add(new ResultSetGrid(pst));

            } catch (SQLException ex) {
                Notification.show("Erreur : " + ex.getMessage());
            }

            // Formulaire création utilisateur
            add(new H2("Créer un utilisateur"));
            add(new CreationUtilisateur());
        }

        /* ================================
               4) SI UTILISATEUR NORMAL
           ================================= */
        else {
            add(new H2("Mon profil"));
            add(new Paragraph("Surnom : " + u.getSurnom()));
            add(new Paragraph("Rôle : utilisateur standard"));

            Button logout = new Button("Se déconnecter",
                    e -> {
                        SessionInfo.logout();
                        UI.getCurrent().navigate(Acceuil.class);
                    });
            add(logout);
        }
    }
}