package io.redspace.ironsspellbooks.jei;

import io.redspace.ironsspellbooks.api.item.UpgradeData;
import io.redspace.ironsspellbooks.api.item.curios.AffinityData;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.item.InkItem;
import io.redspace.ironsspellbooks.item.UpgradeOrbTypeData;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import io.redspace.ironsspellbooks.registries.UpgradeOrbTypeRegistry;
import io.redspace.ironsspellbooks.util.UpgradeUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.core.HolderGetter;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

public class ArcaneAnvilJeiRecipe {
   @NotNull
   ArcaneAnvilJeiRecipe.Type type;
   @Nullable
   Item leftItem;
   @Nullable
   Item rightItem;
   @Nullable
   AbstractSpell spell;
   @Nullable
   int level;

   public ArcaneAnvilJeiRecipe(Item leftItem, Item rightItem) {
      this.leftItem = leftItem;
      this.rightItem = rightItem;
      this.type = ArcaneAnvilJeiRecipe.Type.Item_Upgrade;
   }

   public ArcaneAnvilJeiRecipe(Item leftItem, AbstractSpell spell) {
      this.leftItem = leftItem;
      this.spell = spell;
      this.type = ArcaneAnvilJeiRecipe.Type.Imbue;
   }

   public ArcaneAnvilJeiRecipe(AbstractSpell spell, int baseLevel) {
      this.spell = spell;
      this.level = baseLevel;
      this.type = ArcaneAnvilJeiRecipe.Type.Scroll_Upgrade;
   }

   public ArcaneAnvilJeiRecipe(AbstractSpell spell) {
      this.spell = spell;
      this.type = ArcaneAnvilJeiRecipe.Type.Affinity_Ring_Attune;
   }

   public ArcaneAnvilJeiRecipe.Tuple<List<ItemStack>, List<ItemStack>, List<ItemStack>> getRecipeItems() {
      return switch (this.type) {
         case Scroll_Upgrade -> {
            ItemStack scroll1 = new ItemStack((ItemLike)ItemRegistry.SCROLL.get());
            ItemStack scroll2 = new ItemStack((ItemLike)ItemRegistry.SCROLL.get());
            ItemStack ink = new ItemStack(InkItem.getInkForRarity(this.spell.getRarity(this.level + 1)));
            ISpellContainer.createScrollContainer(this.spell, this.level, scroll1);
            ISpellContainer.createScrollContainer(this.spell, this.level + 1, scroll2);
            yield new ArcaneAnvilJeiRecipe.Tuple<>(List.of(scroll1), List.of(ink), List.of(scroll2));
         }
         case Imbue -> {
            ArcaneAnvilJeiRecipe.Tuple<List<ItemStack>, List<ItemStack>, List<ItemStack>> tuple = new ArcaneAnvilJeiRecipe.Tuple<>(
               new ArrayList<>(), new ArrayList<>(), new ArrayList<>()
            );
            tuple.a.add(new ItemStack(this.leftItem));
            SpellRegistry.getEnabledSpells().forEach(spell -> IntStream.rangeClosed(spell.getMinLevel(), spell.getMaxLevel()).forEach(i -> {
               ItemStack scroll = new ItemStack((ItemLike)ItemRegistry.SCROLL.get());
               ISpellContainer.createScrollContainer(spell, i, scroll);
               ItemStack resultx = new ItemStack(this.leftItem);
               ISpellContainer.createScrollContainer(spell, i, resultx);
               tuple.b.add(scroll);
               tuple.c.add(resultx);
            }));
            yield tuple;
         }
         case Item_Upgrade -> {
            ArcaneAnvilJeiRecipe.Tuple<List<ItemStack>, List<ItemStack>, List<ItemStack>> tuple = new ArcaneAnvilJeiRecipe.Tuple<>(
               new ArrayList<>(), new ArrayList<>(), new ArrayList<>()
            );
            tuple.a.add(new ItemStack(this.leftItem));
            ItemStack upgradeStack = new ItemStack(this.rightItem);
            ItemStack result = new ItemStack(this.leftItem);
            UpgradeData.set(
               result,
               UpgradeData.NONE
                  .addUpgrade(
                     result,
                     ((HolderGetter)Minecraft.m_91087_().f_91073_.m_9598_().m_255325_().m_255095_(UpgradeOrbTypeRegistry.UPGRADE_ORB_REGISTRY_KEY).get())
                        .m_255043_(UpgradeOrbTypeData.get(upgradeStack).type()),
                     UpgradeUtils.getRelevantEquipmentSlot(result)
                  )
            );
            tuple.b.add(upgradeStack);
            tuple.c.add(result);
            yield tuple;
         }
         case Affinity_Ring_Attune -> {
            ArcaneAnvilJeiRecipe.Tuple<List<ItemStack>, List<ItemStack>, List<ItemStack>> tuple = new ArcaneAnvilJeiRecipe.Tuple<>(
               new ArrayList<>(), new ArrayList<>(), new ArrayList<>()
            );
            ItemStack result = new ItemStack((ItemLike)ItemRegistry.AFFINITY_RING.get());
            AffinityData.set(result, new AffinityData(this.spell));
            SpellRegistry.getEnabledSpells().forEach(randomSpell -> {
               ItemStack baseRing = new ItemStack((ItemLike)ItemRegistry.AFFINITY_RING.get());
               AffinityData.set(baseRing, new AffinityData(randomSpell));
               tuple.a.add(baseRing);
            });
            IntStream.rangeClosed(this.spell.getMinLevel(), this.spell.getMaxLevel()).forEach(i -> {
               ItemStack scroll = new ItemStack((ItemLike)ItemRegistry.SCROLL.get());
               ISpellContainer.createScrollContainer(this.spell, i, scroll);
               tuple.b.add(scroll);
            });
            tuple.c.add(result);
            yield tuple;
         }
      };
   }

   public record Tuple<A, B, C>(A a, B b, C c) {
   }

   enum Type {
      Scroll_Upgrade,
      Item_Upgrade,
      Imbue,
      Affinity_Ring_Attune;
   }
}
