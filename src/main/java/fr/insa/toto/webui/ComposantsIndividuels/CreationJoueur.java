package fr.insa.toto.webui.ComposantsIndividuels;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.TextField;
import fr.insa.toto.model.Jeu.Joueur;
import fr.insa.toto.webui.session.SessionInfo;

import java.util.function.Consumer;

public class CreationJoueur extends FormLayout {

    private final Consumer<Joueur> onCreate;

    private TextField surnom;
    private ComboBox<String> categorie;
    private TextField taillecm;
    private Button save;

    public CreationJoueur(Consumer<Joueur> onCreate) {
        this.onCreate = onCreate;

        /* =======================
           SÉCURITÉ
           ======================= */
        if (!SessionInfo.adminConnected()) {
            add(new H3("Accès réservé à l'administrateur"));
            return;
        }

        /* =======================
           CHAMPS
           ======================= */
        surnom = new TextField("Surnom");
        surnom.setRequired(true);

        categorie = new ComboBox<>("Catégorie");
        categorie.setItems("Junior", "Senior");
        categorie.setValue("Junior");

        taillecm = new TextField("Taille (cm)");
        taillecm.setPlaceholder("ex : 175");

        save = new Button("Ajouter le joueur");
        save.addClickListener(e -> creerJoueur());

        add(
            new H3("Ajouter un joueur"),
            surnom,
            categorie,
            taillecm,
            save
        );
    }

    /* =======================
       CRÉATION LOGIQUE
       ======================= */
    private void creerJoueur() {

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

        Joueur joueur = new Joueur(
                surnom.getValue(),
                catCode,
                taille
        );

        /* 👉 On RENVOIE le joueur au parent */
        onCreate.accept(joueur);

        /* Reset formulaire */
        surnom.clear();
        taillecm.clear();
        categorie.setValue("Junior");
    }
}
