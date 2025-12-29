package jp.stach.mobcatch.datagen;

import jp.stach.mobcatch.MobCatch;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MobCatch.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModDataGen {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        var gen = event.getGenerator();
        var packOutput = gen.getPackOutput();
        var existing = event.getExistingFileHelper();

        gen.addProvider(event.includeClient(), new CapturedItemModelProvider(packOutput, existing));
    }
}
