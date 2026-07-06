package io.redspace.ironsspellbooks.util;

import io.redspace.ironsspellbooks.entity.spells.portal.PortalPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class NBT {
   public static CompoundTag writePortalPos(PortalPos globalPos) {
      CompoundTag tag = new CompoundTag();
      tag.m_128359_("res", globalPos.dimension().m_135782_().toString());
      CompoundTag posTag = writeVec3Pos(globalPos.pos());
      tag.m_128365_("pos", posTag);
      tag.m_128350_("rot", globalPos.rotation());
      return tag;
   }

   public static PortalPos readPortalPos(CompoundTag compoundTag) {
      ResourceLocation resourcelocation = ResourceLocation.parse(compoundTag.m_128461_("res"));
      CompoundTag posTag = (CompoundTag)compoundTag.m_128423_("pos");
      Vec3 pos = readVec3(posTag);
      ResourceKey<Level> resourceKey = ResourceKey.m_135785_(Registries.f_256858_, resourcelocation);
      float rotation = compoundTag.m_128457_("rot");
      return PortalPos.of(resourceKey, pos, rotation);
   }

   public static Vec3 readVec3(CompoundTag pTag) {
      return new Vec3(pTag.m_128459_("X"), pTag.m_128459_("Y"), pTag.m_128459_("Z"));
   }

   public static CompoundTag writeVec3Pos(Vec3 pPos) {
      CompoundTag compoundtag = new CompoundTag();
      compoundtag.m_128347_("X", pPos.f_82479_);
      compoundtag.m_128347_("Y", pPos.f_82480_);
      compoundtag.m_128347_("Z", pPos.f_82481_);
      return compoundtag;
   }
}
