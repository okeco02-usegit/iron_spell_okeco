package io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Optional;
import java.util.UUID;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class NeutralWizard extends AbstractSpellCastingMob implements NeutralMob {
   private static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.m_145020_(20, 39);
   private int remainingPersistentAngerTime;
   @Nullable
   private UUID persistentAngerTarget;
   private int lastAngerLevelUpdate;
   private final Object2IntArrayMap<UUID> angerLevels = new Object2IntArrayMap();

   protected NeutralWizard(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   public void m_6825_() {
      this.m_7870_(PERSISTENT_ANGER_TIME.m_214085_(this.f_19796_));
   }

   public void m_7870_(int pTime) {
      this.remainingPersistentAngerTime = pTime;
   }

   public int m_6784_() {
      return this.remainingPersistentAngerTime;
   }

   public void m_6925_(@Nullable UUID pTarget) {
      this.persistentAngerTarget = pTarget;
   }

   @Nullable
   public UUID m_6120_() {
      return this.persistentAngerTarget;
   }

   @Override
   public void m_7380_(CompoundTag pCompound) {
      this.m_21678_(pCompound);
      if (!this.angerLevels.isEmpty()) {
         ListTag levels = new ListTag();
         ObjectIterator var3 = this.angerLevels.object2IntEntrySet().iterator();

         while (var3.hasNext()) {
            Entry<UUID, Integer> entry = (Entry<UUID, Integer>)var3.next();
            CompoundTag tag = new CompoundTag();
            tag.m_128362_("player", entry.getKey());
            tag.m_128405_("anger", entry.getValue());
            levels.add(tag);
         }

         pCompound.m_128365_("angerLevels", levels);
      }

      super.m_7380_(pCompound);
   }

   @Override
   public void m_7378_(CompoundTag pCompound) {
      this.m_147285_(this.f_19853_, pCompound);
      if (pCompound.m_128441_("angerLevels")) {
         for (Tag tag : pCompound.m_128437_("angerLevels", 10)) {
            try {
               this.angerLevels.put(((CompoundTag)tag).m_128342_("player"), ((CompoundTag)tag).m_128451_("anger"));
            } catch (Exception exception) {
            }
         }
      }

      super.m_7378_(pCompound);
   }

   public void m_8107_() {
      super.m_8107_();
      if (!this.f_19853_.f_46443_) {
         this.m_21666_((ServerLevel)this.f_19853_, true);
      }

      if (!this.angerLevels.isEmpty() && this.lastAngerLevelUpdate + 400 < this.f_19797_) {
         ObjectIterator<it.unimi.dsi.fastutil.objects.Object2IntMap.Entry<UUID>> it = this.angerLevels.object2IntEntrySet().iterator();

         while (it.hasNext()) {
            it.unimi.dsi.fastutil.objects.Object2IntMap.Entry<UUID> entry = (it.unimi.dsi.fastutil.objects.Object2IntMap.Entry<UUID>)it.next();
            int newLevel = entry.getIntValue() - 1;
            if (newLevel == 0) {
               it.remove();
            } else {
               this.angerLevels.put((UUID)entry.getKey(), newLevel);
            }
         }

         this.lastAngerLevelUpdate = this.f_19797_;
      }
   }

   public boolean m_6469_(DamageSource pSource, float pAmount) {
      if (pSource.m_7639_() instanceof Player player && !player.m_7500_()) {
         this.increaseAngerLevel(player, (int)Math.ceil(pAmount), !this.isHostileTowards(player));
      }

      return super.m_6469_(pSource, pAmount);
   }

   public void increaseAngerLevel(Player angryAt, int levels, boolean showParticles) {
      if (!this.f_19853_.f_46443_) {
         int anger = Math.min(this.angerLevels.getOrDefault(angryAt.m_20148_(), 0) + levels, 10);
         this.angerLevels.put(angryAt.m_20148_(), anger);
         this.lastAngerLevelUpdate = this.f_19797_;
         if (anger < this.getAngerThreshold() && showParticles) {
            MagicManager.spawnParticles(
               this.f_19853_, ParticleTypes.f_123792_, this.m_20185_(), this.m_20186_() + 1.25, this.m_20189_(), 15, 0.3, 0.2, 0.3, 0.0, false
            );
            this.getAngerSound().ifPresent(sound -> this.m_5496_(sound, this.m_6121_(), this.m_6100_()));
         }

         if (anger >= this.getAngerThreshold()) {
            this.m_6925_(angryAt.m_20148_());
         }
      }
   }

   @Deprecated
   public void increaseAngerLevel(int levels, boolean showParticles) {
      IronsSpellbooks.LOGGER.warn("Warning! Use of deprecated NeutralWizard#increaseAngerLevel");
      ObjectIterator<it.unimi.dsi.fastutil.objects.Object2IntMap.Entry<UUID>> it = this.angerLevels.object2IntEntrySet().iterator();

      while (it.hasNext()) {
         it.unimi.dsi.fastutil.objects.Object2IntMap.Entry<UUID> entry = (it.unimi.dsi.fastutil.objects.Object2IntMap.Entry<UUID>)it.next();
         int newLevel = entry.getIntValue() + 1;
         entry.setValue(newLevel);
      }
   }

   public Optional<SoundEvent> getAngerSound() {
      return Optional.empty();
   }

   public int getAngerThreshold() {
      return 2;
   }

   public boolean isHostileTowards(LivingEntity entity) {
      return this.m_21674_(entity)
         && (entity.m_6095_() != EntityType.f_20532_ || this.angerLevels.getOrDefault(entity.m_20148_(), 0) >= this.getAngerThreshold());
   }

   public boolean m_21674_(LivingEntity pTarget) {
      return pTarget.m_6095_() == EntityType.f_20532_ && this.angerLevels.containsKey(pTarget.m_20148_()) || super.m_21674_(pTarget);
   }

   public boolean guardsBlocks() {
      return true;
   }

   public void m_147285_(Level level, CompoundTag tag) {
      this.m_7870_(tag.m_128451_("AngerTime"));
      if (level instanceof ServerLevel) {
         if (!tag.m_128403_("AngryAt")) {
            this.m_6925_(null);
         } else {
            UUID uuid = tag.m_128342_("AngryAt");
            this.m_6925_(uuid);
         }
      }
   }

   public void m_21676_(@NotNull Player player) {
      super.m_21676_(player);
      if (player.m_9236_().m_46469_().m_46207_(GameRules.f_46126_)) {
         this.angerLevels.removeInt(player.m_20148_());
      }
   }
}
