package jp.stach.mobcatch.datagen;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

public class CapturedItemModelProvider extends ItemModelProvider {

    public CapturedItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, "mobcatch", existingFileHelper);
    }

    @Override
    protected void registerModels() {

        getBuilder("captured_base")
                .parent(new ModelFile.UncheckedModelFile("minecraft:builtin/entity"));

        for (ResourceLocation id : BuiltInRegistries.ENTITY_TYPE.keySet()) {
            EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(id);
            if (type == null) {
                continue;
            }

            // バニラだけなら解除
            // if (!"minecraft".equals(id.getNamespace())) continue;
            if (type == EntityType.PLAYER) {
                continue;
            }

            boolean isVillagerLike
                    = type == EntityType.VILLAGER
                    || type == EntityType.WANDERING_TRADER;

            if (!isVillagerLike && type.getCategory() == MobCategory.MISC) {
                continue;
            }

            String itemId = "captured_" + id.getNamespace() + "_" + id.getPath();
            withExistingParent(itemId, modLoc("item/captured_base"));
        }
    }
}
