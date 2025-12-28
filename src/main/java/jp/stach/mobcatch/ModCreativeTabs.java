package jp.stach.mobcatch;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class ModCreativeTabs {
    private ModCreativeTabs() {}

    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MobCatch.MOD_ID);

    public static final RegistryObject<CreativeModeTab> MOB_CATCH_TAB =
            TABS.register("mobcatch", () -> CreativeModeTab.builder()
                    .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
                    .title(Component.translatable("itemGroup.mobcatch"))
                    .icon(() -> {
                        if (!ModItems.CAPTURED_ITEMS.isEmpty()) {
                            return ModItems.CAPTURED_ITEMS.values()
                                    .iterator()
                                    .next()
                                    .get()
                                    .getDefaultInstance();
                        }
                        return new ItemStack(Items.STICK);
                    })
                    .displayItems((params, output) -> {
                        ModItems.CAPTURED_ITEMS.values()
                            .forEach(reg -> output.accept(reg.get()));
                    })
                    .build()
            );

    public static void register(IEventBus modEventBus) {
        TABS.register(modEventBus);
    }
}
