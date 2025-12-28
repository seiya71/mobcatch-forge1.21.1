package jp.stach.mobcatch;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;


@Mod.EventBusSubscriber(modid = MobCatch.MOD_ID)
public final class MobCaptureEvents {
    private MobCaptureEvents() {}

    @SubscribeEvent
    public static void onEntityRightClick(PlayerInteractEvent.EntityInteract event) {
        Player player = event.getEntity();
        InteractionHand hand = event.getHand();
        Entity target = event.getTarget();

        if (!player.isShiftKeyDown()) return;

        if (hand != InteractionHand.MAIN_HAND) return;

        if (player.level().isClientSide) return;

        if (!(target instanceof LivingEntity living)) return;

        EntityType<?> type = living.getType();

        if (type == EntityType.PLAYER) return;

        if (type.getCategory() == MobCategory.MISC) return;

        var reg = ModItems.CAPTURED_ITEMS.get(type);
        if (reg == null) return;

        ItemStack captured = new ItemStack(reg.get());

        CapturedMobItem.ensureUUID(captured);

        CompoundTag entityTag = new CompoundTag();
        living.saveWithoutId(entityTag);
        CustomData.update(DataComponents.CUSTOM_DATA, captured, t -> {
            t.put("EntityTag", entityTag);
            t.putString("CapturedDisplayName", living.getName().getString());
        });

        if (!player.getInventory().add(captured)) {
            player.drop(captured, false);
        }

        living.discard();

        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.CONSUME);
    }
}
