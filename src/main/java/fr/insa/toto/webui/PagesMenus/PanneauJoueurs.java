package fr.insa.toto.webui.PagesMenus;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import fr.insa.beuvron.utils.database.ConnectionPool;
import fr.insa.toto.model.Jeu.Joueur;
import fr.insa.toto.webui.ComposantsIndividuels.CreationJoueur;
import fr.insa.toto.webui.session.SessionInfo;

import java.sql.Connection;
import java.util.List;

public class PanneauJoueurs extends VerticalLayout {

    private Grid<Joueur> grid;
    private VerticalLayout adminZone;

    public PanneauJoueurs() {
        setPadding(true);
        setSpacing(true);
        setWidthFull();

        add(new H3("Joueurs du tournoi"));

        /* =======================
           TABLE DES JOUEURS
           ======================= */
        grid = new Grid<>(Joueur.class, false);

        grid.addColumn(Joueur::getSurnom).setHeader("Surnom");
        grid.addColumn(Joueur::getCategorie).setHeader("Catégorie");
        grid.addColumn(Joueur::getTaillecm).setHeader("Taille (cm)");
        grid.addColumn(Joueur::getScore).setHeader("Score");

        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.setWidthFull();
        grid.setHeight("400px");

        add(grid);

        /* =======================
           ZONE ADMIN
           ======================= */
        adminZone = new VerticalLayout();
        adminZone.setSpacing(true);
        add(adminZone);

        refreshJoueurs();
        refreshAdminZone();
    }

    /* =======================
       RAFRAÎCHIR LES JOUEURS
       ======================= */
    private void refreshJoueurs() {
        try (Connection con = ConnectionPool.getConnection()) {
            List<Joueur> joueurs = Joueur.tousLesJoueur(con);
            grid.setItems(joueurs);
        } catch (Exception e) {
            Notification.show(
                "Erreur chargement joueurs : " + e.getMessage(),
                3000,
                Notification.Position.BOTTOM_END
            );
        }
    }

    /* =======================
       RAFRAÎCHIR LA ZONE ADMIN
       ======================= */
    private void refreshAdminZone() {
        adminZone.removeAll();

        if (!SessionInfo.adminConnected()) {
            return;
        }

        adminZone.add(new H3("Gestion des joueurs"));

        Button ajouter = new Button("➕ Ajouter joueur");
        Button supprimer = new Button("🗑 Supprimer joueur");

        adminZone.add(ajouter, supprimer);

        /* === AJOUTER JOUEUR === */
        ajouter.addClickListener(e -> {
            Dialog dialog = new Dialog();
            dialog.setWidth("400px");

            CreationJoueur form = new CreationJoueur(() -> {
                refreshJoueurs();
                dialog.close();
            });

            dialog.add(form);
            dialog.open();
        });

        /* === SUPPRIMER JOUEUR === */
        supprimer.addClickListener(e -> {
            Joueur selection = grid.asSingleSelect().getValue();

            if (selection == null) {
                Notification.show(
                    "Sélectionnez un joueur",
                    2000,
                    Notification.Position.BOTTOM_END
                );
                return;
            }

            try (Connection con = ConnectionPool.getConnection()) {
                selection.deleteInDB(con);

                Notification.show(
                    "Joueur supprimé : " + selection.getSurnom(),
                    2000,
                    Notification.Position.BOTTOM_END
                );

                refreshJoueurs();

            } catch (Exception ex) {
                Notification.show(
                    "Erreur suppression : " + ex.getMessage(),
                    3000,
                    Notification.Position.BOTTOM_END
                );
            }
        });
    }
}