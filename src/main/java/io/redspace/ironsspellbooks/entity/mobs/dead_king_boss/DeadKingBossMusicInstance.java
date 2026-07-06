package io.redspace.ironsspellbooks.entity.mobs.dead_king_boss;

import io.redspace.ironsspellbooks.registries.SoundRegistry;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance.Attenuation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public class DeadKingBossMusicInstance extends AbstractTickableSoundInstance {
   final DeadKingBoss boss;
   boolean starting = true;
   boolean ending = false;
   boolean triggerEnd = false;
   int transitionTicks = 40;
   private static final int START_TRANSITION_TIME = 40;
   private static final int END_TRANSITION_TIME = 40;

   protected DeadKingBossMusicInstance(DeadKingBoss boss) {
      super((SoundEvent)SoundRegistry.DEAD_KING_DRUM_LOOP.get(), SoundSource.RECORDS, SoundInstance.m_235150_());
      this.f_119580_ = Attenuation.NONE;
      this.f_119578_ = true;
      this.f_119579_ = 0;
      this.f_119573_ = 0.0F;
      this.boss = boss;
      this.starting = true;
      this.transitionTicks = 40;
   }

   public void m_7788_() {
      if (this.transitionTicks > 0) {
         this.transitionTicks--;
      }

      if (this.starting) {
         this.f_119573_ = 1.0F - this.transitionTicks / 40.0F;
         if (this.transitionTicks == 0) {
            this.starting = false;
         }
      }

      if (this.triggerEnd || this.boss.m_21224_()) {
         this.starting = false;
         if (!this.ending) {
            this.ending = true;
            this.transitionTicks = 40;
         }

         this.f_119573_ = this.transitionTicks / 40.0F;
         if (this.transitionTicks == 0) {
            this.m_119609_();
         }
      }

      if (this.boss.isPhase(DeadKingBoss.Phases.FinalPhase)) {
         this.f_119574_ = 1.75F;
      }
   }

   public boolean m_7784_() {
      return true;
   }

   public void triggerStop() {
      this.triggerEnd = true;
   }
}
