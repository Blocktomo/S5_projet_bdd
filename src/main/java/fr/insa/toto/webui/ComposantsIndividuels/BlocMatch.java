package fr.insa.toto.webui.ComposantsIndividuels;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
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
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class BlocMatch extends VerticalLayout {
   
    private final Matchs matchs;
    private VerticalLayout pageInfos;

    public BlocMatch(Matchs matchs) throws SQLException {
        this.matchs = matchs;
        try (Connection con = ConnectionPool.getConnection()){
            if (!SessionInfo.adminConnected()) {
                throw new Error("an admin must be connected to use this");
            }
            setPadding(true);
            setSpacing(true);
            setWidthFull();

            H3 titreTerrain = new H3("Terrain : " + matchs.getTerrain().getNom());
            H4 titreIdMatch = new H4("match n° "+ matchs.getId());
            add(titreTerrain, titreIdMatch);

            pageInfos = new VerticalLayout();
            
            /*=============
            pagesInfos : contient les infos sur l'equipe
            ==========*/
            Equipe equipeA = Equipe.chercherParId(con, matchs.getIdEquipeA());
            Equipe equipeB = Equipe.chercherParId(con, matchs.getIdEquipeB());
            
            String ligne1 = "equipes : A (id %s) vs B (id %s)";
            String ligne1Formate = String.format(ligne1, equipeA.getId(), equipeB.getId());
            
            String ligne2 = "score :   %s           %s";
            String ligne2Formate = String.format(ligne2, equipeA.getScore(), equipeB.getScore());
            
            pageInfos.add(new Span(ligne1Formate));
            pageInfos.add(new Span(ligne2Formate));
            
            add(pageInfos);
            
            /*==============
            modification des scores
            =============*/
            Button boutonModifScore = new Button("Modifier les scores");
            add(boutonModifScore);
            boutonModifScore.addClickListener(e ->{
                try {
                    modifScore(con, equipeA, equipeB);
                } catch (SQLException ex) {
                    Notification.show(ex.getMessage());
                }
            });
            
//            this.refresh();
        }catch (Exception ex){
            
        }
    }

    private void modifScore(Connection con, Equipe equipeA, Equipe equipeB)throws SQLException{
        Dialog dialogModifScore = new Dialog();
        VerticalLayout contenu = new VerticalLayout();
        Notification.show("action : modif score");
        
        HorizontalLayout deuxNumberField = new HorizontalLayout();
        
        contenu.add(new H3("modifier les scores"));
        contenu.add(deuxNumberField);
        
        IntegerField scoreEquipeA = new IntegerField();
        IntegerField scoreEquipeB = new IntegerField();
        deuxNumberField.add(scoreEquipeA, scoreEquipeB);
        
        scoreEquipeA.setLabel("Equipe A");
        scoreEquipeA.setValue(equipeA.getScore());
        scoreEquipeA.setRequired(true);
        
        scoreEquipeB.setLabel("Equipe B");
        scoreEquipeB.setValue(equipeB.getScore());
        scoreEquipeB.setRequired(true);
        
        Button valider = new Button();
        contenu.add(valider);
        valider.addClickListener(e->{
            try{
                if (scoreEquipeA.isEmpty() || scoreEquipeB.isEmpty()) {
                Notification.show("Merci de remplir les champs");
                return;
                }

                int  newScoreA;
                int  newScoreB;

                newScoreA = scoreEquipeA.getValue();
                newScoreB = scoreEquipeB.getValue();

                equipeA.setScore(newScoreA);
                equipeB.setScore(newScoreB);
                equipeA.sauvegarderScore(con);
                equipeB.sauvegarderScore(con);
                
                Notification.show(String.format("nouveaux scores : A : %s, B : %s",equipeA.getScore(), equipeB.getScore()));
                dialogModifScore.close();
            }catch (SQLException ex){
                Notification.show(ex.getMessage());
            }
        });
        
        dialogModifScore.add(contenu);
        
        dialogModifScore.open();
    }
//    private void refresh() {
//        try (Connection con = ConnectionPool.getConnection()) {
//            rondesTournoi = Ronde.rondesDuTournoi(con, this.tournoi);
//            grid.setItems(rondesTournoi);
//        } catch (Exception ex) {
//            Notification.show("Erreur chargement rondes : " + ex.getMessage());
//            System.out.println("Erreur chargement rondes : " + ex.getMessage());
//        }
//    }

    
}
