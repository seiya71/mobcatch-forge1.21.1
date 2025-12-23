package jp.stach.mobcatch;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class ModCreativeTabs {
    private ModCreativeTabs() {}

    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MobCatch.MODID);

    // Phase A: MobCatch 専用タブを1個作る
    public static final RegistryObject<CreativeModeTab> MOBCATCH_TAB =
            TABS.register("mobcatch", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.mobcatch")) // langで表示名を付ける
                    .icon(() -> ModItems.MOB_CAPTURE_ZOMBIE.get().getDefaultInstance())
                    .displayItems((params, output) -> {
                        output.accept(ModItems.MOB_CAPTURE_ZOMBIE.get());
                        output.accept(ModItems.MOB_CAPTURE_COW.get());
                        output.accept(ModItems.MOB_CAPTURE_VILLAGER.get());
                    })
                    .build());
}
