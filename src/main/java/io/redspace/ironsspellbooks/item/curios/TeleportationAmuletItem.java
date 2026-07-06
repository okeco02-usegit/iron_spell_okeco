package io.redspace.ironsspellbooks.item.curios;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.compat.Curios;
import io.redspace.ironsspellbooks.util.MinecraftInstanceHelper;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.Vec3;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;

public class TeleportationAmuletItem extends SimpleDescriptiveCurio {
   private static final Component VANITY_DESCRIPTION = Component.m_237115_("item.irons_spellbooks.teleportation_amulet.desc.alt")
      .m_130944_(new ChatFormatting[]{ChatFormatting.GRAY, ChatFormatting.ITALIC});

   public TeleportationAmuletItem(Properties properties) {
      super(properties, Curios.NECKLACE_SLOT);
   }

   private void handleCurse(SlotContext slotContext, ItemStack stack) {
      LivingEntity entity = slotContext.entity();
      if (entity != null && !slotContext.entity().f_19853_.f_46443_ && !this.canUse(entity)) {
         CuriosApi.getCuriosInventory(slotContext.entity()).ifPresent(handler -> {
            ItemStack equippedStack = handler.getEquippedCurios().getStackInSlot(slotContext.index());
            if (ItemStack.m_41728_(stack, equippedStack)) {
               handler.setEquippedCurio(Curios.NECKLACE_SLOT, slotContext.index(), ItemStack.f_41583_);
               this.createItemEntity(slotContext.entity().f_19853_, stack, slotContext.entity().m_20182_());
            }
         });
      }
   }

   @Override
   public List<Component> getSlotsTooltip(List<Component> tooltips, ItemStack stack) {
      Player player = MinecraftInstanceHelper.getPlayer();
      if (player != null && this.canUse(player)) {
         super.getSlotsTooltip(tooltips, stack);
      }

      tooltips.add(0, VANITY_DESCRIPTION);
      return tooltips;
   }

   public void curioTick(SlotContext slotContext, ItemStack stack) {
      super.curioTick(slotContext, stack);
      if (slotContext.entity().f_19797_ % 5 == 0) {
         this.handleCurse(slotContext, stack);
      }
   }

   private boolean canUse(LivingEntity livingEntity) {
      return livingEntity.m_21133_((Attribute)AttributeRegistry.ENDER_SPELL_POWER.get()) > 1.25;
   }

   private void createItemEntity(Level level, ItemStack stack, Vec3 center) {
      Vec3 target = center.m_82549_(
         new Vec3(Utils.random.m_216332_(4, 8) + Utils.random.m_188501_(), 0.0, 0.0).m_82524_(Utils.random.m_188501_() * (float) (Math.PI * 2))
      );
      Vec3 clipped = Utils.raycastForBlock(level, center.m_82520_(0.0, 0.5, 0.0), target.m_82520_(0.0, 0.5, 0.0), Fluid.NONE).m_82450_();
      Vec3 placement = Utils.moveToRelativeGroundLevel(level, clipped, 5).m_82520_(0.0, 0.75, 0.0);
      ItemEntity item = new ItemEntity(level, placement.f_82479_, placement.f_82480_, placement.f_82481_, stack);
      level.m_7967_(item);
      MagicManager.spawnParticles(
         level, ParticleHelper.UNSTABLE_ENDER, placement.f_82479_, placement.f_82480_, placement.f_82481_, 20, 0.2, 0.2, 0.2, 0.2, false
      );
      level.m_5594_(null, BlockPos.m_274446_(placement), SoundEvents.f_11852_, SoundSource.PLAYERS, 1.0F, 1.0F);
      level.m_5594_(null, BlockPos.m_274446_(center), SoundEvents.f_11852_, SoundSource.PLAYERS, 1.0F, 1.0F);
   }
}
