package io.redspace.ironsspellbooks.entity.spells;

import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.player.ClientMagicData;
import io.redspace.ironsspellbooks.player.SpinAttackType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SpinAttackModel extends GeoModel<AbstractSpellCastingMob> {
   public static final ResourceLocation FIRE_TEXTURE = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "textures/entity/fire_riptide.png");
   public static final ResourceLocation LIGHTNING_TEXTURE = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "textures/entity/lightning_riptide.png");
   public static final ResourceLocation DEFAULT_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/trident_riptide.png");
   private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "geo/spin_attack_model.geo.json");

   public ResourceLocation getTextureResource(AbstractSpellCastingMob object) {
      SpinAttackType spinAttackType = ClientMagicData.getSyncedSpellData(object).getSpinAttackType();
      return spinAttackType.textureId();
   }

   public ResourceLocation getAnimationResource(AbstractSpellCastingMob animatable) {
      return null;
   }

   public ResourceLocation getModelResource(AbstractSpellCastingMob object) {
      return MODEL;
   }
}
