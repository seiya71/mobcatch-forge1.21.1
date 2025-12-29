package jp.stach.mobcatch.client;

import com.mojang.blaze3d.vertex.PoseStack;
import jp.stach.mobcatch.CapturedMobItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class CapturedMobItemRenderer extends BlockEntityWithoutLevelRenderer {

    public static final CapturedMobItemRenderer INSTANCE = new CapturedMobItemRenderer();

    private static int dbg = 0;

    public CapturedMobItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),
              Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(ItemStack stack,
                             ItemDisplayContext ctx,
                             PoseStack pose,
                             MultiBufferSource buffer,
                             int packedLight,
                             int packedOverlay) {

        if ((dbg++ % 60) == 0) {
            System.out.println("[MobCatch] renderByItem ctx=" + ctx + " item=" + stack.getItem());
        }

        Minecraft mc = Minecraft.getInstance();
        Level level = mc.level;
        if (level == null) return;

        LivingEntity living = createLivingForRender(stack, level);
        if (living == null) return;

        // 捕獲データがあるなら反映（共通仕様）
        applyCapturedDataIfPresent(stack, living);

        pose.pushPose();

        // アイテム枠中心＆縮小
        pose.translate(0.5D, 0.0D, 0.5D);
        float scale = 0.5F;
        pose.scale(scale, scale, scale);

        // 正面固定
        living.setYRot(0.0F);
        living.setXRot(0.0F);
        living.yBodyRot = 0.0F;
        living.yHeadRot = 0.0F;
        living.yHeadRotO = 0.0F;

        EntityRenderDispatcher dispatcher = mc.getEntityRenderDispatcher();
        dispatcher.render(living, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F, pose, buffer, packedLight);

        pose.popPose();
    }

    /**
     * 共通仕様：
     * - CapturedMobItem なら item が持つ EntityType を使う
     * - 取れない/違うアイテムなら牛にフォールバック（今の挙動維持）
     */
    private LivingEntity createLivingForRender(ItemStack stack, Level level) {
        EntityType<?> type = EntityType.COW; // フォールバック（今は牛だけ表示したい）

        if (stack.getItem() instanceof CapturedMobItem captured) {
            Optional<EntityType<?>> opt = captured.getEntityType();
            if (opt.isPresent()) {
                type = opt.get();
            }
        }

        Entity entity = type.create(level);
        if (!(entity instanceof LivingEntity living)) return null;
        return living;
    }

    /**
     * 共通仕様：ItemStack の CUSTOM_DATA に EntityTag があればロードする
     */
    private void applyCapturedDataIfPresent(ItemStack stack, LivingEntity living) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null) return;

        CompoundTag root = data.copyTag();
        if (!root.contains(CapturedMobItem.TAG_ENTITY_TAG, CompoundTag.TAG_COMPOUND)) return;

        CompoundTag entityTag = root.getCompound(CapturedMobItem.TAG_ENTITY_TAG).copy();
        entityTag.remove("UUID");
        entityTag.remove("Pos");
        entityTag.remove("Rotation");
        entityTag.remove("Motion");

        living.load(entityTag);
    }
}

