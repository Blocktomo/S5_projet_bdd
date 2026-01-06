package fr.insa.toto.webui.PagesMenus.ComposantsPanneauActions;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import fr.insa.beuvron.utils.database.ConnectionPool;
import fr.insa.toto.model.Jeu.Joueur;
import fr.insa.toto.model.Jeu.Tournoi;

import java.sql.Connection;
import java.util.List;

public class PodiumDialog extends Dialog {

    public PodiumDialog(Tournoi tournoi) {

        setWidth("420px");
        setCloseOnEsc(true);
        setCloseOnOutsideClick(true);

        VerticalLayout layout = new VerticalLayout();
        layout.setAlignItems(Alignment.CENTER);
        layout.getStyle()
                .set("animation", "pop 0.5s ease-out")
                .set("text-align", "center");

        H2 titre = new H2("🏆 Podium – " + tournoi.getNom());

        Div podium = new Div();
        podium.getStyle()
                .set("display", "flex")
                .set("gap", "20px")
                .set("align-items", "flex-end")
                .set("margin-top", "30px");

        try (Connection con = ConnectionPool.getConnection()) {

            List<Joueur> top3 = Joueur.joueursDuTournoi(con, tournoi).stream()
                    .sorted((j1, j2) -> Integer.compare(j2.getScore(), j1.getScore()))
                    .limit(3)
                    .toList();

            if (top3.size() > 1) podium.add(bloc(top3.get(1), "🥈", "#e5e7eb", "100px"));
            if (top3.size() > 0) podium.add(bloc(top3.get(0), "🥇", "#fde047", "140px"));
            if (top3.size() > 2) podium.add(bloc(top3.get(2), "🥉", "#fcd34d", "80px"));

        } catch (Exception e) {
            podium.add(new Span("Erreur chargement podium"));
        }

        layout.add(titre, podium);
        add(layout);

        // animation CSS
        getElement().executeJs("""
            const style = document.createElement('style');
            style.innerHTML = `
                @keyframes pop {
                    from { transform: scale(0.7); opacity: 0; }
                    to { transform: scale(1); opacity: 1; }
                }
            `;
            document.head.appendChild(style);
        """);
    }

    private Div bloc(Joueur joueur, String medal, String color, String height) {
        Div d = new Div(
                new Span(medal),
                new Span(joueur.getSurnom()),
                new Span(joueur.getScore() + " pts")
        );

        d.getStyle()
                .set("background", color)
                .set("width", "110px")
                .set("height", height)
                .set("border-radius", "14px")
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("justify-content", "center")
                .set("align-items", "center")
                .set("font-size", "15px")
                .set("font-weight", "600");

        return d;
    }
}
