package fr.insa.toto.webui.PagesMenus;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import fr.insa.beuvron.utils.database.ConnectionPool;
import fr.insa.toto.model.Jeu.Tournoi;
import fr.insa.toto.webui.Acceuil;

import java.sql.Connection;

@Route("tournoi")
public class Pagetournoi extends HorizontalLayout
        implements HasUrlParameter<Integer> {

    private Tournoi tournoi;

    public Pagetournoi() {
        setSizeFull();
        setPadding(false);
        setSpacing(false);
    }

    @Override
    public void setParameter(BeforeEvent event, Integer idTournoi) {

        // sécurité : URL invalide
        if (idTournoi == null) {
            Notification.show("Aucun tournoi sélectionné");
            event.forwardTo(Acceuil.class);
            return;
        }

        try (Connection con = ConnectionPool.getConnection()) {

            tournoi = Tournoi.chercherParId(con, idTournoi);

            if (tournoi == null) {
                Notification.show("Tournoi introuvable");
                event.forwardTo(Acceuil.class);
                return;
            }

        } catch (Exception e) {
            Notification.show("Erreur chargement tournoi : " + e.getMessage());
            event.forwardTo(Acceuil.class);
            return;
        }

        buildUI();
    }

    private void buildUI() {
        removeAll();

        // panneau gauche = joueurs DU TOURNOI
        PanneauJoueurs panneauJoueurs = new PanneauJoueurs(tournoi);
        panneauJoueurs.setWidth("60%");
        panneauJoueurs.setHeightFull();

        // panneau droit = actions (équipes, rondes, matchs, etc.)
        PanneauActions panneauActions = new PanneauActions(tournoi);
        panneauActions.setWidth("40%");
        panneauActions.setHeightFull();

        add(panneauJoueurs, panneauActions);
    }
}
