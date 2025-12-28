package jp.stach.mobcatch;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import java.util.UUID;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

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
        Component mobName = getEntityType()
                .map(t -> Component.translatable(t.getDescriptionId()))
                .orElse(Component.literal(entityId.toString()));

        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null) return mobName;

        CompoundTag tag = data.copyTag();
        if (!tag.contains(TAG_ENTITY_TAG, CompoundTag.TAG_COMPOUND)) {
            return mobName;
        }

        String display = tag.getString(TAG_DISPLAY_NAME);

        if (!display.isEmpty() && !display.equals(mobName.getString())) {
            return Component.literal(display);
        }

        return mobName;
    }


    public static final String TAG_CAPTURE_UUID = "CaptureUUID";

    public static void ensureUUID(ItemStack stack) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
            if (!tag.hasUUID(TAG_CAPTURE_UUID)) {
                tag.putUUID(TAG_CAPTURE_UUID, UUID.randomUUID());
            }
        });
    }

    public static final String TAG_ENTITY_TAG = "EntityTag";

    public static final String TAG_DISPLAY_NAME = "CapturedDisplayName";

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        Level level = ctx.getLevel();
        if (level.isClientSide) return InteractionResult.SUCCESS;

        Player player = ctx.getPlayer();
        if (player == null) return InteractionResult.FAIL;

        ItemStack stack = ctx.getItemInHand();

        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null) return InteractionResult.FAIL;

        CompoundTag root = data.copyTag();
        if (!root.contains(TAG_ENTITY_TAG)) return InteractionResult.FAIL;

        CompoundTag entityTag = root.getCompound(TAG_ENTITY_TAG).copy();

        Optional<EntityType<?>> optType = getEntityType();
        if (optType.isEmpty()) return InteractionResult.FAIL;

        EntityType<?> type = optType.get();
        Entity entity = type.create(level);
        if (!(entity instanceof LivingEntity living)) return InteractionResult.FAIL;

        BlockPos spawnPos = ctx.getClickedPos().relative(ctx.getClickedFace());
        double x = spawnPos.getX() + 0.5;
        double y = spawnPos.getY();
        double z = spawnPos.getZ() + 0.5;

        entityTag.remove("UUID");
        entityTag.remove("Pos");
        entityTag.remove("Rotation");
        entityTag.remove("Motion");

        living.moveTo(x, y, z, player.getYRot(), 0.0F);
        living.load(entityTag);
        living.moveTo(x, y, z, player.getYRot(), 0.0F);

        if (!level.noCollision(living)) {
            return InteractionResult.FAIL;
        }

        boolean added = level.addFreshEntity(living);
        if (!added) return InteractionResult.FAIL;

        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }

        return InteractionResult.CONSUME;
    }
}
