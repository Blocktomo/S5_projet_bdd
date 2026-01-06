package fr.insa.toto.webui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.*;
import com.vaadin.flow.router.Route;
import fr.insa.beuvron.utils.database.ConnectionPool;
import fr.insa.toto.model.GestionRH.BdDTest;
import fr.insa.toto.model.GestionRH.GestionBdD;
import fr.insa.toto.model.Jeu.Ronde;
import fr.insa.toto.model.Jeu.Tournoi;
import fr.insa.toto.webui.ComposantsIndividuels.EditionTournoiDialog;
import fr.insa.toto.webui.ComposantsIndividuels.ModeEditionTournoi;
import fr.insa.toto.webui.session.LoginDialog;
import fr.insa.toto.webui.session.SessionInfo;

import java.sql.Connection;
import java.util.List;

@Route("")
public class Acceuil extends VerticalLayout {

    private VerticalLayout listeTournois;

    public Acceuil() {

        /* =======================
           STYLE GLOBAL
           ======================= */
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        getStyle().set("background", "linear-gradient(135deg, #004e92, #000428)");

        /* =======================
           ICÔNE PARAMÈTRES
           ======================= */
        Icon settings = VaadinIcon.COG.create();
        settings.setColor("white");
        settings.setSize("28px");
        settings.getStyle()
                .set("cursor", "pointer")
                .set("position", "absolute")
                .set("top", "20px")
                .set("right", "20px");
        add(settings);

        ContextMenu menu = new ContextMenu(settings);
        menu.setOpenOnClick(true);

        if (!SessionInfo.userConnected()) {
            menu.addItem("Se connecter", e -> new LoginDialog().open());
        } else {
            menu.addItem("Espace utilisateur",
                    e -> getUI().ifPresent(ui -> ui.navigate("utilisateurs")));
            menu.addItem("Se déconnecter", e -> {
                SessionInfo.logout();
                getUI().ifPresent(ui -> ui.getPage().reload());
            });
        }
        
        /* =======================
           RAZ BDD
           ======================= */
        Button raz_bdd = new Button("RAZ_BDD");
        raz_bdd.getStyle()
                .set("cursor", "pointer")
                .set("position", "absolute")
                .set("top", "50px")
                .set("right", "20px");
        add(raz_bdd);
        raz_bdd.addClickListener(e -> {
            try (Connection con = ConnectionPool.getConnection()) {
                GestionBdD.razBdd(con);
                Notification.show("RAZ BDD effectué.");
                System.out.println("razBdd effectué.");
                BdDTest.createBdDTestV4(con);
                Notification.show(" Init createBdDTestV4 effectuée");

            } catch (Exception ex) {
                Notification.show("Erreur pour la RAZ BDD : " + ex.getMessage());
                System.out.println("Erreur pour la RAZ BDD : " + ex.getMessage());
            }
        });
        
        /* =======================
           INFO UTILISATEUR
           ======================= */
        String nom = SessionInfo.curUser()
                .map(u -> u.getSurnom())
                .orElse("Personne");

        H2 userInfo = new H2("Utilisateur connecté : " + nom);
        userInfo.getStyle()
                .set("color", "white")
                .set("margin-bottom", "30px");

        /* =======================
           CARTE CENTRALE
           ======================= */
        VerticalLayout card = new VerticalLayout();
        card.setWidth("520px");
        card.setPadding(true);
        card.setSpacing(true);
        card.setAlignItems(Alignment.STRETCH);
        card.getStyle()
                .set("background", "white")
                .set("padding", "40px")
                .set("border-radius", "20px")
                .set("box-shadow", "0 10px 30px rgba(0,0,0,0.25)");

        H1 titre = new H1("Liste des tournois");

        /* =======================
           LISTE DES TOURNOIS
           ======================= */
        listeTournois = new VerticalLayout();
        listeTournois.setSpacing(true);

        card.add(titre, listeTournois);

        /* =======================
           BOUTON AJOUT (ADMIN)
           ======================= */
        if (SessionInfo.adminConnected()) {
            Button ajouter = new Button("➕ Ajouter un tournoi");
            ajouter.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            ajouter.setWidthFull();

            ajouter.addClickListener(e -> {
                Tournoi nouveau = new Tournoi(
                        "",
                        2025,
                        1,
                        90,
                        2
                );

                new EditionTournoiDialog(
                        nouveau,
                        ModeEditionTournoi.CREATE,
                        this::refreshTournois
                ).open();
            });

            card.add(ajouter);
        }

        /* =======================
           AJOUT FINAL À LA PAGE
           ======================= */
        add(userInfo, card);

        refreshTournois();
    }

