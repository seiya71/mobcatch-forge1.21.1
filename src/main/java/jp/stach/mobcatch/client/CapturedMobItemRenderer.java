package jp.stach.mobcatch.client;

import com.mojang.blaze3d.vertex.PoseStack;

import jp.stach.mobcatch.CapturedMobItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
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
        if (level == null) {
            return;
        }

        LivingEntity living = createLivingForRender(stack, level);
        if (living == null) {
            return;
        }

        pose.pushPose();

        // =========================
        // 0) 基準位置：GUIの「枠の下辺」基準で揃える
        // =========================
        double baseY = switch (ctx) {
            case GUI ->
                0.01D;     // ★調整ポイント
            case GROUND ->
                0.01D;
            case FIXED ->
                0.01D;
            default ->
                0.01D;
        };

        pose.translate(0.5D, baseY, 0.5D);

        // =========================
        // 1) サイズ計測
        // =========================
        float w = living.getBbWidth();
        float h = living.getBbHeight();

        float widthBias = 1.10f;
        if (living.getType() == EntityType.GHAST) {
            widthBias = 1.25f;
        }
        if (living.getType() == EntityType.PHANTOM) {
            widthBias = 1.20f;
        }
        if (living.getType() == EntityType.SPIDER || living.getType() == EntityType.CAVE_SPIDER) {
            widthBias = 1.20f;
        }

        float max = Math.max(h, w * widthBias);

        // =========================
        // 2) スケール算出
        // =========================
        float target = 0.78f; // 全体の収まり（0.72〜0.85で調整）
        float scale = (max <= 0.0001f) ? 0.5f : (target / max);

        // アニメで伸びたり揺れたりする保険
        scale *= 0.90f;

        // 個別補正
        if (living.getType() == EntityType.SQUID || living.getType() == EntityType.GLOW_SQUID) {
            scale *= 0.65f;
        }
        if (living.getType() == EntityType.GHAST) {
            scale *= 0.78f;
        }
        if (living.getType() == EntityType.PHANTOM) {
            scale *= 0.85f;
        }
        if (living.getType() == EntityType.SPIDER || living.getType() == EntityType.CAVE_SPIDER) {
            scale *= 0.85f;
        }

        // ★子供は「正規化で大人と同サイズに見える」ので追加で縮小
        if (isBabyFromStackOrEntity(stack, living)) {
            scale *= 0.65f; // 0.55〜0.8
        }

        // clamp（暴れ止め）
        scale = Math.min(scale, 1.10f);
        scale = Math.max(scale, 0.08f);

        // =========================
        // 3) 下辺揃え（重要）
        // =========================
        double yNudge = 0.0D;

        // 例：イカは「上にあげたい」ならプラス
        if (living.getType() == EntityType.SQUID || living.getType() == EntityType.GLOW_SQUID) {
            yNudge += 0.6D;
        }

        // 例：ガストは少し上に寄せた方が見栄えがいいことが多い
        if (living.getType() == EntityType.GHAST) {
            yNudge += 0.4D;
        }

        pose.translate(0.0D, yNudge, 0.0D);

        // =========================
        // 4) スケール（横だけ縮めるならここ）
        // =========================
        float xzScale = 1.0f;
        if (living.getType() == EntityType.PHANTOM) {
            xzScale = 0.92f;
        }

        pose.scale(scale * xzScale, scale, scale * xzScale);

        // =========================
        // 5) 向き固定
        // =========================
        living.setYRot(0.0F);
        living.setXRot(0.0F);
        living.yBodyRot = 0.0F;
        living.yHeadRot = 0.0F;
        living.yHeadRotO = 0.0F;

        mc.getEntityRenderDispatcher().render(
                living,
                0.0D, 0.0D, 0.0D,
                0.0F, 0.0F,
                pose,
                buffer,
                packedLight
        );

        pose.popPose();
    }

    /**
     * - CapturedMobItem なら item が持つ EntityType を使う - NBT があるなら EntityTag
     * から復元（村人のバイオーム/職業なども反映） - 無いなら通常生成
     */
    private LivingEntity createLivingForRender(ItemStack stack, Level level) {
        EntityType<?> type = EntityType.COW;
        if (stack.getItem() instanceof CapturedMobItem captured) {
            type = captured.getEntityType().orElse(EntityType.COW);
        }

        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null) {
            Entity e = type.create(level);
            return (e instanceof LivingEntity le) ? le : null;
        }

        CompoundTag root = data.copyTag();
        if (!root.contains(CapturedMobItem.TAG_ENTITY_TAG, CompoundTag.TAG_COMPOUND)) {
            Entity e = type.create(level);
            return (e instanceof LivingEntity le) ? le : null;
        }

        CompoundTag entityTag = root.getCompound(CapturedMobItem.TAG_ENTITY_TAG).copy();

        // saveWithoutId() だと "id" が無いので補う
        ResourceLocation typeId = BuiltInRegistries.ENTITY_TYPE.getKey(type);
        if (typeId != null) {
            entityTag.putString("id", typeId.toString());
        } else {
            // 念のため（基本ここには来ない）
            entityTag.putString("id", EntityType.getKey(type).toString());
        }

        // 描画用に邪魔なものは削る
        entityTag.remove("UUID");
        entityTag.remove("Pos");
        entityTag.remove("Rotation");
        entityTag.remove("Motion");

        Entity e = EntityType.loadEntityRecursive(entityTag, level, x -> x);
        return (e instanceof LivingEntity le) ? le : null;
    }

    private static boolean isBabyFromStackOrEntity(ItemStack stack, LivingEntity living) {
        // 1) Stack NBT を最優先
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data != null) {
            CompoundTag root = data.copyTag();
            if (root.contains(CapturedMobItem.TAG_ENTITY_TAG, CompoundTag.TAG_COMPOUND)) {
                CompoundTag tag = root.getCompound(CapturedMobItem.TAG_ENTITY_TAG);

                // Ageable系：Age < 0 が子供
                if (tag.contains("Age", CompoundTag.TAG_INT) && tag.getInt("Age") < 0) {
                    return true;
                }

                // ゾンビ等：IsBaby
                if (tag.contains("IsBaby", CompoundTag.TAG_BYTE) && tag.getBoolean("IsBaby")) {
                    return true;
                }
            }
        }

        // 2) フォールバック
        if (living instanceof net.minecraft.world.entity.AgeableMob a) {
            return a.isBaby();
        }

        return false;
    }
}
