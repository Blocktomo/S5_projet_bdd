package fr.insa.toto.model.Jeu;

import fr.insa.beuvron.utils.ConsoleFdB;
import fr.insa.beuvron.utils.database.ClasseMiroir;
import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Joueur extends ClasseMiroir implements Serializable {

    private static final long serialVersionUID = 1L;

    private String surnom;
    private String categorie;   // J ou S
    private double taillecm;
    private int score;

    /* =======================
       CONSTRUCTEURS
       ======================= */

    /** Nouveau joueur en mémoire */
    public Joueur(String surnom, String categorie, double taillecm) {
        super();
        this.surnom = surnom;
        this.categorie = categorie;
        this.taillecm = taillecm;
        this.score = 0;
    }

    /** Joueur récupéré depuis la BDD */
    public Joueur(int id, String surnom, String categorie, double taillecm, int score) {
        super(id);
        this.surnom = surnom;
        this.categorie = categorie;
        this.taillecm = taillecm;
        this.score = score;
    }

    /* =======================
       PERSISTENCE
       ======================= */

    @Override
    public Statement saveSansId(Connection con) throws SQLException {
        PreparedStatement pst = con.prepareStatement(
                """
                INSERT INTO joueur (surnom, categorie, taillecm, score)
                VALUES (?, ?, ?, ?)
                """,
                Statement.RETURN_GENERATED_KEYS
        );

        pst.setString(1, surnom);
        pst.setString(2, categorie);

        if (taillecm <= 0) {
            pst.setNull(3, Types.DOUBLE);
        } else {
            pst.setDouble(3, taillecm);
        }

        pst.setInt(4, score);
        pst.executeUpdate();
        return pst;
    }

    /* =======================
       REQUÊTES STATIQUES
       ======================= */

    public static List<Joueur> tousLesJoueurs(Connection con) throws SQLException {
        List<Joueur> res = new ArrayList<>();

        try (PreparedStatement pst = con.prepareStatement(
                "SELECT id, surnom, categorie, taillecm, score FROM joueur"
        )) {
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
    
    /** Cherche une joueur par id */
    public static Joueur chercherParId(Connection con, int id) throws SQLException {
        try (PreparedStatement pst = con.prepareStatement(
                """
                SELECT id, surnom, categorie, taillecm, score
                FROM joueur
                WHERE id = ?
                """
        )) {
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return new Joueur(
                            rs.getInt("id"),
                            rs.getString("surnom"),
                            rs.getString("categorie"),
                            rs.getDouble("taillecm"),
                            rs.getInt("score")
                    );
                }
            }
        }
        return null;
    }
    
    public static List<Joueur> joueursDuTournoi(Connection con, Tournoi tournoi) throws SQLException {
    List<Joueur> res = new ArrayList<>();

    try (PreparedStatement pst = con.prepareStatement(
        """
        SELECT j.id, j.surnom, j.categorie, j.taillecm, j.score
        FROM joueur j
        JOIN participation p ON j.id = p.idjoueur
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


    /* =======================
       SUPPRESSION
       ======================= */

   
    public void deleteInDB(Connection con) throws SQLException {
        if (getId() == -1) {
            throw new ClasseMiroir.EntiteNonSauvegardee();
        }

        try {
            con.setAutoCommit(false);

            // Supprimer d'abord les compositions
            try (PreparedStatement pst = con.prepareStatement(
                    "DELETE FROM composition WHERE idjoueur = ?")) {
                pst.setInt(1, getId());
                pst.executeUpdate();
            }

            // Puis le joueur
            try (PreparedStatement pst = con.prepareStatement(
                    "DELETE FROM joueur WHERE id = ?")) {
                pst.setInt(1, getId());
                pst.executeUpdate();
            }

            entiteSupprimee();
            con.commit();
        } catch (SQLException ex) {
            con.rollback();
            throw ex;
        } finally {
            con.setAutoCommit(true);
        }
    }

    /* =======================
       CONSOLE
       ======================= */

    public static Joueur entreeConsole() {
        String nom = ConsoleFdB.entreeString("Surnom du joueur : ");
        String cat = ConsoleFdB.entreeString("Catégorie (J/S) : ");
        double taille = ConsoleFdB.entreeDouble("Taille (cm) : ");
        return new Joueur(nom, cat, taille);
    }

    /* =======================
       GETTERS / SETTERS
       ======================= */

    public String getSurnom() {
        return surnom;
    }

    public void setSurnom(String surnom) {
        this.surnom = surnom;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public double getTaillecm() {
        return taillecm;
    }

    public void setTaillecm(double taillecm) {
        this.taillecm = taillecm;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}

