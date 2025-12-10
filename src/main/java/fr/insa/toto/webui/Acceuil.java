package fr.insa.toto.webui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import fr.insa.toto.model.Jeu.Tournoi;

@Route("")
public class Acceuil extends VerticalLayout {

    public Acceuil() {

        /* ======= STYLE DE LA PAGE ======= */

        // Dégradé BG moderne
        getStyle().set("background", "linear-gradient(135deg, #004e92, #000428)");
        setSizeFull();
        setSpacing(false);
        setPadding(false);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        /* ======= CARTE CENTRALE ======= */

        VerticalLayout card = new VerticalLayout();
        card.setWidth("450px");
        card.setPadding(true);
        card.setSpacing(true);
        card.setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        card.getStyle()
                .set("background", "white")
                .set("padding", "40px")
                .set("border-radius", "20px")
                .set("box-shadow", "0 10px 30px rgba(0,0,0,0.2)");

        /* ======= TITRE ======= */

        H1 titre = new H1(Tournoi.getNom());
        titre.getStyle()
                .set("font-size", "40px")
                .set("margin-bottom", "30px")
                .set("color", "#333")
                .set("text-align", "center");

        /* ======= BOUTONS STYLÉS ======= */

        Button joueursBtn = new Button("Gérer les joueurs");
        Button equipesBtn = new Button("Gérer les équipes");
        Button rondesBtn = new Button("Gérer les rondes");

        for (Button b : new Button[]{joueursBtn, equipesBtn, rondesBtn}) {
            b.setWidth("250px");
            b.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            b.getStyle()
                    .set("font-size", "18px")
                    .set("border-radius", "10px");
        }

        joueursBtn.addClickListener(e -> joueursBtn.getUI().ifPresent(ui -> ui.navigate("joueurs")));
        equipesBtn.addClickListener(e -> equipesBtn.getUI().ifPresent(ui -> ui.navigate("equipes")));
        rondesBtn.addClickListener(e -> rondesBtn.getUI().ifPresent(ui -> ui.navigate("rondes")));

        /* ======= AJOUTS ======= */

        card.add(titre, joueursBtn, equipesBtn, rondesBtn);
        add(card);

        // Centrer verticalement
        setJustifyContentMode(JustifyContentMode.CENTER);
    }
}