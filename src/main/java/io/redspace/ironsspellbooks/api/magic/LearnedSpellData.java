package io.redspace.ironsspellbooks.api.magic;

import io.redspace.ironsspellbooks.api.network.ISerializable;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class LearnedSpellData implements ISerializable {
   public static final String LEARNED_SPELLS = "learnedSpells";
   public final Set<ResourceLocation> learnedSpells = new HashSet<>();

   public void saveToNBT(CompoundTag compound) {
      if (!this.learnedSpells.isEmpty()) {
         ListTag listTag = new ListTag();

         for (ResourceLocation resourceLocation : this.learnedSpells) {
            listTag.add(StringTag.m_129297_(resourceLocation.toString()));
         }

         compound.m_128365_("learnedSpells", listTag);
      }
   }

   public void loadFromNBT(CompoundTag compound) {
      ListTag learnedTag = (ListTag)compound.m_128423_("learnedSpells");
      if (learnedTag != null && !learnedTag.isEmpty()) {
         for (Tag tag : learnedTag) {
            if (tag instanceof StringTag stringTag) {
               ResourceLocation resourceLocation = ResourceLocation.parse(stringTag.m_7916_());
               if (SpellRegistry.getSpell(resourceLocation) != null) {
                  this.learnedSpells.add(resourceLocation);
               }
            }
         }
      }
   }

   @Override
   public void writeToBuffer(FriendlyByteBuf buf) {
      buf.writeInt(this.learnedSpells.size());

      for (ResourceLocation resourceLocation : this.learnedSpells) {
         buf.m_130085_(resourceLocation);
      }
   }

   @Override
   public void readFromBuffer(FriendlyByteBuf buf) {
      int i = buf.readInt();
      if (i > 0) {
         for (int j = 0; j < i; j++) {
            ResourceLocation resourceLocation = buf.m_130281_();
            if (SpellRegistry.getSpell(resourceLocation) != null) {
               this.learnedSpells.add(resourceLocation);
            }
         }
      }
   }
}
