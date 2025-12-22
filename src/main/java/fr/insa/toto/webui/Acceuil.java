package fr.insa.toto.webui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import fr.insa.toto.model.GestionRH.Utilisateur;
import fr.insa.toto.model.Jeu.Tournoi;
import fr.insa.toto.webui.PagesMenus.*;
import fr.insa.toto.webui.session.SessionInfo;
import java.util.Optional;

@Route("")
public class Acceuil extends VerticalLayout {

    public Acceuil() {

        
        /* ======= UTILISATEUR CONNECTE =======*/
        //NOM UTILISATEUR CONNECTE
        String nomCurUser;
        int idCurUser = -1;
        int roleCurUser = -1;
        Optional<Utilisateur> curUser = SessionInfo.curUser();
        if (curUser.isEmpty()){
            nomCurUser = "Personne";
            //role = -1 ; idCurUser = -1 (par défaut)
        }else{
            nomCurUser = curUser.get().getSurnom();
            idCurUser = curUser.get().getId();
            roleCurUser = curUser.get().getRole();
        }
        
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
        Button utilisateurBtn = new Button("Gérer les utilisateurs");


        for (Button b : new Button[]{joueursBtn, equipesBtn, rondesBtn, utilisateurBtn}) {
            b.setWidth("250px");
            b.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            b.getStyle()
                    .set("font-size", "18px")
                    .set("border-radius", "10px");
        }

        joueursBtn.addClickListener(e -> joueursBtn.getUI().ifPresent(ui -> ui.navigate(PageJoueur.class)));
        equipesBtn.addClickListener(e -> equipesBtn.getUI().ifPresent(ui -> ui.navigate("equipes")));
        rondesBtn.addClickListener(e -> rondesBtn.getUI().ifPresent(ui -> ui.navigate("rondes")));
        utilisateurBtn.addClickListener(e -> utilisateurBtn.getUI().ifPresent(ui -> ui.navigate("utilisateurs")));
        

        /* ======= AJOUTS ======= */

        card.add(titre, joueursBtn, utilisateurBtn);
        if (roleCurUser != -1){
            card.add(equipesBtn, rondesBtn);
        }
        H2 quiEstConnecte = new H2("Utilisateur connecté : "+ nomCurUser +" (id: " + idCurUser +")");
        
        quiEstConnecte.getStyle()
                .set("font-size", "20px")
                .set("margin-bottom", "30px")
                .set("color", "#e8092e")
                .set("text-align", "left");
        this.add(quiEstConnecte);
        add(card);

        // Centrer verticalement
        setJustifyContentMode(JustifyContentMode.CENTER);
    }
}