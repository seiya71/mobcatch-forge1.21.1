package jp.stach.mobcatch;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems {
    private ModItems() {}

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MobCatch.MODID);

    // B-2: まずは“同じ挙動の別ID”を複数作る
    public static final RegistryObject<Item> MOB_CAPTURE_ZOMBIE =
            ITEMS.register("mob_capture_zombie", () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> MOB_CAPTURE_COW =
            ITEMS.register("mob_capture_cow", () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> MOB_CAPTURE_VILLAGER =
            ITEMS.register("mob_capture_villager", () -> new Item(new Item.Properties()));
}

