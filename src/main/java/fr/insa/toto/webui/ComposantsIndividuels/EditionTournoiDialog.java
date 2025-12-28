package fr.insa.toto.webui.ComposantsIndividuels;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import fr.insa.beuvron.utils.database.ConnectionPool;
import fr.insa.toto.model.Jeu.Tournoi;

import java.sql.Connection;

public class EditionTournoiDialog extends Dialog {

    public EditionTournoiDialog(
            Tournoi tournoi,
            ModeEditionTournoi mode,
            Runnable onSuccess
    ) {

        setWidth("420px");

        setHeaderTitle(
                mode == ModeEditionTournoi.CREATE ? "Ajouter un tournoi" :
                mode == ModeEditionTournoi.EDIT   ? "Modifier le tournoi" :
                                                    "Détails du tournoi"
        );

        /* ===== CHAMPS ===== */
        TextField nom = new TextField("Nom du tournoi");
        IntegerField annee = new IntegerField("Année");
        IntegerField nbRondes = new IntegerField("Nombre de rondes");
        IntegerField duree = new IntegerField("Durée des matchs (min)");
        IntegerField nbJoueurs = new IntegerField("Joueurs par équipe");

        if (tournoi != null) {
            nom.setValue(tournoi.getNom());
            annee.setValue(tournoi.getAnnee());
            nbRondes.setValue(tournoi.getNbDeRondes());
            duree.setValue(tournoi.getDureeMatch());
            nbJoueurs.setValue(tournoi.getNbJoueursEquipe());
        }

        /* ===== MODE VIEW → READ ONLY ===== */
        boolean readOnly = (mode == ModeEditionTournoi.VIEW);
        nom.setReadOnly(readOnly);
        annee.setReadOnly(readOnly);
        nbRondes.setReadOnly(readOnly);
        duree.setReadOnly(readOnly);
        nbJoueurs.setReadOnly(readOnly);

        FormLayout form = new FormLayout(
                nom, annee, nbRondes, duree, nbJoueurs
        );

        add(form);

        /* ===== BOUTONS ===== */
        Button fermer = new Button(readOnly ? "Fermer" : "Annuler", e -> close());
        getFooter().add(fermer);

        if (!readOnly) {
            Button enregistrer = new Button("Enregistrer", e -> {
                try (Connection con = ConnectionPool.getConnection()) {

                    if (mode == ModeEditionTournoi.CREATE) {
                        Tournoi t = new Tournoi(
                                nom.getValue(),
                                annee.getValue(),
                                nbRondes.getValue(),
                                duree.getValue(),
                                nbJoueurs.getValue()
                        );
                        t.saveInDB(con);
                    } else {
                        tournoi.setNom(nom.getValue());
                        tournoi.setAnnee(annee.getValue());
                        tournoi.setNbDeRondes(nbRondes.getValue());
                        tournoi.setDureeMatch(duree.getValue());
                        tournoi.setNbJoueursEquipe(nbJoueurs.getValue());
                        tournoi.updateInDB(con);
                    }

                    Notification.show("Tournoi enregistré");
                    close();
                    if (onSuccess != null) onSuccess.run();

                } catch (Exception ex) {
                    Notification.show("Erreur : " + ex.getMessage());
                }
            });

            getFooter().add(enregistrer);
        }
    }
}
