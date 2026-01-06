package fr.insa.toto.webui.ComposantsIndividuels;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.popover.Popover;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import fr.insa.beuvron.utils.database.ConnectionPool;
import fr.insa.toto.model.Jeu.Equipe;
import fr.insa.toto.model.Jeu.Joueur;
import fr.insa.toto.model.Jeu.Matchs;
import fr.insa.toto.model.Jeu.Terrain;
import fr.insa.toto.webui.session.SessionInfo;

import java.sql.Connection;
import java.util.List;

public class BlocMatch extends VerticalLayout {

    private final Matchs match;
    private final boolean rondeTerminee;

    private Equipe equipeA;
    private Equipe equipeB;

    private Span scoreSpan; // 🔴 IMPORTANT : pour mise à jour dynamique

    public BlocMatch(Matchs match, boolean rondeTerminee) {

        this.match = match;
        this.rondeTerminee = rondeTerminee;

        setPadding(true);
        setSpacing(false);
        setWidthFull();

        getStyle()
                .set("border", "1px solid #ddd")
                .set("border-radius", "8px")
                .set("background", "#fafafa")
                .set("margin-bottom", "10px");

        try (Connection con = ConnectionPool.getConnection()) {

            Terrain terrain = match.getTerrain();
            equipeA = Equipe.chercherParId(con, match.getIdEquipeA());
            equipeB = Equipe.chercherParId(con, match.getIdEquipeB());

            /* =======================
               TITRE
               ======================= */
            Span titre = new Span(
                    "Terrain : " + (terrain == null ? "Non attribué" : terrain.getNom())
                            + " — Match #" + match.getId()
            );
            titre.getStyle().set("font-weight", "600");

            /* =======================
               ÉQUIPES
               ======================= */
            Button buttonEquipeA = new Button(String.format("Equipe A (id %s)", this.equipeA.getId()));
            buttonEquipeA.setHeight("20px");
            
            Button buttonEquipeB = new Button(String.format("Equipe B (id %s)", this.equipeB.getId()));
            buttonEquipeB.setHeight("20px");
            HorizontalLayout nomsEquipes = new HorizontalLayout();
            nomsEquipes.add(buttonEquipeA, new Span(" vs "),buttonEquipeB);
            
            VerticalLayout joueursEquipeA = new PanneauJoueursEquipe(this.equipeA, "A");
            
            VerticalLayout joueursEquipeB = new PanneauJoueursEquipe(this.equipeB, "A");
            
            Popover contenuEquipeA = new Popover();
            contenuEquipeA.setWidth("400px");
            contenuEquipeA.setHoverDelay(0);
            contenuEquipeA.setTarget(buttonEquipeA);
            contenuEquipeA.setOpenOnClick(true);
            contenuEquipeA.setOpenOnHover(true);
            contenuEquipeA.add(joueursEquipeA);
            
            Popover contenuEquipeB = new Popover();
            contenuEquipeB.setWidth("400px");
            contenuEquipeB.setHoverDelay(0);
            contenuEquipeB.setTarget(buttonEquipeB);
            contenuEquipeB.setOpenOnClick(true);
            contenuEquipeB.setOpenOnHover(true);
            contenuEquipeB.add(joueursEquipeB);
            

            /* =======================
               SCORE (modifiable dynamiquement)
               ======================= */
            scoreSpan = new Span();
            majScore();
            scoreSpan.getStyle().set("font-weight", "bold");

            add(titre, nomsEquipes, scoreSpan);

            /* =======================
               BOUTON ADMIN
               ======================= */
            if (SessionInfo.adminConnected() && !rondeTerminee) {
                Button modifier = new Button("✏ Modifier score");
                modifier.getStyle().set("font-size", "12px");
                modifier.addClickListener(e -> ouvrirDialogScore());
                add(modifier);
            }

        } catch (Exception e) {
            Notification.show("Erreur affichage match : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /* =======================
       METTRE À JOUR LE TEXTE DU SCORE
       ======================= */
    private void majScore() {
        scoreSpan.setText(
                "Score : " + equipeA.getScore() + " - " + equipeB.getScore()
        );
    }

    /* =======================
       DIALOG MODIFICATION SCORE
       ======================= */
private void ouvrirDialogScore() {

    Dialog dialog = new Dialog();
    dialog.setWidth("450px");
    dialog.setCloseOnEsc(true);
    dialog.setCloseOnOutsideClick(true);

    IntegerField scoreA = new IntegerField("Score équipe A");
    IntegerField scoreB = new IntegerField("Score équipe B");

    scoreA.setWidth("150px");
    scoreB.setWidth("150px");

    scoreA.setValue(equipeA.getScore());
    scoreB.setValue(equipeB.getScore());

    HorizontalLayout scoresLayout = new HorizontalLayout(scoreA, scoreB);
    scoresLayout.setJustifyContentMode(JustifyContentMode.CENTER);
    scoresLayout.setWidthFull();

    Button valider = new Button("Valider");

    valider.addClickListener(e -> {
        try (Connection con = ConnectionPool.getConnection()) {

            if (scoreA.isEmpty() || scoreB.isEmpty()) {
                Notification.show("Veuillez saisir les deux scores");
                return;
            }

            equipeA.setScore(scoreA.getValue());
            equipeB.setScore(scoreB.getValue());

            equipeA.sauvegarderScore(con);
            equipeB.sauvegarderScore(con);

            majScore(); // mise à jour immédiate UI

            Notification.show("Scores mis à jour");
            dialog.close();

        } catch (Exception ex) {
            Notification.show("Erreur sauvegarde score : " + ex.getMessage());
            ex.printStackTrace();
        }
    });

    VerticalLayout contenu = new VerticalLayout(
            new H4("Modifier les scores"),
            scoresLayout,
            valider
    );

    contenu.setAlignItems(Alignment.CENTER);
    contenu.setSpacing(true);
    contenu.setPadding(true);
    contenu.setWidthFull();

    dialog.add(contenu);
    dialog.open();
}

}
