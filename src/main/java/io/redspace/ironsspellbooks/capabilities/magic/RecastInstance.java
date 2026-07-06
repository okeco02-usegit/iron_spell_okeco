package io.redspace.ironsspellbooks.capabilities.magic;

import io.redspace.ironsspellbooks.api.network.ISerializable;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.ICastDataSerializable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.common.util.INBTSerializable;

public class RecastInstance implements ISerializable, INBTSerializable<CompoundTag> {
   protected String spellId;
   protected int spellLevel;
   protected int remainingRecasts;
   protected int totalRecasts;
   protected ICastDataSerializable castData;
   protected int ticksToLive;
   protected int remainingTicks;
   protected CastSource castSource;

   public RecastInstance() {
   }

   public RecastInstance(String spellId, int spellLevel, int totalRecasts, int ticksToLive, CastSource castSource, ICastDataSerializable castData) {
      this.spellId = spellId;
      this.spellLevel = spellLevel;
      this.remainingRecasts = totalRecasts - 1;
      this.totalRecasts = totalRecasts;
      this.ticksToLive = ticksToLive;
      this.remainingTicks = ticksToLive;
      this.castSource = castSource;
      this.castData = castData;
   }

   public String getSpellId() {
      return this.spellId;
   }

   public int getSpellLevel() {
      return this.spellLevel;
   }

   public int getRemainingRecasts() {
      return this.remainingRecasts;
   }

   public int getTotalRecasts() {
      return this.totalRecasts;
   }

   public int getTicksToLive() {
      return this.ticksToLive;
   }

   public int getTicksRemaining() {
      return this.remainingTicks;
   }

   public CastSource getCastSource() {
      return this.castSource;
   }

   public ICastDataSerializable getCastData() {
      return this.castData;
   }

   @Override
   public void writeToBuffer(FriendlyByteBuf buffer) {
      buffer.m_130070_(this.spellId);
      buffer.writeInt(this.spellLevel);
      buffer.writeInt(this.remainingRecasts);
      buffer.writeInt(this.totalRecasts);
      buffer.writeInt(this.ticksToLive);
      buffer.writeInt(this.remainingTicks);
      buffer.m_130068_(this.castSource);
      if (this.castData != null) {
         buffer.writeBoolean(true);
         this.castData.writeToBuffer(buffer);
      } else {
         buffer.writeBoolean(false);
      }
   }

   @Override
   public void readFromBuffer(FriendlyByteBuf buffer) {
      this.spellId = buffer.m_130277_();
      this.spellLevel = buffer.readInt();
      this.remainingRecasts = buffer.readInt();
      this.totalRecasts = buffer.readInt();
      this.ticksToLive = buffer.readInt();
      this.remainingTicks = buffer.readInt();
      this.castSource = (CastSource)buffer.m_130066_(CastSource.class);
      boolean hasCastData = buffer.readBoolean();
      if (hasCastData) {
         ICastDataSerializable tmpCastData = SpellRegistry.getSpell(this.spellId).getEmptyCastData();
         tmpCastData.readFromBuffer(buffer);
         this.castData = tmpCastData;
      }
   }

   public CompoundTag serializeNBT() {
      CompoundTag tag = new CompoundTag();
      tag.m_128359_("spellId", this.spellId);
      tag.m_128405_("spellLevel", this.spellLevel);
      tag.m_128405_("remainingRecasts", this.remainingRecasts);
      tag.m_128405_("totalRecasts", this.totalRecasts);
      tag.m_128405_("ticksToLive", this.ticksToLive);
      tag.m_128405_("ticksRemaining", this.remainingTicks);
      tag.m_128359_("castSource", this.castSource.toString());
      if (this.castData != null) {
         tag.m_128365_("cd", this.castData.serializeNBT());
      }

      return tag;
   }

   public void deserializeNBT(CompoundTag compoundTag) {
      this.spellId = compoundTag.m_128461_("spellId");
      this.spellLevel = compoundTag.m_128451_("spellLevel");
      this.remainingRecasts = compoundTag.m_128451_("remainingRecasts");
      this.totalRecasts = compoundTag.m_128451_("totalRecasts");
      this.ticksToLive = compoundTag.m_128451_("ticksToLive");
      this.remainingTicks = compoundTag.m_128451_("ticksRemaining");
      this.castSource = CastSource.valueOf(compoundTag.m_128461_("castSource"));
      if (compoundTag.m_128441_("cd")) {
         this.castData = SpellRegistry.getSpell(this.spellId).getEmptyCastData();
         if (this.castData != null) {
            this.castData.deserializeNBT((CompoundTag)compoundTag.m_128423_("cd"));
         }
      }
   }

   @Override
   public String toString() {
      String cd = this.castData == null ? "" : ((CompoundTag)this.castData.serializeNBT()).toString();
      return String.format(
         "spellId:%s, spellLevel:%d, remainingRecasts:%d, totalRecasts:%d, ticksToLive:%d, ticksRemaining:%d, castData:%s",
         this.spellId,
         this.spellLevel,
         this.remainingRecasts,
         this.totalRecasts,
         this.ticksToLive,
         this.remainingTicks,
         cd
      );
   }
}
