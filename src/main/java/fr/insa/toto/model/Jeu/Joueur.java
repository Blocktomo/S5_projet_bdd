
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
 * @author francois
 */
public class Joueur extends ClasseMiroir implements Serializable {

    private static final long serialVersionUID = 1L;

    private String surnom;
    private String categorie;
    private double taillecm;
    private int score = 0;

    /**
     * pour nouveau joueur en mémoire
     */
    public Joueur(int id, String surnom, String categorie, double taillecm, int score) {
    super(id);
    this.surnom = surnom;
    this.categorie = categorie;
    this.taillecm = taillecm;
    this.score = score;
    Tournoi.addJoueur(this);
    }
 //TODO : ajouter le traitement de la valeur "null" pour les différentes variables

    /**
     * pour utilisateur récupéré de la base de données
     */
   public Joueur(String surnom, String categorie, double taillecm) {
    super();
    this.surnom = surnom;
    this.categorie = categorie;
    this.taillecm = taillecm;
    this.score = 0;
    Tournoi.addJoueur(this);
}
    
    @Override
    public Statement saveSansId(Connection con) throws SQLException {
                PreparedStatement insert = con.prepareStatement(
            "insert into joueur (surnom,categorie,taillecm,score) values (?,?,?,?)",
            PreparedStatement.RETURN_GENERATED_KEYS);

        insert.setString(1, this.surnom);
        insert.setString(2, this.categorie);
        if(this.taillecm == -1){
            insert.setNull(3, Types.DOUBLE);
        } else {
            insert.setDouble(3, this.taillecm);
        }
        insert.setInt(4, this.score);
        insert.executeUpdate();
        return insert;
    }

  public static List<Joueur> tousLesJoueur(Connection con) throws SQLException {
    List<Joueur> res = new ArrayList<>();
    try (PreparedStatement pst = con.prepareStatement(
        "select id,surnom,categorie,taillecm,score from joueur")) {
        try (ResultSet allU = pst.executeQuery()) {
            while (allU.next()) {
                res.add(new Joueur(
                    allU.getInt("id"),
                    allU.getString("surnom"),
                    allU.getString("categorie"),
                    allU.getDouble("taillecm"),
                    allU.getInt("score")
                ));
            }
        }
    }

    return res;
} 
/* TODO : voir si c'est utile
    public static Optional<Joueur> findBySurnomPass(Connection con,String surnom,String pass) throws SQLException {
        try (PreparedStatement pst = con.prepareStatement(
                "select id,role from utilisateur where surnom = ? and pass = ?")) {
            pst.setString(1, surnom);
            pst.setString(2, pass);
            ResultSet res = pst.executeQuery();
            if (res.next()) {
                int id = res.getInt(1);
                int role = res.getInt(2);
                return Optional.of(new Joueur(id,surnom, pass, role));
            } else {
                return Optional.empty();
            }

        }
    }*/

    /**
     * supprime l'utilisateur de la BdD. Attention : supprime d'abord les
     * éventuelles dépendances.
     *
     * @param con
     * @throws SQLException
     */ //TODO : éditer cela en fonction des dépendances de joueur.
    public void deleteInDB(Connection con) throws SQLException {
        if (this.getId() == -1) {
            throw new ClasseMiroir.EntiteNonSauvegardee();
        }
        try {
            con.setAutoCommit(false);

            try (PreparedStatement pst = con.prepareStatement(
                    "delete from joueur where id = ?")) {
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

    public static Joueur entreeConsole() {
        String nom = ConsoleFdB.entreeString("surnom du joueur : ");
        String categorie = ConsoleFdB.entreeString("categorie (S/J/null): ");
        double taillecm = ConsoleFdB.entreeDouble("taillecm du joueur (double) : ");
        return new Joueur(nom, categorie, taillecm);
    }

    /**
     * @return the surnom
     */
    public String getSurnom() {
        return surnom;
    }

    /**
     * @param surnom the surnom to set
     */
    public void setSurnom(String surnom) {
        this.surnom = surnom;
    }

    /**
     * @return sa categorie
     */
    public String getCategorie() {
        return this.categorie;
    }

    /**
     * @param categorie la categorie du joueur (J, S)
     */
    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    /**
     * @return sa taillecm
     */
    public double getTaillecm() {
        return taillecm;
    }

    /**
     * @param taillecm du joueur (double)
     */
    public void setTaillecm(double taillecm) {
        this.taillecm = taillecm;
    }
    
    public int getScore() {
    return score;
}

public void setScore(int score) {
    this.score = score;
}

}
