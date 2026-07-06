package io.redspace.ironsspellbooks.entity.spells.fiery_dagger;

import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.render.RenderHelper;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.model.GeoModel;

public class FieryDaggerModel extends GeoModel<FieryDaggerEntity> {
   public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "textures/entity/fiery_dagger.png");
   public static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "geo/fiery_dagger.geo.json");

   public ResourceLocation getModelResource(FieryDaggerEntity animatable) {
      return MODEL;
   }

   public ResourceLocation getTextureResource(FieryDaggerEntity animatable) {
      return TEXTURE;
   }

   public ResourceLocation getAnimationResource(FieryDaggerEntity animatable) {
      return AbstractSpellCastingMob.animationInstantCast;
   }

   @Nullable
   public RenderType getRenderType(FieryDaggerEntity animatable, ResourceLocation texture) {
      return RenderHelper.CustomerRenderType.magic(TEXTURE);
   }
}
