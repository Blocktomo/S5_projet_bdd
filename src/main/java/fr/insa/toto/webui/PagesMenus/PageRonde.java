package fr.insa.toto.webui.PagesMenus;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import fr.insa.beuvron.utils.database.ConnectionPool;
import fr.insa.toto.model.Jeu.*;
import fr.insa.toto.webui.ComposantsIndividuels.BlocMatch;
import fr.insa.toto.webui.session.SessionInfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

@Route("ronde")
public class PageRonde extends VerticalLayout implements HasUrlParameter<Integer> {

    private Ronde ronde;

    @Override
    public void setParameter(BeforeEvent event, Integer idRonde) {

        try (Connection con = ConnectionPool.getConnection()) {

            ronde = Ronde.chercherRondeParId(con, idRonde);

            if (ronde == null) {
                Notification.show("Ronde introuvable");
                UI.getCurrent().navigate("");
                return;
            }

            buildUI(con);

        } catch (Exception e) {
            Notification.show("Erreur chargement ronde : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void buildUI(Connection con) throws Exception {

        removeAll();

        boolean rondeTerminee = ronde.getTerminer() == 2;

        /* =======================
           NUMÉRO DE RONDE (SAFE)
           ======================= */
        int numeroRonde = 1;
        List<Ronde> rondes = Ronde.rondesDuTournoi(con, ronde.getTournoi());
        for (int i = 0; i < rondes.size(); i++) {
            if (rondes.get(i).getId() == ronde.getId()) {
                numeroRonde = i + 1;
                break;
            }
        }

        /* =======================
           TITRE
           ======================= */
        H2 titre = new H2(
                "Ronde " + numeroRonde + " — " + ronde.getTournoi().getNom()
        );

        Button retour = new Button("⬅ Retour au tournoi",
                e -> UI.getCurrent().navigate("tournoi/" + ronde.getTournoi().getId())
        );

        HorizontalLayout actions = new HorizontalLayout(retour);

        if (SessionInfo.adminConnected() && !rondeTerminee) {
            Button terminer = new Button("🏁 Terminer la ronde");
            terminer.addClickListener(e -> terminerRonde());
            actions.add(terminer);
        }

        add(titre, actions, new Hr());

        /* =======================
           MATCHS
           ======================= */
        List<Matchs> matchs = Matchs.tousLesMatchsDeLaRonde(con, ronde.getId());

        if (matchs.isEmpty()) {
            add(new Paragraph("Aucun match pour cette ronde"));
            return;
        }

        for (Matchs m : matchs) {
            add(new BlocMatch(m, rondeTerminee));
        }
    }

    //TERMINER LA RONDE
private void terminerRonde() {

    try (Connection con = ConnectionPool.getConnection()) {

        con.setAutoCommit(false);

        //on attribue le score aux joueurs
        List<Equipe> equipes = Equipe.toutesLesEquipes(con);

        for (Equipe e : equipes) {
            if (e.getRonde().getId() == ronde.getId()) {
                e.ajouterScoreAuxJoueurs(con, e.getScore());
            }
        }

        //LIBÉRER LES TERRAINS
        List<Matchs> matchs = Matchs.tousLesMatchsDeLaRonde(con, ronde.getId());

        for (Matchs m : matchs) {
            Terrain t = m.getTerrain();
            if (t != null) {
                t.setOccupe(con, false); 
            }
        }

        //MARQUER LA RONDE TERMINÉE
                try (PreparedStatement pst = con.prepareStatement(
                "UPDATE ronde SET terminer = 2 WHERE idronde = ?")) {
            pst.setInt(1, ronde.getIdronde());
            pst.executeUpdate();
        }

        con.commit();

        Notification.show("Ronde terminée, terrains libérés, scores attribués");
        UI.getCurrent().getPage().reload();

    } catch (Exception e) {
        try {
            ConnectionPool.getConnection().rollback();
        } catch (Exception ignored) {}

        Notification.show("Erreur fin de ronde : " + e.getMessage());
        e.printStackTrace();
    }
}

}
