https://lucid.app/lucidchart/ebc83882-e98e-4a42-baeb-7bd54f4cd9ca/edit?viewport_loc=-1709%2C-915%2C1141%2C1329%2C0_0&invitationId=inv_a073d439-21d9-4d68-b4bc-f817c417a1f9

A FAIRE : 

# Classes
Créer/modifier chaque classe pour que cela corresponde au diagramme UML 
- classe équipe : --?
- Thomas : classe tournoi  : créer les attributs suivants, ainsi que les tables pour les gérer :  liste des rondes ; **liste des terrains** ; liste de joueurs
- classe matchs : les deux équipes qui s'affrontent (parmi les équipes de la ronde)
- Rayan : classe ronde : liste de toutes les équipes de cette ronde; liste des joueurs exclus (??) ; **assignation des matchs aux terrains**
- classe terrain : méthodes pour créer le terrain dans le BDD; intégrer ces méthodes dans MainConsole.
- classe utilisateur : classe mère dont admin, et joueur héritent. Il faut donc créer la classe admin. On peut aussi créer une classe utilisateur_standard...
- on doit pouvoir créer le tournoi ou simplement ronde par ronde en spécifiant si on veut des matchs séparés pour les seniors et les juniors, ou bien si on veut mélanger les deux catégories dans les matchs

# menu textuel gestion bdd
## Ronde : 
- consulter les matchs de la ronde
- consulter les équipes
- consulter les scores des équipes
- créer une ronde (et les équipes et les matchs en même temps alétoirement -->les assigner à un terrain)

## tournoi : 
- créer un tournoi
- afficher les tournois existants (passés, futurs, présent)
- "activer" un tournoi; toutes les données entrée par ailleurs doivent être relatives à ce tournoi.
- renseigner le nombre de terrains disponibles

## Joueurs : 
- consulter la lste des joueurs et leur score
- modifier les caractéristiques d'un joueur
- créer/supprimer joueurs

# VAADIN

 # A modifier : 
 - Supprimer l'attribut # dans joueur (C'est le même que ID)
 - Ali: DANS EQUIPE, supprimer num (1 ou 2 ) ne sert à  rien, proble id match,  le même id pour tous les match,
 - Tournoi : Choisir Option : PAR catégorie ou équilibrer les équipes
 - Catégorie choisir ebtre S et J 
 - 
 -  
 


