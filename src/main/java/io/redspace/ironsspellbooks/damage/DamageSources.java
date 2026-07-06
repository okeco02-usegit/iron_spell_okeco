package io.redspace.ironsspellbooks.damage;

import io.redspace.ironsspellbooks.api.entity.NoKnockbackProjectile;
import io.redspace.ironsspellbooks.api.events.SpellDamageEvent;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.entity.mobs.IMagicSummon;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.Team;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class DamageSources {
   private static final HashMap<UUID, Integer> knockbackImmunes = new HashMap<>();

   public static DamageSource get(Level level, ResourceKey<DamageType> damageType) {
      return level.m_269111_().m_269079_(damageType);
   }

   public static Holder<DamageType> getHolderFromResource(Entity entity, ResourceKey<DamageType> damageTypeResourceKey) {
      Optional<Reference<DamageType>> option = entity.m_9236_().m_9598_().m_175515_(Registries.f_268580_).m_203636_(damageTypeResourceKey);
      return option.isPresent() ? (Holder)option.get() : entity.m_9236_().m_269111_().m_287172_().m_269150_();
   }

   public static boolean applyDamage(Entity target, float baseAmount, DamageSource damageSource) {
      if (target instanceof LivingEntity livingTarget && damageSource instanceof SpellDamageSource spellDamageSource) {
         SpellDamageEvent e = new SpellDamageEvent(livingTarget, baseAmount, spellDamageSource);
         if (MinecraftForge.EVENT_BUS.post(e)) {
            return false;
         }

         baseAmount = e.getAmount();
         float adjustedDamage = baseAmount * getResist(livingTarget, spellDamageSource.spell.getSchoolType());
         if (damageSource.m_7640_() instanceof NoKnockbackProjectile) {
            ignoreNextKnockback(livingTarget);
         }

         if (damageSource.m_7639_() instanceof LivingEntity livingAttacker) {
            if (isFriendlyFireBetween(livingAttacker, livingTarget)) {
               return false;
            }

            livingAttacker.m_21335_(target);
         }

         return livingTarget.m_6469_(damageSource, adjustedDamage);
      } else {
         return target.m_6469_(damageSource, baseAmount);
      }
   }

   public static void ignoreNextKnockback(LivingEntity livingEntity) {
      if (livingEntity.m_20194_() != null) {
         int tickCount = livingEntity.m_20194_().m_129921_();
         knockbackImmunes.entrySet().removeIf(entry -> tickCount - entry.getValue() >= 10);
         knockbackImmunes.put(livingEntity.m_20148_(), tickCount);
      }
   }

   @SubscribeEvent
   public static void cancelKnockback(LivingKnockBackEvent event) {
      LivingEntity entity = event.getEntity();
      if (entity.m_20194_() != null && knockbackImmunes.containsKey(event.getEntity().m_20148_())) {
         if (entity.m_20194_().m_129921_() - knockbackImmunes.get(entity.m_20148_()) <= 1) {
            event.setCanceled(true);
         }

         knockbackImmunes.remove(entity.m_20148_());
      }
   }

   @SubscribeEvent
   public static void postHitEffects(LivingHurtEvent event) {
      DamageSource damageSource = event.getSource();
      if (damageSource instanceof SpellDamageSource spellDamageSource && spellDamageSource.hasPostHitEffects()) {
         float actualDamage = event.getAmount();
         LivingEntity target = event.getEntity();
         if (event.getSource().m_7639_() instanceof LivingEntity livingAttacker && spellDamageSource.getLifestealPercent() > 0.0F) {
            livingAttacker.m_5634_(spellDamageSource.getLifestealPercent() * actualDamage);
         }

         if (spellDamageSource.getFreezeTicks() > 0 && target.m_142079_()) {
            target.m_146917_(target.m_146888_() + spellDamageSource.getFreezeTicks() * 2);
         }

         if (spellDamageSource.getFireTime() > 0 && target instanceof LivingEntity) {
            target.m_7311_(Math.max(target.m_20094_(), spellDamageSource.getFireTime()));
         }

         if (spellDamageSource.getIFrames() >= 0) {
            target.f_19802_ = spellDamageSource.getIFrames();
         }
      }

      IMagicSummon fromSummon = damageSource.m_7640_() instanceof IMagicSummon summon
         ? summon
         : (damageSource.m_7639_() instanceof IMagicSummon summon ? summon : null);
      if (fromSummon != null) {
         Entity summoner = fromSummon.getSummoner();
         if (summoner != null && summoner.m_20148_().equals(event.getEntity().m_20148_())) {
            event.setCanceled(true);
            return;
         }

         if (summoner instanceof LivingEntity livingSummoner) {
            event.setAmount(event.getAmount() * (float)livingSummoner.m_21133_((Attribute)AttributeRegistry.SUMMON_DAMAGE.get()));
         }
      }
   }

   public static void preHitEffects(LivingAttackEvent event) {
      if (event.getSource() instanceof SpellDamageSource var2) {
         ;
      }
   }

   public static boolean isFriendlyFireBetween(@Nullable Entity attacker, @Nullable Entity target) {
      if (attacker != null && target != null) {
         if (attacker instanceof IMagicSummon summon) {
            Entity tmp = summon.getSummoner();
            if (tmp != null) {
               attacker = tmp;
            }
         }

         if (target instanceof IMagicSummon summon) {
            Entity tmp = summon.getSummoner();
            if (tmp != null) {
               target = tmp;
            }
         }

         if (attacker.m_20365_(target)) {
            return true;
         } else if (attacker instanceof Player playerAttacker && target instanceof Player playertarget && !playerAttacker.m_7099_(playertarget)) {
            return true;
         } else {
            Team team = attacker.m_5647_();
            return team == null ? attacker.m_7307_(target) : team.m_83536_(target.m_5647_()) && !team.m_6260_();
         }
      } else {
         return false;
      }
   }

   public static float getResist(LivingEntity entity, SchoolType damageSchool) {
      double baseResist = entity.m_21133_((Attribute)AttributeRegistry.SPELL_RESIST.get());
      return damageSchool == null
         ? 2.0F - (float)Utils.softCapFormula(baseResist)
         : 2.0F - (float)Utils.softCapFormula(damageSchool.getResistanceFor(entity) * baseResist);
   }
}
