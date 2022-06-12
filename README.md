# HEIG-VD | AND - Projet

> Auteurs : Zwick Gaétan, Maziero Marco, Lamrani Soulaymane

## Description du projet

Le but de ce projet de cours est de mettre en place et utiliser une fonctionnalité avancée du système Android. Parmi la liste des différentes idées que nous avions à choix, nous avons choisi le Bluetooth low energy (BLE).

L'application permet à un *Host* de créer une *Room* avec une question, acceptant des *Participants* qui peuvent y répondre via une connexion BLE. 

#### Host

Il peut créer une *Room* en indiquant un nom et une question. Il pourra ensuite recevoir les réponses des *Participants* qui seront affichées sur la page de la *Room* du *Host*. La Room est détruite en même temps que l'Activité *Host*.

#### Participant

Il peut voir une liste de noms de toutes les *Room* ouvertes autour de lui. Puis cliquer sur le nom de la *Room* voulue pour la rejoindre. Une fois rejoint, il devra entrer son nom et une réponse pour pouvoir envoyer sa réponse au Host. Une fois la réponse envoyée, l'Activité du participant se fermera et il reviendra dans la page de liste des Rooms. Il ne peut pas voter 2 fois à la même Room (même s'il peut toujours la rejoindre...). 

## Technologies utilisées

### BLE

BLE est un standard ouvert basé sur Bluetooth qui consomme jusqu'à 10 fois moins d'énergie que le Bluetooth de base, ce qui le rend idéal pour des appareils tels que les smartwatches, appareils de surveillance médicale, car il est surtout utilisé pour des applications n'ayant pas besoin d'échanger de grandes quantités de données comme l'heure ou la température.

### Google Nearby Connections API

Nearby connection est une API permettant de découvrir et établir des canaux de communications entre plusieurs appareil. Son utilisation n'est pas très complexe et plutôt haut niveau. 

Il suffit d'ajouter les permissions requises au BLE, choisir la stratégie de connexion (ici en P2P_STAR, donc 1-to-N).

Il nous permet ensuite de gérer les différentes phases nécessaires à la communication entre les appareils:

1. Annonce et découverte

L'annonceur montre qu'il est disponible pour une connexion et quand un appareil le découvre, il pourra lui demander de se connecter.

2. Connexion

Pour se connecter, le découvreur doit faire une requête de connexion et l'annonceur pourra alors choisir de l'accepter ou de la refuser. Une fois la connexion établie, les deux appareils pourront commencer à échanger des données.

3. Échange de données

À cette étape, la connexion est symétrique et les deux appareils peuvent 3 types d'échanges:

- `BYTES`: Les données sont envoyées sous de tableau d'octets limités à 32k, utile pour envoyer des métadonnées ou des messages de contrôles (l'option que nous avons choisie),
- `FILE`: Pour envoyer des fichiers de tailles arbitraires,
- `STREAM`: Un flux de données est généré à la volée comme pour un flux audio ou vidéo.

4. Déconnexion

Les deux appareils se déconnectent l'un de l'autre.

Les phases de connexions peuvent être associées à des callbacks (addOnSuccessListener, addOnFailureListener, etc...) qui nous permet d'ajouter notre logique lors des différents états de la connexion.

## Difficultés

### Authentification de connexions

Il y a la possibilité d'ouvrir un Dialog lorsqu'une tentative de connexion se fait entre 2 appareils pour afficher un code (token) commun et laisser les 2 utilisateurs comparer les code pour ensuite accepter la connexion. Nous avons choisi de ne pas le faire pour ce projet pour simplifier l'application, mais en réalité il faut absolument le faire pour des connexions BLE pour éviter d'exposer des failles de sécurités.

### Mise à jour des listes

La mise à jour de la liste basée sur une live data mutable ne se faisait pas. Après plusieurs tentatives nous avons finalement reussi à faire fonctionner la mise à jour de la liste. Il faut cependant faire une copie de la liste pour la reposter dans la live data.

### Utilisation des ViewModels et du contexte

Lors de la phase de factorisation du code. Nous avons voulu placer la logique liée aux connections BLE et échanges de données dans les ViewModels des activités et fragments. Cependant, la plupart de ces fonctionnalités ont besoin du contexte de l'application pour effectuer des opérations. Il n'était pas envisageable de stocker le contexte dans les ViewModels car si l'activité est stoppée, la référence vers le contexte n'est plus valide.

Nous avons premièrement envisagé d'utiliser AndroidViewModel au lieu de ViewModel car il est dit que ces alternatives possèdent une référence vers le contexte. Cependant, après avoir changé les ViewModel en AndroidViewModel, le BLE ne fonctionnait plus.

Finalement nous avons décidé garder les éléments nécessitant le contexte dans l'activité et de placer dans le ViewModel uniquement la logique intéragissant avec les données de l'application. Par exemple, la connection à un host se fait dans le code de l'activité mais l'ajout des données reçues par BLE dans la liste est fait dans le ViewModel.

## Problèmes connus

### Problèmes de déconnexions 

Lorsqu'on quitte une activité / fragment ayant une connexion BLE, ce dernier ne se déconnecte pas toujours apparemment.

Il y a aussi des problèmes où le Client ne se déconnecte pas vraiment du Host lorsque ce dernier quitte l'activité de réponse.

Ces problèmes étant compliqués à débugger (mettre des logs partout, tester avec plusieurs appareils, etc...) nous avons pas pris le temps de les régler.

### Problème de découverte

Parfois un des appareils n'arrivent pas à démarrer le "discovery" de endpoint BLE. C'est sûrement dû à un problème de permissions (même si elles sont dans le manifest)

### Problème de redécouverte

Lorsqu'un appareil à découvert l'annonceur, qu'il quitte l'application ou désactive le Bluetooth pour le réactiver, il n'est pas possible de redécouvrir l'annonceur.

### Problème de réponses

Il arrive parfois que l'envoi de réponse ne fonctionne pas. Quand on fait *Send*, l'activité se ferme (comme prévu) mais la réponse n'atteint pas le Host. Il faut se reconnecter à la même Room et renvoyer une réponse pour que cela fonctionne.

### Envoie lent du contenu de la question

Lorsqu'un Client se connecte à une Room, ce dernier attend qu'on lui envoie le contenu de la question, cependant cette attente peut parfois prendre plusieurs secondes.

## Conclusion

Malgré le fait que l'application ne soit pas sans défauts, elle reste utilisables pour son but principal qui était de faire une application permettant à un Host d'ouvrir un vote et les Participant de le rejoindre et d'y voter. Le BLE est une technologie compliquée à utiliser toute seule avec Android cependant l'API Nearby Connections a été d'une grande aide pour établir des connexions facile et rapide entre les appareils sans trop se soucier des détails bas-niveaux du BLE.
