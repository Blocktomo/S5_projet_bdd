package fr.insa.toto.webui.ComposantsIndividuels;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.TextField;
import fr.insa.toto.model.Jeu.Joueur;
import fr.insa.toto.model.Jeu.Terrain;
import fr.insa.toto.webui.session.SessionInfo;

import java.util.function.Consumer;

public class CreationTerrain extends FormLayout {

    private final Consumer<Terrain> onCreate;

    private TextField nom;
    private Button save;

    public CreationTerrain(Consumer<Terrain> onCreate) {
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
        nom = new TextField("Nom du terrain");
        nom.setRequired(true);

        save = new Button("ajouter le terrain");
        save.addClickListener(e -> creerTerrain());

        add(
            new H3("Créer un nouveau terrain"),
            nom,
            save
        );
    }

    /* =======================
       CRÉATION LOGIQUE
       ======================= */
    private void creerTerrain() {

        if (nom.isEmpty()) {
            Notification.show("Tous les champs sont obligatoires");
            return;
        }

        Terrain terrain = new Terrain(
                nom.getValue()
        );

        /* 👉 On RENVOIE le joueur au parent */
        onCreate.accept(terrain);

        /* Reset formulaire */
        nom.clear();
    }
}
