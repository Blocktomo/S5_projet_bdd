package fr.insa.toto.webui.ComposantsIndividuels;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import fr.insa.beuvron.utils.database.ConnectionPool;
import fr.insa.toto.model.Jeu.*;
import fr.insa.toto.webui.session.SessionInfo;
//import fr.insa.toto.webui.MainLayout;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class CreationJoueur extends FormLayout {

    private TextField surnom;
    private ComboBox<String> categorie;
    private TextField taillecm;
    private Button save;
    private Runnable onSuccess; 

        public CreationJoueur(Runnable onSuccess) {
        this.onSuccess = onSuccess;

        // sécurité : admin uniquement
        if (!SessionInfo.adminConnected()) {
            add(new H3("Accès réservé à l'administrateur"));
            return;
        }

        this.surnom = new TextField("Surnom");
        this.surnom.setRequired(true);

        this.categorie = new ComboBox<>("Catégorie");
        this.categorie.setItems("Junior", "Senior");
        this.categorie.setValue("Junior");

        this.taillecm = new TextField("Taille (cm)");
        this.taillecm.setPlaceholder("ex : 175");

        this.save = new Button("Ajouter le joueur");
        this.save.addClickListener(e -> doSave());

        add(
            new H3("Ajouter un joueur"),
            surnom,
            categorie,
            taillecm,
            save
        );
    }

    private void doSave() {
        
        

        if (surnom.isEmpty() || taillecm.isEmpty()) {
            Notification.show("Tous les champs sont obligatoires");
            return;
        }

        double taille;
        try {
            taille = Double.parseDouble(taillecm.getValue());
        } catch (NumberFormatException e) {
            Notification.show("La taille doit être un nombre");
            return;
        }

        String catCode = categorie.getValue().equals("Senior") ? "S" : "J";

        try (Connection con = ConnectionPool.getConnection()) {
            
            con.setAutoCommit(true);

            Joueur j = new Joueur(
                surnom.getValue(),
                catCode,
                taille
            );
            j.saveInDB(con);
            //UI.getCurrent().getPage().reload();

            Notification.show("Joueur ajouté : " + j.getSurnom());
            
            if (onSuccess != null) {
    onSuccess.run();   //  prévient le panneau
}

            // reset formulaire
            surnom.clear();
            taillecm.clear();
            categorie.setValue("Junior");

        } catch (SQLException ex) {
            Notification.show("Erreur BDD : " + ex.getMessage());
        }
    }
}