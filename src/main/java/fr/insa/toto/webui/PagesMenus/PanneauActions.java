package fr.insa.toto.webui.PagesMenus;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import fr.insa.toto.model.Jeu.Tournoi;

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

        add(equipes, rondes, matchs);

        /* =======================
           ACTIONS (à compléter)
           ======================= */

        equipes.addClickListener(e ->
                Notification.show("Équipes du tournoi : " + tournoi.getNom())
        );

        rondes.addClickListener(e ->
                Notification.show("Rondes du tournoi : " + tournoi.getNom())
        );

        matchs.addClickListener(e ->
                Notification.show("Matchs du tournoi : " + tournoi.getNom())
        );
    }
}
