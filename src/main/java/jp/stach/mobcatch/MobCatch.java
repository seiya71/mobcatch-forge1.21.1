package jp.stach.mobcatch;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(MobCatch.MODID)
public class MobCatch {
    public static final String MODID = "mobcatch";

    public MobCatch(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        // ★C：登録より前に “登録予約” を作る
        ModItems.bootstrapCaptureItems();

        ModItems.ITEMS.register(modEventBus);
        ModCreativeTabs.TABS.register(modEventBus);
    }
}
