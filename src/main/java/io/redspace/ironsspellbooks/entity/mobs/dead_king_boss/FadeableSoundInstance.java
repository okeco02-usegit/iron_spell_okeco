package io.redspace.ironsspellbooks.entity.mobs.dead_king_boss;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance.Attenuation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public class FadeableSoundInstance extends AbstractTickableSoundInstance {
   boolean starting = false;
   private int transitionTicks;
   private boolean triggerEnd = false;
   private static final int START_TRANSITION_TIME = 40;
   private static final int END_TRANSITION_TIME = 40;
   private int customFadeIn;

   public FadeableSoundInstance(SoundEvent soundEvent, SoundSource source, boolean loop) {
      super(soundEvent, source, SoundInstance.m_235150_());
      this.f_119580_ = Attenuation.NONE;
      this.f_119578_ = loop;
      this.f_119579_ = 0;
      this.f_119573_ = 1.0F;
      this.starting = false;
   }

   public void m_7788_() {
      if (this.transitionTicks > 0) {
         this.transitionTicks--;
      }

      if (this.starting) {
         int max = this.customFadeIn > 0 ? this.customFadeIn : 40;
         this.f_119573_ = 1.0F - (float)this.transitionTicks / max;
         if (this.transitionTicks == 0) {
            this.starting = false;
            this.customFadeIn = 0;
         }
      }

      if (this.triggerEnd) {
         this.f_119573_ = this.transitionTicks / 40.0F;
         if (this.transitionTicks == 0) {
            this.m_119609_();
         }
      }
   }

   public void fadeIn(int ticks) {
      this.customFadeIn = ticks;
      this.transitionTicks = ticks;
      this.starting = true;
      this.f_119573_ = 0.0F;
   }

   public void unstop() {
      this.f_119604_ = false;
      this.f_119573_ = 1.0F;
   }

   public boolean m_7784_() {
      return true;
   }

   public void triggerStop() {
      this.triggerEnd = true;
      if (this.f_119573_ < 1.0F) {
         this.transitionTicks = (int)(40.0F * this.f_119573_);
      } else {
         this.transitionTicks = 40;
      }
   }

   public void triggerStart() {
      this.f_119604_ = false;
      this.triggerEnd = false;
      if (this.f_119573_ < 1.0F) {
         this.transitionTicks = (int)(40.0F * this.f_119573_);
      } else {
         this.transitionTicks = 40;
      }

      this.starting = true;
   }
}
