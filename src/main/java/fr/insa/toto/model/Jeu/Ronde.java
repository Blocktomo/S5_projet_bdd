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


import fr.insa.beuvron.utils.ConsoleFdB;
import fr.insa.beuvron.utils.database.ClasseMiroir;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
/**
 *
 * @author rdorgli01
 */
public class Ronde extends ClasseMiroir {
    
    private int terminer;
    private Tournoi tournoi;
    
    //Thomas : on va devoir supprimer ce constructeur, car il faut toujours spécifier un tournoi d'appartenance
    public Ronde(int terminer){
        this.terminer = terminer; 
        Tournoi.addRonde(this);
        
    }
    
    /**pour récupérer une ronde depuis la BDD
     * @param terminer : int // 0 ou 1
     */
    public Ronde (int idronde, int terminer){
            super(idronde);
            this.terminer = terminer;
            Tournoi.addRonde(this);
    }
    
    
    @Override
    public Statement saveSansId(Connection con) throws SQLException {
        PreparedStatement insert = con.prepareStatement(
                "insert into ronde (terminer) values (?)",
                PreparedStatement.RETURN_GENERATED_KEYS);
        insert.setInt(1, this.getTerminer());
        insert.executeUpdate();
        return insert;
    }
    
    
    public static Ronde chercherRondeParId(Connection con, int idronde) throws SQLException {
    try (PreparedStatement pst = con.prepareStatement(
            "select idronde, terminer from ronde where idronde = ?")) {
        pst.setInt(1, idronde);

        try (ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                return new Ronde(
                        rs.getInt("id"),
                        rs.getInt("terminer")
                );
            } 
        }
    }
    return null; // ou exception
}
    
    /**permet de récupérer les rondes depuis la BDD, en plus de Tournoi.getListe_rondes().
     */
    public static List<Ronde> toutesLesRondes(Connection con) throws SQLException {
        List<Ronde> res = new ArrayList<>();
        String query= "SElECT idronde, terminer FROM ronde ORDER BY idronde";
        
        try (PreparedStatement pst = con.prepareStatement(query)) {
            try(ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    res.add(new Ronde (rs.getInt("idronde"), rs.getInt("terminer")));
                }
            }
        }
        return res;
    }
    
    public void deleteInDB(Connection con) throws SQLException {
        if (this.getId()== -1 ){
            throw new ClasseMiroir.EntiteNonSauvegardee();
        }
        try {
            con.setAutoCommit(false);
            
            try (PreparedStatement pstMatches = con.prepareStatement(
                    "DELETE FROM matchs WHERE ronde = ?")) {
                pstMatches.setInt(1,this.getId());
                pstMatches.executeUpdate();
            }
            
            this.entiteSupprimee();
            con.commit();
        } catch (SQLException ex){
            con.rollback();
            throw ex;
        } finally {
            con.setAutoCommit(true);
        }
    }

    public int getTerminer() {
        return terminer;
    }

    public Tournoi getTournoi() {
        return tournoi;
    }

    
    public void setTerminer(int terminer) {
        this.terminer = terminer;
    }
    
   
    
    
}
