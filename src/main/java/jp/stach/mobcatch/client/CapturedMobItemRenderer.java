package jp.stach.mobcatch.client;

import com.mojang.blaze3d.vertex.PoseStack;
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

        // ★ 今は牛固定（将来ここをEntityId参照にする）
        Entity entity = EntityType.COW.create(level);
        if (!(entity instanceof LivingEntity living)) return;

        // 捕獲データがあるなら反映
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data != null) {
            CompoundTag root = data.copyTag();
            if (root.contains("EntityTag", CompoundTag.TAG_COMPOUND)) {
                CompoundTag entityTag = root.getCompound("EntityTag").copy();
                entityTag.remove("UUID");
                entityTag.remove("Pos");
                entityTag.remove("Rotation");
                entityTag.remove("Motion");
                living.load(entityTag);
            }
        }

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
}
