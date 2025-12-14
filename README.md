https://lucid.app/lucidchart/ebc83882-e98e-4a42-baeb-7bd54f4cd9ca/edit?viewport_loc=-1709%2C-915%2C1141%2C1329%2C0_0&invitationId=inv_a073d439-21d9-4d68-b4bc-f817c417a1f9

A FAIRE : 

# Classes
Créer/modifier chaque classe pour que cela corresponde au diagramme UML 
- classe équipe : --?
- Thomas : classe tournoi  : créer les tables pour gérer les listes suivantes :  liste des rondes ; **liste des terrains**
- classe matchs : les deux équipes qui s'affrontent (parmi les équipes de la ronde)
- Rayan : classe ronde : liste de toutes les équipes de cette ronde; liste des joueurs exclus (??) ; **assignation des matchs aux terrains**
- classe terrain : méthodes pour créer le terrain dans la BDD; intégrer ces méthodes dans MainConsole.
- classe utilisateur : classe mère dont admin, et joueur héritent. Il faut donc créer la classe admin. On peut aussi créer une classe utilisateur_standard...
- on doit pouvoir créer le tournoi ou simplement ronde par ronde en spécifiant si on veut des matchs séparés pour les seniors et les juniors, ou bien si on veut mélanger les deux catégories dans les matchs

# menu textuel gestion bdd
## Ronde : 
- consulter les matchs de la ronde
- consulter les équipes
- consulter les scores des équipes
- créer une ronde (et les équipes et les matchs en même temps alétoirement -->les assigner à un terrain)

## tournoi : 
- afficher le nombre de terrains disponibles, mais aussi le nombre actuel de joueurs et de rondes terminées...

## Joueurs : 
- consulter la lste des joueurs et leur score
- modifier les caractéristiques d'un joueur

## Terrains : 
- "ajouter" des terrains
- consulter la liste des matchs ayant été joués sur un terrain.


# VAADIN

## Menu "Joueurs"
- afficher la liste des joueurs, avec leur score. 
     - plus tard : option de cliquer sur un joueur pour avoir des infos détaillées sur lui, p.ex. le nombre de matchs qu'il a joués, avec qui d'autres il a joué... jsp
## Menu "Rondes"
- créer une ronde > créer les équipes > créer les matchs (+assignation aux terrains), tout dans le même menu je pense
- consulter les rondes terminées, avec un affichage d'une liste des matchs par exemple
    consulter la ronde en cours; entrer les scores pour un match depuis ce menu

 # A modifier : 
 - Supprimer l'attribut # dans joueur (C'est le même que ID)
 - Ali: DANS EQUIPE, supprimer num (1 ou 2 ) ne sert à  rien, proble id match,  le même id pour tous les match,
 - Tournoi : Choisir Option : PAR catégorie ou équilibrer les équipes
 - Catégorie choisir ebtre S et J 
 - 
 -  
 


