package com.heigvd.and_projet.model

/**
 * Represents a list item with a title and a content. It is liked to a BLE endpoint id
 * Authors : Zwick Gaétan, Maziero Marco, Lamrani Soulaymane
 * Date : 10.06.2022
 */
data class ListRecord(val title: String, val content: String, val endPointId: String)