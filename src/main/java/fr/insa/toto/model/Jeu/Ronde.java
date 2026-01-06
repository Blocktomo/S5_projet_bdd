package fr.insa.toto.model.Jeu;

import fr.insa.beuvron.utils.database.ClasseMiroir;
import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Ronde extends ClasseMiroir implements Serializable {

    private static final long serialVersionUID = 1L;

    private int terminer;        // 0 = non initiée, 1 = en cours, 2 = terminée
    private Tournoi tournoi;

    /* =======================
       CONSTRUCTEURS
       ======================= */

    public Ronde(int terminer, Tournoi tournoi) {
        super();
        this.terminer = terminer;
        this.tournoi = tournoi;
    }

    public Ronde(int id, int terminer, Tournoi tournoi) {
        super(id);
        this.terminer = terminer;
        this.tournoi = tournoi;
    }

    /* =======================
       PERSISTENCE
       ======================= */

    @Override
    public Statement saveSansId(Connection con) throws SQLException {
        PreparedStatement pst = con.prepareStatement(
                """
                INSERT INTO ronde (terminer, idtournoi)
                VALUES (?, ?)
                """,
                Statement.RETURN_GENERATED_KEYS
        );
        pst.setInt(1, terminer);
        pst.setInt(2, tournoi.getId());
        pst.executeUpdate();
        return pst;
    }

    public static void creerRondesVides(Tournoi tournoi, Connection con) throws SQLException {
        for (int i = 0; i < tournoi.getNbDeRondes(); i++) {
            new Ronde(0, tournoi).saveInDB(con);
        }
    }

    /* =======================
       REQUÊTES STATIQUES
       ======================= */

    public static Ronde chercherRondeParId(Connection con, int idRonde) throws SQLException {
        try (PreparedStatement pst = con.prepareStatement(
                """
                SELECT r.idronde, r.terminer,
                       t.id AS tid, t.nom, t.annee,
                       t.nb_de_rondes, t.duree_match,
                       t.nb_joueurs_equipe, t.nb_joueurs_max
                FROM ronde r
                JOIN tournoi t ON r.idtournoi = t.id
                WHERE r.idronde = ?
                """
        )) {
            pst.setInt(1, idRonde);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {

                    Tournoi tournoi = new Tournoi(
                            rs.getInt("tid"),
                            rs.getString("nom"),
                            rs.getInt("annee"),
                            rs.getInt("nb_de_rondes"),
                            rs.getInt("duree_match"),
                            rs.getInt("nb_joueurs_equipe"),
                            rs.getInt("nb_joueurs_max")
                    );

                    return new Ronde(
                            rs.getInt("idronde"),
                            rs.getInt("terminer"),
                            tournoi
                    );
                }
            }
        }
        return null;
    }

    public static List<Ronde> rondesDuTournoi(Connection con, Tournoi tournoi) throws SQLException {
        List<Ronde> res = new ArrayList<>();

        try (PreparedStatement pst = con.prepareStatement(
                """
                SELECT idronde, terminer
                FROM ronde
                WHERE idtournoi = ?
                ORDER BY idronde
                """
        )) {
            pst.setInt(1, tournoi.getId());

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    res.add(new Ronde(
                            rs.getInt("idronde"),
                            rs.getInt("terminer"),
                            tournoi
                    ));
                }
            }
        }
        return res;
    }
    
    public static List<Equipe> equipesRonde(Connection con, Ronde ronde) throws SQLException {
        List<Equipe> res = new ArrayList<>();
        
        List<Equipe> toutesLesEquipes = Equipe.toutesLesEquipes(con);
        
        for (Equipe equipe:toutesLesEquipes){
            if (equipe.getRonde().getId() == ronde.getId()){
                res.add(equipe);
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

            try (PreparedStatement pst = con.prepareStatement(
                    "DELETE FROM ronde WHERE id = ?")) {
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
       GETTERS
       ======================= */

    public int getIdronde() {
        return getId();
    }

    public int getTerminer() {
        return terminer;
    }

    public Tournoi getTournoi() {
        return tournoi;
    }
}
