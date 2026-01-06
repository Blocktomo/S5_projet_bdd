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
package fr.insa.beuvron.utils.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Définition d'une "connecion pool" à l'aide de la librairie HikariCP.
 * <pre>
 * repris de https://www.baeldung.com/hikaricp.
 * Voir https://github.com/brettwooldridge/hikaricp/wiki/MYSQL-Configuration pour
 * une explication de certains paramètres
 * </pre>
 *
 * @author francois
 */
public class ConnectionPool {

    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;

    // un bloc static directement dans une classe est exécuté au chargement
    // de la classe
    // pour une BdD en mémoire en utilisant le sgbd H2
        static {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException ex) {
                throw new Error("driver mysql not found", ex);
            }

            config.setJdbcUrl("jdbc:mysql://92.222.25.165:3306/m3_tbeverly01");
            config.setUsername("m3_tbeverly01");
            config.setPassword("71884255");
            config.setMaximumPoolSize(4);
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("useServerPrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            config.setTransactionIsolation("TRANSACTION_SERIALIZABLE");
            ds = new HikariDataSource(config);
        }
    // pour une BdD en utilisant le sgbd mysql pour module M3
//    static {
//        try {
//            Class.forName("com.mysql.cj.jdbc.Driver");
//        } catch (ClassNotFoundException ex) {
//            throw new Error("driver mysql not found", ex);
//        }
//        config.setJdbcUrl("jdbc:mysql://92.222.25.165:3306/m3_fdebertranddeb01");
//        config.setUsername("m3_fdebertranddeb01");
//        config.setPassword("je le donne pas");
//        config.setMaximumPoolSize(10);
//        config.addDataSourceProperty("cachePrepStmts", "true");
//        config.addDataSourceProperty("useServerPrepStmts", "true");
//        config.addDataSourceProperty("prepStmtCacheSize", "250");
//        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
//        config.setTransactionIsolation("TRANSACTION_SERIALIZABLE");
//        ds = new HikariDataSource(config);
//    }

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
}
