
package fr.insa.toto.model.Jeu;

import fr.insa.toto.model.GestionRH.*;
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
import java.util.Collections;


public class Tournoi extends ClasseMiroir {

    private String nom;
    private int nb_de_rondes;
    private int duree_match;
    private int nb_joueurs_equipe;
    private int annee;
    
    
    /**
     * pour nouveau tournoi en mémoire
     */
    public Tournoi(String nom, int nb_de_rondes, int duree_match, int nb_joueurs_equipe, int annee) {    
        this.nom = nom;
        this.nb_de_rondes = nb_de_rondes;
        this.duree_match = duree_match;
        this.nb_joueurs_equipe = nb_joueurs_equipe;
        this.annee = annee;
    }

    /**
     * pour tournoi récupéré de la base de données
     */
    

    public Tournoi( int id, String nom, int nb_de_rondes, int duree_match, int nb_joueurs_equipe, int annee) {
        super(id);
        this.nom = nom;
        this.nb_de_rondes = nb_de_rondes;
        this.duree_match = duree_match;
        this.nb_joueurs_equipe = nb_joueurs_equipe;
        this.annee = annee;
    }

    @Override
    public Statement saveSansId(Connection con) throws SQLException {
        PreparedStatement insert = con.prepareStatement(
                    "insert into tournoi (nom, nb_de_rondes, duree_match, nb_joueurs_equipe, annee) values (?,?,?,?,?)",
                PreparedStatement.RETURN_GENERATED_KEYS);
        insert.setString(1, this.getNom());
        insert.setInt(2, this.getNb_de_rondes());
        insert.setInt(3, getDuree_match());
        insert.setInt(4, getNb_joueurs_equipe());
        insert.setInt(5, getAnnee());
        insert.executeUpdate();
        return insert;
    } 

    public static List<Tournoi> tousLesTournois(Connection con) throws SQLException {
        List<Tournoi> res = new ArrayList<>();
        try (PreparedStatement pst = con.prepareStatement("select id, nom, nb_de_rondes, duree_match, nb_joueurs_equipe, annee from tournoi")) {
            try (ResultSet allT = pst.executeQuery()) {
                while (allT.next()) {
                    res.add(new Tournoi(allT.getInt("id"), allT.getString("nom"),
                            allT.getInt("nb_de_rondes"), allT.getInt("duree_match"), 
                            allT.getInt("nb_joueurs_equipe"), allT.getInt("annee")));
                }
            }
        }
        return res;
    }
    
    public static Tournoi entreeConsole() {
        String nom = ConsoleFdB.entreeString("nom du tournoi : ");
        int nb_de_rondes = ConsoleFdB.entreeInt("combien de rondes pour ce tournoi? Votre choix :  ");
        int duree_match = ConsoleFdB.entreeInt("combien de temps durent les matchs (nombre entier, donc en minutes). Votre choix : ");
        int nb_joueurs_equipe = ConsoleFdB.entreeInt("combien de joueurs par équipe? Votre choix : ");
        int annee = ConsoleFdB.entreeInt("quelle est l'année de ce tournoi?");

        return new Tournoi(nom, nb_de_rondes, duree_match, nb_joueurs_equipe, annee);
    }

    public String getNom() {
        return nom;
    }

    public int getNb_de_rondes() {
        return nb_de_rondes;
    }

    public int getDuree_match() {
        return duree_match;
    }

    public int getNb_joueurs_equipe() {
        return nb_joueurs_equipe;
    }

    public int getAnnee() {
        return annee;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setNb_de_rondes(int nb_de_rondes) {
        this.nb_de_rondes = nb_de_rondes;
    }

    public void setDuree_match(int duree_match) {
        this.duree_match = duree_match;
    }

    public void setNb_joueurs_equipe(int nb_joueurs_equipe) {
        this.nb_joueurs_equipe = nb_joueurs_equipe;
    }

    public void setAnnee(int annee) {
        this.annee = annee;
    }
    
    
   

}
