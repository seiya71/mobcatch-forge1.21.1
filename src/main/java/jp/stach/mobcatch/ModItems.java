package jp.stach.mobcatch;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.LinkedHashMap;
import java.util.Map;

public final class ModItems {
    private ModItems() {}

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MobCatch.MODID);

    // EntityType -> そのモブ用アイテム
    public static final Map<EntityType<?>, RegistryObject<Item>> CAPTURED_ITEMS = new LinkedHashMap<>();

    /**
     * MobCatch のコンストラクタから一度だけ呼ぶ。
     * 全バニラ LivingEntity（プレイヤー以外）分のアイテムを登録予約する。
     */
    public static void bootstrapCaptureItems() {
        if (!CAPTURED_ITEMS.isEmpty()) return;

        // 1.21 では keySet() は ResourceLocation の集合
        for (ResourceLocation entityId : BuiltInRegistries.ENTITY_TYPE.keySet()) {
            // バニラのみ
            if (!"minecraft".equals(entityId.getNamespace())) continue;

            EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(entityId);
            if (type == null) continue;

            // プレイヤー除外
            if (type == EntityType.PLAYER) continue;

            String itemId = "captured_" + entityId.getNamespace() + "_" + entityId.getPath();

            RegistryObject<Item> reg = ITEMS.register(
                    itemId,
                    () -> new CapturedMobItem(new Item.Properties(), entityId)
            );

            CAPTURED_ITEMS.put(type, reg);
        }
    }

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}
