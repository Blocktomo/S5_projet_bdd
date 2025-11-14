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
package fr.insa.toto.model.GestionRH;

import fr.insa.toto.model.Jeu.Matchs;
import fr.insa.beuvron.utils.database.ConnectionSimpleSGBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author thomas
 */
public class BdDTest {

    //V1 et V2 était les tests du prof (loisirs, utilisateurs,etc.)  
    
    public static void createBdDTestV3(Connection con) throws SQLException {
        List<Joueur> players = List.of(
                new Joueur("Toto", "S", 180),
                new Joueur("Titi", "J", 160),
                new Joueur("Tutu", null, -1),
                new Joueur("Toti", null, 170),
                new Joueur("Tuti", "J", 190)
        );
        for (var u : players) {
            u.saveInDB(con);
        }
        List<Matchs> matchs = List.of(
                new Matchs(1),
                new Matchs(1)
        );
        for (var mat : matchs) {
            mat.saveInDB(con);
        }
        List<Equipe> equipe = List.of(
                new Equipe(1,10,1),
                new Equipe(2,15,1),
                new Equipe(1,15,2),
                new Equipe(2,5,2)
        );
        for (var eq : equipe) {
            eq.saveInDB(con);
        }
        int[][] composition = new int[][]{
            {1, 1},
            {1, 2},
            {2, 3},
            {2, 4},
            {3, 5},
            {3, 3},
            {4, 4},
            {4, 2},};
        try (PreparedStatement compo = con.prepareStatement(
                "insert into composition (idequipe,idjoueur) values (?,?)")) {
            for (int[] cmp : composition) {
                compo.setInt(1, cmp[0]);
                compo.setInt(2, cmp[1]);
                compo.executeUpdate();
            }
        }
    }

    public static void main(String[] args) {
        try (Connection con = ConnectionSimpleSGBD.defaultCon()) {
            GestionBdD.razBdd(con);
            createBdDTestV3(con);
        } catch (SQLException ex) {
            throw new Error(ex);
        }
    }

}