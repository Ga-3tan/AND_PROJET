# HEIG-VD | AND - Projet

> Auteurs : Zwick Gaétan, Maziero Marco, Lamrani Soulaymane

## Description du projet

Le but de ce projet de cours est de mettre en place et utiliser une
fonctionnalité avancée du système Android. Parmi la liste des différentes idées
que nous avions à choix, nous avons choisi le Bluetooth low energy (BLE).

Notre application permet donc à un utilisateur qu'on appellera Host de créer une
« room » en lui donnant un nom ainsi qu'une question. Un autre utilisateur,
appelé Participant, pourra alors aller sur une liste des différentes rooms
existantes et se connecter à la room qu'il souhaite rejoindre. Une fois la room
rejoint, Participant pourra répondre à la question en replissant le champ de
texte prévu à cet effet et Host recevra la réponse ce qui conduira Participant à
quitter la room. Host ne retiendra que la première réponse de Participant si ce
dernier voulait re-rentrer dans la room et répondre à nouveau à la question.

## Technologies utilisées

### BLE

BLE est un standard ouvert basé sur Bluetooth qui consomme jusqu'à 10 fois moins
d'énergie que le Bluetooth de base, ce qui le rend idéal pour des appareils tels
que les smartwatches, appareils de surveillance médicale, car il est surtout
utilisé pour des applications n'ayant pas besoin d'échanger de grandes quantités
de données comme l'heure ou la température.

#### Google Nearby

TODO

## Difficultés

### Mise à jour des listes

La mise à jour de la liste basée sur une live data mutable ne se faisait pas. Après plusieurs tentatives nous avons finalement reussi à faire fonctionner la mise à jour de la liste. Il faut cependant faire une copie de la liste pour la reposter dans la live data.

### Utilisation des ViewModels et du contexte

Lors de la phase de factorisation du code. Nous avons voulu placer la logique liée aux connections BLE et échanges de données dans les ViewModels des activités et fragments. Cependant, la plupart de ces fonctionnalités ont besoin du contexte de l'application pour effectuer des opérations. Il n'était pas envisageable de stocker le contexte dans les ViewModels car si l'activité est stoppée, la référence vers le contexte n'est plus valide.

Nous avons premièrement envisagé d'utiliser AndroidViewModel au lieu de ViewModel car il est dit que ces alternatives possèdent une référence vers le contexte. Cependant, après avoir changé les ViewModel en AndroidViewModel, le BLE ne fonctionnait plus.

Finalement nous avons décidé garder les éléments nécessitant le contexte dans l'activité et de placer dans le ViewModel uniquement la logique intéragissant avec les données de l'application. Par exemple, la connection à un host se fait dans le code de l'activité mais l'ajout des données reçues par BLE dans la liste est fait dans le ViewModel.

## Problèmes connus

### (problèmes de déconnection ?)
