
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


public class Tournoi { //suppression de "extends ClasseMiroir" car cela ne convenait pas

    private static String nom="Tournoi COPA AMERICANO 2027";
    private static int annee=2025;
    private static int nb_de_rondes=10;
    private static int duree_match=90;
    private static int nb_joueurs_equipe=2;
    private static List<Terrain> liste_terrains = new ArrayList<>();
    private static List<Joueur> liste_joueurs = new ArrayList<>(); //cette liste sera remplie avec la table "participe" joueur-tournoi
    private static List<Ronde> liste_rondes = new ArrayList<>();
    

//    @Override
    public static Statement saveSansId(Connection con) throws SQLException {
        PreparedStatement insert = con.prepareStatement(
                    "insert into tournoi (nom, annee, nb_de_rondes, duree_match, nb_joueurs_equipe) values (?,?,?,?,?)",
                PreparedStatement.RETURN_GENERATED_KEYS);
        insert.setString(1, getNom());
        insert.setInt(2, getAnnee());
        insert.setInt(3, getNb_de_rondes());
        insert.setInt(4, getDuree_match());
        insert.setInt(5, getNb_joueurs_equipe());
        
        insert.executeUpdate();
        return insert;
    } 
    
    /* //pas d'uitlité pour le moment pour cette méthode
    public final static void saveInDB(Connection con) throws SQLException {
        if (Tournoi.id != -1) {
            throw new EntiteDejaSauvegardee();
        }
        //Statement saveAllButId = Tournoi.saveSansId(con);
        try (ResultSet rid = saveAllButId.getGeneratedKeys()) {
            rid.next();
            this.id = rid.getInt(1);
            return this.id;
        }
    }*/

    public static void initTournoi(Connection con) throws SQLException {
        try (PreparedStatement pst = con.prepareStatement("select nom, annee, nb_de_rondes, duree_match, nb_joueurs_equipe from tournoi")) {
            try (ResultSet theT = pst.executeQuery()) {
                while (theT.next()) {
                    Tournoi.setNom(theT.getString("nom"));
                    Tournoi.setAnnee(theT.getInt("annee"));
                    Tournoi.setNb_de_rondes(theT.getInt("nb_de_rondes"));
                    Tournoi.setDuree_match(theT.getInt("duree_match"));
                    Tournoi.setNb_joueurs_equipe(theT.getInt("nb_joueurs_equipe"));
                }
            }
        }
        //TODO : get info from the tables like Terrain and Ronde and Joueur
    }
    
