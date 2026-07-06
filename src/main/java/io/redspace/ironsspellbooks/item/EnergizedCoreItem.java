package io.redspace.ironsspellbooks.item;

import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.config.ServerConfigs;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class EnergizedCoreItem extends Item {
   public EnergizedCoreItem(Properties pProperties) {
      super(pProperties);
   }

   public InteractionResult m_6225_(UseOnContext pContext) {
      Level level = pContext.m_43725_();
      BlockPos blockPos = pContext.m_8083_();
      if (pContext.m_43723_() != null && level.m_8055_(blockPos).m_60713_(Blocks.f_152587_)) {
         if (level.m_46470_()) {
            if (!level.f_46443_ && level.m_46597_(blockPos, Blocks.f_50016_.m_49966_())) {
               ItemStack itemstack = pContext.m_43722_();
               if (!pContext.m_43723_().m_150110_().f_35937_) {
                  itemstack.m_41774_(1);
               }

               pContext.m_43723_().m_21008_(pContext.m_43724_(), itemstack);
               Vec3 center = new Vec3(blockPos.m_123341_() + 0.5, blockPos.m_123342_(), blockPos.m_123343_() + 0.5);
               this.doLightningBolt(level, center);
               ExplosionInteraction blockinteraction = ServerConfigs.SPELL_GREIFING.get() ? ExplosionInteraction.TNT : ExplosionInteraction.NONE;
               MagicManager.spawnParticles(
                  level, ParticleHelper.ELECTRIC_SPARKS, center.f_82479_, center.f_82480_ + 0.5, center.f_82481_, 50, 0.1F, 0.1F, 0.1F, 0.6, false
               );
               MagicManager.spawnParticles(
                  level,
                  new BlastwaveParticleOptions(new Vector3f(0.7F, 1.0F, 1.0F), 6.0F),
                  center.f_82479_,
                  center.f_82480_ + 0.15,
                  center.f_82481_,
                  1,
                  0.0,
                  0.0,
                  0.0,
                  0.0,
                  true
               );
               level.m_254877_(null, level.m_269111_().m_269548_(), null, center.f_82479_, center.f_82480_, center.f_82481_, 3.0F, true, blockinteraction);
               level.m_5594_(null, blockPos, SoundEvents.f_12521_, SoundSource.PLAYERS, 2.0F, 0.6F);
               ItemEntity itementity = new ItemEntity(
                  level, center.f_82479_, center.f_82480_ + 1.0, center.f_82481_, new ItemStack((ItemLike)ItemRegistry.LIGHTNING_ROD_STAFF.get())
               );
               itementity.m_146915_(true);
               level.m_7967_(itementity);
            }
         } else if (level.f_46443_) {
            pContext.m_43723_().m_5661_(Component.m_237115_("item.irons_spellbooks.energized_core.failure").m_130940_(ChatFormatting.AQUA), true);
         }

         return InteractionResult.m_19078_(level.f_46443_);
      } else {
         return super.m_6225_(pContext);
      }
   }

   private void doLightningBolt(Level level, Vec3 pos) {
      LightningBolt lightningBolt = (LightningBolt)EntityType.f_20465_.m_20615_(level);
      lightningBolt.m_20874_(true);
      lightningBolt.setDamage(0.0F);
      lightningBolt.m_146884_(pos);
      level.m_7967_(lightningBolt);
   }
}
