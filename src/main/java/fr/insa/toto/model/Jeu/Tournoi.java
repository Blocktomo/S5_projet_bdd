package fr.insa.toto.model.Jeu;

import fr.insa.beuvron.utils.ConsoleFdB;
import fr.insa.beuvron.utils.database.ClasseMiroir;
import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Représente un tournoi.
 * Version non statique, compatible console + web.
 */
public class Tournoi extends ClasseMiroir implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private String nom;
    private int annee;
    private int nbDeRondes;
    private int dureeMatch;
    private int nbJoueursEquipe;
    private int nbJoueursMax; // Nombre maximum de joueurs (0 = illimité)

    /** Nouveau tournoi */
    public Tournoi(String nom, int annee, int nbDeRondes,
                   int dureeMatch, int nbJoueursEquipe, int nbJoueursMax) {
        super();
        this.nom = nom;
        this.annee = annee;
        this.nbDeRondes = nbDeRondes;
        this.dureeMatch = dureeMatch;
        this.nbJoueursEquipe = nbJoueursEquipe;
        this.nbJoueursMax = nbJoueursMax;
    }

    /** Compatibilité ancienne version (nbjoueurs illimité) */
    public Tournoi(String nom, int annee, int nbDeRondes,
                   int dureeMatch, int nbJoueursEquipe) {
        this(nom, annee, nbDeRondes, dureeMatch, nbJoueursEquipe, 0);
    }

    /** Tournoi depuis la BDD */
    public Tournoi(int id, String nom, int annee, int nbDeRondes,
                   int dureeMatch, int nbJoueursEquipe, int nbJoueursMax) {
        super(id);
        this.nom = nom;
        this.annee = annee;
        this.nbDeRondes = nbDeRondes;
        this.dureeMatch = dureeMatch;
        this.nbJoueursEquipe = nbJoueursEquipe;
        this.nbJoueursMax = nbJoueursMax;
    }

    /* =======================
       METHODES DB
       ======================= */

    @Override
    public Statement saveSansId(Connection con) throws SQLException {
        PreparedStatement pst = con.prepareStatement(
                """
                INSERT INTO tournoi
                (nom, annee, nb_de_rondes, duree_match,
                 nb_joueurs_equipe, nb_joueurs_max)
                VALUES (?, ?, ?, ?, ?, ?)
                """,
                Statement.RETURN_GENERATED_KEYS
        );

        pst.setString(1, nom);
        pst.setInt(2, annee);
        pst.setInt(3, nbDeRondes);
        pst.setInt(4, dureeMatch);
        pst.setInt(5, nbJoueursEquipe);
        pst.setInt(6, nbJoueursMax);

        pst.executeUpdate();
        return pst;
    }

    public void updateInDB(Connection con) throws SQLException {
        if (getId() == -1) throw new ClasseMiroir.EntiteNonSauvegardee();

        try (PreparedStatement pst = con.prepareStatement(
                """
                UPDATE tournoi
                SET nom = ?, annee = ?, nb_de_rondes = ?, duree_match = ?,
                    nb_joueurs_equipe = ?, nb_joueurs_max = ?
                WHERE id = ?
                """
        )) {
            pst.setString(1, nom);
            pst.setInt(2, annee);
            pst.setInt(3, nbDeRondes);
            pst.setInt(4, dureeMatch);
            pst.setInt(5, nbJoueursEquipe);
            pst.setInt(6, nbJoueursMax);
            pst.setInt(7, getId());
            pst.executeUpdate();
        }
    }

    
    /* =======================
       REQUÊTES STATIQUES
       ======================= */

    public static List<Tournoi> tousLesTournois(Connection con) throws SQLException {
        List<Tournoi> res = new ArrayList<>();

        try (PreparedStatement pst = con.prepareStatement(
                """
                SELECT id, nom, annee, nb_de_rondes, duree_match,
                       nb_joueurs_equipe, nb_joueurs_max
                FROM tournoi
                ORDER BY annee DESC
                """
        )) {
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    res.add(new Tournoi(
                            rs.getInt("id"),
                            rs.getString("nom"),
                            rs.getInt("annee"),
                            rs.getInt("nb_de_rondes"),
                            rs.getInt("duree_match"),
                            rs.getInt("nb_joueurs_equipe"),
                            rs.getInt("nb_joueurs_max")
                    ));
                }
            }
        }
        return res;
    }

    public static Tournoi chercherParId(Connection con, int id) throws SQLException {
        try (PreparedStatement pst = con.prepareStatement(
                """
                SELECT id, nom, annee, nb_de_rondes, duree_match,
                       nb_joueurs_equipe, nb_joueurs_max
                FROM tournoi
                WHERE id = ?
                """
        )) {
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return new Tournoi(
                            rs.getInt("id"),
                            rs.getString("nom"),
                            rs.getInt("annee"),
                            rs.getInt("nb_de_rondes"),
                            rs.getInt("duree_match"),
                            rs.getInt("nb_joueurs_equipe"),
                            rs.getInt("nb_joueurs_max")
                    );
                }
            }
        }
        return null;
    }

    /* =======================
       MÉTHODES SUR INSTANCE
       ======================= */

    public int getNbJoueursInscrits(Connection con) throws SQLException {
        return Joueur.joueursDuTournoi(con, this).size();
    }

    /** Nombre de places restantes (Integer.MAX si illimité) */
    public int getPlacesRestantes(Connection con) throws SQLException {
        if (nbJoueursMax <= 0) {
            return Integer.MAX_VALUE;
        }
        return nbJoueursMax - getNbJoueursInscrits(con);
    }

    public boolean isComplet(Connection con) throws SQLException {
        if (nbJoueursMax <= 0) return false;
        return getNbJoueursInscrits(con) >= nbJoueursMax;
    }

    public boolean hasLimiteJoueurs() {
        return nbJoueursMax > 0;
    }

    /* =======================
       GETTERS / SETTERS
       ======================= */

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public int getAnnee() { return annee; }
    public void setAnnee(int annee) { this.annee = annee; }

    public int getNbDeRondes() { return nbDeRondes; }
    public void setNbDeRondes(int nbDeRondes) { this.nbDeRondes = nbDeRondes; }

    public int getDureeMatch() { return dureeMatch; }
    public void setDureeMatch(int dureeMatch) { this.dureeMatch = dureeMatch; }

    public int getNbJoueursEquipe() { return nbJoueursEquipe; }
    public void setNbJoueursEquipe(int nbJoueursEquipe) {
        this.nbJoueursEquipe = nbJoueursEquipe;
    }

    public int getNbJoueursMax() { return nbJoueursMax; }
    public void setNbJoueursMax(int nbJoueursMax) {
        this.nbJoueursMax = nbJoueursMax;
    }

    @Override
    public String toString() {
        return nom + " (" + annee + ")";
    }
    
    
    /* ====================
    pour MainConsole (utilisation désuète)
    ======================= */
    
    public void modifTournoi(Connection con) throws SQLException {
        boolean sortirDeLa = false;
        while (!sortirDeLa){
            int i=1;

            int choix = ConsoleFdB.entreeEntier("que souhaitez-vous modifier?\n"
                    + (i++) + ") le nom ("+this.getNom()+") \n"
                    + (i++) + ") l\'annee du tournoi ("+this.getAnnee()+") \n"
                    + (i++) + ") le nombres de rondes ("+this.getNbDeRondes() +") \n"
                    + (i++) + ") la duree des matchs (" + this.getDureeMatch() + ") \n"
                    + (i++) + ") le nombre de joueurs par equipe (" + this.getNbJoueursEquipe() + ") \n"
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
                            this.setNom(res_string);
                            commit_modif.setString(2, res_string);
                            break;
                        case 2 : 
                            nom_colonne = "annee";
                            res_int = ConsoleFdB.entreeInt("quelle est l'année de ce tournoi?");
                            this.setAnnee(res_int);
                            commit_modif.setInt(2, res_int);
                            break;
                        case 3 : 
                            nom_colonne = "nb_de_rondes";
                            res_int = ConsoleFdB.entreeInt("combien de rondes pour ce tournoi? Votre choix :  ");
                            this.setNbDeRondes(res_int);
                            commit_modif.setInt(2, res_int);    
                            break;
                        case 4 : 
                            nom_colonne = "duree_match";
                            res_int = ConsoleFdB.entreeInt("combien de temps durent les matchs (nombre entier, donc en minutes). Votre choix : ");
                            this.setDureeMatch(res_int);
                            commit_modif.setInt(2, res_int);
                            break;
                        case 5 : 
                            nom_colonne = "nb_joueurs_equipe";
                            res_int = ConsoleFdB.entreeInt("combien de joueurs par équipe? Votre choix : ");
                            this.setNbJoueursEquipe(res_int);
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
    
}
