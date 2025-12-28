package jp.stach.mobcatch;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(MobCatch.MODID)
public class MobCatch {
    public static final String MODID = "mobcatch";

    public MobCatch() {
        // Forge 1.21.1 の標準パターン：0 引数コンストラクタ ＋ get()
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // ① DeferredRegister<Item> を EventBus に登録
        ModItems.register(modEventBus);

        // ② LivingEntity 系モブだけの捕獲アイテムを全部予約登録
        ModItems.bootstrapCapturedItems();

        // ③ クリエタブ登録（中身は CAPTURED_ITEMS を参照）
        ModCreativeTabs.register(modEventBus);
    }
}

