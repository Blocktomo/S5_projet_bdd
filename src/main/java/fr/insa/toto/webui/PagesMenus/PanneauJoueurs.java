package fr.insa.toto.webui.PagesMenus;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import fr.insa.beuvron.utils.database.ConnectionPool;
import fr.insa.toto.model.Jeu.Joueur;
import fr.insa.toto.model.Jeu.Tournoi;
import fr.insa.toto.webui.ComposantsIndividuels.CreationJoueur;
import fr.insa.toto.webui.session.SessionInfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.stream.Collectors;

public class PanneauJoueurs extends VerticalLayout {

    private final Tournoi tournoi;
    private Grid<Joueur> grid;
    private List<Joueur> joueursTournoi;

    public PanneauJoueurs(Tournoi tournoi) {
        this.tournoi = tournoi;

        setPadding(true);
        setSpacing(true);
        setWidthFull();

        add(new H3("Joueurs du tournoi : " + tournoi));

        /* =======================
           BARRE DE RECHERCHE
           ======================= */
        TextField recherche = new TextField();
        recherche.setPlaceholder("Rechercher un joueur...");
        recherche.setWidth("300px");
        add(recherche);

        /* =======================
           GRID TOURNOI
           ======================= */
        grid = new Grid<>(Joueur.class, false);
        grid.addColumn(Joueur::getSurnom).setHeader("Surnom");
        grid.addColumn(Joueur::getCategorie).setHeader("Catégorie");
        grid.addColumn(Joueur::getTaillecm).setHeader("Taille (cm)");
        grid.addColumn(Joueur::getScore).setHeader("Score").setSortable(true) ;

        grid.setWidthFull();
        grid.setHeight("420px");

        if (SessionInfo.adminConnected()) {
            grid.setSelectionMode(Grid.SelectionMode.MULTI);
        } else {
            grid.setSelectionMode(Grid.SelectionMode.NONE);
        }

        add(grid);

        recherche.addValueChangeListener(e -> {
            String filtre = e.getValue().toLowerCase();
            grid.setItems(
                    joueursTournoi.stream()
                            .filter(j -> j.getSurnom().toLowerCase().contains(filtre))
                            .collect(Collectors.toList())
            );
        });

        if (SessionInfo.adminConnected()) {
            Button inscrire = new Button("📌 Inscrire joueur(s)");
            Button retirer = new Button("🗑 Retirer du tournoi");
            add(new HorizontalLayout(inscrire, retirer));

            inscrire.addClickListener(e -> ouvrirDialogInscription());
            retirer.addClickListener(e -> retirerDuTournoi());
        }

        refresh();
    }

    private void refresh() {
        try (Connection con = ConnectionPool.getConnection()) {
            joueursTournoi = Joueur.joueursDuTournoi(con, tournoi);
            grid.setItems(joueursTournoi);
        } catch (Exception ex) {
            Notification.show("Erreur chargement joueurs : " + ex.getMessage());
        }
    }

