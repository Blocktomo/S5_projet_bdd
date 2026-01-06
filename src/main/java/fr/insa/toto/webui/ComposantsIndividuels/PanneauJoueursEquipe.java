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


/**
 * Cette classe permet d'afficher les joueurs d'une équipe en partculier
 * surnomEquipe : "equipe A" ou "equipe B"
 */
public class PanneauJoueursEquipe extends VerticalLayout {

    private final Equipe equipe;

    public PanneauJoueursEquipe(Equipe equipe, String surnomEquipe) {
        this.equipe = equipe;
        this.add(new Span("Joueurs de l'équipe " + surnomEquipe));
        this.add(this.afficherGridJoueurEquipe(this.equipe));
    }

    public Grid<Joueur> afficherGridJoueurEquipe(Equipe equipe){
        Grid<Joueur> grid =  new Grid<>(Joueur.class, false);
        grid.addColumn(Joueur::getSurnom).setHeader("Surnom");
        grid.addColumn(Joueur::getCategorie).setHeader("Catégorie");
        
        grid.setAllRowsVisible(true);
        try (Connection con = ConnectionPool.getConnection()) {
            List<Joueur> joueursTournoi = equipe.joueursEquipe(con);
            grid.setItems(joueursTournoi);
        } catch (Exception ex) {
            Notification.show("Erreur chargement joueurs : " + ex.getMessage());
        }
    
        return grid;
    }

}
