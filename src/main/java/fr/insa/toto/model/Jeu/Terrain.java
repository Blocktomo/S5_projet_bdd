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

/**
 *
 * @author tbeverly01
 */
public class Terrain extends ClasseMiroir {

    private boolean libre;
    private String nom_terrain = "";

    public Terrain(boolean libre) {
        this.libre = libre;
        Tournoi.addTerrain(this);
    }

    public Terrain(boolean libre, String nom_terrain) {
        this(libre);
        this.nom_terrain = nom_terrain;
        Tournoi.addTerrain(this);
    }

    /**
     * pour récupérer de la BDD un terrain
     * si le terrain n'a pas de nom, la valeur de nom_terrain est "".
     */
    public Terrain(int idTerrain, boolean libre, String nom_terrain) {
        super(idTerrain);
        this.libre = libre;
        this.nom_terrain = nom_terrain; //proposition : nom_terrain peut être NULL.
        Tournoi.addTerrain(this);
    }

    @Override
    public Statement saveSansId(Connection con) throws SQLException {
        PreparedStatement insert = con.prepareStatement(
                "insert into terrain (libre, nom_terrain) values (?, ?)",
                PreparedStatement.RETURN_GENERATED_KEYS);
        insert.setBoolean(1, this.isLibre());
        insert.setString(2, this.getNom_terrain());
        insert.executeUpdate();
        return insert;
    }

    /* 
    public static List<Ronde> creerRondes{
            Connection con,
            int idMatch,
            int idJoueur,
        List<Joueur> joueurs = Joueur.tousLesJoueur (con);
        
    }
     */

    public boolean isLibre() {
        return libre;
    }

    public String getNom_terrain() {
        return nom_terrain;
    }

    public void setLibre(boolean libre) {
        this.libre = libre;
    }

    public void setNom_terrain(String nom_terrain) {
        this.nom_terrain = nom_terrain;
    }

    

}
