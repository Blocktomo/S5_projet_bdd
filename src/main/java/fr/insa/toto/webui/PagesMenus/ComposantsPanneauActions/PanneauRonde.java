package fr.insa.toto.webui.PagesMenus.ComposantsPanneauActions;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import fr.insa.beuvron.utils.database.ConnectionPool;
import fr.insa.toto.model.Jeu.*;
import fr.insa.toto.webui.ComposantsIndividuels.CreationJoueur;
import fr.insa.toto.webui.ComposantsIndividuels.CreationTerrain;
import fr.insa.toto.webui.ComposantsIndividuels.CreationTerrain;
import fr.insa.toto.webui.session.SessionInfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.stream.Collectors;

public class PanneauRonde extends VerticalLayout {
   
    private final Tournoi tournoi;
    private Grid<Ronde> grid;
    private List<Ronde> rondesTournoi;

    public PanneauRonde(Tournoi tournoi) {
        this.tournoi = tournoi;
        
        setPadding(true);
        setSpacing(true);
        setWidthFull();
        
        add(new H3("Rondes du tournoi : " + tournoi));

        /* =======================
           GRID RONDES
           ======================= */
        grid = new Grid<>(Ronde.class, false);
        grid.addColumn(Ronde::getIdronde).setHeader("IdRonde");
        grid.addColumn(ron -> (ron.getTerminer()==0)?"non initiée":"terminée").setHeader("Etat"); //si terminer==0, alors afficher "non-initiée"
        
        grid.setWidthFull();
        grid.setHeight("420px");

        grid.setSelectionMode(Grid.SelectionMode.SINGLE); //TODO à voir si utile
        
        add(grid);

        if (SessionInfo.adminConnected()) {
            Button initialiserSuivant = new Button("Initialiser la ronde suivante");
            add(new HorizontalLayout(initialiserSuivant));

            initialiserSuivant.addClickListener(e -> initialiserLaRonde());
        }else{
            Paragraph messageAdmin = new Paragraph("(seuls les admins peuvent modifier cette liste)");
            add(messageAdmin);
        }
        this.refresh();
    }

    private void refresh() {
        try (Connection con = ConnectionPool.getConnection()) {
            rondesTournoi = Ronde.rondesDuTournoi(con, this.tournoi);
            grid.setItems(rondesTournoi);
        } catch (Exception ex) {
            Notification.show("Erreur chargement rondes : " + ex.getMessage());
            System.out.println("Erreur chargement rondes : " + ex.getMessage());
        }
    }

    /* =======================
       DIALOG INITIALISER LA RONDE
       ======================= */
    private void initialiserLaRonde() {
        Notification.show("action : initialiser la ronde");
    }
}
