package io.redspace.ironsspellbooks.network.particles;

import io.redspace.ironsspellbooks.network.AbstractVec3Packet;
import java.util.function.Consumer;
import net.minecraft.world.phys.Vec3;

public abstract class AbstractSimpleParticlePacket extends AbstractVec3Packet {
   public AbstractSimpleParticlePacket(Vec3 pos) {
      super(pos);
   }

   abstract Consumer<Vec3> particleFunction();
}
