package io.redspace.ironsspellbooks.item;

import io.redspace.ironsspellbooks.api.item.ISpellbook;
import io.redspace.ironsspellbooks.api.magic.SpellSelectionManager;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.IPresetSpellContainer;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.api.spells.SpellSlot;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.compat.Curios;
import io.redspace.ironsspellbooks.item.curios.CurioBaseItem;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import io.redspace.ironsspellbooks.player.ClientMagicData;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.render.RenderHelper;
import io.redspace.ironsspellbooks.util.ItemPropertiesHelper;
import io.redspace.ironsspellbooks.util.MinecraftInstanceHelper;
import io.redspace.ironsspellbooks.util.TooltipsUtils;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.ChatFormatting;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.ClickEvent.Action;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio.SoundInfo;

public class SpellBook extends CurioBaseItem implements ISpellbook, IPresetSpellContainer, ILecternPlaceable {
   protected final int maxSpellSlots;

   public SpellBook() {
      this(1);
   }

   public SpellBook(int maxSpellSlots) {
      this(maxSpellSlots, ItemPropertiesHelper.equipment().m_41487_(1).m_41497_(Rarity.UNCOMMON));
   }

   public SpellBook(int maxSpellSlots, Properties pProperties) {
      super(pProperties);
      this.maxSpellSlots = maxSpellSlots;
   }

   public SpellBook withAttribute(Holder<Attribute> attribute, double value) {
      return (SpellBook)this.withAttributes(Curios.SPELLBOOK_SLOT, new AttributeContainer(attribute, value, Operation.MULTIPLY_BASE));
   }

   public int getMaxSpellSlots() {
      return this.maxSpellSlots;
   }

