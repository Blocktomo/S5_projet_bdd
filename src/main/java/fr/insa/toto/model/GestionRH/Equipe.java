
package fr.insa.toto.model.GestionRH;

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
import fr.insa.toto.model.Jeu.Ronde;


public class Equipe extends ClasseMiroir implements Serializable {

    private static final long serialVersionUID = 1L;

    private int num;
    private int score;
    private int idmatch;
    private Ronde ronde; 

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

    public Ronde getRonde() {
        return ronde;
    }
    
    
    public int getNb_joueurs(){
        int nb_joueurs_equipe = this.getRonde().getTournoi().getNb_joueurs_equipe();
        return nb_joueurs_equipe;
    }
    
    
    /**
     * pour nouvelle équipe en mémoire
     */
    public Equipe(int num, int score, int idmatch) {
        super();
        this.num = num;
        this.score = score;
        this.idmatch = idmatch;
    }

    /**
     * pour équipe récupérée de la base de données
     */
    public Equipe(int id, int num, int score, int idmatch) {
        super(id);
        this.num = num;
        this.score = score;
        this.idmatch = idmatch;
    }
    
    /**
     * pour nouvelle équipe en mémoire
     */
    public Equipe(int num, int score, int idmatch, Ronde ronde) {
        super();
        this.num = num;
        this.score = score;
        this.idmatch = idmatch;
        this.ronde = ronde;
    }

    /**
     * pour équipe récupérée de la base de données
     */
    public Equipe(int id, int num, int score, int idmatch, Ronde ronde) {
        super(id);
        this.num = num;
        this.score = score;
        this.idmatch = idmatch;
        this.ronde = ronde;
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
    
   
    public static List<Equipe> creerEquipes(
            Connection con,
            int idMatch,
            int tailleEquipe) throws SQLException {

        // 1) On récupère tous les joueurs existants
        List<Joueur> joueurs = Joueur.tousLesJoueur(con);

        // 2) On mélange la liste pour que ça soit aléatoire
        Collections.shuffle(joueurs);

        List<Equipe> equipesCreees = new ArrayList<>();

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
                    Equipe e = new Equipe(numEquipe, 0, idMatch);
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

public static List<Equipe> toutesLesEquipes(Connection con) throws SQLException {
    List<Equipe> res = new ArrayList<>();
    try (PreparedStatement pst = con.prepareStatement(
            "select id, num, score, idmatch from equipe")) {
        try (ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                res.add(new Equipe(
                        rs.getInt("id"),
                        rs.getInt("num"),
                        rs.getInt("score"),
                        rs.getInt("idmatch")
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
    
   

}
