package io.redspace.ironsspellbooks.entity.mobs.dead_king_boss;

import io.redspace.ironsspellbooks.api.util.IMusicHandler;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public class DeadKingMusicHandler implements IMusicHandler {
   static final SoundSource SOUND_SOURCE = SoundSource.RECORDS;
   static final int FIRST_PHASE_MELODY_LENGTH_MILIS = 28790;
   static final int INTRO_LENGTH_MILIS = 17600;
   DeadKingBoss boss;
   final int entityid;
   final SoundManager soundManager;
   FadeableSoundInstance beginSound;
   FadeableSoundInstance firstPhaseMelody;
   FadeableSoundInstance firstPhaseAccent;
   FadeableSoundInstance secondPhaseMelody;
   FadeableSoundInstance transitionMusic;
   Set<FadeableSoundInstance> layers = new HashSet<>();
   private long lastMilisPlayed;
   private boolean hasPlayedIntro;
   DeadKingBoss.Phases stage;
   boolean finishing = false;

   public DeadKingMusicHandler(DeadKingBoss boss) {
      this.boss = boss;
      this.entityid = boss.m_19879_();
      this.soundManager = Minecraft.m_91087_().m_91106_();
      this.stage = DeadKingBoss.Phases.values()[boss.getPhase()];
      this.beginSound = new FadeableSoundInstance((SoundEvent)SoundRegistry.DEAD_KING_MUSIC_INTRO.get(), SOUND_SOURCE, false);
      this.firstPhaseMelody = new FadeableSoundInstance((SoundEvent)SoundRegistry.DEAD_KING_FIRST_PHASE_MELODY.get(), SOUND_SOURCE, true);
      this.firstPhaseAccent = new FadeableSoundInstance((SoundEvent)SoundRegistry.DEAD_KING_FIRST_PHASE_ACCENT_01.get(), SOUND_SOURCE, false);
      this.secondPhaseMelody = new FadeableSoundInstance((SoundEvent)SoundRegistry.DEAD_KING_SECOND_PHASE_MELODY_ALT.get(), SOUND_SOURCE, true);
      this.transitionMusic = new FadeableSoundInstance((SoundEvent)SoundRegistry.DEAD_KING_SUSPENSE.get(), SOUND_SOURCE, false);
   }

   @Override
   public void init() {
      this.soundManager.m_120386_(null, SoundSource.MUSIC);
      switch (this.stage) {
         case FirstPhase:
            this.addLayer(this.beginSound);
            this.lastMilisPlayed = System.currentTimeMillis();
            break;
         case FinalPhase:
            this.initSecondPhase();
      }
   }

   @Override
   public void stop() {
      this.stopLayers();
      this.finishing = true;
   }

   @Override
   public void tick() {
      if (!this.isDone() && !this.finishing) {
         if (!this.boss.m_21224_() && !this.boss.m_213877_()) {
            DeadKingBoss.Phases bossPhase = DeadKingBoss.Phases.values()[this.boss.getPhase()];
            switch (bossPhase) {
               case FirstPhase:
                  if (!this.hasPlayedIntro) {
                     if (!this.soundManager.m_120403_(this.beginSound) || this.lastMilisPlayed + 17600L < System.currentTimeMillis()) {
                        this.hasPlayedIntro = true;
                        this.layers.remove(this.beginSound);
                        this.initFirstPhase();
                     }
                  } else if (this.lastMilisPlayed + 57580L < System.currentTimeMillis()) {
                     this.playAccent(this.firstPhaseAccent);
                  }
                  break;
               case FinalPhase:
                  if (this.stage != DeadKingBoss.Phases.FinalPhase) {
                     this.stage = DeadKingBoss.Phases.FinalPhase;
                     this.initSecondPhase();
                  }
                  break;
               case Transitioning:
                  if (this.stage != DeadKingBoss.Phases.Transitioning) {
                     this.stage = DeadKingBoss.Phases.Transitioning;
                     this.stopLayers();
                     this.addLayer(this.transitionMusic);
                  }
            }
         } else {
            this.stopLayers();
            this.finishing = true;
         }
      }
   }

   @Override
   public boolean isDone() {
      for (FadeableSoundInstance soundInstance : this.layers) {
         if (!soundInstance.m_7801_() && this.soundManager.m_120403_(soundInstance)) {
            return false;
         }
      }

      return true;
   }

   private void addLayer(FadeableSoundInstance soundInstance) {
      this.layers.stream().filter(sound -> sound.m_7801_() || !this.soundManager.m_120403_(sound)).toList().forEach(this.layers::remove);
      this.soundManager.m_120367_(soundInstance);
      this.layers.add(soundInstance);
   }

   private void playAccent(FadeableSoundInstance soundInstance) {
      this.lastMilisPlayed = System.currentTimeMillis();
      this.addLayer(soundInstance);
   }

   public void stopLayers() {
      this.layers.forEach(FadeableSoundInstance::triggerStop);
   }

   @Override
   public void hardStop() {
      this.layers.forEach(this.soundManager::m_120399_);
   }

   @Override
   public void triggerResume() {
      if (Minecraft.m_91087_().f_91073_ != null) {
         this.boss = Minecraft.m_91087_().f_91073_.m_6815_(this.entityid) instanceof DeadKingBoss deadKingBoss ? deadKingBoss : this.boss;
      }

      if (!this.boss.m_213877_()) {
         this.layers.forEach(sound -> {
            sound.triggerStart();
            if (!this.soundManager.m_120403_(sound)) {
               this.soundManager.m_120367_(sound);
            }
         });
      }
   }

   private void initFirstPhase() {
      this.addLayer(this.firstPhaseMelody);
      this.playAccent(this.firstPhaseAccent);
   }

   private void initSecondPhase() {
      this.addLayer(this.secondPhaseMelody);
   }
}
