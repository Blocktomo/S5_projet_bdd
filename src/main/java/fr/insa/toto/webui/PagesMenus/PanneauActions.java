package fr.insa.toto.webui.PagesMenus;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class PanneauActions extends VerticalLayout {

    public PanneauActions() {
        setPadding(true);
        setSpacing(true);

        add(new H3("Actions"));

        Button equipes = new Button("Consulter équipes");
        Button rondes = new Button("Consulter rondes");
        Button matchs = new Button("Consulter matchs");

        add(equipes, rondes, matchs);

        // Navigation plus tard
    }
}