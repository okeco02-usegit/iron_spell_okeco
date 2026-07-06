package io.redspace.ironsspellbooks.spells.ender;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.entity.spells.AbstractConeProjectile;
import io.redspace.ironsspellbooks.entity.spells.dragon_breath.DragonBreathProjectile;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.spells.EntityCastData;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;

public class DragonBreathSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "dragon_breath");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.COMMON)
      .setSchoolResource(SchoolRegistry.ENDER_RESOURCE)
      .setMaxLevel(10)
      .setCooldownSeconds(12.0)
      .build();

   @Override
   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(Component.m_237110_("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 2)}));
   }

   public DragonBreathSpell() {
      this.manaCostPerLevel = 1;
      this.baseSpellPower = 0;
      this.spellPowerPerLevel = 1;
      this.castTime = 100;
      this.baseManaCost = 5;
   }

   @Override
   public CastType getCastType() {
      return CastType.CONTINUOUS;
   }

   @Override
   public DefaultConfig getDefaultConfig() {
      return this.defaultConfig;
   }

   @Override
   public ResourceLocation getSpellResource() {
      return this.spellId;
   }

   @Override
   public Optional<SoundEvent> getCastStartSound() {
      return Optional.of(SoundEvents.f_11894_);
   }

   @Override
   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of((SoundEvent)SoundRegistry.FIRE_BREATH_LOOP.get());
   }

   @Override
   public void onCast(Level world, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      if (playerMagicData.isCasting()
         && playerMagicData.getCastingSpell().getSpell().equals(this)
         && playerMagicData.getAdditionalCastData() instanceof EntityCastData entityCastData
         && entityCastData.getCastingEntity() instanceof AbstractConeProjectile cone) {
         cone.setDealDamageActive();
      } else {
         DragonBreathProjectile dragonBreathProjectile = new DragonBreathProjectile(world, entity);
         dragonBreathProjectile.m_146884_(entity.m_20182_().m_82520_(0.0, entity.m_20192_() * 0.7, 0.0));
         dragonBreathProjectile.setDamage(this.getDamage(spellLevel, entity));
         world.m_7967_(dragonBreathProjectile);
         playerMagicData.setAdditionalCastData(new EntityCastData(dragonBreathProjectile));
      }

      super.onCast(world, spellLevel, entity, castSource, playerMagicData);
   }

   public float getDamage(int spellLevel, LivingEntity caster) {
      return 1.0F + this.getSpellPower(spellLevel, caster) * 0.75F;
   }

   @Override
   public boolean shouldAIStopCasting(int spellLevel, Mob mob, LivingEntity target) {
      return mob.m_20280_(target) > 120.0;
   }
}
