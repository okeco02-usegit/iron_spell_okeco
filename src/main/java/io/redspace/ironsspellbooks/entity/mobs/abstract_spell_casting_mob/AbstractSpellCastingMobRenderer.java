package io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob;

import com.mojang.blaze3d.vertex.PoseStack;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.entity.mobs.HumanoidRenderer;
import io.redspace.ironsspellbooks.player.ClientMagicData;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import io.redspace.ironsspellbooks.render.ChargeSpellLayer;
import io.redspace.ironsspellbooks.render.EnergySwirlLayer;
import io.redspace.ironsspellbooks.render.GeoSpinAttackLayer;
import io.redspace.ironsspellbooks.render.GlowingEyesLayer;
import io.redspace.ironsspellbooks.render.SpellRenderingHelper;
import io.redspace.ironsspellbooks.render.SpellTargetingLayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.object.Color;

public abstract class AbstractSpellCastingMobRenderer extends HumanoidRenderer<AbstractSpellCastingMob> {
   private ResourceLocation textureResource;

   public AbstractSpellCastingMobRenderer(Context renderManager, AbstractSpellCastingMobModel model) {
      super(renderManager, model);
      this.f_114477_ = 0.5F;
      this.addRenderLayer(new EnergySwirlLayer.Geo(this, EnergySwirlLayer.EVASION_TEXTURE, MobEffectRegistry.EVASION));
      this.addRenderLayer(new EnergySwirlLayer.Geo(this, EnergySwirlLayer.CHARGE_TEXTURE, MobEffectRegistry.CHARGED));
      this.addRenderLayer(new ChargeSpellLayer.Geo(this));
      this.addRenderLayer(new GlowingEyesLayer.Geo(this));
      this.addRenderLayer(new SpellTargetingLayer.Geo(this));
      this.addRenderLayer(new GeoSpinAttackLayer(this));
   }

   public static ItemStack makePotion(AbstractSpellCastingMob entity) {
      ItemStack healthPotion = new ItemStack(Items.f_42589_);
      return Utils.setPotion(healthPotion, entity.m_21222_() ? Potions.f_43582_ : Potions.f_43623_);
   }

   public void render(AbstractSpellCastingMob entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
      super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
      SpellRenderingHelper.renderSpellHelper(ClientMagicData.getSyncedSpellData(entity), entity, poseStack, bufferSource, partialTick);
   }

   public RenderType getRenderType(AbstractSpellCastingMob animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
      return animatable.m_20145_() ? RenderType.m_110473_(texture) : super.getRenderType(animatable, texture, bufferSource, partialTick);
   }

   public Color getRenderColor(AbstractSpellCastingMob animatable, float partialTick, int packedLight) {
      return super.getRenderColor(animatable, partialTick, packedLight);
   }
}
