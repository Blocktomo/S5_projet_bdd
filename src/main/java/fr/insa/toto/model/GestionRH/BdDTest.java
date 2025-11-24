

package fr.insa.toto.model.GestionRH;

import fr.insa.toto.model.Jeu.Equipe;
import fr.insa.toto.model.Jeu.Joueur;
import fr.insa.toto.model.Jeu.Matchs;
import fr.insa.beuvron.utils.database.ConnectionSimpleSGBD;
import fr.insa.toto.model.Jeu.Tournoi;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;


public class BdDTest {

    //V1 et V2 était les tests du prof (loisirs, utilisateurs,etc.)  
    
    public static void createBdDTestV3(Connection con) throws SQLException {
        Tournoi tournoi1 = new Tournoi("Mondial 2025", 10, 90, 2, 2025);
        List<Joueur> players = List.of(
                new Joueur("Pierre", "S", 180),
                new Joueur("Ahmed", "J", 160),
                new Joueur("Arthur", null, 177),
                new Joueur("Thomas", null, 170),
                new Joueur("Rayan", "J", 190)
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
              /*  new Equipe(1,10,1),
                new Equipe(2,15,1),
                new Equipe(1,15,2),
                new Equipe(2,5,2)*/
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
 public static void createBdDTestV4(Connection con) throws SQLException {
        Tournoi tournoi1 = new Tournoi("Mondial 2025", 10, 90, 2, 2025);
        
        List<Joueur> players = List.of(
                new Joueur("Pierre", "S", 180),
                new Joueur("Ahmed", "J", 160),
                new Joueur("Arthur", null, 177),
                new Joueur("Thomas", null, 170),
                new Joueur("Rayan", "J", 190)
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
                
        );
        for (var eq : equipe) {
            eq.saveInDB(con);
        }
        int[][] composition = new int[][]{

            };
        try (PreparedStatement compo = con.prepareStatement(
                "insert into composition (idequipe,idjoueur) values (?,?)")) {
            for (int[] cmp : composition) {
                compo.setInt(1, cmp[0]);
                compo.setInt(2, cmp[1]);
                compo.executeUpdate();
            }
        }
    }
 
}
