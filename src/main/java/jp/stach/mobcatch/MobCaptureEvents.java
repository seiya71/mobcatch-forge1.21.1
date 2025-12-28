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


@Mod.EventBusSubscriber(modid = MobCatch.MOD_ID) // MOD_ID統一してる前提
public final class MobCaptureEvents {
    private MobCaptureEvents() {}

    @SubscribeEvent
    public static void onEntityRightClick(PlayerInteractEvent.EntityInteract event) {
        Player player = event.getEntity();
        InteractionHand hand = event.getHand();
        Entity target = event.getTarget();

        // Shift（スニーク）中じゃなければ何もしない
        if (!player.isShiftKeyDown()) return;

        // 右手だけ処理（二重発火防止）
        if (hand != InteractionHand.MAIN_HAND) return;

        // サーバー側だけで捕獲処理する
        if (player.level().isClientSide) return;

        // LivingEntityだけ対象
        if (!(target instanceof LivingEntity living)) return;

        EntityType<?> type = living.getType();

        // プレイヤーは対象外
        if (type == EntityType.PLAYER) return;

        // 雑多カテゴリは対象外（防具立て等）
        if (type.getCategory() == MobCategory.MISC) return;

        // “このモブ専用の捕獲アイテム” を Map から探す
        var reg = ModItems.CAPTURED_ITEMS.get(type);
        if (reg == null) return;

        // 捕獲アイテムを生成
        ItemStack captured = new ItemStack(reg.get());

        // 個体が混ざらないようにUUID付与
        CapturedMobItem.ensureUUID(captured);

        // （段階③の入口）EntityのNBTを丸ごと保存（まずは箱として入れる）
        CompoundTag entityTag = new CompoundTag();
        // UUIDや位置なども含めて保存される。後でスポーン時に調整する
        living.saveWithoutId(entityTag);
        CustomData.update(DataComponents.CUSTOM_DATA, captured, t -> {
            t.put("EntityTag", entityTag);
        });
        // プレイヤーに渡す（入らなければ足元に落とす）
        if (!player.getInventory().add(captured)) {
            player.drop(captured, false);
        }

        // モブを消す（捕獲完了）
        living.discard();

        // 右クリック本来の動作（乗る/取引/開く等）を止める
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.CONSUME);
    }
}