    /* =======================
       DIALOG INSCRIPTION
       ======================= */
    private void ouvrirDialogInscription() {
        Dialog dialog = new Dialog();
        dialog.setWidth("700px");

        VerticalLayout contenu = new VerticalLayout();

        TextField recherche = new TextField();
        recherche.setPlaceholder("Rechercher un joueur...");
        recherche.setWidth("300px");

        Grid<Joueur> tousLesJoueurs = new Grid<>(Joueur.class, false);
        tousLesJoueurs.addColumn(Joueur::getSurnom).setHeader("Surnom");
        tousLesJoueurs.addColumn(Joueur::getCategorie).setHeader("Catégorie");
        tousLesJoueurs.addColumn(Joueur::getTaillecm).setHeader("Taille (cm)");
        tousLesJoueurs.setHeight("300px");
        tousLesJoueurs.setSelectionMode(Grid.SelectionMode.MULTI);

        List<Joueur> tous;

        try (Connection con = ConnectionPool.getConnection()) {
            tous = Joueur.tousLesJoueurs(con);
            tousLesJoueurs.setItems(tous);
        } catch (Exception e) {
            Notification.show("Erreur chargement joueurs");
            return;
        }

        recherche.addValueChangeListener(e -> {
            String filtre = e.getValue().toLowerCase();
            tousLesJoueurs.setItems(
                    tous.stream()
                            .filter(j -> j.getSurnom().toLowerCase().contains(filtre))
                            .collect(Collectors.toList())
            );
        });

        Button inscrire = new Button("✅ Inscrire sélection");
        Button creer = new Button("➕ Créer joueur");
        Button supprimer = new Button("🗑 Supprimer joueur");
        Button fermer = new Button("Fermer");

        /* === INSCRIRE === */
        inscrire.addClickListener(e -> {
            if (tousLesJoueurs.getSelectedItems().isEmpty()) {
                Notification.show("Sélectionnez au moins un joueur");
                return;
            }

            try (Connection con = ConnectionPool.getConnection();
                 PreparedStatement pst = con.prepareStatement(
                         "INSERT INTO participation (idtournoi, idjoueur) VALUES (?, ?)"
                 )) {

                for (Joueur j : tousLesJoueurs.getSelectedItems()) {
                    pst.setInt(1, tournoi.getId());
                    pst.setInt(2, j.getId());
                    pst.addBatch();
                }
                pst.executeBatch();

                refresh();
                dialog.close();

            } catch (Exception ex) {
                Notification.show("Erreur inscription : " + ex.getMessage());
            }
        });
        
        /*====== CREER UN JOUEUR ============*/
        
        creer.addClickListener(e -> {
            Dialog d2 = new Dialog();
            d2.setWidth("400px");

            CreationJoueur form = new CreationJoueur(joueur -> {
                try (Connection con = ConnectionPool.getConnection()) {

                    joueur.saveInDB(con); // 🔥 FIX PRINCIPAL

                    try (PreparedStatement pst = con.prepareStatement(
                            "INSERT INTO participation (idtournoi, idjoueur) VALUES (?, ?)"
                    )) {
                        pst.setInt(1, tournoi.getId());
                        pst.setInt(2, joueur.getId());
                        pst.executeUpdate();
                    }

                    refresh();
                    d2.close();
                    dialog.close();

                } catch (Exception ex) {
                    Notification.show("Erreur création : " + ex.getMessage());
                }
            });

            d2.add(form);
            d2.open();
        });

        /* === SUPPRIMER JOUEUR (GLOBAL) === */
        supprimer.addClickListener(e -> {
            if (tousLesJoueurs.getSelectedItems().isEmpty()) {
                Notification.show("Sélectionnez au moins un joueur");
                return;
            }

            try (Connection con = ConnectionPool.getConnection()) {
                for (Joueur j : tousLesJoueurs.getSelectedItems()) {
                    j.deleteInDB(con);
                }

                refresh();
                dialog.close();

            } catch (Exception ex) {
                Notification.show("Erreur suppression : " + ex.getMessage());
            }
        });
        
        /*====== FERMER ============*/
        fermer.addClickListener(e-> dialog.close());
        
        
        contenu.add(
                new H3("Inscrire des joueurs au tournoi"),
                recherche,
                tousLesJoueurs,
                new HorizontalLayout(inscrire, creer, supprimer, fermer)
        );

        dialog.add(contenu);
        dialog.open();
    }

    private void retirerDuTournoi() {
        if (grid.getSelectedItems().isEmpty()) {
            Notification.show("Sélectionnez au moins un joueur");
            return;
        }

        try (Connection con = ConnectionPool.getConnection();
             PreparedStatement pst = con.prepareStatement(
                     "DELETE FROM participation WHERE idtournoi = ? AND idjoueur = ?"
             )) {

            for (Joueur j : grid.getSelectedItems()) {
                pst.setInt(1, tournoi.getId());
                pst.setInt(2, j.getId());
                pst.addBatch();
            }
            pst.executeBatch();

            refresh();

        } catch (Exception ex) {
            Notification.show("Erreur suppression : " + ex.getMessage());
        }
    }
}
