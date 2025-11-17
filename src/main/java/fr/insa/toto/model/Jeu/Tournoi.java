
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
//TODO ---------------------------------------
    @Override
    public Statement saveSansId(Connection con) throws SQLException {
        PreparedStatement insert = con.prepareStatement(
                    "insert into tournoi (nom, nb_de_rondes, nb_de_rondes, duree_match, nb_joueurs_equipe, annee) values (?,?,?,?,?,?)",
                PreparedStatement.RETURN_GENERATED_KEYS);
        /*insert.setInt(1, this.getNum());
        insert.setInt(2, this.getScore());
        insert.setInt(3, getIdmatch());*/
        insert.executeUpdate();
        return insert;
    } //TODO : à finir
    
   
    public static List<Tournoi> creerEquipes(
            Connection con,
            int idMatch,
            int tailleEquipe) throws SQLException {

        // 1) On récupère tous les joueurs existants
        List<Joueur> joueurs = Joueur.tousLesJoueur(con);

        // 2) On mélange la liste pour que ça soit aléatoire
        Collections.shuffle(joueurs);

        List<Tournoi> equipesCreees = new ArrayList<>();

        // 3) On calcule le nombre d'équipes complètes possibles
        int nbEquipesCompletes = joueurs.size() / tailleEquipe;
        // Les joueurs restants (modulo) ne joueront pas cette ronde (trop de joueurs)
    

        try {
            con.setAutoCommit(false);

            // Prépare l'insert dans la table composition
            try (PreparedStatement pstCompo = con.prepareStatement(
                    "insert into composition (idequipe,idjoueur) values (?,?)")) {

                int indexJoueur = 0;

                for (int numEquipe = 1; numEquipe <= nbEquipesCompletes; numEquipe++) {
                    // Score de départ = 0
                    Tournoi e = new Tournoi(numEquipe, 0, idMatch);
                    e.saveInDB(con);  // va insérer dans equipe et remplir son id
                    equipesCreees.add(e);

                    // On met tailleEquipe joueurs dans cette équipe
                    for (int k = 0; k < tailleEquipe; k++) {
                        Joueur j = joueurs.get(indexJoueur++);

                        // insertion dans composition (idequipe, idjoueur)
                        pstCompo.setInt(1, e.getId());
                        pstCompo.setInt(2, j.getId());
                        pstCompo.executeUpdate();
                    }
                }
            }

            con.commit();
        } catch (SQLException ex) {
            con.rollback();
            throw ex;
        } finally {
            con.setAutoCommit(true);
        }

        return equipesCreees;
    }

    public static List<Tournoi> tousLesUtilisateur(Connection con) throws SQLException {
        List<Tournoi> res = new ArrayList<>();
        try (PreparedStatement pst = con.prepareStatement("select id,surnom,pass,role from utilisateur")) {
            try (ResultSet allU = pst.executeQuery()) {
                while (allU.next()) {
                    res.add(new Tournoi(allU.getInt("id"), allU.getInt("num"),
                            allU.getInt("score"), allU.getInt("idmatch")));
                }
            }
        }
        return res;
    }
    
   

}
