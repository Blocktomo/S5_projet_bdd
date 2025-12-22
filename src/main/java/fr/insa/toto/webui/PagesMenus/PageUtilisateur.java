package fr.insa.toto.webui.PagesMenus;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import fr.insa.beuvron.utils.database.ConnectionPool;
import fr.insa.beuvron.vaadin.utils.dataGrid.ResultSetGrid;
import fr.insa.toto.webui.Acceuil;
import fr.insa.toto.webui.ComposantsIndividuels.*;
import fr.insa.toto.webui.session.LoginEntete;
import fr.insa.toto.webui.session.LogoutEntete;
import fr.insa.toto.webui.session.SessionInfo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;



@Route(value = "/utilisateurs")
@PageTitle("Utilisateurs")
public class PageUtilisateur extends VerticalLayout {

    public PageUtilisateur() {
        Button retourBtn = new Button("<-- retour"); //TODO faire une jolie mise en forme
        retourBtn.addClickListener(e -> retourBtn.getUI().ifPresent(ui -> ui.navigate(Acceuil.class)));
        this.add(retourBtn);
        
        if (SessionInfo.userConnected()) {
            this.add(new LogoutEntete());
        } else {
            this.add(new LoginEntete());
        }
        
        //LISTE DES UTILISATEURS
        this.add(new H2("Voici la liste des utilisateurs"));
        try (Connection con = ConnectionPool.getConnection()) {
            PreparedStatement pst = con.prepareStatement(
                    "select surnom, role from utilisateur\n"
                    );
            this.add(new ResultSetGrid(pst));
        } catch (SQLException ex) {
            Notification.show("Problème : " + ex.getLocalizedMessage());
        }
        
        if (SessionInfo.adminConnected()){
            this.add(new CreationUtilisateur());
        }
        
        
    }

}
