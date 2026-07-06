package io.redspace.ironsspellbooks.worldgen;

import net.minecraft.world.level.levelgen.structure.BoundingBox;

public interface IExtendedNoiseChunk {
   IExtendedNoiseChunk.AquifierNuke irons_spellbooks$getAquifierStatus();

   void irons_spellbooks$setAquifierStatus(IExtendedNoiseChunk.AquifierNuke var1);

   record AquifierNuke(BoundingBox[] boundingBoxes) {
   }
}
