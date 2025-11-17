
package fr.insa.toto.model.GestionRH;

import fr.insa.toto.model.GestionRH.GestionBdD;
//import fr.insa.toto.model.GestionRH.BdDTest;
import fr.insa.beuvron.utils.ConsoleFdB;
import fr.insa.beuvron.utils.database.ConnectionSimpleSGBD;
import fr.insa.beuvron.utils.database.ResultSetUtils;
import fr.insa.beuvron.utils.exceptions.ExceptionsUtils;
import fr.insa.beuvron.utils.list.ListUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class MainConsole {

    public static void menuJoueur(Connection con) {
        int rep = -1;
        while (rep != 0) {
            int i = 1;
            System.out.println("Menu joueurs");
            System.out.println("============================");
            System.out.println((i++) + ") liste des joueurs");
            System.out.println((i++) + ") ajouter un joueur");
            System.out.println((i++) + ") supprimer des joueurs");
            System.out.println("0) Retour");
            rep = ConsoleFdB.entreeEntier("Votre choix : ");
            try {
                int j = 1;
                if (rep == j++) {
                    List<Joueur> tous = Joueur.tousLesJoueur(con);
                    System.out.println(tous.size() + " joueurs trouvés :");
                    System.out.println(ListUtils.formatList(tous, "---- tous les joueurs\n",
                            "\n", "\n", u -> u.getId() + " : " + u.getSurnom()));
                } else if (rep == j++) { //j++ incrémente j au sein de la condition, si je comprends bien
                    System.out.println("Nouveau Joueur : ");
                    Joueur u = Joueur.entreeConsole();
                    u.saveInDB(con);
                } else if (rep == j++) {
                    List<Joueur> tous = Joueur.tousLesJoueur(con);
                    List<Joueur> selected = ListUtils.selectMultiple(
                            "selectionnez les joueurs à supprimer : ", tous, 
                            u -> u.getId() + " : " + u.getSurnom());
                    for (var u : selected) {
                        u.deleteInDB(con);
                    }
                }
            } catch (Exception ex) {
                System.out.println(ExceptionsUtils.messageEtPremiersAppelsDansPackage(ex, "fr.insa", 3));
            }
        }
    }

    public static void menuBdD(Connection con) {
        int rep = -1;
        while (rep != 0) {
            int i = 1;
            System.out.println("Menu gestion base de données");
            System.out.println("============================");
            System.out.println((i++) + ") RAZ BdD = delete + create + init");
            System.out.println((i++) + ") donner un ordre SQL update quelconque"); //TODO partie très importante
            System.out.println((i++) + ") donner un ordre SQL query quelconque");
            System.out.println("0) Retour");
            rep = ConsoleFdB.entreeEntier("Votre choix : ");
            try {
                int j = 1;
                if (rep == j++) {
                    GestionBdD.razBdd(con);
                    BdDTest.createBdDTestV4(con); //la V3 est celle décrite dans la question 7 : c'est des joueurs et non des utilisateurs.
                } else if (rep == j++) {
                    String ordre = ConsoleFdB.entreeString("ordre SQL : ");
                    try (PreparedStatement pst = con.prepareStatement(ordre)) {
                        pst.executeUpdate();
                    }
                } else if (rep == j++) {
                    String ordre = ConsoleFdB.entreeString("requete SQL : ");
                    try (PreparedStatement pst = con.prepareStatement(ordre)) {
                        try (ResultSet rst = pst.executeQuery()) {
                            System.out.println(ResultSetUtils.formatResultSetAsTxt(rst));
                        }
                    }
                }
            } catch (Exception ex) {
                System.out.println(ExceptionsUtils.messageEtPremiersAppelsDansPackage(ex, "fr.insa", 3));
            }
        }
    }

    public static void menuPrincipal() {
        int rep = -1;
        Connection con = null;
        try {
            con = ConnectionSimpleSGBD.defaultCon();
            System.out.println("Connection OK");
        } catch (SQLException ex) {
            System.out.println("Problème de connection : " + ex.getLocalizedMessage());
            throw new Error(ex);
        }
        while (rep != 0) {
            int i = 1;
            System.out.println("Menu principal");
            System.out.println("==================");
            System.out.println((i++) + ") menu gestion BdD");
            System.out.println((i++) + ") menu joueurs");
            System.out.println("Menu principal");
System.out.println((i++) + ") menu équipes");

            System.out.println("0) Fin");
            rep = ConsoleFdB.entreeEntier("Votre choix : ");
            try {
                int j = 1;
                if (rep == j++) {
                    menuBdD(con);
                } else if (rep == j++) {
                    menuJoueur(con);
                } else if (rep == j++) {
                    menuEquipe(con);    
                }
            } catch (Exception ex) {
                System.out.println(ExceptionsUtils.messageEtPremiersAppelsDansPackage(ex, "fr.insa", 3));
            }
        }
    }

  
    
public static void menuEquipe(Connection con) {
    int rep = -1;
    while (rep != 0) {
        int i = 1;
        System.out.println("Menu équipes");
        System.out.println("================================");
        System.out.println((i++) + ") créer des équipes aléatoires pour un match");
        System.out.println((i++) + ") afficher les équipes et leurs joueurs");
        System.out.println((i++) + ") supprimer des équipes");
        System.out.println("0) Retour");
        rep = ConsoleFdB.entreeEntier("Votre choix : ");
        try {
            int j = 1;
            if (rep == j++) {
                // créer des équipes
                int idMatch = ConsoleFdB.entreeEntier("Id du match : ");
                int tailleEquipe = ConsoleFdB.entreeEntier("Taille des équipes (nb joueurs par équipe) : ");

                var equipes = Equipe.creerEquipes(con, idMatch, tailleEquipe);
                System.out.println(equipes.size() + " équipes créées pour le match " + idMatch);

            } else if (rep == j++) {
                // afficher toutes les équipes + joueurs
                String ordre =
                        "select e.id as idEquipe, e.num, e.score, e.idmatch, " +
                        "       j.id as idJoueur, j.surnom " +
                        "from equipe e " +
                        "left join composition c on c.idequipe = e.id " +
                        "left join joueur j on j.id = c.idjoueur " +
                        "order by e.id, j.id";
                try (PreparedStatement pst = con.prepareStatement(ordre)) {
                    try (ResultSet rst = pst.executeQuery()) {
                        System.out.println(ResultSetUtils.formatResultSetAsTxt(rst));
                    }
                }

            } else if (rep == j++) {
                // SUPPRIMER DES ÉQUIPES
                List<Equipe> toutes = Equipe.toutesLesEquipes(con);
                System.out.println(toutes.size() + " équipes trouvées :");
                // même principe que pour les joueurs
                List<Equipe> selected = ListUtils.selectMultiple(
                        "Sélectionnez les équipes à supprimer : ",
                        toutes,
                        e -> "Equipe " + e.getId() + " (match " + e.getIdmatch() + ", num " + e.getNum() + ")"
                );
                for (var e : selected) {
                    e.SuppEquipe(con);
                }
            }
        } catch (Exception ex) {
            System.out.println(ExceptionsUtils.messageEtPremiersAppelsDansPackage(ex, "fr.insa", 3));
        }
    }
}

      public static void main(String[] args) {
        menuPrincipal();
    }

    
    
    
}
