package fr.insa.toto.webui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.dialog.Dialog;
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
import fr.insa.toto.model.Jeu.Participation;
import fr.insa.toto.model.Jeu.Ronde;
import fr.insa.toto.model.Jeu.Tournoi;
import fr.insa.toto.model.Jeu.Joueur;
import fr.insa.toto.webui.ComposantsIndividuels.EditionTournoiDialog;
import fr.insa.toto.webui.ComposantsIndividuels.ModeEditionTournoi;
import fr.insa.toto.webui.session.CreationUtilisateur;
import fr.insa.toto.webui.session.InscriptionJoueurDialog;
import fr.insa.toto.webui.session.LoginDialog;
import fr.insa.toto.webui.session.SessionInfo;
import fr.insa.toto.webui.session.InscriptionOuConnexionDialog;



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
                con.setAutoCommit(false);
                GestionBdD.razBdd(con);
                BdDTest.createBdDTestV4(con);
                con.commit();
                Notification.show("RAZ BDD + init effectuées");
                refreshTournois();
            } catch (Exception ex) {
                Notification.show("Erreur RAZ BDD : " + ex.getMessage());
                ex.printStackTrace();
            }
        });

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
           INFO UTILISATEUR
           ======================= */
        String nom = SessionInfo.curUser()
                .map(u -> u.getSurnom())
                .orElse("Personne");

        H2 userInfo = new H2("Utilisateur connecté : " + nom);
        userInfo.getStyle().set("color", "white");

        /* =======================
           CARTE CENTRALE
           ======================= */
        VerticalLayout card = new VerticalLayout();
        card.setWidth("560px"); // ✅ plus large → plus de débordement
        card.setPadding(true);
        card.setSpacing(true);
        card.setAlignItems(Alignment.STRETCH);
        card.getStyle()
                .set("background", "white")
                .set("padding", "40px")
                .set("border-radius", "20px")
                .set("box-shadow", "0 10px 30px rgba(0,0,0,0.25)");

        H1 titre = new H1("Liste des tournois");

        listeTournois = new VerticalLayout();
        listeTournois.setSpacing(true);
        listeTournois.setPadding(false);

        card.add(titre, listeTournois);

                if (SessionInfo.adminConnected()) {
            Button ajouter = new Button("➕ Ajouter un tournoi");
            ajouter.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            ajouter.setWidthFull();

            ajouter.addClickListener(e -> {
                Tournoi nouveau = new Tournoi("", 2025, 1, 90, 2);
                new EditionTournoiDialog(
                        nouveau,
                        ModeEditionTournoi.CREATE,
                        this::refreshTournois
                ).open();
            });

            card.add(ajouter);
        }

        add(userInfo, card);
        refreshTournois();
    }
    
    

    private void refreshTournois() {
        listeTournois.removeAll();
        try (Connection con = ConnectionPool.getConnection()) {
            for (Tournoi t : Tournoi.tousLesTournois(con)) {
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

    /* =======================
       BLOC GAUCHE
       ======================= */
    HorizontalLayout gauche = new HorizontalLayout();
    gauche.setAlignItems(Alignment.CENTER);
    gauche.setSpacing(true);
    gauche.setWidthFull();

    /* ----- TITRE ----- */
    Button titre = new Button(tournoi.toString());
    titre.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
    titre.getStyle()
            .set("border", "1.5px solid #2563eb")
            .set("color", "#1e3a8a")
            .set("font-size", "15px")
            .set("border-radius", "8px")
            .set("background", "white");

    titre.addClickListener(e ->
            getUI().ifPresent(ui ->
                    ui.navigate("tournoi/" + tournoi.getId()))
    );

    Span etatSpan = new Span();
    Span placesSpan = new Span();
    Button inscrire = new Button("S'inscrire");
    inscrire.addThemeVariants(ButtonVariant.LUMO_SMALL);

    gauche.add(titre, etatSpan, placesSpan);

    try (Connection con = ConnectionPool.getConnection()) {

        String etat = calculerEtatTournoi(con, tournoi);
        etatSpan.setText(etat);

        etatSpan.getStyle()
                .set("padding", "4px 10px")
                .set("border-radius", "12px")
                .set("font-size", "13px")
                .set("font-weight", "600");

        switch (etat) {
            case "Non initié" -> etatSpan.getStyle().set("background", "#e5e7eb");
            case "En cours" -> etatSpan.getStyle().set("background", "#fde68a");
            case "Terminé" -> etatSpan.getStyle().set("background", "#bbf7d0");
        }

        /* ===== PLACES + INSCRIPTION ===== */
        if ("Non initié".equals(etat) && tournoi.hasLimiteJoueurs()) {

            placesSpan.getStyle().set("font-size", "13px");

            if (tournoi.isComplet(con)) {
                placesSpan.setText("Complet");
                placesSpan.getStyle().set("color", "#dc2626");
            } else {
                int restantes = tournoi.getPlacesRestantes(con);

                if (restantes == 1) {
                    placesSpan.setText("Dernière place disponible");
                    placesSpan.getStyle().set("color", "#f97316");
                } else {
                    placesSpan.setText("Places restantes : " + restantes);
                    placesSpan.getStyle().set("color", "#16a34a");
                }

                gauche.add(inscrire);

            inscrire.addClickListener(e -> {

                if (!SessionInfo.userConnected()) {
                    new InscriptionOuConnexionDialog().open();
                    return;
                }
                new InscriptionJoueurDialog(
                        tournoi,
                        this::refreshTournois
                ).open();
            });

            }
        }

    } catch (Exception e) {
        etatSpan.setText("État inconnu");
    }

    /* =======================
       ACTIONS DROITE
       ======================= */
    HorizontalLayout actions = new HorizontalLayout();
    actions.setAlignItems(Alignment.CENTER);
    actions.setSpacing(false);

    Button view = new Button(new Icon(VaadinIcon.EYE));
    view.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ICON);
    view.addClickListener(e ->
            new EditionTournoiDialog(tournoi, ModeEditionTournoi.VIEW, null).open()
    );
    actions.add(view);

    if (SessionInfo.adminConnected()) {
        Button edit = new Button(new Icon(VaadinIcon.EDIT));
        edit.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ICON);
        edit.addClickListener(e ->
                new EditionTournoiDialog(
                        tournoi,
                        ModeEditionTournoi.EDIT,
                        this::refreshTournois
                ).open()
        );
        actions.add(edit);
    }

    /* =======================
       ASSEMBLAGE FINAL
       ======================= */
    ligne.add(gauche, actions);
    ligne.setFlexGrow(1, gauche);

    return ligne;
}


    private String calculerEtatTournoi(Connection con, Tournoi tournoi) throws Exception {

        List<Ronde> rondes = Ronde.rondesDuTournoi(con, tournoi);

        if (rondes.isEmpty()) return "Non initié";

        boolean auMoinsUneInitieeOuTerminee = false;
        boolean toutesTerminees = true;

        for (Ronde r : rondes) {
            if (r.getTerminer() >= 1) auMoinsUneInitieeOuTerminee = true;
            if (r.getTerminer() != 2) toutesTerminees = false;
        }

        if (toutesTerminees) return "Terminé";
        if (auMoinsUneInitieeOuTerminee) return "En cours";
        return "Non initié";
    }



       

}
