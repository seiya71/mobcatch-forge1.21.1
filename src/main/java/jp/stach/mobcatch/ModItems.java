package jp.stach.mobcatch;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.LinkedHashMap;
import java.util.Map;

public final class ModItems {
    private ModItems() {}

    // この DeferredRegister 自体は「mobcatch」名前空間
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MobCatch.MODID);

    /**
     * EntityType -> そのモブ用の捕獲アイテム
     */
    public static final Map<EntityType<?>, RegistryObject<Item>> CAPTURED_ITEMS = new LinkedHashMap<>();

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }

    /**
     * Living 系モブ ＆ Player 以外だけを対象に、捕獲アイテムを動的に全部予約する。
     * （バニラ minecraft 名前空間のみ）
     */
    public static void bootstrapCapturedItems() {
        if (!CAPTURED_ITEMS.isEmpty()) return; // 二重実行防止

        for (ResourceLocation id : BuiltInRegistries.ENTITY_TYPE.keySet()) {
            // まず EntityType を引く
            EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(id);
            if (type == null) continue;

            // バニラだけ対象
            if (!"minecraft".equals(id.getNamespace())) continue;

            // プレイヤーは対象外
            if (type == EntityType.PLAYER) continue;

            // Living 的なものだけにしたいので、ざっくり MobCategory.MISC を除外
            MobCategory cat = type.getCategory();
            if (cat == MobCategory.MISC) continue;

            // ここまで通ったものだけ捕獲アイテム生成
            String itemId = "captured_" + id.getNamespace() + "_" + id.getPath();

            RegistryObject<Item> reg = ITEMS.register(
                    itemId,
                    () -> new CapturedMobItem(
                            new Item.Properties(),
                            id          // CapturedMobItem には「どの EntityType か」の ID を渡す
                    )
            );

            CAPTURED_ITEMS.put(type, reg);
        }
    }
}
