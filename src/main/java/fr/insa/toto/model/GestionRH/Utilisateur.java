
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

/**
 *
 * @author francois
 */
public class Utilisateur extends ClasseMiroir implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String surnom;
    private String pass;
    private int role; //role : 1 : utilisateur_standard ; 2 : administrateur

    /**
     * pour nouvel utilisateur en mémoire
     */
    public Utilisateur(String surnom, String pass, int role) {
        super();
        this.surnom = surnom;
        this.pass = pass;
        this.role = role;
    }

    /**
     * pour utilisateur récupéré de la base de données
     */
    public Utilisateur(int id, String surnom, String pass, int role) {
        super(id);
        this.surnom = surnom;
        this.pass = pass;
        this.role = role;
    }

    @Override
    public String toString() {
        return "Utilisateur{" + "id=" + this.getId() + "surnom=" + surnom + ", role=" + role + '}';
    }
    
    @Override
    public Statement saveSansId(Connection con) throws SQLException {
        PreparedStatement insert = con.prepareStatement(
                "insert into utilisateur (surnom,pass,role) values (?,?,?)",
                PreparedStatement.RETURN_GENERATED_KEYS);
        insert.setString(1, this.getSurnom());
        insert.setString(2, this.getPass());
        insert.setInt(3, getRole());
        insert.executeUpdate();
        return insert;
    }

    
    private static List<Utilisateur> fromResultSetToList(ResultSet users) throws SQLException {
        List<Utilisateur> res = new ArrayList<>();
        while (users.next()) {
            res.add(new Utilisateur(users.getInt("id"), users.getString("surnom"),
                    users.getString("pass"), users.getInt("role")));
        }
        return res;
        
    }
    
    public static List<Utilisateur> tousLesUtilisateur(Connection con) throws SQLException {
        List<Utilisateur> res = new ArrayList<>();
        try (PreparedStatement pst = con.prepareStatement("select id,surnom,pass,role from utilisateur")) {
            try (ResultSet allU = pst.executeQuery()) {
                return fromResultSetToList(allU);
            }
        }
    }
    
    public static Optional<Utilisateur> findBySurnomPass(Connection con, String surnom, String pass) throws SQLException {
        try (PreparedStatement pst = con.prepareStatement(
                "select id,role from utilisateur where surnom = ? and pass = ?")) {
            pst.setString(1, surnom);
            pst.setString(2, pass);
            ResultSet res = pst.executeQuery();
            if (res.next()) {
                int id = res.getInt(1);
                int role = res.getInt(2);
                return Optional.of(new Utilisateur(id, surnom, pass, role));
            } else {
                return Optional.empty();
            }
            
        }
    }
    

    /**
     * supprime l'utilisateur de la BdD. Attention : supprime d'abord les
     * éventuelles dépendances.
     *
     * @param con
     * @throws SQLException
     */
    public void deleteInDB(Connection con) throws SQLException {
        if (this.getId() == -1) {
            throw new ClasseMiroir.EntiteNonSauvegardee();
        }
        try {
            con.setAutoCommit(false);
            
            try (PreparedStatement pst = con.prepareStatement(
                    "delete from utilisateur where id = ?")) {
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
    
    public static Utilisateur entreeConsole() {
        String nom = ConsoleFdB.entreeString("surnom de l'utilisateur : ");
        String pass = ConsoleFdB.entreeString("password : ");
        int role = ConsoleFdB.entreeEntier("role (admin :1, standard: 2) : ");
        return new Utilisateur(nom, pass, 2);
    }
    
    public void modifierUtilisateurConsole(Connection con) {
        System.out.println("utilisateur à modifier : " + this.toString());
        String nouveauNom = ConsoleFdB.entreeString("nouveau surnom de l'utilisateur : ");
        String nouveauPass = ConsoleFdB.entreeString("nouveau MdP : ");
        int nouveauRole = ConsoleFdB.entreeEntier("nouveau role (admin :1, standard: 2) : ");
        try(PreparedStatement pst = con.prepareStatement("""
                                                              update utilisateur
                                                              set surnom = ?, pass = ?, role = ?
                                                              where id = ?""")) {
                pst.setString(1, nouveauNom);
                pst.setString(2, nouveauPass);                
                pst.setInt(3, nouveauRole);
                pst.setInt(4, this.getId());
                pst.executeUpdate();
        } catch (SQLException ex) {
        }
    }
    /**
     * @return the surnom
     */
    public String getSurnom() {
        if (this==null){
            return "Personne";
        }
        else{
            return surnom;
        }
    }

    /**
     * @param surnom the surnom to set
     */
    public void setSurnom(String surnom) {
        this.surnom = surnom;
    }

    /**
     * @return the pass
     */
    public String getPass() {
        return pass;
    }

    /**
     * @param pass the pass to set
     */
    public void setPass(String pass) {
        this.pass = pass;
    }

    /**
     * @return the role
     */
    public int getRole() {
        return role;
    }

    /**
     * @param role the role to set
     */
    public void setRole(int role) {
        this.role = role;
    }
    
}
