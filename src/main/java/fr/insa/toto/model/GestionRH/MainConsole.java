package fr.insa.toto.model.GestionRH;

import fr.insa.toto.model.Jeu.Equipe;
import fr.insa.toto.model.Jeu.Joueur;
import fr.insa.toto.model.GestionRH.GestionBdD;
import fr.insa.toto.model.GestionRH.BdDTest;
import fr.insa.beuvron.utils.ConsoleFdB;
import fr.insa.beuvron.utils.database.ConnectionSimpleSGBD;
import fr.insa.beuvron.utils.database.ResultSetUtils;
import fr.insa.beuvron.utils.exceptions.ExceptionsUtils;
import fr.insa.beuvron.utils.list.ListUtils;
import fr.insa.toto.model.Jeu.Tournoi;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import fr.insa.toto.model.Jeu.Ronde;

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
            System.out.println("Menu principal du tournoi " + Tournoi.getNom() +" "+  Tournoi.getAnnee());
            System.out.println("======================================");
            System.out.println((i++) + ") menu tournoi");
            System.out.println((i++) + ") menu gestion BdD");
            System.out.println((i++) + ") menu joueurs");
            System.out.println((i++) + ") menu Ronde");
            System.out.println((i++) + ") menu équipes");

            System.out.println("0) Fin");
            rep = ConsoleFdB.entreeEntier("Votre choix : ");
            try {
                int j = 1;
                if (rep == j++) {
                    menuTournoi(con);
                }else if (rep == j++) {
                    menuBdD(con);
                } else if (rep == j++) {
                    menuJoueur(con);
                } else if (rep == j++) {
                    menuRonde(con);    
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
        System.out.println((i++) + ") créer des équipes aléatoires pour une ronde");
        System.out.println((i++) + ") afficher les équipes et leurs joueurs");
        System.out.println((i++) + ") supprimer des équipes");
        System.out.println("0) Retour");

        rep = ConsoleFdB.entreeEntier("Votre choix : ");

        try {
            int j = 1;

            /* =============================
                 1) CRÉER DES ÉQUIPES
               ============================= */
            if (rep == j++) {

                int idRonde = ConsoleFdB.entreeEntier("Id de la ronde : ");

                // On récupère la ronde à partir de son ID
               Ronde r = Ronde.chercherRondeParId(con, idRonde);
                if (r == null) {
                    System.out.println("⚠️  Aucune ronde avec cet id.");
                    continue;
                }

                // Création automatique : la taille d’équipe vient du tournoi
                var equipes = Equipe.creerEquipes(con, r);

                System.out.println(equipes.size() + " équipes créées pour la ronde " + idRonde);
            }

            /* =============================
                 2) AFFICHER LES ÉQUIPES
               ============================= */
            else if (rep == j++) {

                String ordre =
        "select "
        + "e.id as idEquipe, "
        + "e.score as scoreEquipe, "
        + "e.idronde as idRonde, "
        + "j.id as idJoueur, "
        + "j.surnom as surnomJoueur "
        + "from equipe e "
        + "left join composition c on c.idequipe = e.id "
        + "left join joueur j on j.id = c.idjoueur "
        + "order by e.id, j.id";

                try (PreparedStatement pst = con.prepareStatement(ordre)) {
                    try (ResultSet rst = pst.executeQuery()) {
                        System.out.println(ResultSetUtils.formatResultSetAsTxt(rst));
                    }
                }
            }

            /* =============================
                 3) SUPPRIMER DES ÉQUIPES
               ============================= */
            else if (rep == j++) {

                List<Equipe> toutes = Equipe.toutesLesEquipes(con);
                System.out.println(toutes.size() + " équipes trouvées :");

                List<Equipe> selected = ListUtils.selectMultiple(
                        "Sélectionnez les équipes à supprimer : ",
                        toutes,
                        e -> "Equipe " + e.getId() +
                             " (ronde " + e.getRonde().getId() +
                             ", score " + e.getScore() + ")"
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

    
     // TODO : menu à finir
    public static void menuTournoi(Connection con) {
        int rep = -1;
        while (rep != 0) {
            System.out.println("Menu tournoi");
            System.out.println("================================");
            //TODO : faire un texte intermédiaire qui affiche un aperçu de tous les tournois existants
            System.out.println("Voici les caractérisitques du Tournoi actuel.");
            Tournoi.affichageTexte();
            System.out.println("1) modifier le tournoi");
            System.out.println("0) retour");
            rep = ConsoleFdB.entreeEntier("Votre choix : ");
            try {
                if (rep == 1) {
                    Tournoi.modifTournoi(con); //modifier un tournoi
                } else{}
            } catch (Exception ex) {
                System.out.println(ExceptionsUtils.messageEtPremiersAppelsDansPackage(ex, "fr.insa", 3));
            }
        }
    } 
    
    public static void menuRonde(Connection con){
        int rep= -1;
        while (rep !=0) {
            int i = 1;
            System.out.println("Menu Ronde");
            System.out.println("======================================");
            System.out.println((i++) + ") creer Ronde");
            System.out.println((i++) + ")Afficher Ronde et detail");
            System.out.println((i++) + ")Modifier etat Ronde");
            System.out.println((i++) + ")Supprimer Ronde");
            System.out.println("0) Retour");
            rep = ConsoleFdB.entreeEntier("Votre choix : ");
            try{
                int j = 1;
                
                if (rep == j++) {
                    //creer Ronde
                    Ronde r = new Ronde(0);
                    r.saveInDB(con);
                    System.out.println("Ronde creer , ID = " + r.getId());                    
                    
                }else if (rep == j++) {
                    // afficher  Ronde et detail
                    int idRonde = ConsoleFdB.entreeEntier("Quel est l'ID de la ronde à afficher ?");
                    
                    String ordre=
                        "SELECT m.id as MatchID, m.ronde, " +
                        "       e.id as Equipe ID, e.score, " +
                        "       j.surnom as Joueur " +
                        "From matchs m" +
                        "LEFT JOIN equipe e ON e.idmatch = m.id" +
                        "LEFT JOIN composition c ON c.idequipe = e.id " +
                        "LEFT JOIN joueur j ON j.id = c.idjoueur" +
                        "WHERE JOIN joueur j ON j.id = c.idjoueur" +
                        "ORDER BY m.id, e.id";
                    System.out.println("Details Ronde"+ idRonde);
                    try (PreparedStatement pst = con.prepareStatement(ordre)) {
                        pst.setInt(1, idRonde);
                        try (ResultSet rst = pst.executeQuery()){
                            System.out.println(ResultSetUtils.formatResultSetAsTxt(rst));
                    }
                }
                }else if (rep == j++) {
                    //SUPPRIMER RONDE
                    List<Ronde> toutes = Ronde.toutesLesRondes(con);
                    System.out.println(toutes.size() + "Rondes trouvées :");
                    
                    List<Ronde> selected = ListUtils.selectMultiple(
                        "Sélectionnez les Rondes à supprimer : ",
                            toutes,
                            e -> "Ronde " + e.getId() + "(Statut: " + (e.getTerminer()==1 ? "Finie": "En cours") + ")"
                    );
                    for (Ronde r : selected) {
                        r.deleteInDB(con);
                    }
                    System.out.println("Rondes supprimées.");
                }
            } catch (SQLException ex) {
                System.out.println(ExceptionsUtils.messageEtPremiersAppelsDansPackage(ex,"fr.insa",3));
                        }
                }
    }
                    
                    
                    
                            
                
    
      public static void main(String[] args) {
        menuPrincipal();
        }
}

    
    
  

