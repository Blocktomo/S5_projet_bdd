package fr.insa.toto.model.Jeu;

import fr.insa.beuvron.utils.database.ClasseMiroir;
import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Equipe extends ClasseMiroir implements Serializable {

    private static final long serialVersionUID = 1L;

    private int score;
    private Ronde ronde;

    /* =======================
       CONSTRUCTEURS
       ======================= */

    // Nouvelle équipe en mémoire
    public Equipe(int score, Ronde ronde) {
        super();
        this.score = score;
        this.ronde = ronde;
    }

    // Équipe depuis la base
    public Equipe(int id, int score, Ronde ronde) {
        super(id);
        this.score = score;
        this.ronde = ronde;
    }

    /* =======================
       PERSISTENCE
       ======================= */

    @Override
    public Statement saveSansId(Connection con) throws SQLException {
        PreparedStatement pst = con.prepareStatement(
                """
                INSERT INTO equipe (score, idronde)
                VALUES (?, ?)
                """,
                Statement.RETURN_GENERATED_KEYS
        );

        pst.setInt(1, score);
        pst.setInt(2, ronde.getId());
        pst.executeUpdate();
        return pst;
    }

    /* =======================
       MÉTIER
       ======================= */

    /**
     * Création automatique des équipes pour une ronde donnée
     */
    public static List<Equipe> creerEquipes(Connection con, Ronde ronde) throws SQLException {

        //  taille équipe depuis le tournoi de la ronde
        int tailleEquipe = ronde.getTournoi().getNbJoueursEquipe();

        List<Joueur> joueurs = Joueur.tousLesJoueurs(con);
        Collections.shuffle(joueurs);

        List<Equipe> equipes = new ArrayList<>();
        int nbEquipes = joueurs.size() / tailleEquipe;

        try {
            con.setAutoCommit(false);

            try (PreparedStatement pstCompo = con.prepareStatement(
                    "INSERT INTO composition (idequipe, idjoueur) VALUES (?, ?)")) {

                int index = 0;

                for (int i = 0; i < nbEquipes; i++) {
                    Equipe e = new Equipe(0, ronde);
                    e.saveInDB(con);
                    equipes.add(e);

                    for (int j = 0; j < tailleEquipe; j++) {
                        Joueur joueur = joueurs.get(index++);
                        pstCompo.setInt(1, e.getId());
                        pstCompo.setInt(2, joueur.getId());
                        pstCompo.executeUpdate();
                    }
                }
            }

            con.commit();
        } catch (SQLException ex) {
            con.rollback();
            throw ex;
        } finally {
            con.setAutoCommit(true);
        }

        return equipes;
    }

    /* =======================
       LECTURE
       ======================= */

    public static List<Equipe> toutesLesEquipes(Connection con) throws SQLException {
        List<Equipe> res = new ArrayList<>();

        try (PreparedStatement pst = con.prepareStatement(
                "SELECT id, score, idronde FROM equipe")) {

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    int score = rs.getInt("score");
                    int idRonde = rs.getInt("idronde");

                    Ronde ronde = Ronde.chercherRondeParId(con, idRonde);
                    res.add(new Equipe(id, score, ronde));
                }
            }
        }
        return res;
    }
    
    /** Cherche une équipe par id */
    public static Equipe chercherParId(Connection con, int id) throws SQLException {
        try (PreparedStatement pst = con.prepareStatement(
                """
                SELECT id, score, idronde
                FROM equipe
                WHERE id = ?
                """
        )) {
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    int idronde = rs.getInt("idronde"); 
                    Ronde rondeEquipe = Ronde.chercherRondeParId(con, idronde);
                    return new Equipe(
                            rs.getInt("id"),
                            rs.getInt("score"),
                            rondeEquipe
                    );
                }
            }
        }
        return null;
    }


    /* =======================
       SUPPRESSION
       ======================= */

    public void supprimer(Connection con) throws SQLException {
        if (getId() == -1) {
            throw new ClasseMiroir.EntiteNonSauvegardee();
        }

        try {
            con.setAutoCommit(false);

            try (PreparedStatement pst = con.prepareStatement(
                    "DELETE FROM composition WHERE idequipe = ?")) {
                pst.setInt(1, getId());
                pst.executeUpdate();
            }

            try (PreparedStatement pst = con.prepareStatement(
                    "DELETE FROM equipe WHERE id = ?")) {
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
       SCORE
       ======================= */

    public void sauvegarderScore(Connection con) throws SQLException {
        if (getId() == -1) {
            throw new ClasseMiroir.EntiteNonSauvegardee();
        }

        try (PreparedStatement pst = con.prepareStatement(
                "UPDATE equipe SET score = ? WHERE id = ?")) {
            pst.setInt(1, score);
            pst.setInt(2, getId());
            pst.executeUpdate();
        }
    }

    public void ajouterScoreAuxJoueurs(Connection con, int points) throws SQLException {
        try (PreparedStatement pst = con.prepareStatement(
                """
                UPDATE joueur
                SET score = score + ?
                WHERE id IN (
                    SELECT idjoueur FROM composition WHERE idequipe = ?
                )
                """)) {
            pst.setInt(1, points);
            pst.setInt(2, getId());
            pst.executeUpdate();
        }
    }
public static List<Equipe> creerEquipesPourTournoi(
        Connection con,
        Ronde ronde,
        Tournoi tournoi
) throws SQLException {

    int tailleEquipe = tournoi.getNbJoueursEquipe();

    // ✅ UNIQUEMENT joueurs du tournoi
    List<Joueur> joueurs = Joueur.joueursDuTournoi(con, tournoi);

    if (joueurs.size() < tailleEquipe * 2) {
        throw new SQLException("Pas assez de joueurs dans le tournoi");
    }

    Collections.shuffle(joueurs);

    List<Equipe> equipes = new ArrayList<>();
    int nbEquipes = joueurs.size() / tailleEquipe;

    try {
        con.setAutoCommit(false);

        try (PreparedStatement pstCompo = con.prepareStatement(
                "INSERT INTO composition (idequipe, idjoueur) VALUES (?, ?)"
        )) {

            int index = 0;

            for (int i = 0; i < nbEquipes; i++) {

                Equipe e = new Equipe(0, ronde);
                e.saveInDB(con);
                equipes.add(e);

                for (int j = 0; j < tailleEquipe; j++) {
                    Joueur joueur = joueurs.get(index++);
                    pstCompo.setInt(1, e.getId());
                    pstCompo.setInt(2, joueur.getId());
                    pstCompo.executeUpdate();
                }
            }
        }

        con.commit();

    } catch (SQLException ex) {
        con.rollback();
        throw ex;
    } finally {
        con.setAutoCommit(true);
    }

    return equipes;
}


    /* =======================
       GETTERS
       ======================= */

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Ronde getRonde() {
        return ronde;
    }
}

