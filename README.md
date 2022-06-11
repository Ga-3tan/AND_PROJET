# HEIG-VD | AND - Projet

> Auteurs : Zwick Gaétan, Maziero Marco, Lamrani Soulaymane

## Description du projet

TODO

## Technologies utilisées

### BLE

TODO

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
