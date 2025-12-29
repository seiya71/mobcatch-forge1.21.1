package jp.stach.mobcatch.datagen;

import jp.stach.mobcatch.MobCatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

public class CapturedItemModelProvider extends ItemModelProvider {

    public CapturedItemModelProvider(PackOutput out, ExistingFileHelper existing) {
        super(out, MobCatch.MOD_ID, existing);
    }

    @Override
    protected void registerModels() {
        // 親モデル（存在チェックを回避するため Unchecked を使う）
        getBuilder("captured_base")
                .parent(new ModelFile.UncheckedModelFile("minecraft:builtin/entity"));

        for (ResourceLocation id : BuiltInRegistries.ENTITY_TYPE.keySet()) {
            EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(id);
            if (type == null) continue;

            if (type == EntityType.PLAYER) continue;
            if (type.getCategory() == MobCategory.MISC) continue;

            // まずはバニラだけにしたいならこれON
            // if (!"minecraft".equals(id.getNamespace())) continue;

            String itemId = "captured_" + id.getNamespace() + "_" + id.getPath();
            withExistingParent(itemId, modLoc("item/captured_base"));
        }
    }
}
