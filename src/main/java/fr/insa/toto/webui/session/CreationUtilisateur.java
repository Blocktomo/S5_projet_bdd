
package fr.insa.toto.webui.session;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import fr.insa.beuvron.utils.database.ConnectionPool;
import fr.insa.toto.model.GestionRH.Utilisateur;
import fr.insa.toto.model.Jeu.*;
//import fr.insa.toto.webui.MainLayout;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 *ceci est un FormLayout : c'est un "composant individuel" que l'on peut ajouter sur une page web
 * Par "composant individuel", on entend que c'est un élément, un bloc, qui peut être ajouté n'import où
 *
 */
public class CreationUtilisateur extends FormLayout {

    private TextField surnom;
    private PasswordField password;
    private ComboBox<String> role;
    private Button save;
    
    public CreationUtilisateur() {        
        this.surnom = new TextField("surnom");
        this.password = new PasswordField("password");
        this.role = new ComboBox<>("role");
        this.role.setItems(List.of("admin (1)","standard (2)"));
        this.role.setValue("standard (2)");
        
        this.save = new Button("save");
        this.save.addClickListener((t) -> {
            this.doSave();
        });

        this.setAutoResponsive(true);
        this.addFormRow(this.surnom,this.password, this.role);
        this.addFormRow(this.save);
    }
    
    public void doSave() {
        try (Connection con = ConnectionPool.getConnection()) {
            String surnom = this.surnom.getValue();
            String pass = this.password.getValue();
            int role = 2;
            if (this.role.getValue() != null && this.role.getValue().equals("admin (1)")) {
                role = 1;
            }            
            Utilisateur u = new Utilisateur(surnom, pass, role);
            u.saveInDB(con);  
            Notification.show("utilisateur "+ surnom + " créé");
        } catch (SQLException ex) {
            Notification.show("Problème : " + ex.getLocalizedMessage());
        }
        UI.getCurrent().refreshCurrentRoute(true);
        //TODO : problème 22/12/2025 : il semble que la BdD en ligne ne se mette pas réellement à jour lorsqu'on clique sur "save"... lorsqu'on consulte la BBD rien ne se passe
    }
public String getSurnomValue() {
    return surnom.getValue();
}

public String getPasswordValue() {
    return password.getValue();
}

/** Création utilisateur STANDARD uniquement */
public Utilisateur creerUtilisateurStandard() throws SQLException {
    try (Connection con = ConnectionPool.getConnection()) {
        Utilisateur u = new Utilisateur(
                getSurnomValue(),
                getPasswordValue(),
                2 // standard
        );
        u.saveInDB(con);
        return u;
    }
}



}
