package io.redspace.ironsspellbooks.network;

import io.redspace.ironsspellbooks.api.backwards_compat.CustomPacketPayload;
import io.redspace.ironsspellbooks.block.scroll_forge.ScrollForgeTile;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public class ScrollForgeSelectSpellPacket implements CustomPacketPayload {
   private final BlockPos pos;
   private final String spellId;

   public ScrollForgeSelectSpellPacket(BlockPos pos, String spellId) {
      this.pos = pos;
      this.spellId = spellId;
   }

   public ScrollForgeSelectSpellPacket(FriendlyByteBuf buf) {
      int x = buf.readInt();
      int y = buf.readInt();
      int z = buf.readInt();
      this.pos = new BlockPos(x, y, z);
      this.spellId = buf.m_130277_();
   }

   public void toBytes(FriendlyByteBuf buf) {
      buf.writeInt(this.pos.m_123341_());
      buf.writeInt(this.pos.m_123342_());
      buf.writeInt(this.pos.m_123343_());
      buf.m_130070_(this.spellId);
   }

   public boolean handle(Supplier<Context> supplier) {
      Context ctx = supplier.get();
      ctx.enqueueWork(() -> {
         ScrollForgeTile scrollForgeTile = (ScrollForgeTile)ctx.getSender().m_9236_().m_7702_(this.pos);
         if (scrollForgeTile != null) {
            scrollForgeTile.setRecipeSpell(this.spellId);
         }
      });
      return true;
   }
}
