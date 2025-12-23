package jp.stach.mobcatch;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems {
    private ModItems() {}

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MobCatch.MODID);

    // Phase A: ダミーの1個だけ登録（後でモブ数ぶん増やす）
    public static final RegistryObject<Item> MOB_CAPTURE =
            ITEMS.register("mob_capture", () -> new Item(new Item.Properties()));
}
