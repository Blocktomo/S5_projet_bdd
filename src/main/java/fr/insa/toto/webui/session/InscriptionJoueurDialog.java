package fr.insa.toto.webui.session;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import fr.insa.beuvron.utils.database.ConnectionPool;
import fr.insa.toto.model.Jeu.Joueur;
import fr.insa.toto.model.Jeu.Participation;
import fr.insa.toto.model.Jeu.Tournoi;
import fr.insa.toto.webui.session.SessionInfo;

import java.sql.Connection;

public class InscriptionJoueurDialog extends Dialog {

    public InscriptionJoueurDialog(Tournoi tournoi, Runnable onSuccess) {

        setWidth("400px");
        setCloseOnEsc(true);
        setCloseOnOutsideClick(true);

        String surnomUtilisateur = SessionInfo.curUser().get().getSurnom();

        TextField surnom = new TextField("Surnom");
        surnom.setValue(surnomUtilisateur);
        surnom.setReadOnly(true);

        ComboBox<String> categorie = new ComboBox<>("Catégorie");
        categorie.setItems("J", "S");
        categorie.setPlaceholder("Choisir la catégorie");
        categorie.setRequired(true);

        IntegerField taille = new IntegerField("Taille (cm)");
        taille.setMin(100);
        taille.setMax(250);
        taille.setRequired(true);

        Button confirmer = new Button("Confirmer l'inscription");
        confirmer.setWidthFull();

        confirmer.addClickListener(e -> {

            if (categorie.isEmpty() || taille.isEmpty()) {
                Notification.show("Veuillez compléter tous les champs");
                return;
            }

            try (Connection con = ConnectionPool.getConnection()) {

                Joueur joueur = new Joueur(
                        surnomUtilisateur,
                        categorie.getValue(),
                        taille.getValue()
                );
                joueur.saveInDB(con);

                new Participation(joueur, tournoi).saveInDB(con);

                Notification.show("Inscription réussie 🎉");
                close();

                if (onSuccess != null) onSuccess.run();

            } catch (Exception ex) {
                Notification.show("Erreur : " + ex.getMessage());
            }
        });

        add(new VerticalLayout(
                surnom,
                categorie,
                taille,
                confirmer
        ));
    }
}
