package jp.stach.mobcatch;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class CapturedMobItem extends Item {

    private final ResourceLocation entityId;

    public CapturedMobItem(Properties props, ResourceLocation entityId) {
        super(props);
        this.entityId = entityId;
    }

    public Optional<EntityType<?>> getEntityType() {
        return Optional.ofNullable(BuiltInRegistries.ENTITY_TYPE.get(entityId));
    }

    @Override
    public Component getName(ItemStack stack) {
        // バニラの「モブ名」をそのまま翻訳キーで引く
        Component mobName = getEntityType()
                .map(t -> Component.translatable(t.getDescriptionId()))
                .orElse(Component.literal(entityId.toString()));

        // ひとまず「捕獲/空」の区別は後回し。
        // まずはモブ名だけちゃんと出ることを確認する。
        return mobName;
    }
}
