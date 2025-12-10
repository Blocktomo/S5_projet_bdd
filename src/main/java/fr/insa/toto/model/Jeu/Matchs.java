/*
Copyright 2000- Francois de Bertrand de Beuvron

This file is part of CoursBeuvron.

CoursBeuvron is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

CoursBeuvron is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with CoursBeuvron.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.insa.toto.model.Jeu;

import fr.insa.beuvron.utils.database.ClasseMiroir;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author francois
 */
public class Matchs extends ClasseMiroir {

    private int idRonde;
    private int idEquipeA;
    private int idEquipeB;
    private Terrain terrain; ///TODO : est-ce mieux d'avoir un ID Terrain ou un objet Terrain?
    //TODO : MàJ des méthodes de la classe pour ajouter l'attribut Terrain.

    
    public Matchs(int idRonde) {
        this.idRonde = idRonde;        
    }
    
    public Matchs(int idRonde, Terrain terrain) {
        this.idRonde = idRonde;
        this.terrain = terrain;
    }

    public Matchs(int idRonde, int idEquipeA, int idEquipeB, Terrain terrain) {
        this.idRonde = idRonde;
        this.idEquipeA = idEquipeA;
        this.idEquipeB = idEquipeB;
        this.terrain = terrain;
    }

    public Matchs(int idRonde, int idEquipeA, int idEquipeB, Terrain terrain, int id) {
        super(id);
        this.idRonde = idRonde;
        this.idEquipeA = idEquipeA;
        this.idEquipeB = idEquipeB;
        this.terrain = terrain;
    }
    
    
    
    
    @Override
    public Statement saveSansId(Connection con) throws SQLException {
        PreparedStatement insert = con.prepareStatement(
                "insert into matchs (idronde, idEquipeA, idEquipeB, idTerrain) values (?, ?, ?, ?)",
                PreparedStatement.RETURN_GENERATED_KEYS);
        insert.setInt(1, this.getIdRonde()); //TODO à modifier //getIdRonde()
        insert.setInt(1, this.getIdEquipeA());
        insert.setInt(1, this.getIdEquipeB());
        insert.setInt(1, this.getIdTerrain());

        insert.executeUpdate();
        return insert;
    }
    
    /**
     * crée les matchs d'une ronde. Ne fonctionne qu'avec un nombre pair d'équipes.
     * @param con: Connection
     * @param idronde
     * @throws SQLException 
     */
    public static void creerMatchs(Connection con, int idronde) throws SQLException{
        Ronde rondeActuelle = Ronde.chercherRondeParId(con, idronde);
        List<Equipe> listeEquipes = rondeActuelle.getEquipesDeLaRonde(con);
        
        List<Terrain> listeTerrains = Tournoi.getListe_terrains();
        //TODO : vérifier qu'il ya assez de terrains pour tous les matchs... ou bien à défaut jouer plus d'un match sur un terrain (l'un à la suite de l'autre)
        if (listeEquipes.size()%2!=0){
            throw new Error("nombre impair d'équipes..."); //TODO ? : ajouter une fonctio qui crée des matcsh avec un nombre d'équipes impaires?
        }
        
        for (int i=0; i<listeEquipes.size()/2; i++){
            Equipe equipeA = listeEquipes.get(i);
            Equipe equipeB = listeEquipes.get(i+1);
            i++; //il faut sauter l'objet suivant
            Terrain terrainMatch= listeTerrains.get(i);
            Matchs nouveauMatch = new Matchs(idronde, equipeA.getId(), equipeB.getId(), terrainMatch);
            nouveauMatch.saveInDB(con); 
        }
    }

    /**
     * @return the ronde
     */
    public int getIdRonde() {
        return this.idRonde;
    }
    
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
     * @param nom the idronde to set
     */
    public void setIdRonde(int idronde) {
        this.idRonde = idronde;
    }

    public void setTerrain(Terrain terrain) {
        this.terrain = terrain;
    }
    
}
