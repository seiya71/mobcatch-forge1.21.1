package jp.stach.mobcatch;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(MobCatch.MODID)
public class MobCatch {
    public static final String MODID = "mobcatch";

    // ★ Forge が呼び出すコンストラクタ
    public MobCatch(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        // 全モブ分の捕獲アイテムを登録予約
        ModItems.bootstrapCaptureItems();

        // 通常の DeferredRegister 登録
        ModItems.register(modEventBus);
        ModCreativeTabs.register(modEventBus);
    }
}

