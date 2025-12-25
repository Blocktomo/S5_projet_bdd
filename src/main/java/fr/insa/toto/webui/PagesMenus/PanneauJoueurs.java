package fr.insa.toto.webui.PagesMenus;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import fr.insa.beuvron.utils.database.ConnectionPool;
import fr.insa.beuvron.vaadin.utils.dataGrid.ResultSetGrid;
import fr.insa.toto.webui.session.SessionInfo;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class PanneauJoueurs extends VerticalLayout {

    public PanneauJoueurs() {
        setPadding(true);
        setSpacing(true);

        add(new H3("Joueurs du tournoi"));

        // TABLE DES JOUEURS (VISIBLE POUR TOUS)
        try (Connection con = ConnectionPool.getConnection()) {
            PreparedStatement pst = con.prepareStatement(
                "SELECT surnom, categorie, taillecm, score FROM joueur ORDER BY surnom"
            );
            add(new ResultSetGrid(pst));
        } catch (Exception e) {
            add(new H3("Erreur chargement joueurs"));
        }

        // 🔐 OPTIONS ADMIN UNIQUEMENT
        if (SessionInfo.adminConnected()) {
            add(new H3("Gestion des joueurs"));

            Button ajouter = new Button("➕ Ajouter joueur");
            Button supprimer = new Button("🗑 Supprimer joueur");

            add(ajouter, supprimer);

            // (on branchera le vrai code plus tard)
            ajouter.addClickListener(e -> {
                // ouvrir dialog création joueur
            });

            supprimer.addClickListener(e -> {
                // logique suppression
            });
        }
    }
}