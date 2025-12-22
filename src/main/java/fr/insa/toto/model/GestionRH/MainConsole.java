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
import fr.insa.toto.model.Jeu.Matchs;
import fr.insa.toto.model.Jeu.Tournoi;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import fr.insa.toto.model.Jeu.Ronde;
import fr.insa.toto.model.Jeu.Terrain;
import java.util.Optional;

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
                            "\n", "\n", u -> u.getId() + " : " + u.getSurnom() + ", score = " + u.getScore()));
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
            System.out.println((i++) + ") menu matchs");
            System.out.println((i++) + ") menu terrain");
            System.out.println((i++) + ") menu utilisateur");

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
                } else if (rep == j++) {
                menuMatch(con); }
                else if (rep == j++) {
                    menuTerrain(con);
                }
                else if (rep == j++) {
                    menuUtilisateur(con);
                }
            }
            catch (Exception ex) {
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
    public static void menuTerrain(Connection con) {
    int rep = -1;

    while (rep != 0) {
        int i = 1;
        System.out.println("Menu Terrain");
        System.out.println("============================");
        System.out.println((i++) + ") ajouter un terrain");
        System.out.println((i++) + ") afficher tous les terrains");
        System.out.println((i++) + ") historique matchs sur le terrain");
        System.out.println((i++) + ") supprimer un terrain");
        System.out.println((i++) + ") marquer un terrain comme occupé / libre");
        System.out.println("0) Retour");

        rep = ConsoleFdB.entreeEntier("Votre choix : ");

        try {
            int j = 1;

            // 1) Ajouter un terrain
            if (rep == j++) {
                String nom = ConsoleFdB.entreeString("Nom du terrain : ");
                Terrain t = new Terrain(nom);
                t.saveInDB(con);
                System.out.println("Terrain ajouté (ID = " + t.getId() + ")");
            }

            // 2) Afficher tous les terrains
            else if (rep == j++) {
                List<Terrain> tous = Terrain.tousLesTerrains(con);

                if (tous.isEmpty()) {
                    System.out.println("Aucun terrain trouvé.");
                } else {
                    System.out.println("Liste des terrains :");
                    for (Terrain t : tous) {
                        System.out.println(
                                "ID=" + t.getId() +
                                " | Nom=" + t.getNom() +
                                " | " + (t.getOccupe() == 1 ? "Occupé" : "Libre")
                        );
                    }
                }
            }
            else if (rep == j++) {
                int idterrain = ConsoleFdB.entreeEntier("entrez l'id du terrain que vous souhaitez regarder?");
                
                
                String sql =
                        "SELECT " +
                        "   m.id AS MatchID, " +
                        "   m.idronde AS RondeID, " +
                        "   m.idEquipeA AS EquipeA_ID, " +
                        "   ea.score AS Score_A, " +
                        "   m.idEquipeB AS EquipeB_ID, " +
                        "   eb.score AS Score_B " +
                        "FROM matchs m " +
                        "JOIN equipe ea ON m.idEquipeA = ea.id " +
                        "JOIN equipe eb ON m.idEquipeB = eb.id " +
                        "WHERE m.idTerrain = ? " +
                        "ORDER BY m.idronde, m.id ";
                
                try(PreparedStatement pst = con.prepareStatement(sql)) {
                    pst.setInt(1, idterrain);
                    try(ResultSet rs =  pst.executeQuery()) {
                        System.out.println("=== Matchs joues sur le terrain " + idterrain + "===");
                        System.out.println("=== !!! => le premier id correspond a idmatchs ===");
                        System.out.println(ResultSetUtils.formatResultSetAsTxt(rs));
                    }
                    
                }
                
            }

            // 3) Supprimer un terrain
            else if (rep == j++) {
                List<Terrain> tous = Terrain.tousLesTerrains(con);

                if (tous.isEmpty()) {
                    System.out.println("Aucun terrain à supprimer.");
                    continue;
                }

                Terrain t = (Terrain) ListUtils.selectOne(
                        "Choisis un terrain à supprimer : ",
                        tous,
                        x -> "Terrain " + x.getId() + " (" + x.getNom() + ")"
                );

                t.deleteInDB(con);
                System.out.println("Terrain supprimé !");
            }

            // 4) Marquer terrain occupé / libre
            else if (rep == j++) {
                List<Terrain> tous = Terrain.tousLesTerrains(con);

                if (tous.isEmpty()) {
                    System.out.println("Aucun terrain trouvé.");
                    continue;
                }

                Terrain t = (Terrain) ListUtils.selectOne(
                        "Quel terrain modifier ?",
                        tous,
                        x -> "Terrain " + x.getId() + " | " + x.getNom() +
                                " | " + (x.getOccupe() == 1 ? "Occupé" : "Libre")
                );

                boolean occupe = ConsoleFdB.entreeEntier(
                        "1 = occupé, 0 = libre : ") == 1;

                t.setOccupe(con, occupe);

                System.out.println("Terrain mis à jour !");
            }

        } catch (Exception ex) {
            System.out.println("Erreur : " + ex.getMessage());
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
            System.out.println((i++) + ")Afficher/Modifier etat Ronde");
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
                    
                        String ordre =
                        "SELECT m.id AS matchID, "
                        + "m.idronde AS rondeID, "
                        + "e.id AS equipeID, "
                        + "e.score AS scoreEquipe, "
                        + "j.surnom AS joueur "
                        + "FROM matchs m "
                        + "LEFT JOIN equipe e ON (e.id = m.idEquipeA OR e.id = m.idEquipeB) "
                        + "LEFT JOIN composition c ON c.idequipe = e.id "
                        + "LEFT JOIN joueur j ON j.id = c.idjoueur "
                        + "WHERE m.idronde = ? "
                        + "ORDER BY m.id, e.id, j.id";
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
                    
                    
    public static void menuMatch(Connection con) {
        int rep = -1;

        while (rep != 0) {
            int i = 1;
            System.out.println("Menu Matchs");
            System.out.println("============================");
            System.out.println((i++) + ") créer automatiquement les matchs d'une ronde");
            System.out.println((i++) + ") attribuer les scores d'un match");
            System.out.println((i++) + ") voir tous les matchs");
            System.out.println("0) Retour");

            rep = ConsoleFdB.entreeEntier("Votre choix : ");

            try {
                int j = 1;

                // 1) Création auto des matchs
                if (rep == j++) {
                    int idRonde = ConsoleFdB.entreeEntier("Id de la ronde : ");
                    Ronde r = Ronde.chercherRondeParId(con, idRonde);
                    if (r == null) {
                        System.out.println("Ronde introuvable");
                        continue;
                    }

                    // récupérer teams
                    String sql = "SELECT * FROM equipe WHERE idronde = ?";
                    List<Equipe> equipes = new ArrayList<>();

                    try (PreparedStatement pst = con.prepareStatement(sql)) {
                        pst.setInt(1, idRonde);
                        try (ResultSet rs = pst.executeQuery()) {
                            while (rs.next()) {
                                equipes.add(new Equipe(
                                        rs.getInt("id"),
                                        rs.getInt("score"),
                                        r
                                ));
                            }
                        }
                    }

                    if (equipes.size() % 2 != 0) {
                        System.out.println("Nombre d'équipes impair → impossible.");
                        continue;
                    }

                    List<Matchs> matchs = Matchs.creerMatchsAuto(con, r, equipes);
                    System.out.println(matchs.size() + " matchs créés.");
                }

                // 2) Attribuer scores
                else if (rep == j++) {

                    int idRonde = ConsoleFdB.entreeEntier("Id de la ronde : ");
                    List<Matchs> matchs = Matchs.tousLesMatchsDeLaRonde(con, idRonde);

                    if (matchs.isEmpty()) {
                        System.out.println("Aucun match dans cette ronde.");
                        continue;
                    }

                    Matchs mm = (Matchs) ListUtils.selectOne(
                            "Choisis un match : ",
                            matchs,
                            m -> "Match " + m.getId() + " : équipe " + m.getIdEquipeA() + " VS équipe " + m.getIdEquipeB()
                    );

                    int scoreA = ConsoleFdB.entreeEntier("Score équipe A : ");
                    int scoreB = ConsoleFdB.entreeEntier("Score équipe B : ");

                    // récupérer équipes
                    Equipe A = Equipe.toutesLesEquipes(con).stream()
                            .filter(e -> e.getId() == mm.getIdEquipeA())
                            .findFirst().get();

                    Equipe B = Equipe.toutesLesEquipes(con).stream()
                            .filter(e -> e.getId() == mm.getIdEquipeB())
                            .findFirst().get();

                    // mettre à jour équipes
                    A.setScore(A.getScore() + scoreA);
                    B.setScore(B.getScore() + scoreB);

                    A.sauvegarderScore(con);
                    B.sauvegarderScore(con);

                    // mettre à jour joueurs
                    A.ajouterScoreAuxJoueurs(con, scoreA);
                    B.ajouterScoreAuxJoueurs(con, scoreB);

                    System.out.println("Scores mis à jour !");
                                if (mm.getTerrain() != null) {
                    mm.getTerrain().setOccupe(con, false);
                }
                }

                // 3) Voir matchs
                else if (rep == j++) {
                    try (PreparedStatement pst = con.prepareStatement("select * from matchs")) {
                        try (ResultSet rs = pst.executeQuery()) {
                            System.out.println(ResultSetUtils.formatResultSetAsTxt(rs));
                        }
                    }
                }

            } catch (Exception ex) {
                System.out.println("Erreur : " + ex.getMessage());
            }
        }
    }
    
    //TODO à finir. il faut encore implémenter chaque option du menu.
    public static void menuUtilisateur(Connection con) {
        int rep = -1;
        String utilisateur_connecte = "personne";
        while (rep != 0) {
            int i = 1;
            System.out.println("Menu utilisateur");
            System.out.println("============================");
            System.out.println("utilisateur actuellement connecté : " + utilisateur_connecte);
            System.out.println((i++) + ") liste des utilisateurs");
            System.out.println((i++) + ") ajouter un utilisateur");
            System.out.println((i++) + ") supprimer un utilisateur");
            System.out.println((i++) + ") se connecter");            
            System.out.println((i++) + ") modifier un utilisateur (admin)");            
            System.out.println("0) Retour");
            rep = ConsoleFdB.entreeEntier("Votre choix : ");
            try {
                int j = 1;
                if (rep == j++) {
                    List<Utilisateur> tous = Utilisateur.tousLesUtilisateur(con);
                    System.out.println(tous.size() + " utilisateurs trouvés :");
                    System.out.println(ListUtils.formatList(tous, "---- tous les utilisateurs\n",
                            "\n", "\n", u -> u.getId() + " : " + u.getSurnom() + ", pass = " + u.getPass() + ", role : "+ u.getRole()));
                } else if (rep == j++) { //j++ incrémente j au sein de la condition, si je comprends bien
                    System.out.println("Nouvel Utilisateur : ");
                    Utilisateur u = Utilisateur.entreeConsole();
                    u.saveInDB(con);
                } else if (rep == j++) { //SUPPRIMER
                    List<Utilisateur> tous = Utilisateur.tousLesUtilisateur(con);
                    List<Utilisateur> selected = ListUtils.selectMultiple(
                            "selectionnez les utilisateurs à supprimer : ", tous, 
                            u -> u.getId() + " : " + u.getSurnom());
                    for (var u : selected) {
                        u.deleteInDB(con);
                    }
                }else if (rep == j++) { //CONNEXION A UN UTIILISATEUR
                    String surnomU = ConsoleFdB.entreeString("surnom");
                    String passwordU = ConsoleFdB.entreeString("votre MdP");
                    try {
                        Optional<Utilisateur> trouve = Utilisateur.findBySurnomPass(con, surnomU, passwordU);
                        if (trouve.isEmpty()) {
                            System.out.println("Surnom ou pass incorrect");
                        } else {
                            utilisateur_connecte = trouve.get().getSurnom();
                            System.out.println("votre rôle est : " + trouve.get().getRole());
                        }
                    } catch (SQLException ex) {
                    }
                }
                else if (rep == j++) { //MODIFICATION UTILISATEUR
                    List<Utilisateur> tous = Utilisateur.tousLesUtilisateur(con);
                    List<Utilisateur> selected = ListUtils.selectMultiple(
                            "selectionnez les utilisateurs à modifier : ", tous, 
                            u -> u.getId() + " : " + u.getSurnom());
                    for (var u : selected) {
                        u.modifierUtilisateurConsole(con);
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

    
    
  

