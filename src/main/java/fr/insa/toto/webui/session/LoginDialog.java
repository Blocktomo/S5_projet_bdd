package fr.insa.toto.webui.session;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import fr.insa.beuvron.utils.database.ConnectionPool;
import fr.insa.toto.model.GestionRH.Utilisateur;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public class LoginDialog extends Dialog {

    public LoginDialog() {

        setWidth("350px");
        setHeight("auto");

        TextField surnom = new TextField("Surnom");
        PasswordField pass = new PasswordField("Mot de passe");

        Button login = new Button("Se connecter");
        login.getStyle().set("background", "#1a73e8").set("color", "white");

        login.addClickListener(e -> {
            try (Connection con = ConnectionPool.getConnection()) {

                Optional<Utilisateur> u = Utilisateur.findBySurnomPass(con, surnom.getValue(), pass.getValue());

                if (u.isEmpty()) {
                    Notification.show("Identifiants incorrects");
                } else {
                    SessionInfo.login(u.get());
                    close();
                    UI.getCurrent().getPage().reload();
                }

            } catch (SQLException ex) {
                Notification.show("Erreur : " + ex.getMessage());
            }
        });

        VerticalLayout layout = new VerticalLayout(
                new H2("Connexion"),
                surnom,
                pass,
                login
        );
        layout.setSpacing(true);
        layout.setPadding(true);
        layout.setAlignItems(Alignment.CENTER);

        add(layout);
    }
}