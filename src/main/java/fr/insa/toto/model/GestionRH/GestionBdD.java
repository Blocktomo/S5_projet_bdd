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
package fr.insa.toto.model.GestionRH;

import fr.insa.beuvron.utils.database.ConnectionSimpleSGBD;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author thomas_insa
 */
public class GestionBdD {
    public static void creeSchema(Connection con)
            throws SQLException {
        try {
            con.setAutoCommit(false);
            try (Statement st = con.createStatement()) {
                // creation des tables
                
                st.executeUpdate("create table tournoi ( "
                        + ConnectionSimpleSGBD.sqlForGeneratedKeys(con, "id") + ","
                        + " nom varchar(30) not null,"
                        + " annee integer not null,"
                        + " nb_de_rondes integer not null,"
                        + " duree_match integer not null, "
                        + "nb_joueurs_equipe integer not null"
                        + ") "
                );
                st.executeUpdate("create table terrain ( "
                        + ConnectionSimpleSGBD.sqlForGeneratedKeys(con, "id") + ","
                        + " libre boolean not null,"
                        + " nom_terrain varchar(30) not null"
                        + ") "
                ); //TODO créer table de remplissage des terrains (une table "assignation" qui permet de lister qules matchs ont été joués sur le terrain)
                
                st.executeUpdate("create table joueur ( "
                        + ConnectionSimpleSGBD.sqlForGeneratedKeys(con, "id") + ","
                        + " surnom varchar(30) not null unique,"
                        + " categorie varchar(20),"
                        + " taillecm double "
                        + ") "
                );
                st.executeUpdate("create table matchs ( "
                        + ConnectionSimpleSGBD.sqlForGeneratedKeys(con, "id") + ","
                        + " ronde integer not null"
                        + ") "
                );
                st.executeUpdate("create table equipe ( "
                        + ConnectionSimpleSGBD.sqlForGeneratedKeys(con, "id") + ","
                        + " score integer,"
                        + "idronde integer not null"
                        + ") "
                
                );
                st.executeUpdate("create table ronde ( "
                        + ConnectionSimpleSGBD.sqlForGeneratedKeys(con, "idronde") + ","
                        + " terminer integer CHECK ( terminer=0 or terminer=1) "
                        + ") "
                );
                
                st.executeUpdate("create table composition ( "
                        + " idequipe integer not null,"
                        + " idjoueur integer not null"
                        + ") "
                );

                st.executeUpdate("alter table composition\n"
                        + "  add constraint fk_composition_idequipe\n"
                        + "  foreign key (idequipe) references equipe(id)"
                );
                st.executeUpdate("alter table composition\n"
                        + "  add constraint fk_composition_idjoueur\n"
                        + "  foreign key (idjoueur) references joueur(id)"
                );
               
                con.commit();
                }
        } catch (SQLException ex) {
            con.rollback();
            throw ex;
        } finally {
            con.setAutoCommit(true);
        }
    }

    /**
     *
     * @param con
     * @throws SQLException
     */ 
    public static void deleteSchema(Connection con) throws SQLException {
        try (Statement st = con.createStatement()) {
            
            
            
            try {
                st.executeUpdate(
                        "alter table composition "
                        + "drop constraint fk_composition_idequipe");
            } catch (SQLException ex) {
            }
            try {
                st.executeUpdate(
                        "alter table composition "
                        + "drop constraint fk_composition_idjoueur");
            } catch (SQLException ex) {
            }
            
            try {
                st.executeUpdate("drop table tournoi");
            } catch (SQLException ex) {
            }
            try {
                st.executeUpdate("drop table joueur");
            } catch (SQLException ex) {
            }
            try {
                st.executeUpdate("drop table matchs");
            } catch (SQLException ex) {
            }
            try {
                st.executeUpdate("drop table equipe");
            } catch (SQLException ex) {
            }
            try {
                st.executeUpdate("drop table ronde");
            } catch (SQLException ex) {
            }
            try {
                st.executeUpdate("drop table composition");
            } catch (SQLException ex) {
            }
        }
    }
    
    public static void razBdd(Connection con) throws SQLException {
        deleteSchema(con);
        creeSchema(con);
    }
    
    public static void main(String[] args) {
        try (Connection con = ConnectionSimpleSGBD.defaultCon()) {
            razBdd(con);
        } catch (SQLException ex) {
            throw new Error(ex);
        }
    }
}
