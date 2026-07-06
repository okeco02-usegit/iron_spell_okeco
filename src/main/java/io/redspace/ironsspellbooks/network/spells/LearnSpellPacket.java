package io.redspace.ironsspellbooks.network.spells;

import io.redspace.ironsspellbooks.api.backwards_compat.CustomPacketPayload;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.capabilities.magic.SyncedSpellData;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent.Context;

public class LearnSpellPacket implements CustomPacketPayload {
   private final byte hand;
   private final String spell;

   public LearnSpellPacket(InteractionHand interactionHand, String spell) {
      this.hand = handToByte(interactionHand);
      this.spell = spell;
   }

   public LearnSpellPacket(FriendlyByteBuf buf) {
      this.hand = buf.readByte();
      this.spell = buf.m_130277_();
   }

   public void toBytes(FriendlyByteBuf buf) {
      buf.writeByte(this.hand);
      buf.m_130070_(this.spell);
   }

   public boolean handle(Supplier<Context> supplier) {
      Context ctx = supplier.get();
      ctx.enqueueWork(
         () -> {
            ServerPlayer serverPlayer = ctx.getSender();
            ItemStack itemStack = serverPlayer.m_21120_(byteToHand(this.hand));
            AbstractSpell spell = SpellRegistry.getSpell(this.spell);
            SyncedSpellData data = MagicData.getPlayerMagicData(serverPlayer).getSyncedData();
            if (spell != SpellRegistry.none()
               && !data.isSpellLearned(spell)
               && itemStack.m_150930_((Item)ItemRegistry.ELDRITCH_PAGE.get())
               && itemStack.m_41613_() > 0) {
               data.learnSpell(spell);
               if (!serverPlayer.m_150110_().f_35937_) {
                  itemStack.m_41774_(1);
               }
            }
         }
      );
      return true;
   }

   public static byte handToByte(InteractionHand hand) {
      return (byte)(hand == InteractionHand.MAIN_HAND ? 1 : 0);
   }

   public static InteractionHand byteToHand(byte b) {
      return b > 0 ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
   }
}
