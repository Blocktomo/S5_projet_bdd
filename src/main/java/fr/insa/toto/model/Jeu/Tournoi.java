package fr.insa.toto.model.Jeu;

import fr.insa.beuvron.utils.database.ClasseMiroir;
import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Représente un tournoi.
 * Version non statique, compatible console + web.
 */
public class Tournoi extends ClasseMiroir implements Serializable {

    private static final long serialVersionUID = 1L;

    /* =======================
       ATTRIBUTS
       ======================= */
    private String nom;
    private int annee;
    private int nbDeRondes;
    private int dureeMatch;
    private int nbJoueursEquipe;

    /* =======================
       CONSTRUCTEURS
       ======================= */

    /** Nouveau tournoi en mémoire */
    public Tournoi(String nom, int annee, int nbDeRondes, int dureeMatch, int nbJoueursEquipe) {
        super();
        this.nom = nom;
        this.annee = annee;
        this.nbDeRondes = nbDeRondes;
        this.dureeMatch = dureeMatch;
        this.nbJoueursEquipe = nbJoueursEquipe;
    }

    /** Tournoi récupéré depuis la BDD */
    public Tournoi(int id, String nom, int annee, int nbDeRondes, int dureeMatch, int nbJoueursEquipe) {
        super(id);
        this.nom = nom;
        this.annee = annee;
        this.nbDeRondes = nbDeRondes;
        this.dureeMatch = dureeMatch;
        this.nbJoueursEquipe = nbJoueursEquipe;
    }

    /* =======================
       PERSISTENCE
       ======================= */

    @Override
    public Statement saveSansId(Connection con) throws SQLException {
        PreparedStatement pst = con.prepareStatement(
                """
                INSERT INTO tournoi (nom, annee, nb_de_rondes, duree_match, nb_joueurs_equipe)
                VALUES (?, ?, ?, ?, ?)
                """,
                Statement.RETURN_GENERATED_KEYS
        );

        pst.setString(1, nom);
        pst.setInt(2, annee);
        pst.setInt(3, nbDeRondes);
        pst.setInt(4, dureeMatch);
        pst.setInt(5, nbJoueursEquipe);

        pst.executeUpdate();
        return pst;
    }

    /** Met à jour le tournoi en base */
    public void updateInDB(Connection con) throws SQLException {
        if (getId() == -1) {
            throw new ClasseMiroir.EntiteNonSauvegardee();
        }

        try (PreparedStatement pst = con.prepareStatement(
                """
                UPDATE tournoi
                SET nom = ?, annee = ?, nb_de_rondes = ?, duree_match = ?, nb_joueurs_equipe = ?
                WHERE id = ?
                """
        )) {
            pst.setString(1, nom);
            pst.setInt(2, annee);
            pst.setInt(3, nbDeRondes);
            pst.setInt(4, dureeMatch);
            pst.setInt(5, nbJoueursEquipe);
            pst.setInt(6, getId());
            pst.executeUpdate();
        }
    }

    /* =======================
       REQUÊTES STATIQUES
       ======================= */

    /** Récupère tous les tournois */
    public static List<Tournoi> tousLesTournois(Connection con) throws SQLException {
        List<Tournoi> res = new ArrayList<>();

        try (PreparedStatement pst = con.prepareStatement(
                """
                SELECT id, nom, annee, nb_de_rondes, duree_match, nb_joueurs_equipe
                FROM tournoi
                ORDER BY annee DESC
                """
        )) {
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

    /** Cherche un tournoi par id */
    public static Tournoi chercherParId(Connection con, int id) throws SQLException {
        try (PreparedStatement pst = con.prepareStatement(
                """
                SELECT id, nom, annee, nb_de_rondes, duree_match, nb_joueurs_equipe
                FROM tournoi
                WHERE id = ?
                """
        )) {
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return new Tournoi(
                            rs.getInt("id"),
                            rs.getString("nom"),
                            rs.getInt("annee"),
                            rs.getInt("nb_de_rondes"),
                            rs.getInt("duree_match"),
                            rs.getInt("nb_joueurs_equipe")
                    );
                }
            }
        }
        return null;
    }



    /* =======================
       GETTERS / SETTERS
       ======================= */

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getAnnee() {
        return annee;
    }

    public void setAnnee(int annee) {
        this.annee = annee;
    }

    public int getNbDeRondes() {
        return nbDeRondes;
    }

    public void setNbDeRondes(int nbDeRondes) {
        this.nbDeRondes = nbDeRondes;
    }

    public int getDureeMatch() {
        return dureeMatch;
    }

    public void setDureeMatch(int dureeMatch) {
        this.dureeMatch = dureeMatch;
    }

    public int getNbJoueursEquipe() {
        return nbJoueursEquipe;
    }

    public void setNbJoueursEquipe(int nbJoueursEquipe) {
        this.nbJoueursEquipe = nbJoueursEquipe;
    }

    @Override
    public String toString() {
        return nom + " (" + annee + ")";
    }
}
