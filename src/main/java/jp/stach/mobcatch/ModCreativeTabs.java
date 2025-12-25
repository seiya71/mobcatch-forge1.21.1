package jp.stach.mobcatch;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

@net.minecraftforge.fml.common.Mod.EventBusSubscriber(modid = MobCatch.MODID, bus = net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus.MOD)
public final class ModCreativeTabs {
    private ModCreativeTabs() {}

    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MobCatch.MODID);

    public static final RegistryObject<CreativeModeTab> MOB_CATCH_TAB =
            TABS.register("mobcatch", () ->
                    CreativeModeTab.builder()
                            .withTabsBefore(CreativeModeTabs.COMBAT)
                            .icon(() -> {
                                if (ModItems.CAPTURED_ITEMS.isEmpty()) {
                                    return ItemStack.EMPTY;
                                }
                                return ModItems.CAPTURED_ITEMS
                                        .values()
                                        .iterator()
                                        .next()
                                        .get()
                                        .getDefaultInstance();
                            })
                            .title(Component.translatable("itemGroup.mobcatch.mobcatch"))
                            .displayItems((params, output) -> {
                                ModItems.CAPTURED_ITEMS
                                        .values()
                                        .forEach(ro -> output.accept(ro.get()));
                            })
                            .build()
            );

    public static void register(IEventBus modEventBus) {
        TABS.register(modEventBus);
    }
}
