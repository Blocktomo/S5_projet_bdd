package fr.insa.toto.webui.PagesMenus.ComposantsPanneauActions;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import fr.insa.beuvron.utils.database.ConnectionPool;
import fr.insa.toto.model.Jeu.*;
import fr.insa.toto.webui.ComposantsIndividuels.CreationJoueur;
import fr.insa.toto.webui.ComposantsIndividuels.CreationTerrain;
import fr.insa.toto.webui.ComposantsIndividuels.CreationTerrain;
import fr.insa.toto.webui.session.SessionInfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.stream.Collectors;

public class PanneauTerrains extends VerticalLayout {
   
    private final Tournoi tournoi;
    private Grid<Terrain> grid;
    private List<Terrain> terrainsTournoi;

    public PanneauTerrains(Tournoi tournoi) {
        this.tournoi = tournoi;
        
        setPadding(true);
        setSpacing(true);
        setWidthFull();
        
        add(new H3("Terrains du tournoi : " + tournoi));

        /* =======================
           BARRE DE RECHERCHE
           ======================= */
        TextField recherche = new TextField();
        recherche.setPlaceholder("Rechercher un terrain...");
        recherche.setWidth("300px");
        add(recherche);

        /* =======================
           GRID TERRAINS
           ======================= */
        grid = new Grid<>(Terrain.class, false);
        grid.addColumn(Terrain::getNom).setHeader("Nom");
        grid.addColumn(ter -> (ter.getOccupe()==0)?"libre":"occupé").setHeader("Disponibilité"); //si occupe==0, alors afficher "libre"
        
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
                    terrainsTournoi.stream()
                            .filter(j -> j.getNom().toLowerCase().contains(filtre))
                            .collect(Collectors.toList())
            );
        });

        if (SessionInfo.adminConnected()) {
            Button ajouterNewTerrain = new Button("📌 Ajouter un terrain");
            Button retirer = new Button("🗑 Retirer du tournoi");
            add(new HorizontalLayout(ajouterNewTerrain, retirer));

            ajouterNewTerrain.addClickListener(e -> ouvrirDialogAjout());
            retirer.addClickListener(e -> retirerDuTournoi());
        }else{
            Paragraph messageAdmin = new Paragraph("(seuls les admins peuvent modifier cette liste)");
            add(messageAdmin);
        }

        refresh();
    }

    private void refresh() {
        try (Connection con = ConnectionPool.getConnection()) {
            terrainsTournoi = Terrain.terrainsDuTournoi(con, tournoi);
            grid.setItems(terrainsTournoi);
        } catch (Exception ex) {
            Notification.show("Erreur chargement joueurs : " + ex.getMessage());
        }
    }

    /* =======================
       DIALOG INSCRIPTION
       ======================= */
    private void ouvrirDialogAjout() {
        Dialog dialog = new Dialog();
        dialog.setWidth("700px");

        VerticalLayout contenu = new VerticalLayout();

        TextField recherche = new TextField();
        recherche.setPlaceholder("Rechercher un terrain...");
        recherche.setWidth("300px");

        Grid<Terrain> tousLesTerrains = new Grid<>(Terrain.class, false);
        tousLesTerrains.addColumn(Terrain::getNom).setHeader("Nom");
        tousLesTerrains.addColumn(ter -> (ter.getOccupe()==0)?"libre (0)":"occupé (1)").setHeader("Disponibilité");
        tousLesTerrains.setHeight("300px");
        tousLesTerrains.setSelectionMode(Grid.SelectionMode.MULTI);

        List<Terrain> tous;

        try (Connection con = ConnectionPool.getConnection()) {
            tous = Terrain.tousLesTerrains(con);
            tousLesTerrains.setItems(tous);
        } catch (Exception e) {
            Notification.show("Erreur chargement terrains");
            return;
        }

        recherche.addValueChangeListener(e -> {
            String filtre = e.getValue().toLowerCase();
            tousLesTerrains.setItems(
                    tous.stream()
                            .filter(ter -> ter.getNom().toLowerCase().contains(filtre))
                            .collect(Collectors.toList())
            );
        });

        Button ajouter = new Button("✅ ajouter la sélection");
        Button creer = new Button("➕ Créer terrain");
        Button supprimer = new Button("🗑 Supprimer terrain");
        Button fermer = new Button("Fermer");

        /* === AJOUTER LA SELECTION === */
        ajouter.addClickListener(e -> {
            if (tousLesTerrains.getSelectedItems().isEmpty()) {
                Notification.show("Sélectionnez au moins un terrain");
                return;
            }

            try (Connection con = ConnectionPool.getConnection();
                 PreparedStatement pst = con.prepareStatement(
                         "INSERT INTO terrains_tournois (idtournoi, idterrain) VALUES (?, ?)"
                 )) {

                for (Terrain ter : tousLesTerrains.getSelectedItems()) {
                    pst.setInt(1, tournoi.getId());
                    pst.setInt(2, ter.getId());
                    pst.addBatch();
                }
                pst.executeBatch();

                refresh();
                dialog.close();

            } catch (Exception ex) {
                Notification.show("Erreur inscription : " + ex.getMessage());
            }
        });

        /* === CRÉER TERRAIN (FIX FK) === */
        creer.addClickListener(e -> {
            Dialog d2 = new Dialog();
            d2.setWidth("400px");

            CreationTerrain form = new CreationTerrain(ter -> {
                try (Connection con = ConnectionPool.getConnection()) {

                    ter.saveInDB(con); // 🔥 FIX PRINCIPAL

                    try (PreparedStatement pst = con.prepareStatement(
                            "INSERT INTO terrains_tournois (idtournoi, idterrain) VALUES (?, ?)"
                    )) {
                        pst.setInt(1, tournoi.getId());
                        pst.setInt(2, ter.getId());
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

        /* === SUPPRIMER TERRAIN (GLOBAL) === */
        supprimer.addClickListener(e -> {
            if (tousLesTerrains.getSelectedItems().isEmpty()) {
                Notification.show("Sélectionnez au moins un terrain");
                return;
            }

            try (Connection con = ConnectionPool.getConnection()) {
                for (Terrain ter : tousLesTerrains.getSelectedItems()) {
                    ter.deleteInDB(con);
                }

                refresh();
                dialog.close();

            } catch (Exception ex) {
                Notification.show("Erreur suppression : " + ex.getMessage());
            }
        });
        
        /* === BOUTON FERMER === */
        fermer.addClickListener(e -> {
            dialog.close();
        });

        contenu.add( //TODO mofif ce titre
                new H3("Inscrire des terrains au tournoi"),
                recherche,
                tousLesTerrains,
                new HorizontalLayout(ajouter, creer, supprimer, fermer)
        );

        dialog.add(contenu);
        dialog.open();
    }

    private void retirerDuTournoi() {
        if (grid.getSelectedItems().isEmpty()) {
            Notification.show("Sélectionnez au moins un terrain");
            return;
        }

        try (Connection con = ConnectionPool.getConnection();
             PreparedStatement pst = con.prepareStatement(
                     "DELETE FROM terrains_tournois WHERE idtournoi = ? AND idterrain = ?"
             )) {

            for (Terrain ter : grid.getSelectedItems()) {
                pst.setInt(1, tournoi.getId());
                pst.setInt(2, ter.getId());
                pst.addBatch();
            }
            pst.executeBatch();

            refresh();

        } catch (Exception ex) {
            Notification.show("Erreur suppression : " + ex.getMessage());
        }
    }
}
