package fr.insa.toto.webui.PagesMenus;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import fr.insa.beuvron.utils.database.ConnectionPool;
import fr.insa.toto.model.Jeu.Joueur;
import fr.insa.toto.model.Jeu.Tournoi;
import fr.insa.toto.webui.ComposantsIndividuels.CreationJoueur;
import fr.insa.toto.webui.session.SessionInfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

public class PanneauJoueurs extends VerticalLayout {

    private final Tournoi tournoi;
    private Grid<Joueur> grid;

    public PanneauJoueurs(Tournoi tournoi) {
        this.tournoi = tournoi;

        setPadding(true);
        setSpacing(true);
        setWidthFull();

        add(new H3("Joueurs du tournoi : " + tournoi));

        /* =======================
           TABLE
           ======================= */
        grid = new Grid<>(Joueur.class, false);
        grid.addColumn(Joueur::getSurnom).setHeader("Surnom");
        grid.addColumn(Joueur::getCategorie).setHeader("Catégorie");
        grid.addColumn(Joueur::getTaillecm).setHeader("Taille (cm)");
        grid.addColumn(Joueur::getScore).setHeader("Score");

        grid.setWidthFull();
        grid.setHeight("400px");

        add(grid);

        /* =======================
           ADMIN
           ======================= */
        if (SessionInfo.adminConnected()) {
            Button ajouter = new Button("➕ Ajouter joueur au tournoi");
            Button supprimer = new Button("🗑 Supprimer joueur du tournoi");

            add(ajouter, supprimer);

            ajouter.addClickListener(e -> ouvrirAjout());
            supprimer.addClickListener(e -> supprimerDuTournoi());
        }

        refresh();
    }

    /* =======================
       RAFRAÎCHIR
       ======================= */
    private void refresh() {
        try (Connection con = ConnectionPool.getConnection()) {
            List<Joueur> joueurs = Joueur.joueursDuTournoi(con, tournoi);
            grid.setItems(joueurs);
        } catch (Exception ex) {
            Notification.show("Erreur : " + ex.getMessage());
        }
    }

    /* =======================
       AJOUT
       ======================= */
    private void ouvrirAjout() {
        Dialog dialog = new Dialog();
        dialog.setWidth("400px");

        CreationJoueur form = new CreationJoueur(joueur -> {
            try (Connection con = ConnectionPool.getConnection()) {

                joueur.saveInDB(con);

                try (PreparedStatement pst = con.prepareStatement(
                        "INSERT INTO participation (idtournoi, idjoueur) VALUES (?, ?)"
                )) {
                    pst.setInt(1, tournoi.getId());
                    pst.setInt(2, joueur.getId());
                    pst.executeUpdate();
                }

                refresh();
                dialog.close();

            } catch (Exception ex) {
                Notification.show("Erreur : " + ex.getMessage());
            }
        });

        dialog.add(form);
        dialog.open();
    }

    /* =======================
       SUPPRESSION
       ======================= */
    private void supprimerDuTournoi() {
        Joueur j = grid.asSingleSelect().getValue();
        if (j == null) {
            Notification.show("Sélectionnez un joueur");
            return;
        }

        try (Connection con = ConnectionPool.getConnection()) {
            try (PreparedStatement pst = con.prepareStatement(
                    "DELETE FROM participation WHERE idtournoi = ? AND idjoueur = ?"
            )) {
                pst.setInt(1, tournoi.getId());
                pst.setInt(2, j.getId());
                pst.executeUpdate();
            }
            refresh();
        } catch (Exception ex) {
            Notification.show("Erreur : " + ex.getMessage());
        }
    }
}
