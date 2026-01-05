package fr.insa.toto.webui.PagesMenus.ComposantsPanneauActions;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import fr.insa.beuvron.utils.database.ConnectionPool;
import fr.insa.toto.model.Jeu.Ronde;
import fr.insa.toto.model.Jeu.Tournoi;
import fr.insa.toto.webui.session.SessionInfo;
import fr.insa.toto.model.Jeu.Joueur;
import fr.insa.toto.model.Jeu.Equipe;
import fr.insa.toto.model.Jeu.Matchs;


import java.sql.Connection;
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

        if (SessionInfo.adminConnected()) {
            Button initRonde = new Button("⚙ Initialiser la ronde");
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
                data.add(new RondeAffichage(
                        num++,
                        r.getTerminer() == 0 ? "non initiée" : "initiée",
                        r
                ));
            }

            grid.setItems(data);

        } catch (Exception ex) {
            Notification.show("Erreur chargement rondes : " + ex.getMessage());
        }
    }

    /* =======================
       CLASSE INTERNE D’AFFICHAGE
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
