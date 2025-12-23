package jp.stach.mobcatch;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(MobCatch.MODID)
public class MobCatch {
    public static final String MODID = "mobcatch";

    // Forge 1.21.1 MDKのテンプレ形式に寄せる（get()系の差分を避ける）
    public MobCatch(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        // DeferredRegister を MODイベントバスへ登録
        ModItems.ITEMS.register(modEventBus);
        ModCreativeTabs.TABS.register(modEventBus);
    }
}
