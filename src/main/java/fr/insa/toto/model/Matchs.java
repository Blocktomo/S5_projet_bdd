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
package fr.insa.toto.model;

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

    public Matchs(int ronde) {
        this.ronde = ronde;
    }

    @Override
    public Statement saveSansId(Connection con) throws SQLException {
        PreparedStatement insert = con.prepareStatement(
                "insert into matchs (ronde) values (?)",
                PreparedStatement.RETURN_GENERATED_KEYS);
        insert.setInt(1, this.getRonde());
        insert.executeUpdate();
        return insert;
    }

    /**
     * @return the ronde
     */
    public int getRonde() {
        return this.ronde;
    }

    /**
     * @param nom the ronde to set
     */
    public void setRonde(int ronde) {
        this.ronde = ronde;
    }
}
