/*
 */
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


public class Terrain extends ClasseMiroir {

    private String nom;
    private int occupe; // 0 = libre, 1 = occupé

    // constructeur pour nouveau terrain
    public Terrain(String nom) {
        super();
        this.nom = nom;
        this.occupe = 0; // libre par défaut
    }

    // constructeur pour BDD
    public Terrain(int id, String nom, int occupe) {
        super(id);
        this.nom = nom;
        this.occupe = occupe;
    }

    @Override
    public Statement saveSansId(Connection con) throws SQLException {
        PreparedStatement pst = con.prepareStatement(
            "INSERT INTO terrain (nom, occupe) VALUES (?,?)",
            PreparedStatement.RETURN_GENERATED_KEYS
        );
        pst.setString(1, this.nom);
        pst.setInt(2, this.occupe);
        pst.executeUpdate();
        return pst;
    }

    public static List<Terrain> tousLesTerrains(Connection con) throws SQLException {
        List<Terrain> res = new ArrayList<>();

        try (PreparedStatement pst = con.prepareStatement("SELECT * FROM terrain")) {
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                res.add(new Terrain(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getInt("occupe")
                ));
            }
        }
        return res;
    }

    public void setOccupe(Connection con, boolean occupe) throws SQLException {
        this.occupe = occupe ? 1 : 0;

        try (PreparedStatement pst = con.prepareStatement(
                "UPDATE terrain SET occupe = ? WHERE id = ?")) {
            pst.setInt(1, this.occupe);
            pst.setInt(2, this.getId());
            pst.executeUpdate();
        }
    }
    
    public static List<Terrain> terrainsDuTournoi(Connection con, Tournoi tournoi) throws SQLException {
        List<Terrain> res = new ArrayList<>();

        try (PreparedStatement pst = con.prepareStatement(
            """
            SELECT ter.id, ter.nom, ter.occupe
            FROM terrain ter
            JOIN terrains_tournois p ON ter.id = p.idterrain
            WHERE p.idtournoi = ?
            """
        )) {
            pst.setInt(1, tournoi.getId());

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    res.add(new Terrain(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getInt("occupe")
                    ));
                }
            }
        }
        return res;
    }

    public void deleteInDB(Connection con) throws SQLException {
        if (this.getId() == -1) throw new ClasseMiroir.EntiteNonSauvegardee();

        try (PreparedStatement pst = con.prepareStatement(
                "DELETE FROM terrain WHERE id = ?")) {
            pst.setInt(1, this.getId());
            pst.executeUpdate();
        }
    }

    public String getNom() {
        return nom;
    }

    public int getOccupe() {
        return occupe;
    }
}