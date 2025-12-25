package fr.insa.toto.webui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import fr.insa.toto.model.Jeu.Tournoi;
import fr.insa.toto.webui.session.LoginDialog;
import fr.insa.toto.webui.session.SessionInfo;
import fr.insa.toto.model.GestionRH.Utilisateur;
import com.vaadin.flow.component.contextmenu.ContextMenu;

import java.util.Optional;

@Route("")
public class Acceuil extends VerticalLayout {

    public Acceuil() {

        /* ======= STYLE GLOBAL ======= */
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        getStyle().set("background", "linear-gradient(135deg, #004e92, #000428)");

                /* ======= ICÔNE PARAMÈTRES EN HAUT À DROITE ======= */
                Icon settings = VaadinIcon.COG.create();
                settings.setColor("white");
                settings.setSize("28px");
                settings.getStyle()
                        .set("cursor", "pointer")
                        .set("position", "absolute")
                        .set("top", "20px")
                        .set("right", "20px");

                add(settings);
            /* ===== MENU PARAMÈTRES ===== */

            ContextMenu menu = new ContextMenu();
            menu.setTarget(settings);     // On lie le menu à l’icône
            menu.setOpenOnClick(true);    // Il s’ouvre quand on clique

            if (!SessionInfo.userConnected()) {

                menu.addItem("Se connecter", e -> {
                    LoginDialog dialog = new LoginDialog();
                    dialog.open();
                });

            } else {
    Utilisateur user = SessionInfo.curUser().get();

    menu.addItem("Espace utilisateur",
        e -> this.getUI().ifPresent(ui -> ui.navigate("utilisateurs")));

    menu.addItem("Se déconnecter", e -> {
        SessionInfo.logout();
        this.getUI().ifPresent(ui -> {
            ui.navigate("");
            ui.getPage().reload();
        });
    });
}

        add(settings);


        /* ======= TEXTE UTILISATEUR CONNECTÉ ======= */
        String nom = SessionInfo.curUser().map(u -> u.getSurnom() + " (id: " + u.getId() + ")")
                .orElse("Personne (id: -1)");

        H2 userInfo = new H2("Utilisateur connecté : " + nom);
        userInfo.getStyle()
                .set("color", "white")
                .set("margin-top", "80px");

        add(userInfo);


        /* ======= CARTE CENTRALE ======= */
        VerticalLayout card = new VerticalLayout();
        card.setWidth("450px");
        card.setPadding(true);
        card.setSpacing(true);
        card.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);

        card.getStyle()
                .set("background", "white")
                .set("padding", "40px")
                .set("border-radius", "20px")
                .set("box-shadow", "0 10px 30px rgba(0,0,0,0.2)");

        /* ======= TITRE ======= */
        H1 titre = new H1("Liste des tournois");
        titre.getStyle()
                .set("font-size", "35px")
                .set("color", "#333");

        /* ======= BOUTON TOURNOI ======= */
        Button btnTournoi = new Button(Tournoi.getNom());
        btnTournoi.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        btnTournoi.getStyle()
                .set("font-size", "18px")
                .set("border-radius", "10px")
                .set("white-space", "nowrap")     // empêche le texte d’être coupé
                .set("width", "auto")             // le bouton s’adapte à la taille du texte
                .set("padding-left", "25px")
                .set("padding-right", "25px");

        btnTournoi.addClickListener(e ->
                btnTournoi.getUI().ifPresent(ui -> ui.navigate("tournoi"))
        );

        /* ======= AJOUTS ======= */
        card.add(titre, btnTournoi);
        add(card);

        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
    }
}