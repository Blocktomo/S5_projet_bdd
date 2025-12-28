package fr.insa.toto.model.Jeu;

import fr.insa.beuvron.utils.database.ClasseMiroir;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Représente la participation d'un joueur à un tournoi.
 * Table N–N entre joueur et tournoi.
 */
public class Participation implements Serializable {

    private static final long serialVersionUID = 1L;

    private Joueur joueur;
    private Tournoi tournoi;

    /* =======================
       CONSTRUCTEUR
       ======================= */

    public Participation(Joueur joueur, Tournoi tournoi) {
        this.joueur = joueur;
        this.tournoi = tournoi;
    }

    /* =======================
       PERSISTENCE
       ======================= */

    /** Inscrit un joueur à un tournoi */
    public void saveInDB(Connection con) throws SQLException {
        try (PreparedStatement pst = con.prepareStatement(
                """
                INSERT INTO participation (idjoueur, idtournoi)
                VALUES (?, ?)
                """
        )) {
            pst.setInt(1, joueur.getId());
            pst.setInt(2, tournoi.getId());
            pst.executeUpdate();
        }
    }

    /** Supprime la participation */
    public void deleteInDB(Connection con) throws SQLException {
        try (PreparedStatement pst = con.prepareStatement(
                """
                DELETE FROM participation
                WHERE idjoueur = ? AND idtournoi = ?
                """
        )) {
            pst.setInt(1, joueur.getId());
            pst.setInt(2, tournoi.getId());
            pst.executeUpdate();
        }
    }

    /* =======================
       REQUÊTES STATIQUES
       ======================= */

    /** Tous les joueurs d’un tournoi */
    public static List<Joueur> joueursDuTournoi(Connection con, Tournoi tournoi) throws SQLException {
        List<Joueur> res = new ArrayList<>();

        try (PreparedStatement pst = con.prepareStatement(
                """
                SELECT j.id, j.surnom, j.categorie, j.taillecm, j.score
                FROM joueur j
                JOIN participation p ON p.idjoueur = j.id
                WHERE p.idtournoi = ?
                """
        )) {
            pst.setInt(1, tournoi.getId());

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    res.add(new Joueur(
                            rs.getInt("id"),
                            rs.getString("surnom"),
                            rs.getString("categorie"),
                            rs.getDouble("taillecm"),
                            rs.getInt("score")
                    ));
                }
            }
        }
        return res;
    }

    /** Tous les tournois d’un joueur */
    public static List<Tournoi> tournoisDuJoueur(Connection con, Joueur joueur) throws SQLException {
        List<Tournoi> res = new ArrayList<>();

        try (PreparedStatement pst = con.prepareStatement(
                """
                SELECT t.id, t.nom, t.annee, t.nb_de_rondes, t.duree_match, t.nb_joueurs_equipe
                FROM tournoi t
                JOIN participation p ON p.idtournoi = t.id
                WHERE p.idjoueur = ?
                """
        )) {
            pst.setInt(1, joueur.getId());

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    res.add(new Tournoi(
                            rs.getInt("id"),
                            rs.getString("nom"),
                            rs.getInt("annee"),
                            rs.getInt("nb_de_rondes"),
                            rs.getInt("duree_match"),
                            rs.getInt("nb_joueurs_equipe")
                    ));
                }
            }
        }
        return res;
    }

    /* =======================
       GETTERS
       ======================= */

    public Joueur getJoueur() {
        return joueur;
    }

    public Tournoi getTournoi() {
        return tournoi;
    }
}
