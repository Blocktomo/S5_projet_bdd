package fr.insa.toto.webui.PagesMenus;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.Route;

@Route("tournoi")
public class Pagetournoi extends HorizontalLayout {

    public Pagetournoi() {
        setSizeFull();

        // panneau gauche = joueurs
        PanneauJoueurs panneauJoueurs = new PanneauJoueurs();
        panneauJoueurs.setWidth("60%");
        panneauJoueurs.setHeightFull();

        // panneau droit = actions
        PanneauActions panneauActions = new PanneauActions();
        panneauActions.setWidth("40%");
        panneauActions.setHeightFull();

        add(panneauJoueurs, panneauActions);
    }
}
