https://lucid.app/lucidchart/ebc83882-e98e-4a42-baeb-7bd54f4cd9ca/edit?viewport_loc=-1709%2C-915%2C1141%2C1329%2C0_0&invitationId=inv_a073d439-21d9-4d68-b4bc-f817c417a1f9

A FAIRE : 

# Classes
Créer/modifier chaque classe pour que cela corresponde au diagramme UML 
- classe équipe : --?
- classe tournoi --?    **Extension : créer plusieurs tournois et les gérer**
- classe matchs : les deux équipes qui s'affrontent (parmi les équipes de la ronde)
- Rayan : classe ronde : liste de toutes les équipes de cette ronde; liste des joueurs exclus (??)
- **classe utilisateur : classe mère dont admin, et joueur héritent. Il faut donc créer la classe admin. On peut aussi créer une classe utilisateur_standard...**
- on doit pouvoir créer le tournoi ou simplement ronde par ronde en spécifiant si on veut des matchs séparés pour les seniors et les juniors, ou bien si on veut mélanger les deux catégories dans les matchs
- extension sauvegarde :
-      créer un fichier texte avec toutes les informations d'un tournoi --> pour garder une trace
-       pouvoir lire un fichier .csv de joueurs ; peut-être aussi que ce fichier contiendra des sauvegardes sur les matchs et équipes et rondes...
-  extension : quand on crée le tournoi à partir des joueurs, on doit pouvoir créer un tournoi avec seuls les joueurs d'une catégorie (option)
-        

# menu textuel gestion bdd
## Ronde : 
- avoir un option qui affiche toutes les rondes et leur statut
- option à implémenter : créer une ronde et les équipes et les matchs en même temps alétoirement -->les assigner à un terrain

## tournoi : 
- dans l'entêtre de menuprincipal : afficher le nombre de terrains disponibles, mais aussi le nombre actuel de joueurs et de rondes terminées...

## Joueurs : 

## Terrains : 
- consulter la liste des matchs ayant été joués sur un terrain.

## créer une classe admin et utilisateur
- directement dans vaadin en vrai


# VAADIN

## Menu "Joueurs"
- afficher la liste des joueurs, avec leur score. 
     - plus tard : option de cliquer sur un joueur pour avoir des infos détaillées sur lui, p.ex. le nombre de matchs qu'il a joués, avec qui d'autres il a joué... jsp
## Menu "Rondes"
- créer une ronde > créer les équipes > créer les matchs (+assignation aux terrains), tout dans le même menu je pense
- consulter les rondes terminées, avec un affichage d'une liste des matchs par exemple
- consulter la ronde en cours; entrer les scores pour un match depuis ce menu

## menu utilisateur.admin (connexion)

 # A modifier : 
 - Supprimer l'attribut # dans joueur (C'est le même que ID)
 - Ali: DANS EQUIPE, supprimer num (1 ou 2 ) ne sert à  rien, proble id match,  le même id pour tous les match,
 - Tournoi : Choisir Option : PAR catégorie ou équilibrer les équipes
 - Catégorie choisir ebtre S et J 
 - 
 -  
 