    /* =======================
       RAFRAÎCHIR LA LISTE
       ======================= */
    private void refreshTournois() {
        listeTournois.removeAll();

        try (Connection con = ConnectionPool.getConnection()) {
            List<Tournoi> tournois = Tournoi.tousLesTournois(con);

            for (Tournoi t : tournois) {
                listeTournois.add(ligneTournoi(t));
            }

        } catch (Exception ex) {
            Notification.show("Erreur chargement tournois : " + ex.getMessage());
        }
    }

    /* =======================
       UNE LIGNE TOURNOI
       ======================= */
  private Component ligneTournoi(Tournoi tournoi) {

    HorizontalLayout ligne = new HorizontalLayout();
    ligne.setWidthFull();
    ligne.setAlignItems(Alignment.CENTER);
    ligne.setJustifyContentMode(JustifyContentMode.BETWEEN);

    /* ===== TITRE (BOUTON) ===== */
    Button titre = new Button(tournoi.toString());
    titre.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

    titre.getStyle()
            .set("border", "1.5px solid #2563eb")
            .set("color", "#1e3a8a")
            .set("font-size", "16px")
            .set("border-radius", "8px")
            .set("background", "white");

    titre.addClickListener(e ->
            getUI().ifPresent(ui ->
                    ui.navigate("tournoi/" + tournoi.getId()))
    );

    /* ===== ÉTAT DU TOURNOI ===== */
    Span etatSpan = new Span("…");

    try (Connection con = ConnectionPool.getConnection()) {
        String etat = calculerEtatTournoi(con, tournoi);
        etatSpan.setText(etat);

        etatSpan.getStyle()
                .set("padding", "4px 12px")
                .set("border-radius", "12px")
                .set("font-size", "13px")
                .set("font-weight", "600");

        switch (etat) {
            case "Non initié" ->
                    etatSpan.getStyle().set("background", "#e5e7eb");
            case "En cours" ->
                    etatSpan.getStyle().set("background", "#fde68a");
            case "Terminé" ->
                    etatSpan.getStyle().set("background", "#bbf7d0");
        }

    } catch (Exception e) {
        etatSpan.setText("État inconnu");
    }

    /* ===== ACTIONS ===== */
    HorizontalLayout actions = new HorizontalLayout();
    actions.setAlignItems(Alignment.CENTER);
    actions.setSpacing(false);

    Button view = new Button(new Icon(VaadinIcon.EYE));
    view.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ICON);
    view.getElement().setAttribute("title", "Voir le tournoi");

    view.addClickListener(e ->
            new EditionTournoiDialog(
                    tournoi,
                    ModeEditionTournoi.VIEW,
                    null
            ).open()
    );

    actions.add(view);

    if (SessionInfo.adminConnected()) {
        Button edit = new Button(new Icon(VaadinIcon.EDIT));
        edit.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ICON);
        edit.getElement().setAttribute("title", "Modifier le tournoi");

        edit.addClickListener(e ->
                new EditionTournoiDialog(
                        tournoi,
                        ModeEditionTournoi.EDIT,
                        this::refreshTournois
                ).open()
        );

        actions.add(edit);
    }

    /* ===== ASSEMBLAGE ===== */
    HorizontalLayout centre = new HorizontalLayout(titre, etatSpan);
    centre.setAlignItems(Alignment.CENTER);
    centre.setSpacing(true);

    ligne.add(centre, actions);
    return ligne;
}

private String calculerEtatTournoi(Connection con, Tournoi tournoi) throws Exception {

    List<Ronde> rondes = Ronde.rondesDuTournoi(con, tournoi);

    // Sécurité
    if (rondes.isEmpty()) {
        return "Non initié";
    }

    boolean auMoinsUneInitieeOuTerminee = false;
    boolean toutesTerminees = true;

    for (Ronde r : rondes) {

        // Au moins une ronde initiée OU terminée
        if (r.getTerminer() >= 1) {
            auMoinsUneInitieeOuTerminee = true;
        }

        // Si une seule ronde n'est pas terminée → tournoi pas terminé
        if (r.getTerminer() != 2) {
            toutesTerminees = false;
        }
    }

    if (toutesTerminees) {
        return "Terminé";
    }

    if (auMoinsUneInitieeOuTerminee) {
        return "En cours";
    }

    return "Non initié";
}


}
