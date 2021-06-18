package net.kunmc.lab.randommobappearance.util;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class Utils {
    public static List<Entity> getAllMobs() {
        return Bukkit.getWorlds().stream()
                .map(World::getEntities)
                .flatMap(Collection::stream)
                .filter(ent -> ent instanceof LivingEntity && !(ent instanceof Player))
                .collect(Collectors.toList());
    }
}
