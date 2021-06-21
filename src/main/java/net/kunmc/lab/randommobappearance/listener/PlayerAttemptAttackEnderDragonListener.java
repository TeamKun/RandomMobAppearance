package net.kunmc.lab.randommobappearance.listener;

import net.kunmc.lab.randommobappearance.RandomMobAppearance;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerAttemptAttackEnderDragonListener implements Listener {
    private final Map<UUID, Boolean> uuidToCoolDownFlagMap = new HashMap<>();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (!(e.getAction().equals(Action.LEFT_CLICK_AIR))) {
            return;
        }

        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();

        uuidToCoolDownFlagMap.putIfAbsent(uuid, false);
        if (uuidToCoolDownFlagMap.get(uuid)) {
            return;
        }
        uuidToCoolDownFlagMap.put(uuid, true);
        new BukkitRunnable() {
            @Override
            public void run() {
                uuidToCoolDownFlagMap.put(uuid, false);
            }
        }.runTaskLater(RandomMobAppearance.getInstance(), 12);

        LivingEntity enderDragon = Bukkit.selectEntities(p, "@e[type=minecraft:ender_dragon]").stream()
                .map(x -> ((LivingEntity) x))
                .findFirst()
                .orElse(null);
        if (enderDragon == null) {
            return;
        }

        boolean shouldDamage = enderDragon.getBoundingBox().overlaps(p.getEyeLocation().toVector(),
                p.getEyeLocation().add(p.getEyeLocation().getDirection().multiply(4)).toVector());
        if (shouldDamage) {
            double damage = calcDamage(e.getItem());
            enderDragon.setHealth(enderDragon.getHealth() - damage < 0 ? 0 : enderDragon.getHealth() - damage);
            enderDragon.getWorld().playSound(enderDragon.getLocation(), Sound.ENTITY_ENDER_DRAGON_HURT, 0.5F, 1);
        }
    }

    private double calcDamage(ItemStack item) {
        if (item == null) {
            return 1.25;
        }

        Material material = item.getType();
        double sharpnessLevel = item.getEnchantmentLevel(Enchantment.DAMAGE_ALL);

        double damage;
        switch (material) {
            case NETHERITE_SWORD:
                damage = 8;
                break;
            case DIAMOND_SWORD:
                damage = 7;
                break;
            case IRON_SWORD:
                damage = 6;
                break;
            case STONE_SWORD:
                damage = 5;
                break;
            case GOLDEN_SWORD:
                damage = 4;
                break;
            case WOODEN_SWORD:
                damage = 4;
                break;
            case NETHERITE_AXE:
                damage = 10;
                break;
            case DIAMOND_AXE:
                damage = 9;
                break;
            case IRON_AXE:
                damage = 9;
                break;
            case STONE_AXE:
                damage = 9;
                break;
            case GOLDEN_AXE:
                damage = 7;
                break;
            case WOODEN_AXE:
                damage = 7;
                break;
            default:
                damage = 1;
        }

        return (damage + ((1 + sharpnessLevel) / 2)) / 4 + 1;
    }
}
