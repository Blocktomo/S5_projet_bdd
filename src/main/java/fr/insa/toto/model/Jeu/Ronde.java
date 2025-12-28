/*
Copyright 2000- Francois de Bertrand de Beuvron

This file is part of CoursBeuvron.

CoursBeuvron is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

CoursBeuvron is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with CoursBeuvron.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.insa.toto.model.Jeu;

import fr.insa.beuvron.utils.database.ClasseMiroir;
import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Ronde extends ClasseMiroir implements Serializable {

    private static final long serialVersionUID = 1L;

    private int terminer;        // 0 = en cours, 1 = terminée
    private Tournoi tournoi;

    /* =======================
       CONSTRUCTEURS
       ======================= */

    /** Nouvelle ronde en mémoire */
    public Ronde(int terminer, Tournoi tournoi) {
        super();
        this.terminer = terminer;
        this.tournoi = tournoi;
    }

    /** Ronde récupérée depuis la BDD */
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

    /* =======================
       REQUÊTES STATIQUES
       ======================= */

    public static Ronde chercherRondeParId(Connection con, int id) throws SQLException {
        try (PreparedStatement pst = con.prepareStatement(
                """
                SELECT r.id, r.terminer,
                       t.id AS tid, t.nom, t.annee,
                       t.nb_de_rondes, t.duree_match, t.nb_joueurs_equipe
                FROM ronde r
                JOIN tournoi t ON r.idtournoi = t.id
                WHERE r.id = ?
                """
        )) {
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    Tournoi tournoi = new Tournoi(
                            rs.getInt("tid"),
                            rs.getString("nom"),
                            rs.getInt("annee"),
                            rs.getInt("nb_de_rondes"),
                            rs.getInt("duree_match"),
                            rs.getInt("nb_joueurs_equipe")
                    );

                    return new Ronde(
                            rs.getInt("id"),
                            rs.getInt("terminer"),
                            tournoi
                    );
                }
            }
        }
        return null;
    }

    public static List<Ronde> toutesLesRondes(Connection con) throws SQLException {
        List<Ronde> res = new ArrayList<>();

        try (PreparedStatement pst = con.prepareStatement(
                """
                SELECT r.id, r.terminer,
                       t.id AS tid, t.nom, t.annee,
                       t.nb_de_rondes, t.duree_match, t.nb_joueurs_equipe
                FROM ronde r
                JOIN tournoi t ON r.idtournoi = t.id
                ORDER BY r.id
                """
        )) {
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Tournoi tournoi = new Tournoi(
                            rs.getInt("tid"),
                            rs.getString("nom"),
                            rs.getInt("annee"),
                            rs.getInt("nb_de_rondes"),
                            rs.getInt("duree_match"),
                            rs.getInt("nb_joueurs_equipe")
                    );

                    res.add(new Ronde(
                            rs.getInt("id"),
                            rs.getInt("terminer"),
                            tournoi
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
       GETTERS / SETTERS
       ======================= */

    public int getTerminer() {
        return terminer;
    }

    public void setTerminer(int terminer) {
        this.terminer = terminer;
    }

    public Tournoi getTournoi() {
        return tournoi;
    }
}

