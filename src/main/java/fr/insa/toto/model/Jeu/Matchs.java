
package fr.insa.toto.model.Jeu;

import java.util.List;
import java.util.ArrayList;
import java.sql.ResultSet;
import fr.insa.beuvron.utils.database.ClasseMiroir;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author francois
 */
public class Matchs extends ClasseMiroir {

    private int ronde;
    private int idEquipeA;
    private int idEquipeB;
    private Terrain terrain; ///TODO : est-ce mieux d'avoir un ID Terrain ou un objet Terrain?
    private int score;
//TODO : MàJ des méthodes de la classe pour ajouter l'attribut Terrain.

    
    public Matchs(int ronde) {
        this.ronde = ronde;        
    }
    
    public Matchs(int ronde, Terrain terrain) {
        this.ronde = ronde;
        this.terrain = terrain;
    }

    public Matchs(int ronde, int idEquipeA, int idEquipeB, Terrain terrain) {
        this.ronde = ronde;
        this.idEquipeA = idEquipeA;
        this.idEquipeB = idEquipeB;
        this.terrain = terrain;
    }

    public Matchs(int ronde, int idEquipeA, int idEquipeB, Terrain terrain, int id) {
        super(id);
        this.ronde = ronde;
        this.idEquipeA = idEquipeA;
        this.idEquipeB = idEquipeB;
        this.terrain = terrain;
    }
    
    
    
    
    @Override
        public Statement saveSansId(Connection con) throws SQLException {
            PreparedStatement insert = con.prepareStatement(
                "insert into matchs (idronde, idEquipeA, idEquipeB, idTerrain) values (?, ?, ?, ?)",
                PreparedStatement.RETURN_GENERATED_KEYS);

            insert.setInt(1, this.ronde);
            insert.setInt(2, this.idEquipeA);
            insert.setInt(3, this.idEquipeB);

            if (this.terrain != null)
                insert.setInt(4, this.terrain.getId());
            else
                insert.setNull(4, java.sql.Types.INTEGER);

            insert.executeUpdate();
            return insert;
        }
    public static List<Matchs> tousLesMatchsDeLaRonde(Connection con, int idRonde) throws SQLException {
    List<Matchs> res = new ArrayList<>();

    String sql = "SELECT id, idEquipeA, idEquipeB, idTerrain FROM matchs WHERE idronde = ?";
    try (PreparedStatement pst = con.prepareStatement(sql)) {
        pst.setInt(1, idRonde);
        try (ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                res.add(new Matchs(
                        idRonde,
                        rs.getInt("idEquipeA"),
                        rs.getInt("idEquipeB"),
                        null,
                        rs.getInt("id")
                ));
            }
        }
    }
    return res;
}
    
    public static List<Matchs> creerMatchsAuto(Connection con, Ronde r, List<Equipe> equipes) throws SQLException {

    List<Matchs> matchs = new ArrayList<>();

    // On suppose que les équipes sont 2 par 2
    for (int i = 0; i < equipes.size(); i += 2) {
        int idA = equipes.get(i).getId();
        int idB = equipes.get(i + 1).getId();

        Matchs m = new Matchs(r.getId(), idA, idB, null);
        m.saveInDB(con);
        matchs.add(m);
    }

    return matchs;
}


    /**
     * @return the ronde
     */
    public int getRonde() {
        return this.ronde;
    }
    /*public int getIdRonde() {
        int result = this.ronde.getId(); //TODO à finir
        return result;
    }*/
    
    public int getIdEquipeA() {
        return idEquipeA;
    }

    public int getIdEquipeB() {
        return idEquipeB;
    }

    public int getIdTerrain() {
        int result = this.terrain.getId();
        return result;
    }
    public Terrain getTerrain() {
        return this.terrain;
    }
    
    /**
     * @param nom the ronde to set
     */
    public void setRonde(int ronde) {
        this.ronde = ronde;
    }
}
