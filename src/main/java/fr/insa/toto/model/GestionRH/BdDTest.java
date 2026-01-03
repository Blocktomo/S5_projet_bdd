package fr.insa.toto.model.GestionRH;

import fr.insa.beuvron.utils.database.ConnectionSimpleSGBD;
import fr.insa.toto.model.Jeu.Joueur;
import fr.insa.toto.model.Jeu.Ronde;
import fr.insa.toto.model.Jeu.Terrain;
import fr.insa.toto.model.Jeu.Tournoi;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BdDTest {

    public static void createBdDTestV4(Connection con) throws SQLException {

        /* =======================
           TOURNOIS
           ======================= */
        Tournoi can = new Tournoi(
                "Tournoi CAN",
                2025,
                10,
                90,
                11
        );
        can.saveInDB(con);
        Ronde.creerRondesVides(can, con);

        Tournoi tennis = new Tournoi(
                "Tournoi Tennis Duo",
                2027,
                5,
                60,
                2
        );
        tennis.saveInDB(con);
        Ronde.creerRondesVides(tennis, con);
        
        /* =======================
           JOUEURS CAN (44)
           ======================= */
        List<Joueur> joueursCAN = new ArrayList<>();

        for (int i = 1; i <= 44; i++) {
            joueursCAN.add(
                    new Joueur(
                            "CAN_Player_" + i,
                            (i % 2 == 0) ? "S" : "J",
                            165 + (i % 20)
                    )
            );
        }

        for (Joueur j : joueursCAN) {
            j.saveInDB(con);
            ajouterParticipation(con, j.getId(), can.getId());
        }

        /* =======================
           JOUEURS TENNIS (12)
           ======================= */
        List<Joueur> joueursTennis = new ArrayList<>();

        for (int i = 1; i <= 12; i++) {
            joueursTennis.add(
                    new Joueur(
                            "Tennis_Player_" + i,
                            "S",
                            170 + (i % 10)
                    )
            );
        }

        for (Joueur j : joueursTennis) {
            j.saveInDB(con);
            ajouterParticipation(con, j.getId(), tennis.getId());
        }

        /* =======================
           TERRAINS
           ======================= */
        List<Terrain> terrains = List.of(
                new Terrain("Parc des Princes"),
                new Terrain("Stade Municipal"),
                new Terrain("Court Central"),
                new Terrain("Court Annexe")
        );

        for (Terrain t : terrains) {
            t.saveInDB(con);
            ajouterTerrainsTournois(con, t.getId(), tennis.getId());
            ajouterTerrainsTournois(con, t.getId(), can.getId());
        }

        /* =======================
           UTILISATEURS
           ======================= */
        List<Utilisateur> utilisateurs = List.of(
                new Utilisateur("thomas", "insa67", 1),
                new Utilisateur("tartenpion", "insa67", 2)
                
        );

        for (Utilisateur u : utilisateurs) {
            u.saveInDB(con);
        }
    }

    /* =======================
       PARTICIPATION
       ======================= */
    private static void ajouterParticipation(Connection con, int idJoueur, int idTournoi)
            throws SQLException {

        try (PreparedStatement pst = con.prepareStatement(
                "INSERT INTO participation (idjoueur, idtournoi) VALUES (?, ?)"
        )) {
            pst.setInt(1, idJoueur);
            pst.setInt(2, idTournoi);
            pst.executeUpdate();
        }
    }
    /* =======================
       TERRAINS_TOURNOIS
       ======================= */
    private static void ajouterTerrainsTournois(Connection con, int idTerrain, int idTournoi)
            throws SQLException {

        try (PreparedStatement pst = con.prepareStatement(
                "INSERT INTO terrains_tournois (idterrain, idtournoi) VALUES (?, ?)"
        )) {
            pst.setInt(1, idTerrain);
            pst.setInt(2, idTournoi);
            pst.executeUpdate();
        }
    }

    public static void main(String[] args) {
        try (Connection con = ConnectionSimpleSGBD.defaultCon()) {
            GestionBdD.razBdd(con);
            createBdDTestV4(con);
            System.out.println("✅ BdDTest V4 initialisée avec succès");
        } catch (SQLException ex) {
            throw new Error(ex);

        }
    }
}