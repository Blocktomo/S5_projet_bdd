package fr.insa.toto.webui.PagesMenus.ComposantsPanneauActions;

import fr.insa.toto.webui.PagesMenus.*;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import fr.insa.beuvron.utils.database.ConnectionPool;
import fr.insa.toto.model.Jeu.*;
import fr.insa.toto.webui.ComposantsIndividuels.BlocMatch;
import fr.insa.toto.webui.session.SessionInfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Route("ronde/:idronde/equipes")
public class PageEquipe extends VerticalLayout implements BeforeEnterObserver  {

    private Ronde ronde;
    

    @Override
    public void beforeEnter(BeforeEnterEvent event) {

        Integer idRonde = event.getRouteParameters()
                               .getInteger("idronde")
                               .orElse(null);

        if (idRonde == null) {
            event.rerouteToError(NotFoundException.class);
            return;
        }

        try (Connection con = ConnectionPool.getConnection()) {

            ronde = Ronde.chercherRondeParId(con, idRonde);

            if (ronde == null) {
                Notification.show("Ronde introuvable");
                event.rerouteToError(NotFoundException.class);
                return;
            }

            buildUI(con);

        } catch (Exception e) {
            Notification.show("Erreur chargement ronde : " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void buildUI(Connection con) throws SQLException {

        removeAll();

        /* =======================
           NUMÉRO DE RONDE
           ======================= */
        int numeroRonde = 1;
        List<Ronde> rondes = Ronde.rondesDuTournoi(con, ronde.getTournoi());
        for (int i = 0; i < rondes.size(); i++) {
            if (rondes.get(i).getId() == ronde.getId()) {
                numeroRonde = i + 1;
                break;
            }
        }

        /* =======================
           TITRE
           ======================= */
        H2 titre = new H2(
                "Ronde " + numeroRonde + " — " + ronde.getTournoi().getNom()
        );

        Button retour = new Button(
                "⬅ Retour au tournoi",
                e -> UI.getCurrent().navigate(
                        "tournoi/" + ronde.getTournoi().getId()
                )
        );
 
        add(titre, retour, new Hr());

        /* =======================
           ACCORDION DES ÉQUIPES
           ======================= */
        Accordion accordion = new Accordion();

        List<Equipe> equipes = Ronde.equipesRonde(con, this.ronde);
        
        for (Equipe equipe : equipes) {
            int score = equipe.getScore();

            //TODO : sélectionner le terrain pour cette équipe.
            //Terrain terrain = equipe.getTerrain(); // garanti non null
//            String nomTerrain = terrain.getNom();

            String titreEquipe =
                    "Équipe " + equipe.getId()
                    + " — score : " + equipe.getScore();
                    //+ " — terrain : " + nomTerrain; TODO

            /* -------- Contenu : joueurs -------- */
            VerticalLayout contenu = new VerticalLayout();
            contenu.setPadding(false);
            contenu.setSpacing(false);

            List<Joueur> joueurs = equipe.joueursEquipe(con);

            for (Joueur j : joueurs) {
                contenu.add(
                        new Span(
                                j.getId() + " — " + j.getSurnom() + " - " + j.getCategorie()
                        )
                );
            }

            accordion.add(titreEquipe, contenu);
        }

        add(accordion);
    }




}
