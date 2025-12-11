
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
                
                st.executeUpdate("create table joueur ( "
                            + ConnectionSimpleSGBD.sqlForGeneratedKeys(con, "id") + ","
                            + " surnom varchar(30) not null unique,"
                            + " categorie varchar(20),"
                            + " taillecm double, "
                            + " score integer default 0"
                            + ") ");
                
                
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

                        st.executeUpdate("create table matchs ("
                + ConnectionSimpleSGBD.sqlForGeneratedKeys(con, "id") + ","
                + " idronde integer not null,"
                + " idEquipeA integer not null,"
                + " idEquipeB integer not null,"
                + " idTerrain integer,"
                + " foreign key (idronde) references ronde(idronde),"
                + " foreign key (idEquipeA) references equipe(id),"
                + " foreign key (idEquipeB) references equipe(id)"
                + ")");
                
                
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
