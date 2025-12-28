package jp.stach.mobcatch;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(MobCatch.MOD_ID)
public class MobCatch {
    public static final String MOD_ID = "mobcatch";

    public MobCatch() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.register(modEventBus);

        ModItems.bootstrapCapturedItems();

        ModCreativeTabs.register(modEventBus);
    }
}

