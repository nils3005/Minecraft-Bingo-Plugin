package de.nils.bingo.utils;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;

public class Utils {
    /*
    Die Methode wandelt den Material-Namen eines Items in eine "schönere" Form um.
     */
    public static String getItemName(Material material){
        return WordUtils.capitalize(material.name().replace("_", " ").toLowerCase());
    }
}
