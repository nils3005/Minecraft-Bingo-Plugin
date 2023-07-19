package de.nils.bingo.utils;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Random;

public class ItemSelector {
    private static ArrayList<Material> allItems = new ArrayList<>();

    /*
    Die Klasse verwaltet alle Items, die dem Plugin zur Verfügung stehen.
     */
    static {
        addItem(Material.SUGAR_CANE);
        addItem(Material.WHEAT_SEEDS);
        addItem(Material.WHEAT);
        addItem(Material.GRANITE);
        addItem(Material.PUMPKIN);
        addItem(Material.JUKEBOX);
        addItem(Material.DIAMOND);
        addItem(Material.FISHING_ROD);
        addItem(Material.OBSERVER);
        addItem(Material.BOW);
        addItem(Material.FEATHER);
        addItem(Material.COMPASS);
        addItem(Material.CLOCK);
        addItem(Material.SHIELD);
        addItem(Material.SHEARS);
        addItem(Material.WATER_BUCKET);
        addItem(Material.MILK_BUCKET);
        addItem(Material.LAVA_BUCKET);
        addItem(Material.BUCKET);
        addItem(Material.FLINT_AND_STEEL);
        addItem(Material.LECTERN);
        addItem(Material.NETHERRACK);
        addItem(Material.NAME_TAG);
        addItem(Material.APPLE);
        addItem(Material.LANTERN);
        addItem(Material.ARROW);
        addItem(Material.BLAST_FURNACE);
        addItem(Material.RAIL);
        addItem(Material.BAKED_POTATO);
        addItem(Material.LEATHER_HELMET);
        addItem(Material.BELL);
        addItem(Material.RED_BED);
        addItem(Material.BONE_BLOCK);
        addItem(Material.BRICK);
        addItem(Material.BREWING_STAND);
        addItem(Material.CARROT);
        addItem(Material.COAL_BLOCK);
        addItem(Material.CORNFLOWER);
        addItem(Material.COBWEB);
        addItem(Material.ENDER_PEARL);
        addItem(Material.ANDESITE);
    }

    /*
    Die Methode fügt der lokalen Itemliste ein Item hinzu, wenn es nicht bereits vorhanden ist.
     */
    private static void addItem(Material material) {
        if (!allItems.contains(material)){
            allItems.add(material);
        }
    }

    /*
    Die Methode sucht aus dem Gesamtbestand der Items, 9 zufällige Startitems.
     */
    public static ArrayList<Material> getGameItems() {
        ArrayList<Material> result = new ArrayList<>();
        Random r = new Random();
        Material m;
        for (int i = 0; i < 9; i++) {
            do {
                m = allItems.get(r.nextInt(allItems.size()));
            } while (result.contains(m) || !m.isItem());
            result.add(m);
        }
        return result;
    }
}
