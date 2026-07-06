package io.redspace.ironsspellbooks.item;

import io.redspace.ironsspellbooks.api.item.IScroll;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.magic.SpellSelectionManager;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.api.spells.SpellData;
import io.redspace.ironsspellbooks.player.ClientMagicData;
import io.redspace.ironsspellbooks.util.MinecraftInstanceHelper;
import io.redspace.ironsspellbooks.util.TooltipsUtils;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Scroll extends Item implements IScroll {
   public Scroll() {
      super(new Properties().m_41497_(Rarity.UNCOMMON));
   }

   @NotNull
   private SpellData getSpellSlotFromStack(ItemStack itemStack) {
      return ISpellContainer.getOrCreate(itemStack).getSpellAtIndex(0);
   }

   protected void removeScrollAfterCast(ServerPlayer serverPlayer, ItemStack stack) {
      if (!serverPlayer.m_7500_()) {
         stack.m_41774_(1);
      }
   }

   public static void attemptRemoveScrollAfterCast(ServerPlayer serverPlayer) {
      ItemStack potentialScroll = MagicData.getPlayerMagicData(serverPlayer).getPlayerCastingItem();
      if (potentialScroll.m_41720_() instanceof Scroll scroll) {
         scroll.removeScrollAfterCast(serverPlayer, potentialScroll);
      }
   }

   @Nullable
   public String getCreatorModId(ItemStack itemStack) {
      AbstractSpell spell = this.getSpellSlotFromStack(itemStack).getSpell();
      ResourceLocation id = SpellRegistry.REGISTRY.get().getKey(spell);
      return id == null ? super.getCreatorModId(itemStack) : id.m_135827_();
   }

   @NotNull
   public InteractionResultHolder<ItemStack> m_7203_(Level level, Player player, @NotNull InteractionHand hand) {
      ItemStack stack = player.m_21120_(hand);
      SpellData spellSlot = this.getSpellSlotFromStack(stack);
      AbstractSpell spell = spellSlot.getSpell();
      if (level.f_46443_) {
         if (ClientMagicData.isCasting()) {
            return InteractionResultHolder.m_19096_(stack);
         } else {
            return !ClientMagicData.getSyncedSpellData(player).isSpellLearned(spell)
               ? InteractionResultHolder.m_19098_(stack)
               : InteractionResultHolder.m_19096_(stack);
         }
      } else {
         String castingSlot = hand.ordinal() == 0 ? SpellSelectionManager.MAINHAND : SpellSelectionManager.OFFHAND;
         return spell.attemptInitiateCast(stack, spell.getLevelFor(spellSlot.getLevel(), player), level, player, CastSource.SCROLL, false, castingSlot)
            ? InteractionResultHolder.m_19096_(stack)
            : InteractionResultHolder.m_19100_(stack);
      }
   }

   @NotNull
   public Component m_7626_(@NotNull ItemStack itemStack) {
      return this.getSpellSlotFromStack(itemStack).getDisplayName();
   }

   public void m_7373_(@NotNull ItemStack itemStack, Level context, @NotNull List<Component> lines, @NotNull TooltipFlag flag) {
      super.m_7373_(itemStack, context, lines, flag);
      MinecraftInstanceHelper.ifPlayerPresent(player -> lines.addAll(TooltipsUtils.formatScrollTooltip(itemStack, player)));
   }
}
