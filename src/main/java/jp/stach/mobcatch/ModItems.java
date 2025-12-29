package jp.stach.mobcatch;

import java.util.LinkedHashMap;
import java.util.Map;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems {

    private ModItems() {
    }

    public static final DeferredRegister<Item> ITEMS
            = DeferredRegister.create(ForgeRegistries.ITEMS, MobCatch.MOD_ID);

    public static final Map<EntityType<?>, RegistryObject<Item>> CAPTURED_ITEMS = new LinkedHashMap<>();

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }

    public static void bootstrapCapturedItems() {
        if (!CAPTURED_ITEMS.isEmpty()) {
            return; // 二重実行防止
        }
        for (ResourceLocation id : BuiltInRegistries.ENTITY_TYPE.keySet()) {
            EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(id);
            if (type == null) {
                continue;
            }

            // まずはバニラだけ
            if (!"minecraft".equals(id.getNamespace())) {
                continue;
            }

            if (type == EntityType.PLAYER) {
                continue;
            }

            boolean isVillagerLike
                    = type == EntityType.VILLAGER
                    || type == EntityType.WANDERING_TRADER;

            MobCategory cat = type.getCategory();

            // MISC は基本除外。ただし村人系は通す
            if (!isVillagerLike && cat == MobCategory.MISC) {
                continue;
            }

            String itemId = "captured_" + id.getNamespace() + "_" + id.getPath();

            RegistryObject<Item> reg = ITEMS.register(
                    itemId,
                    () -> new CapturedMobItem(new Item.Properties().stacksTo(1), id)
            );

            CAPTURED_ITEMS.put(type, reg);
        }
    }
}
