package fr.insa.toto.webui.PagesMenus;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import fr.insa.toto.model.Jeu.Tournoi;
import fr.insa.toto.webui.PagesMenus.ComposantsPanneauActions.PanneauRonde;
import fr.insa.toto.webui.PagesMenus.ComposantsPanneauActions.PanneauTerrains;

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

        // ✅ RETOUR PAGE D’ACCUEIL (ROOT)
        retour.addClickListener(e ->
                UI.getCurrent().navigate("")
        );

        equipes.addClickListener(e ->
                Notification.show("Équipes du tournoi : " + tournoi.getNom())
        );

        rondes.addClickListener(e -> {
            Notification.show("Rondes du tournoi : " + tournoi.getNom());
            Dialog dialogRondes = new Dialog();
            dialogRondes.add(new PanneauRonde(tournoi));
            dialogRondes.open();
        });

        terrains.addClickListener(e -> {
            Notification.show("Terrains du tournoi : " + tournoi.getNom());
            Dialog dialogTerrains = new Dialog();
            dialogTerrains.add(new PanneauTerrains(tournoi));
            dialogTerrains.open();
        });
    }
}
