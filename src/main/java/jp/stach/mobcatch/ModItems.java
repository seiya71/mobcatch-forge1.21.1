package jp.stach.mobcatch;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.LinkedHashMap;
import java.util.Map;

public final class ModItems {
    private ModItems() {}

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MobCatch.MODID);

    /**
     * EntityType -> 捕獲アイテム
     * C以降（捕獲/復元処理）で引くための辞書
     */
    public static final Map<EntityType<?>, RegistryObject<Item>> CAPTURE_ITEMS = new LinkedHashMap<>();

    /**
     * 【重要】MobCatch のコンストラクタで ITEMS.register(bus) より前に呼ぶこと
     * ここで “登録予約” を大量に作る。
     */
    public static void bootstrapCaptureItems() {
        if (!CAPTURE_ITEMS.isEmpty()) return;

        // まずは様子見：上限を付ける（動いたら Integer.MAX_VALUE にしてOK）
        int limit = 30;
        int count = 0;

        for (ResourceLocation id : BuiltInRegistries.ENTITY_TYPE.keySet()) {
            if (!"minecraft".equals(id.getNamespace())) continue;

            EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(id);
            if (type == null) continue;
            if (type == EntityType.PLAYER) continue; // プレイヤー除外

            String itemId = "captured_" + id.getNamespace() + "_" + id.getPath();

            RegistryObject<Item> reg = ITEMS.register(itemId, () -> new Item(new Item.Properties()));
            CAPTURE_ITEMS.put(type, reg);

            count++;
            if (count >= limit) break;
        }
    }
}

