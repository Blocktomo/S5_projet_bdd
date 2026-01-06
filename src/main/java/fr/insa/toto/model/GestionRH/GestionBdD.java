package fr.insa.toto.model.GestionRH;

import fr.insa.beuvron.utils.database.ConnectionSimpleSGBD;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Gestion de la création et suppression du schéma de la base de données.
 * Version stable (compatible nouvelle classe Tournoi).
 */
public class GestionBdD {

    /* =======================
       CRÉATION DU SCHÉMA
       ======================= */
    public static void creeSchema(Connection con) throws SQLException {

        try (Statement st = con.createStatement()) {

            /* ========= TOURNOI ========= */
            st.executeUpdate("""
                CREATE TABLE tournoi (
                    id INTEGER AUTO_INCREMENT PRIMARY KEY,
                    nom VARCHAR(30) NOT NULL,
                    annee INTEGER NOT NULL,
                    nb_de_rondes INTEGER NOT NULL,
                    duree_match INTEGER NOT NULL,
                    nb_joueurs_equipe INTEGER NOT NULL,
                    nb_joueurs_max INTEGER NOT NULL DEFAULT 0
                )
            """);

            /* ========= JOUEUR ========= */
            st.executeUpdate("""
                CREATE TABLE joueur (
                    id INTEGER AUTO_INCREMENT PRIMARY KEY,
                    surnom VARCHAR(30) NOT NULL UNIQUE,
                    categorie VARCHAR(20),
                    taillecm DOUBLE,
                    score INTEGER DEFAULT 0
                )
            """);

            /* ========= PARTICIPATION ========= */
            st.executeUpdate("""
                CREATE TABLE participation (
                    idjoueur INTEGER NOT NULL,
                    idtournoi INTEGER NOT NULL,
                    PRIMARY KEY (idjoueur, idtournoi),
                    FOREIGN KEY (idjoueur) REFERENCES joueur(id),
                    FOREIGN KEY (idtournoi) REFERENCES tournoi(id)
                )
            """);

            /* ========= UTILISATEUR ========= */
            st.executeUpdate("""
                CREATE TABLE utilisateur (
                    id INTEGER AUTO_INCREMENT PRIMARY KEY,
                    surnom VARCHAR(30) NOT NULL UNIQUE,
                    pass VARCHAR(30) NOT NULL,
                    role INTEGER NOT NULL CHECK (role = 1 OR role = 2)
                )
            """);

            /* ========= RONDE ========= */
            st.executeUpdate("""
                CREATE TABLE ronde (
                    idronde INTEGER AUTO_INCREMENT PRIMARY KEY,
                    terminer INTEGER CHECK (terminer = 0 OR terminer = 1 OR terminer = 2),
                    idtournoi INTEGER NOT NULL
                )
            """);

            /* ========= EQUIPE ========= */
            st.executeUpdate("""
                CREATE TABLE equipe (
                    id INTEGER AUTO_INCREMENT PRIMARY KEY,
                    score INTEGER,
                    idronde INTEGER NOT NULL,
                    FOREIGN KEY (idronde) REFERENCES ronde(idronde)
                )
            """);

            /* ========= COMPOSITION ========= */
            st.executeUpdate("""
                CREATE TABLE composition (
                    idequipe INTEGER NOT NULL,
                    idjoueur INTEGER NOT NULL,
                    PRIMARY KEY (idequipe, idjoueur),
                    FOREIGN KEY (idequipe) REFERENCES equipe(id),
                    FOREIGN KEY (idjoueur) REFERENCES joueur(id)
                )
            """);

            /* ========= TERRAIN ========= */
            st.executeUpdate("""
                CREATE TABLE terrain (
                    id INTEGER AUTO_INCREMENT PRIMARY KEY,
                    nom VARCHAR(30) NOT NULL,
                    occupe INTEGER DEFAULT 0 CHECK (occupe = 0 OR occupe = 1)
                )
            """);

            /* ========= TERRAINS_TOURNOIS ========= */
            st.executeUpdate("""
                CREATE TABLE terrains_tournois (
                    idtournoi INTEGER NOT NULL,
                    idterrain INTEGER NOT NULL,
                    PRIMARY KEY (idtournoi, idterrain),
                    FOREIGN KEY (idtournoi) REFERENCES tournoi(id),
                    FOREIGN KEY (idterrain) REFERENCES terrain(id)
                )
            """);

            /* ========= MATCHS ========= */
            st.executeUpdate("""
                CREATE TABLE matchs (
                    id INTEGER AUTO_INCREMENT PRIMARY KEY,
                    idronde INTEGER NOT NULL,
                    idEquipeA INTEGER NOT NULL,
                    idEquipeB INTEGER NOT NULL,
                    idTerrain INTEGER,
                    FOREIGN KEY (idronde) REFERENCES ronde(idronde),
                    FOREIGN KEY (idEquipeA) REFERENCES equipe(id),
                    FOREIGN KEY (idEquipeB) REFERENCES equipe(id),
                    FOREIGN KEY (idTerrain) REFERENCES terrain(id)
                )
            """);
        }
    }

    /* =======================
       SUPPRESSION DU SCHÉMA
       ======================= */
    public static void deleteSchema(Connection con) throws SQLException {

        try (Statement st = con.createStatement()) {
            try { st.executeUpdate("DROP TABLE matchs"); } catch (SQLException e) {}
            try { st.executeUpdate("DROP TABLE composition"); } catch (SQLException e) {}
            try { st.executeUpdate("DROP TABLE equipe"); } catch (SQLException e) {}
            try { st.executeUpdate("DROP TABLE ronde"); } catch (SQLException e) {}
            try { st.executeUpdate("DROP TABLE participation"); } catch (SQLException e) {}
            try { st.executeUpdate("DROP TABLE terrains_tournois"); } catch (SQLException e) {}
            try { st.executeUpdate("DROP TABLE terrain"); } catch (SQLException e) {}
            try { st.executeUpdate("DROP TABLE joueur"); } catch (SQLException e) {}
            try { st.executeUpdate("DROP TABLE utilisateur"); } catch (SQLException e) {}
            try { st.executeUpdate("DROP TABLE tournoi"); } catch (SQLException e) {}
        }
    }

    /* =======================
       RAZ BDD
       ======================= */
    public static void razBdd(Connection con) throws SQLException {
        deleteSchema(con);
        creeSchema(con);
    }
 
    /* =======================
       MAIN (TEST)
       ======================= */
    public static void main(String[] args) {
        try (Connection con = ConnectionSimpleSGBD.defaultCon()) {
            razBdd(con);
            System.out.println("✅ Base de données recréée avec succès");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
