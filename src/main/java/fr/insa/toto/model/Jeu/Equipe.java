
package fr.insa.toto.model.Jeu;

import fr.insa.toto.model.Jeu.Joueur;
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


public class Equipe extends ClasseMiroir implements Serializable {

    private static final long serialVersionUID = 1L;

    private int score;
    private Ronde ronde; 

    
    
    
    /**
     * pour nouvelle équipe en mémoire
     */
  
public Equipe(int score, Ronde ronde) {
    super();
    this.score = score;
    this.ronde = ronde;
}

    /**
     * pour équipe récupérée de la base de données
     */
  
    
  public Equipe(int id, int score, Ronde ronde) {
    super(id);
    this.score = score;
    this.ronde = ronde;
}
  
  
 @Override
public Statement saveSansId(Connection con) throws SQLException {
    PreparedStatement insert = con.prepareStatement(
            "insert into equipe (score, idronde) values (?, ?)",
            PreparedStatement.RETURN_GENERATED_KEYS);

    insert.setInt(1, this.score);
    insert.setInt(2, this.ronde.getId());  

    insert.executeUpdate();
    return insert;
}
    
   
    public static List<Equipe> creerEquipes(
        Connection con,
        Ronde ronde) throws SQLException {

    // 1) Taille des équipes grâce au tournoi
    int tailleEquipe = ronde.getTournoi().getNb_joueurs_equipe();

    // 2) On récupère tous les joueurs existants
    List<Joueur> joueurs = Joueur.tousLesJoueur(con);

    // 3) Mélanger pour que ce soit aléatoire
    Collections.shuffle(joueurs);

    List<Equipe> equipesCreees = new ArrayList<>();

    // 4) Nombre d’équipes complètes possibles
    int nbEquipesCompletes = joueurs.size() / tailleEquipe;

    try {
        con.setAutoCommit(false);

        // 5) Préparer l’insertion dans composition
        try (PreparedStatement pstCompo = con.prepareStatement(
                "insert into composition (idequipe,idjoueur) values (?,?)")) {

            int indexJoueur = 0;

            for (int i = 0; i < nbEquipesCompletes; i++) {

                // Créer une nouvelle équipe pour cette ronde
                // Score = 0, ronde = l’objet passé en paramètre
                Equipe e = new Equipe(0, ronde);
                e.saveInDB(con); // insère en BD et récupère l'id
                equipesCreees.add(e);

                // Remplir l’équipe avec 'tailleEquipe' joueurs
                for (int k = 0; k < tailleEquipe; k++) {
                    Joueur j = joueurs.get(indexJoueur++);

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


public static List<Equipe> toutesLesEquipes(Connection con) throws SQLException {
    List<Equipe> res = new ArrayList<>();

    try (PreparedStatement pst = con.prepareStatement(
            "select id, score, idronde from equipe")) {

        try (ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {

                int idEquipe = rs.getInt("id");
                int score = rs.getInt("score");
                int idRonde = rs.getInt("idronde");

                //  On doit reconstruire la Ronde correspondant à idRonde
                Ronde ronde = Ronde.chercherRondeParId(con, idRonde);

                res.add(new Equipe(
                        idEquipe,
                        score,
                        ronde
                ));
            }
        }
    }
    return res;
}
 

    public void SuppEquipe(Connection con) throws SQLException {
    if (this.getId() == -1) {
        throw new ClasseMiroir.EntiteNonSauvegardee();
    }
    try {
        con.setAutoCommit(false);

        // On supprimee les lignes de composition qui utilisent cette équipe
        try (PreparedStatement pst = con.prepareStatement(
                "delete from composition where idequipe = ?")) {
            pst.setInt(1, this.getId());
            pst.executeUpdate();
        }

        // Puis supprimer l'équipe elle-même
        try (PreparedStatement pst = con.prepareStatement(
                "delete from equipe where id = ?")) {
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
   public void sauvegarderScore(Connection con) throws SQLException {
    if (this.getId() == -1) {
        throw new ClasseMiroir.EntiteNonSauvegardee();
    }

    try (PreparedStatement pst = con.prepareStatement(
            "UPDATE equipe SET score = ? WHERE id = ?")) {
        pst.setInt(1, this.score);
        pst.setInt(2, this.getId());
        pst.executeUpdate();
    }
}
   public void ajouterScoreAuxJoueurs(Connection con, int points) throws SQLException {

    String sql = "UPDATE joueur SET score = score + ? "
               + "WHERE id IN (SELECT idjoueur FROM composition WHERE idequipe = ?)";

    try (PreparedStatement pst = con.prepareStatement(sql)) {
        pst.setInt(1, points);
        pst.setInt(2, this.getId());
        pst.executeUpdate();
    }
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

   
    public Ronde getRonde() {
        return ronde;
    }
    
    
    public int getNb_joueurs(){
        int nb_joueurs_equipe = Tournoi.getNb_joueurs_equipe();
        return nb_joueurs_equipe;
        
    }
   

}