    public static void modifTournoi(Connection con) throws SQLException {
        boolean sortirDeLa = false;
        while (!sortirDeLa){
            int i=1;

            int choix = ConsoleFdB.entreeEntier("que souhaitez-vous modifier?\n"
                    + (i++) + ") le nom ("+Tournoi.getNom()+") \n"
                    + (i++) + ") l\'annee du tournoi ("+Tournoi.getAnnee()+") \n"
                    + (i++) + ") le nombres de rondes ("+Tournoi.getNb_de_rondes() +") \n"
                    + (i++) + ") la duree des matchs (" + Tournoi.getDuree_match()+") \n"
                    + (i++) + ") le nombre de joueurs par equipe (" + Tournoi.getNb_joueurs_equipe() + ") \n"
                    + "0) retour en arrière"
                    ); 
            try {
                con.setAutoCommit(false);
                PreparedStatement commit_modif = con.prepareStatement(
                    "update tournoi "
                            + "set ? = ?");
                String nom_colonne = "";
                String res_string = "";
                int res_int;
                switch (choix) {
                        case 1 :  
                            nom_colonne = "nom";
                            res_string = ConsoleFdB.entreeString("nom du tournoi : ");
                            Tournoi.setNom(res_string);
                            commit_modif.setString(2, res_string);
                            break;
                        case 2 : 
                            nom_colonne = "annee";
                            res_int = ConsoleFdB.entreeInt("quelle est l'année de ce tournoi?");
                            Tournoi.setAnnee(res_int);
                            commit_modif.setInt(2, res_int);
                            break;
                        case 3 : 
                            nom_colonne = "nb_de_rondes";
                            res_int = ConsoleFdB.entreeInt("combien de rondes pour ce tournoi? Votre choix :  ");
                            Tournoi.setNb_de_rondes(res_int);
                            commit_modif.setInt(2, res_int);    
                            break;
                        case 4 : 
                            nom_colonne = "duree_match";
                            res_int = ConsoleFdB.entreeInt("combien de temps durent les matchs (nombre entier, donc en minutes). Votre choix : ");
                            Tournoi.setDuree_match(res_int);
                            commit_modif.setInt(2, res_int);
                            break;
                        case 5 : 
                            nom_colonne = "nb_joueurs_equipe";
                            res_int = ConsoleFdB.entreeInt("combien de joueurs par équipe? Votre choix : ");
                            Tournoi.setNb_joueurs_equipe(res_int);
                            commit_modif.setInt(2, res_int);
                            break;
                        case 0 : sortirDeLa=true;
                            break;
                        default : sortirDeLa=true; break;
                }
                if (sortirDeLa==true){
                    con.rollback();
                }else{ //on ne fait le commit que si tout s'est bien passé.
                commit_modif.setString(1, nom_colonne);
                con.commit();
                }
            }catch (SQLException ex){
                con.rollback();
                throw new Error(ex) ;
            }finally{
                con.setAutoCommit(true);
            }
        }
    }
    
    public static void affichageTexte(){
        System.out.println("nom du tournoi : " + Tournoi.getNom()
                        + "annee : " + Tournoi.getAnnee()
                        + "nombres de rondes : " + Tournoi.getNb_de_rondes()
                        + "duree des matchs : " + Tournoi.getDuree_match()
                        + "nombre de joueurs par equipe : " + Tournoi.getNb_joueurs_equipe()
                );
    }

    public static String getNom() {
        return Tournoi.nom;
    }
    public static int getAnnee() {
        return Tournoi.annee;
    }
    public static int getNb_de_rondes() {
        return Tournoi.nb_de_rondes;
    }

    public static int getDuree_match() {
        return Tournoi.duree_match;
    }

    public static int getNb_joueurs_equipe() {
        return Tournoi.nb_joueurs_equipe;
    }
    
    public int getNb_terrains(){
        return Tournoi.liste_terrains.size();
    }

    public static void setNom(String nom) {
        Tournoi.nom = nom;
    }
    public static void setAnnee(int annee) {
        Tournoi.annee = annee;
    }
    public static void setNb_de_rondes(int nb_de_rondes) {
        Tournoi.nb_de_rondes = nb_de_rondes;
    }

    public static void setDuree_match(int duree_match) {
        Tournoi.duree_match = duree_match;
    }

    public static void setNb_joueurs_equipe(int nb_joueurs_equipe) {
        Tournoi.nb_joueurs_equipe = nb_joueurs_equipe;
    }    

    public static void setListe_terrains(List<Terrain> liste_terrains) {
        Tournoi.liste_terrains = liste_terrains;
    }
    
    public static void addTerrain(Terrain nouveau_terrain){
        Tournoi.liste_terrains.add(nouveau_terrain);
    }

    public static void setListe_joueurs(List<Joueur> liste_joueurs) {
        Tournoi.liste_joueurs = liste_joueurs;
    }
    
    public static void addJoueur(Joueur nouveauJoueur){
        Tournoi.liste_joueurs.add(nouveauJoueur);
    }

    public static void setListe_rondes(List<Ronde> liste_rondes) {
        Tournoi.liste_rondes = liste_rondes;
    }
    
    public static void addRonde(Ronde nouvelleRonde){
        Tournoi.liste_rondes.add(nouvelleRonde);
    }
    
}
