package fr.insa.toto.webui.PagesMenus;

import fr.insa.toto.webui.PagesMenus.ComposantsPanneauActions.PanneauTerrains;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import fr.insa.toto.model.Jeu.Tournoi;
import fr.insa.toto.webui.PagesMenus.ComposantsPanneauActions.PanneauRonde;

public class PanneauActions extends VerticalLayout {

    private final Tournoi tournoi;

    public PanneauActions(Tournoi tournoi) {
        this.tournoi = tournoi;

        setPadding(true);
        setSpacing(true);
        setWidthFull();

        add(new H3("Actions – " + tournoi.getNom()));

        Button equipes = new Button("👥 Consulter équipes");
        Button rondes = new Button("🔄 Consulter rondes");
        Button matchs = new Button("⚽ Consulter matchs");
        Button terrains = new Button("[-] Consulter les terrains"); //TODO ajouter un joli symbole

        add(equipes, rondes, matchs, terrains);

        /* =======================
           ACTIONS (à compléter)
           ======================= */

        equipes.addClickListener(e ->
                Notification.show("Équipes du tournoi : " + tournoi.getNom())
        );

        rondes.addClickListener(e -> {
                Notification.show("Rondes du tournoi : " + tournoi.getNom());
                Dialog dialogRondes = new Dialog();
                PanneauRonde panneauRonde = new PanneauRonde(tournoi);
                dialogRondes.add(panneauRonde);
                dialogRondes.open();
        });

        matchs.addClickListener(e ->
                Notification.show("Matchs du tournoi : " + tournoi.getNom())
        );
        
        terrains.addClickListener(e -> {
                Notification.show("Terrains du tournoi : " + tournoi.getNom());
                Dialog dialogTerrains = new Dialog();
                PanneauTerrains panneauTerrains = new PanneauTerrains(tournoi);
                dialogTerrains.add(panneauTerrains);
                dialogTerrains.open();
        });
    }
}
