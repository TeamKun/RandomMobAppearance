package net.kunmc.lab.randommobappearance;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import net.kunmc.lab.randommobappearance.listener.PlayerAttemptAttackEnderDragonListener;
import net.kunmc.lab.randommobappearance.util.Utils;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.*;

public final class RandomMobAppearance extends JavaPlugin {
    /*
     * MapのKeyには特定のエンティティの内部Idが入る
     * Map.EntryのKeyにはMapのKeyに対応するエンティティのインスタンス,Valueには見せかけたいエンティティのTypeIdが入る.
     *
     * エンティティのインスタンスをプールする理由:
     * エンティティが死んだときのサウンドを置き換えるため.
     * Bukkit.selectEntitiesやWorld#getEntitiesでは死んだエンティティを取得できず,サウンドのLocationとエンティティのTypeIdとの対応が
     * 取れないためインスタンスをプールする.
     */
    private final Map<Integer, Map.Entry<Entity, Integer>> entityIdToEntityAndTypeIdMap = new HashMap<>();

    private static JavaPlugin INSTANCE;

    public static JavaPlugin getInstance() {
        return INSTANCE;
    }

    public RandomMobAppearance() {
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this, PacketType.Play.Server.SPAWN_ENTITY_LIVING) {
            final List<EntityType> typeList = new ArrayList<EntityType>(Arrays.asList(EntityType.values())) {{
                remove(EntityType.ARMOR_STAND);
                remove(EntityType.ENDER_DRAGON);
            }};

            @Override
            public void onPacketSending(PacketEvent e) {
                PacketContainer packet = e.getPacket();
                int entityId = packet.getIntegers().read(0);
                if (entityIdToEntityAndTypeIdMap.containsKey(entityId)) {
                    packet.getIntegers().write(1, entityIdToEntityAndTypeIdMap.get(entityId).getValue());
                } else {
                    int typeId;
                    if (new Random().nextInt(200) == 1) {
                        typeId = EntityType.ENDER_DRAGON.getId();
                    } else {
                        typeId = typeList.get(new Random().nextInt(typeList.size())).getId();
                    }

                    Entity entity = Utils.getAllMobs().stream()
                            .filter(x -> x.getEntityId() == entityId)
                            .findFirst()
                            .orElse(null);
                    if (entity == null) {
                        return;
                    }
                    entityIdToEntityAndTypeIdMap.put(entityId, new AbstractMap.SimpleEntry<>(entity, typeId));

                    packet.getIntegers().write(1, typeId);
                }
            }
        });

        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this, PacketType.Play.Server.NAMED_SOUND_EFFECT) {
            @Override
            public void onPacketSending(PacketEvent e) {
                PacketContainer packet = e.getPacket();
                String soundName = packet.getSoundEffects().read(0).getKey().getKey();
                if (!soundName.startsWith("entity.")) {
                    return;
                }
                if (soundName.endsWith("_land")) {
                    soundName = soundName.replace("_land", "");
                }
                if (!soundName.endsWith(".ambient") && !soundName.endsWith(".death") && !soundName.endsWith(".hurt")) {
                    return;
                }

                String categoryName = packet.getSoundCategories().read(0).getKey();
                if (!categoryName.equals("hostile") && !categoryName.equals("neutral")) {
                    return;
                }

                //パケット内部では座標数値が8倍されているので8で除して適切な値にする.
                float x = packet.getIntegers().read(0).floatValue() / 8;
                float y = packet.getIntegers().read(1).floatValue() / 8;
                float z = packet.getIntegers().read(2).floatValue() / 8;
                //座標の位置に存在するエンティティをプールから取得する.
                Entity entity = entityIdToEntityAndTypeIdMap.values().stream()
                        .map(Map.Entry::getKey)
                        .filter(ent -> ent.getLocation().toVector().distance(new Vector(x, y, z)) < 0.25)
                        .findFirst()
                        .orElse(null);
                if (entity == null) {
                    return;
                }

                int entityId = entity.getEntityId();
                String surfaceEntityName = EntityType.valueOf(entityIdToEntityAndTypeIdMap.get(entityId).getValue()).toString().toLowerCase();
                //trader_llamaやcave_spiderはllamaやspiderと同じ音声を使っているため名前を訂正する.
                if (surfaceEntityName.equals("trader_llama")) {
                    surfaceEntityName = "llama";
                } else if (surfaceEntityName.equals("cave_spider")) {
                    surfaceEntityName = "spider";
                }

                String newSoundName = soundName
                        .replaceFirst("\\.[a-z]+\\.", "." + surfaceEntityName + ".");
                Sound sound = Arrays.stream(Sound.values())
                        .filter(s -> s.getKey().getKey().equals(newSoundName))
                        .findFirst()
                        .orElse(null);
                if (sound == null) {
                    e.setCancelled(true);
                    return;
                }
                packet.getSoundEffects().write(0, sound);

                switch (surfaceEntityName) {
                    case "squid":
                    case "rabbit":
                    case "cow":
                    case "horse":
                    case "parrot":
                    case "turtle":
                    case "fox":
                    case "wandering_trader":
                    case "bat":
                    case "salmon":
                    case "skeleton_horse":
                    case "strider":
                    case "snow_golem":
                    case "cod":
                    case "chicken":
                    case "cat":
                    case "tropical_fish":
                    case "sheep":
                    case "puffer_fish":
                    case "pig":
                    case "mooshroom":
                    case "villager":
                    case "ocelot":
                    case "mule":
                    case "donkey":
                        packet.getSoundCategories().write(0, EnumWrappers.SoundCategory.NEUTRAL);
                        break;
                    default:
                        packet.getSoundCategories().write(0, EnumWrappers.SoundCategory.HOSTILE);
                }
            }
        });

        getServer().getPluginManager().registerEvents(new PlayerAttemptAttackEnderDragonListener(), this);
    }

    @Override
    public void onDisable() {
    }
}
