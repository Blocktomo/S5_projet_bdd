package fr.insa.toto.webui.session;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import fr.insa.beuvron.utils.database.ConnectionPool;
import fr.insa.toto.model.GestionRH.Utilisateur;

import java.sql.Connection;
import java.util.Optional;

public class InscriptionOuConnexionDialog extends Dialog {

    public InscriptionOuConnexionDialog() {

        setWidth("420px");
        setCloseOnEsc(true);
        setCloseOnOutsideClick(true);

        /* =======================
           ONGLET
           ======================= */
        Tab tabLogin = new Tab("Se connecter");
        Tab tabCreate = new Tab("Créer un compte");

        Tabs tabs = new Tabs(tabLogin, tabCreate);
        tabs.addThemeVariants(TabsVariant.LUMO_CENTERED);

        VerticalLayout content = new VerticalLayout();
        content.setPadding(true);
        content.setSpacing(true);

        /* =======================
           LOGIN
           ======================= */
        VerticalLayout loginLayout = creerLoginLayout();

        /* =======================
           CREATION
           ======================= */
        VerticalLayout createLayout = creerCreationLayout();

        /* =======================
           SWITCH ONGLET
           ======================= */
        tabs.addSelectedChangeListener(e -> {
            content.removeAll();
            if (e.getSelectedTab() == tabLogin) {
                content.add(loginLayout);
            } else {
                content.add(createLayout);
            }
        });

        /* Onglet par défaut */
        content.add(loginLayout);

        add(tabs, content);
    }

    /* =====================================================
       FORMULAIRE LOGIN
       ===================================================== */
    private VerticalLayout creerLoginLayout() {

        TextField surnom = new TextField("Surnom");
        PasswordField pass = new PasswordField("Mot de passe");

        Button login = new Button("Se connecter");
        login.setWidthFull();

        login.addClickListener(e -> {
            try (Connection con = ConnectionPool.getConnection()) {

                Optional<Utilisateur> u =
                        Utilisateur.findBySurnomPass(con, surnom.getValue(), pass.getValue());

                if (u.isEmpty()) {
                    Notification.show("Identifiants incorrects");
                } else {
                    SessionInfo.login(u.get());
                    Notification.show("Connexion réussie");
                    close();
                    UI.getCurrent().getPage().reload();
                }

            } catch (Exception ex) {
                Notification.show("Erreur : " + ex.getMessage());
            }
        });

        return new VerticalLayout(
                new H2("Connexion"),
                surnom,
                pass,
                login
        );
    }

    /* =====================================================
       FORMULAIRE CREATION COMPTE
       ===================================================== */
    private VerticalLayout creerCreationLayout() {

        TextField surnom = new TextField("Surnom");
        PasswordField pass = new PasswordField("Mot de passe");

        Button creer = new Button("Créer le compte");
        creer.setWidthFull();

        creer.addClickListener(e -> {
            try (Connection con = ConnectionPool.getConnection()) {

                Utilisateur u = new Utilisateur(
                        surnom.getValue(),
                        pass.getValue(),
                        2 // utilisateur standard
                );
                u.saveInDB(con);

                SessionInfo.login(u);

                Notification.show("Compte créé et connecté");
                close();
                UI.getCurrent().getPage().reload();

            } catch (Exception ex) {
                Notification.show("Erreur création compte : " + ex.getMessage());
            }
        });

        return new VerticalLayout(
                new H2("Créer un compte"),
                surnom,
                pass,
                creer
        );
    }
}
