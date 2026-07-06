package io.redspace.ironsspellbooks.item.weapons;

import io.redspace.ironsspellbooks.api.item.weapons.MagicSwordItem;
import io.redspace.ironsspellbooks.api.registry.SpellDataRegistryHolder;
import io.redspace.ironsspellbooks.entity.spells.thrown_spear.ThrownSpear;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow.Pickup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;

public class TwilightGaleItem extends MagicSwordItem {
   public <T extends Tier & IronsWeaponTier> TwilightGaleItem(T pTier, Properties pProperties, SpellDataRegistryHolder[] spellDataRegistryHolders) {
      super(pTier, pProperties, spellDataRegistryHolders);
   }

   public UseAnim m_6164_(ItemStack stack) {
      return UseAnim.SPEAR;
   }

   public int m_8105_(ItemStack pStack) {
      return 72000;
   }

   public InteractionResultHolder<ItemStack> m_7203_(Level level, Player player, InteractionHand hand) {
      ItemStack itemstack = player.m_21120_(hand);
      if (isTooDamagedToUse(itemstack)) {
         return InteractionResultHolder.m_19100_(itemstack);
      }

      player.m_6672_(hand);
      return InteractionResultHolder.m_19096_(itemstack);
   }

   public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
      return super.canApplyAtEnchantingTable(stack, enchantment) || enchantment == Enchantments.f_44955_ || enchantment == Enchantments.f_44958_;
   }

   public void m_5551_(ItemStack stack, Level level, LivingEntity entityLiving, int timeLeft) {
      if (entityLiving instanceof Player player) {
         int i = this.m_8105_(stack) - timeLeft;
         if (i >= 8 && !isTooDamagedToUse(stack)) {
            Holder<SoundEvent> holder = BuiltInRegistries.f_256894_.m_263177_(SoundEvents.f_12520_);
            if (!level.f_46443_) {
               stack.m_41622_(1, player, p_43388_ -> p_43388_.m_21190_(entityLiving.m_7655_()));
               double damage = 1.0F + ExtendedWeaponTier.TWILIGHT_GALE.damage;
               if (stack.equals(player.m_21205_())) {
                  damage = player.m_21133_(Attributes.f_22281_);
               }

               ThrownSpear throwntrident = new ThrownSpear(level, stack, damage);
               throwntrident.m_5602_(player);
               throwntrident.m_20219_(player.m_146892_());
               throwntrident.m_37251_(player, player.m_146909_(), player.m_146908_(), 0.0F, 2.5F, 0.5F);
               if (player.m_150110_().f_35937_) {
                  throwntrident.f_36705_ = Pickup.CREATIVE_ONLY;
               }

               level.m_7967_(throwntrident);
               if (!player.m_150110_().f_35937_) {
                  player.m_36335_().m_41524_(stack.m_41720_(), 200);
               }

               level.m_6269_(null, throwntrident, (SoundEvent)holder.m_203334_(), SoundSource.PLAYERS, 1.0F, 1.0F);
            }

            player.m_36246_(Stats.f_12982_.m_12902_(this));
         }
      }
   }

   private static boolean isTooDamagedToUse(ItemStack stack) {
      return stack.m_41773_() >= stack.m_41776_() - 1;
   }
}
