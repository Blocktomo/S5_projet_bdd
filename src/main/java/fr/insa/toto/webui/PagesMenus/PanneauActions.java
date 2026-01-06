package fr.insa.toto.webui.PagesMenus;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import fr.insa.beuvron.utils.database.ConnectionPool;
import fr.insa.toto.model.Jeu.Ronde;
import fr.insa.toto.model.Jeu.Tournoi;
import fr.insa.toto.webui.PagesMenus.ComposantsPanneauActions.PanneauConsultEquipe;
import fr.insa.toto.webui.PagesMenus.ComposantsPanneauActions.PanneauRonde;
import fr.insa.toto.webui.PagesMenus.ComposantsPanneauActions.PanneauTerrains;
import fr.insa.toto.webui.PagesMenus.ComposantsPanneauActions.PodiumDialog;

import java.sql.Connection;
import java.util.List;

public class PanneauActions extends VerticalLayout {

    private final Tournoi tournoi;

    public PanneauActions(Tournoi tournoi) {
        this.tournoi = tournoi;

        setPadding(true);
        setSpacing(true);
        setWidthFull();

        add(new H3("Actions – " + tournoi.getNom()));

        Button retour = new Button("⬅ Retour à la page principale");
        Button equipes = new Button("👥 Consulter équipes");
        Button rondes = new Button("🔄 Consulter rondes");
        Button terrains = new Button("🏟 Consulter les terrains");

        add(retour, equipes, rondes, terrains);

        /* =======================
           ACTIONS
           ======================= */

        retour.addClickListener(e ->
                UI.getCurrent().navigate("")
        );

        equipes.addClickListener(e -> {
            Notification.show("choisir une ronde du tournoi : " + tournoi.getNom());
            Dialog dialogEquipes = new Dialog();
            dialogEquipes.add(new PanneauConsultEquipe(tournoi));
            dialogEquipes.open();
        });

        rondes.addClickListener(e -> {
            Dialog d = new Dialog();
            d.add(new PanneauRonde(tournoi));
            d.open();
        });

        terrains.addClickListener(e -> {
            Dialog d = new Dialog();
            d.add(new PanneauTerrains(tournoi));
            d.open();
        });

        /* =======================
           🏆 BOUTON PODIUM (CONDITIONNEL)
           ======================= */

  Button podium = new Button("🏆 Voir le podium");

podium.addClickListener(e -> {
    if (tournoiEstTermine()) {
        new PodiumDialog(tournoi).open();
    } else {
        Dialog info = new Dialog();
        info.add("⏳ Le tournoi n'est pas encore terminé.\nVeuillez patienter.");
        info.open();

        // fermeture automatique après 2,5 secondes
        UI.getCurrent().getPage().executeJs(
                "setTimeout(() => $0.close(), 2500);", info
        );
    }
});

add(podium);}

    /* =======================
       ÉTAT TOURNOI (copie logique Acceuil)
       ======================= */

    private boolean tournoiEstTermine() {
        try (Connection con = ConnectionPool.getConnection()) {

            List<Ronde> rondes = Ronde.rondesDuTournoi(con, tournoi);
            if (rondes.isEmpty()) return false;

            for (Ronde r : rondes) {
                if (r.getTerminer() != 2) {
                    return false;
                }
            }
            return true;

        } catch (Exception e) {
            return false;
        }
    }
}
