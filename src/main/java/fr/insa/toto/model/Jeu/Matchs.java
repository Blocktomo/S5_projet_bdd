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

/**
 *
 * @author francois
 */
public class Matchs extends ClasseMiroir {

    private int ronde;
    private int idEquipeA;
    private int idEquipeB;
    private Terrain terrain; ///TODO : est-ce mieux d'avoir un ID Terrain ou un objet Terrain?
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
        insert.setInt(1, this.getRonde()); //TODO à modifier //getIdRonde()
        insert.setInt(1, this.getIdEquipeA());
        insert.setInt(1, this.getIdEquipeB());
        insert.setInt(1, this.getIdTerrain());

        insert.executeUpdate();
        return insert;
    }

    /**
     * @return the ronde
     */
    public int getRonde() {
        return this.ronde;
    }
    public int getIdRonde() {
        int result = this.ronde.getId(); //TODO à finir
        return result;
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
     * @param nom the ronde to set
     */
    public void setRonde(int ronde) {
        this.ronde = ronde;
    }
}
