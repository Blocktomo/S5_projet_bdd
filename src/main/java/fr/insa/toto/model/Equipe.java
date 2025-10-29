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
package fr.insa.toto.model;

import fr.insa.beuvron.utils.ConsoleFdB;
import fr.insa.beuvron.utils.database.ClasseMiroir;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author francois
 */
public class Equipe extends ClasseMiroir implements Serializable {

    private static final long serialVersionUID = 1L;

    private int num;
    private int score;
    private int idmatch;

    /**
     * pour nouvel utilisateur en mémoire
     */
    public Equipe(int num, int score, int idmatch) {
        super();
        this.num = num;
        this.score = score;
        this.idmatch = idmatch;
    }

    /**
     * pour utilisateur récupéré de la base de données
     */
    public Equipe(int id, int num, int score, int idmatch) {
        super(id);
        this.num = num;
        this.score = score;
        this.idmatch = idmatch;
    }

    @Override
    public Statement saveSansId(Connection con) throws SQLException {
        PreparedStatement insert = con.prepareStatement(
                "insert into equipe (num,score,idmatch) values (?,?,?)",
                PreparedStatement.RETURN_GENERATED_KEYS);
        insert.setInt(1, this.getNum());
        insert.setInt(2, this.getScore());
        insert.setInt(3, getIdmatch());
        insert.executeUpdate();
        return insert;
    }

    public static List<Equipe> tousLesUtilisateur(Connection con) throws SQLException {
        List<Equipe> res = new ArrayList<>();
        try (PreparedStatement pst = con.prepareStatement("select id,surnom,pass,role from utilisateur")) {
            try (ResultSet allU = pst.executeQuery()) {
                while (allU.next()) {
                    res.add(new Equipe(allU.getInt("id"), allU.getInt("num"),
                            allU.getInt("score"), allU.getInt("idmatch")));
                }
            }
        }
        return res;
    }
    /*//TODO peut-être implémanter ceci
    public static Optional<Equipe> findBySurnomPass(Connection con,String surnom,String pass) throws SQLException {
        try (PreparedStatement pst = con.prepareStatement(
                "select id,role from utilisateur where surnom = ? and pass = ?")) {
            pst.setString(1, surnom);
            pst.setString(2, pass);
            ResultSet res = pst.executeQuery();
            if (res.next()) {
                int id = res.getInt(1);
                int role = res.getInt(2);
                return Optional.of(new Equipe(id,surnom, pass, role));
            } else {
                return Optional.empty();
            }

        }
    }*/

    /**
     * supprime l'utilisateur de la BdD. Attention : supprime d'abord les
     * éventuelles dépendances.
     *
     * @param con
     * @throws SQLException
     *//*    //TODO à implémenter
    public void deleteInDB(Connection con) throws SQLException {
        if (this.getId() == -1) {
            throw new ClasseMiroir.EntiteNonSauvegardee();
        }
        try {
            con.setAutoCommit(false);
            try (PreparedStatement pst = con.prepareStatement(
                    "delete from pratique where idutilisateur = ?")) {
                pst.setInt(1, this.getId());
                pst.executeUpdate();
            }
            try (PreparedStatement pst = con.prepareStatement(
                    "delete from apprecie where u1 = ?")) {
                pst.setInt(1, this.getId());
                pst.executeUpdate();
            }
            try (PreparedStatement pst = con.prepareStatement(
                    "delete from apprecie where u2 = ?")) {
                pst.setInt(1, this.getId());
                pst.executeUpdate();
            }

            try (PreparedStatement pst = con.prepareStatement(
                    "delete from utilisateur where id = ?")) {
                pst.setInt(1, this.getId());
                pst.executeUpdate();
            }
            this.entiteSupprimee();
            con.commit();
        } catch (SQLException ex) {
            con.rollback();
            throw ex;
        } finally {
            con.setAutoCommit(true);
        }
    }

    public static Equipe entreeConsole() {
        String nom = ConsoleFdB.entreeString("surnom de l'utilisateur : ");
        String pass = ConsoleFdB.entreeString("password : ");
        return new Equipe(nom, pass, 2);
    }*/

    /**
     * @return the NUM
     */
    public int getNum() {
        return num;
    }

    /**
     * @param num the num to set
     */
    public void setNum(int num) {
        this.num = num;
    }

    /**
     * @return the score
     */
    public int getScore() {
        return score;
    }

    /**
     * @param score the score to set
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * @return the idmatch
     */
    public int getIdmatch() {
        return idmatch;
    }

    /**
     * @param idmatch the idmatch to set
     */
    public void setIdmatch(int idmatch) {
        this.idmatch = idmatch;
    }

}
