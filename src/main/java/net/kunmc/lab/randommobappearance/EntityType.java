package net.kunmc.lab.randommobappearance;

import java.util.Arrays;

//BukkitのEntityTypeのEntityType型のtypeIdは何故かマイクラの仕様と異なるのでエンティティ名とtypeIdの対応表を作成した.
public enum EntityType {
    ARMOR_STAND(1),
    ARROW(2),
    BAT(3),
    BEE(4),
    BLAZE(5),
    CAT(7),
    CAVE_SPIDER(8),
    CHICKEN(9),
    COD(10),
    COW(11),
    CREEPER(12),
    DOLPHIN(13),
    DONKEY(14),
    DROWNED(16),
    ELDER_GUARDIAN(17),
    END_CRYSTAL(18),
    ENDER_DRAGON(19),
    ENDERMAN(20),
    ENDERMITE(21),
    EVOKER(22),
    FOX(28),
    GHAST(29),
    GIANT(30),
    GUARDIAN(31),
    HOGLIN(32),
    HORSE(33),
    HUSK(34),
    ILLUSIONER(35),
    IRON_GOLEM(36),
    LLAMA(42),
    MAGMA_CUBE(44),
    MULE(52),
    MOOSHROOM(53),
    OCELOT(54),
    PANDA(56),
    PARROT(57),
    PHANTOM(58),
    PIG(59),
    PIGLIN(60),
    PIGLIN_BRUTE(61),
    PILLAGER(62),
    POLAR_BEAR(63),
    PUFFERFISH(65),
    RABBIT(66),
    RAVAGER(67),
    SALMON(68),
    SHEEP(69),
    SHULKER(70),
    SILVERFISH(72),
    SKELETON(73),
    SKELETON_HORSE(74),
    SLIME(75),
    SNOW_GOLEM(77),
    SPIDER(80),
    SQUID(81),
    STRAY(82),
    STRIDER(83),
    TRADER_LLAMA(89),
    TROPICAL_FISH(90),
    TURTLE(91),
    VEX(92),
    VILLAGER(93),
    VINDICATOR(94),
    WANDERING_TRADER(95),
    WITCH(96),
    WITHER(97),
    WITHER_SKELETON(98),
    WOLF(100),
    ZOGLIN(101),
    ZOMBIE(102),
    ZOMBIE_HORSE(103),
    ZOMBIE_VILLAGER(104),
    ZOMBIFIED_PIGLIN(105);

    private final int id;

    private EntityType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static EntityType valueOf(int id) {
        return Arrays.stream(EntityType.values())
                .filter(x -> x.getId() == id)
                .findFirst()
                .orElse(null);
    }
}
