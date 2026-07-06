package io.redspace.ironsspellbooks.entity.mobs.wizards.fire_boss;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBossEventPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.BossEvent.BossBarColor;
import net.minecraft.world.BossEvent.BossBarOverlay;

public class ExtendedServerBossEvent extends BossEvent {
   private final Set<ServerPlayer> players = Sets.newHashSet();
   private final Set<ServerPlayer> unmodifiablePlayers = Collections.unmodifiableSet(this.players);
   private boolean visible = true;

   public ExtendedServerBossEvent(UUID uuid, Component name, BossBarColor color, BossBarOverlay overlay) {
      super(uuid, name, color, overlay);
   }

   public void m_142711_(float progress) {
      if (progress != this.f_146638_) {
         super.m_142711_(progress);
         this.broadcast(ClientboundBossEventPacket::m_178649_);
      }
   }

   public void m_6451_(BossBarColor color) {
      if (color != this.f_18842_) {
         super.m_6451_(color);
         this.broadcast(ClientboundBossEventPacket::m_178653_);
      }
   }

   public void m_5648_(BossBarOverlay overlay) {
      if (overlay != this.f_18843_) {
         super.m_5648_(overlay);
         this.broadcast(ClientboundBossEventPacket::m_178653_);
      }
   }

   public BossEvent m_7003_(boolean darkenSky) {
      if (darkenSky != this.f_18844_) {
         super.m_7003_(darkenSky);
         this.broadcast(ClientboundBossEventPacket::m_178655_);
      }

      return this;
   }

   public BossEvent m_7005_(boolean playEndBossMusic) {
      if (playEndBossMusic != this.f_18845_) {
         super.m_7005_(playEndBossMusic);
         this.broadcast(ClientboundBossEventPacket::m_178655_);
      }

      return this;
   }

   public BossEvent m_7006_(boolean createFog) {
      if (createFog != this.f_18846_) {
         super.m_7006_(createFog);
         this.broadcast(ClientboundBossEventPacket::m_178655_);
      }

      return this;
   }

   public void m_6456_(Component name) {
      if (!Objects.equal(name, this.f_18840_)) {
         super.m_6456_(name);
         this.broadcast(ClientboundBossEventPacket::m_178651_);
      }
   }

   private void broadcast(Function<BossEvent, ClientboundBossEventPacket> packetGetter) {
      if (this.visible) {
         ClientboundBossEventPacket clientboundbosseventpacket = packetGetter.apply(this);

         for (ServerPlayer serverplayer : this.players) {
            serverplayer.f_8906_.m_9829_(clientboundbosseventpacket);
         }
      }
   }

   public void addPlayer(ServerPlayer player) {
      if (this.players.add(player) && this.visible) {
         player.f_8906_.m_9829_(ClientboundBossEventPacket.m_178639_(this));
      }
   }

   public void removePlayer(ServerPlayer player) {
      if (this.players.remove(player) && this.visible) {
         player.f_8906_.m_9829_(ClientboundBossEventPacket.m_178641_(this.m_18860_()));
      }
   }

   public void removeAllPlayers() {
      if (!this.players.isEmpty()) {
         for (ServerPlayer serverplayer : Lists.newArrayList(this.players)) {
            this.removePlayer(serverplayer);
         }
      }
   }

   public boolean isVisible() {
      return this.visible;
   }

   public void setVisible(boolean visible) {
      if (visible != this.visible) {
         this.visible = visible;

         for (ServerPlayer serverplayer : this.players) {
            serverplayer.f_8906_.m_9829_(visible ? ClientboundBossEventPacket.m_178639_(this) : ClientboundBossEventPacket.m_178641_(this.m_18860_()));
         }
      }
   }

   public Collection<ServerPlayer> getPlayers() {
      return this.unmodifiablePlayers;
   }
}
