package fr.insa.toto.webui.PagesMenus.ComposantsPanneauActions;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
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

public class PanneauConsultEquipe extends VerticalLayout {

    private final Tournoi tournoi;
    private Grid<RondeAffichage> grid;
    private List<RondeAffichage> data;

    public PanneauConsultEquipe(Tournoi tournoi) {
        this.tournoi = tournoi;

        setPadding(true);
        setSpacing(true);
        setWidthFull();
        add(new H3("Rondes du tournoi : " + tournoi));
        add(new H4("choisissez une ronde pour consulter ses équipes"));

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
        Button voirEquipes = new Button("📂 Voir les équipes de la ronde");

        voirEquipes.addClickListener(e -> {
            RondeAffichage sel = grid.asSingleSelect().getValue();
            if (sel == null) {
                Notification.show("Sélectionnez une ronde");
                return;
            }

            getUI().ifPresent(ui -> {
                // 🔥 Fermer le Dialog AVANT navigation
                getParent()
                    .filter(p -> p instanceof com.vaadin.flow.component.dialog.Dialog)
                    .ifPresent(p -> ((com.vaadin.flow.component.dialog.Dialog) p).close());

                ui.navigate("ronde/" + sel.getRonde().getId()+"/equipes");
            });
        });

        add(new HorizontalLayout(voirEquipes));
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
                case 1 -> etat = "en cours";
                case 2 -> etat = "terminée";
                default -> etat = "état inconnu";
            }
            
            if (!etat.equals("état inconnu")){
                data.add(new RondeAffichage(
                        num++,
                        etat,
                        r
                ));
            }
        }
        if (this.data.isEmpty()){
            Notification.show("Il n'y a aucune ronde valide à afficher (initalisée ou bien terminées)");
        }

        grid.setItems(data);

    } catch (Exception ex) {
        Notification.show("Erreur chargement rondes : " + ex.getMessage());
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
