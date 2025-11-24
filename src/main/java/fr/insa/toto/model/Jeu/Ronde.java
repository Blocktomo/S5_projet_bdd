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
        
    }
    
    public Ronde (int idronde, int terminer){
            super(idronde);
            this.terminer = terminer;
    }
    
    public Ronde (int terminer, Tournoi tournoi){
        this.terminer = terminer;
        this.tournoi= tournoi;
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
   /* 
    public static List<Ronde> creerRondes{
            Connection con,
            int idMatch,
            int idJoueur,
        List<Joueur> joueurs = Joueur.tousLesJoueur (con);
        
    }
    */

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
