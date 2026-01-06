package fr.insa.toto.webui.PagesMenus.ComposantsPanneauActions;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import fr.insa.beuvron.utils.database.ConnectionPool;
import fr.insa.toto.model.Jeu.*;
import fr.insa.toto.webui.session.SessionInfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

public class PanneauRonde extends VerticalLayout {

    private final Tournoi tournoi;
    private Grid<RondeAffichage> grid;
    private List<RondeAffichage> data;

    public PanneauRonde(Tournoi tournoi) {
        this.tournoi = tournoi;

        setPadding(true);
        setSpacing(true);
        setWidthFull();

        add(new H3("Rondes du tournoi : " + tournoi));

        /* =======================
           GRID RONDES
           ======================= */
        grid = new Grid<>(RondeAffichage.class, false);

        grid.addColumn(RondeAffichage::getNumero)
                .setHeader("Ronde");

        grid.addColumn(RondeAffichage::getEtat)
                .setHeader("État");

        grid.setWidthFull();
        grid.setHeight("420px");
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);

        add(grid);

        /* =======================
           BOUTONS
           ======================= */
        Button voirRonde = new Button("📂 Voir la ronde");

        voirRonde.addClickListener(e -> {
            RondeAffichage sel = grid.asSingleSelect().getValue();
            if (sel == null) {
                Notification.show("Sélectionnez une ronde");
                return;
            }

            getUI().ifPresent(ui -> {
                getParent()
                    .filter(p -> p instanceof com.vaadin.flow.component.dialog.Dialog)
                    .ifPresent(p -> ((com.vaadin.flow.component.dialog.Dialog) p).close());

                ui.navigate("ronde/" + sel.getRonde().getId());
            });
        });




        if (SessionInfo.adminConnected()) {
            Button initRonde = new Button("⚙ Initialiser la ronde");
            initRonde.addClickListener(e -> initialiserRonde());
            add(new HorizontalLayout(voirRonde, initRonde));
        } else {
            add(voirRonde);
            add(new Paragraph("(seuls les admins peuvent initialiser une ronde)"));
        }

        refresh();
    }

    /* =======================
       RAFRAÎCHIR
       ======================= */

    private void refresh() {
    try (Connection con = ConnectionPool.getConnection()) {

        List<Ronde> rondes = Ronde.rondesDuTournoi(con, tournoi);
        data = new ArrayList<>();

        int num = 1;
        for (Ronde r : rondes) {

            String etat;
            switch (r.getTerminer()) {
                case 0 -> etat = "non initiée";
                case 1 -> etat = "en cours";
                case 2 -> etat = "terminée";
                default -> etat = "état inconnu";
            }

            data.add(new RondeAffichage(
                    num++,
                    etat,
                    r
            ));
        }

        grid.setItems(data);

    } catch (Exception ex) {
        Notification.show("Erreur chargement rondes : " + ex.getMessage());
    }
}

    /* =======================
       INITIALISATION RONDE
       ======================= */
private void initialiserRonde() {

    RondeAffichage sel = grid.asSingleSelect().getValue();

    if (sel == null) {
        Notification.show("Sélectionnez une ronde");
        return;
    }

    Ronde ronde = sel.getRonde();

    if (ronde.getTerminer() == 1) {
        Notification.show("Cette ronde est déjà initiée");
        return;
    }

    try (Connection con = ConnectionPool.getConnection()) {

        /* 1️⃣ Récupérer UNIQUEMENT les joueurs du tournoi */
        List<Joueur> joueurs = Joueur.joueursDuTournoi(con, tournoi);

        int tailleEquipe = tournoi.getNbJoueursEquipe();

        if (joueurs.size() < tailleEquipe * 2) {
            Notification.show("Pas assez de joueurs pour créer des équipes");
            return;
        }

        /* 2️⃣ Calculer nombre d'équipes et de matchs */
        int nbEquipes = joueurs.size() / tailleEquipe;
        int nbMatchs = nbEquipes / 2;

        /* 3️⃣ Vérifier terrains disponibles */
        List<Terrain> terrains = Terrain.terrainsDuTournoi(con, tournoi);
        long terrainsLibres = terrains.stream()
                .filter(t -> t.getOccupe() == 0)
                .count();

        if (terrainsLibres < nbMatchs) {
            Notification.show(
                    "Pas assez de terrains libres (" + terrainsLibres +
                    ") pour " + nbMatchs + " matchs"
            );
            return;
        }

        /* 4️⃣ Créer les équipes (via la logique métier existante) */
        List<Equipe> equipes =
        Equipe.creerEquipesPourTournoi(con, ronde, tournoi);

        /* 5️⃣ Créer les matchs + associer terrains */
        Matchs.creerMatchsAuto(con, ronde, equipes);

        /* 6️⃣ Marquer la ronde comme initiée */
        try (var pst = con.prepareStatement(
                "UPDATE ronde SET terminer = 1 WHERE idronde = ?"
        )) {
            pst.setInt(1, ronde.getIdronde());
            pst.executeUpdate();
        }

        Notification.show("Ronde initialisée avec succès");
        refresh();

    } catch (Exception ex) {
        Notification.show("Erreur initialisation : " + ex.getMessage());
        ex.printStackTrace();
    }
}



    /* =======================
       CLASSE INTERNE AFFICHAGE
       ======================= */
    private static class RondeAffichage {

        private final int numero;
        private final String etat;
        private final Ronde ronde;

        public RondeAffichage(int numero, String etat, Ronde ronde) {
            this.numero = numero;
            this.etat = etat;
            this.ronde = ronde;
        }

        public int getNumero() {
            return numero;
        }

        public String getEtat() {
            return etat;
        }

        public Ronde getRonde() {
            return ronde;
        }
    }
}
