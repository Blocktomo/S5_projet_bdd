package fr.insa.toto.webui.session;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import fr.insa.beuvron.utils.database.ConnectionPool;
import fr.insa.toto.model.GestionRH.Utilisateur;
//import fr.insa.toto.webui.VuePrincipale;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

/**
 *
 * @author francois
 */
public class LoginEntete extends HorizontalLayout {

    public TextField surnom;
    public PasswordField pass;
    public Button login;

    public LoginEntete() {
        this.surnom = new TextField("surnom : ");
        this.pass = new PasswordField("pass : ");
        this.login = new Button("login");
        this.login.addClickListener((t) -> {
            this.doLogin();
        });
        this.add(new H3("se connecter"));
        this.add(this.surnom, this.pass, this.login);
    }

    public void doLogin() {
        String surnom = this.surnom.getValue();
        String pass = this.pass.getValue();
        try (Connection con = ConnectionPool.getConnection()) {
            Optional<Utilisateur> trouve = Utilisateur.findBySurnomPass(con, surnom, pass);
            if (trouve.isEmpty()) {
                Notification.show("Surnom ou pass incorrect");
            } else {
                SessionInfo.login(trouve.get());
                UI.getCurrent().refreshCurrentRoute(true);
            }
        } catch (SQLException ex) {
            Notification.show("Problème "+ex.getLocalizedMessage());
        }
    }

}
