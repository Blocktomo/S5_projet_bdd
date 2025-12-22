package fr.insa.toto.webui.PagesMenus;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import fr.insa.beuvron.utils.database.ConnectionPool;
import fr.insa.beuvron.vaadin.utils.dataGrid.ResultSetGrid;
import fr.insa.toto.webui.Acceuil;
import fr.insa.toto.webui.ComposantsIndividuels.*;
import fr.insa.toto.webui.session.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;



@Route(value = "/joueurs")
@PageTitle("Joueurs")
public class PageJoueur extends VerticalLayout {

    public PageJoueur() {
        Button retourBtn = new Button("<-- retour");
        retourBtn.addClickListener(e -> retourBtn.getUI().ifPresent(ui -> ui.navigate(Acceuil.class)));
        this.add(retourBtn);
        this.add(new H2("Voici la liste des joueurs"));
//        this.add(new Paragraph("une superbe application, kartoffel"));
        
        try (Connection con = ConnectionPool.getConnection()) {
            PreparedStatement pst = con.prepareStatement(
                    "select surnom, score from joueur\n"
                    );
            this.add(new ResultSetGrid(pst));
        } catch (SQLException ex) {
            Notification.show("Problème : " + ex.getLocalizedMessage());
        }
        if (SessionInfo.userConnected()){
            this.add(new CreationJoueur());
        }else{
            this.add(new H3("connectez vous pour ajouter des joueurs"));
        }
        
        
        //TODO : réactualiser à chaque ajout de joueur l'affichage de la liste
        
        
    }

}
