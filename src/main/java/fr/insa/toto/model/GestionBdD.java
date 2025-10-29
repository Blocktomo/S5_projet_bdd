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
                st.executeUpdate("create table utilisateur ( "
                        + ConnectionSimpleSGBD.sqlForGeneratedKeys(con, "id") + ","
                        + " surnom varchar(30) not null unique,"
                        + " pass varchar(20) not null,"
                        + " role integer not null "
                        + ") "
                );
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
                        + " num integer not null,"
                        + " score integer,"
                        + " idmatch integer not null "
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
                
                st.executeUpdate("create table loisir ( "
                        + ConnectionSimpleSGBD.sqlForGeneratedKeys(con, "id") + ","
                        + " nom varchar(20) not null unique,"
                        + " description text not null"
                        + ") "
                );
                st.executeUpdate("create table pratique ( "
                        + " idutilisateur integer not null,"
                        + " idloisir integer not null,"
                        + " niveau integer not null "
                        + ") "
                );
                con.commit();
                st.executeUpdate("create table apprecie ( "
                        + " u1 integer not null,"
                        + " u2 integer not null"
                        + ") "
                );

                st.executeUpdate("alter table apprecie\n"
                        + "  add constraint fk_apprecie_u1\n"
                        + "  foreign key (u1) references utilisateur(id)"
                );
                st.executeUpdate("alter table apprecie\n"
                        + "  add constraint fk_apprecie_u2\n"
                        + "  foreign key (u2) references utilisateur(id)"
                );
                st.executeUpdate("alter table pratique\n"
                        + "  add constraint fk_pratique_idutilisateur\n"
                        + "  foreign key (idutilisateur) references utilisateur(id)"
                );

                st.executeUpdate("alter table pratique\n"
                        + "  add constraint fk_pratique_idloisir\n"
                        + "  foreign key (idloisir) references loisir(id)"
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
                        "alter table utilisateur "
                        + "drop constraint fk_utilisateur_u1");
            } catch (SQLException ex) {
            }
            try {
                st.executeUpdate(
                        "alter table utilisateur "
                        + "drop constraint fk_utilisateur_u2");
            } catch (SQLException ex) {
            }
            try {
                st.executeUpdate(
                        "alter table pratique "
                        + "drop constraint fk_pratique_idutilisateur");
            } catch (SQLException ex) {
            }
            try {
                st.executeUpdate(
                        "alter table pratique "
                        + "drop constraint fk_pratique_idloisir");
            } catch (SQLException ex) {
            }
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
                st.executeUpdate("drop table apprecie");
            } catch (SQLException ex) {
            }
            try {
                st.executeUpdate("drop table pratique");
            } catch (SQLException ex) {
            }
            try {
                st.executeUpdate("drop table loisir");
            } catch (SQLException ex) {
            }
            try {
                st.executeUpdate("drop table utilisateur");
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