   public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
      return true;
   }

   public boolean isUnique() {
      return false;
   }

   public InteractionResult m_6225_(UseOnContext pContext) {
      Level level = pContext.m_43725_();
      BlockPos blockpos = pContext.m_8083_();
      BlockState blockstate = level.m_8055_(blockpos);
      if (blockstate.m_60713_(Blocks.f_50624_)) {
         return LecternBlock.m_269125_(pContext.m_43723_(), level, blockpos, blockstate, pContext.m_43722_())
            ? InteractionResult.m_19078_(level.f_46443_)
            : InteractionResult.PASS;
      } else {
         return InteractionResult.PASS;
      }
   }

   public void m_7373_(@NotNull ItemStack itemStack, Level context, @NotNull List<Component> lines, @NotNull TooltipFlag flag) {
      if (this.isUnique()) {
         lines.add(
            Component.m_237110_(
                  "tooltip.irons_spellbooks.spellbook_rarity",
                  new Object[]{Component.m_237115_("tooltip.irons_spellbooks.spellbook_unique").m_130948_(TooltipsUtils.UNIQUE_STYLE)}
               )
               .m_130940_(ChatFormatting.GRAY)
         );
      }

      Player player = MinecraftInstanceHelper.getPlayer();
      if (player != null && ISpellContainer.isSpellContainer(itemStack)) {
         ISpellContainer spellList = ISpellContainer.get(itemStack);
         lines.add(
            Component.m_237110_("tooltip.irons_spellbooks.spellbook_spell_count", new Object[]{spellList.getMaxSpellCount()}).m_130940_(ChatFormatting.GRAY)
         );
         List<SpellSlot> activeSpellSlots = spellList.getActiveSpells();
         if (!activeSpellSlots.isEmpty()) {
            lines.add(Component.m_237119_());
            lines.add(
               Component.m_237110_("tooltip.irons_spellbooks.press_to_cast", new Object[]{Component.m_237117_("key.irons_spellbooks.spellbook_cast")})
                  .m_130940_(ChatFormatting.GOLD)
            );
            lines.add(Component.m_237119_());
            lines.add(Component.m_237115_("tooltip.irons_spellbooks.spellbook_tooltip").m_130940_(ChatFormatting.GRAY));
            SpellSelectionManager spellSelectionManager = ClientMagicData.getSpellSelectionManager();

            for (int i = 0; i < activeSpellSlots.size(); i++) {
               MutableComponent spellText = TooltipsUtils.getTitleComponent(activeSpellSlots.get(i).spellData(), (LocalPlayer)player).m_6270_(Style.f_131099_);
               SpellSelectionManager.SelectionOption option = spellSelectionManager.getSpellSlot(spellSelectionManager.getSelectionIndex());
               if (MinecraftInstanceHelper.getPlayer() != null
                  && Utils.getPlayerSpellbookStack(MinecraftInstanceHelper.getPlayer()) == itemStack
                  && option != null
                  && option.slot.equals(Curios.SPELLBOOK_SLOT)
                  && option.slotIndex == i) {
                  List<MutableComponent> shiftMessage = TooltipsUtils.formatActiveSpellTooltip(
                     itemStack, spellSelectionManager.getSelectedSpellData(), CastSource.SPELLBOOK, (LocalPlayer)player
                  );
                  shiftMessage.remove(0);
                  TooltipsUtils.addShiftTooltip(
                     lines,
                     Component.m_237113_("> ").m_7220_(spellText).m_130940_(ChatFormatting.YELLOW),
                     shiftMessage.stream().map(component -> Component.m_237113_(" ").m_7220_(component)).collect(Collectors.toList())
                  );
               } else {
                  lines.add(Component.m_237113_(" ").m_7220_(spellText.m_130948_(Style.f_131099_.m_178520_(8947966))));
               }
            }
         }
      }

      super.m_7373_(itemStack, context, lines, flag);
   }

   @NotNull
   @Override
   public SoundInfo getEquipSound(SlotContext slotContext, ItemStack stack) {
      return new SoundInfo((SoundEvent)SoundRegistry.EQUIP_SPELL_BOOK.get(), 1.0F, 1.0F);
   }

   @Override
   public void initializeSpellContainer(ItemStack itemStack) {
      if (itemStack != null) {
         if (!ISpellContainer.isSpellContainer(itemStack)) {
            ISpellContainer.set(itemStack, ISpellContainer.create(this.getMaxSpellSlots(), true, true));
         }
      }
   }

   @Override
   public List<Component> getPages(ItemStack stack) {
      ISpellContainer spellbookData = ISpellContainer.get(stack);
      if (spellbookData != null && !spellbookData.isEmpty()) {
         Player player = MinecraftInstanceHelper.getPlayer();
         return spellbookData.getActiveSpells()
            .stream()
            .map(
               slot -> {
                  int color = slot.getSpell().getSchoolType().getDisplayName().m_7383_().m_131135_().m_131265_();
                  color = RenderHelper.colorLerp(0.6F, color, 0);
                  Style titleStyle = Style.f_131099_
                     .m_178520_(color)
                     .m_131162_(true)
                     .m_131136_(true)
                     .m_131142_(new ClickEvent(Action.OPEN_URL, "https://www.patreon.com/iron431"));
                  boolean hideStats = false;
                  if (player != null) {
                     List<MutableComponent> scrollTooltip = TooltipsUtils.formatActiveSpellTooltip(
                        null, slot.spellData(), CastSource.SPELLBOOK, (LocalPlayer)player
                     );
                     scrollTooltip.remove(0);
                     titleStyle = titleStyle.m_131144_(
                        new HoverEvent(
                           net.minecraft.network.chat.HoverEvent.Action.f_130831_,
                           (Component)scrollTooltip.stream().reduce((a, b) -> a.m_130946_("\n").m_7220_(b)).get()
                        )
                     );
                     if (slot.getSpell().obfuscateStats(player)) {
                        hideStats = true;
                     }
                  }

                  MutableComponent title = Component.m_237115_(slot.getSpell().getComponentId()).m_130948_(titleStyle);
                  MutableComponent desc = Component.m_237115_(slot.getSpell().getComponentId() + ".guide").m_130940_(ChatFormatting.BLACK);
                  MutableComponent page = Component.m_237113_("").m_7220_(title).m_130946_("\n\n").m_7220_(desc);
                  if (hideStats) {
                     page = page.m_130948_(page.m_7383_().m_131146_(Style.f_131099_.m_131150_(ResourceLocation.withDefaultNamespace("alt"))));
                  }

                  return page;
               }
            )
            .toList();
      } else {
         return List.of(
            Component.m_237115_("ui.irons_spellbooks.empty_spellbook_lectern").m_130944_(new ChatFormatting[]{ChatFormatting.GRAY, ChatFormatting.ITALIC})
         );
      }
   }
}
