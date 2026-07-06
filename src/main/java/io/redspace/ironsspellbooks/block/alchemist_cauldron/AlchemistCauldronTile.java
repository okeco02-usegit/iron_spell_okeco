package io.redspace.ironsspellbooks.block.alchemist_cauldron;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.backwards_compat.FluidHelper;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.api.spells.SpellData;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.config.ServerConfigs;
import io.redspace.ironsspellbooks.fluids.PotionFluid;
import io.redspace.ironsspellbooks.item.InkItem;
import io.redspace.ironsspellbooks.recipe_types.alchemist_cauldron.BrewAlchemistCauldronRecipe;
import io.redspace.ironsspellbooks.recipe_types.alchemist_cauldron.EmptyAlchemistCauldronRecipe;
import io.redspace.ironsspellbooks.recipe_types.alchemist_cauldron.FillAlchemistCauldronRecipe;
import io.redspace.ironsspellbooks.registries.BlockRegistry;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import io.redspace.ironsspellbooks.registries.RecipeRegistry;
import io.redspace.ironsspellbooks.util.ModTags;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.Nullable;

public class AlchemistCauldronTile extends BlockEntity implements WorldlyContainer {
   public static int INPUT_SIZE = 4;
   public final NonNullList<ItemStack> inputItems = NonNullList.m_122780_(INPUT_SIZE, ItemStack.f_41583_);
   private final int[] cooktimes = new int[INPUT_SIZE];
   public AlchemistCauldronTile.AlchemistCauldronFluidHandler fluidInventory;
   private LazyOptional<IFluidHandler> fluidHandlerLazyOptional = LazyOptional.of(() -> this.fluidInventory);
   public static final Capability<IFluidHandler> FLUID_HANDLER = CapabilityManager.get(new CapabilityToken<IFluidHandler>() {});

   public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
      return cap == ForgeCapabilities.FLUID_HANDLER ? this.fluidHandlerLazyOptional.cast() : super.getCapability(cap, side);
   }

   public void invalidateCaps() {
      super.invalidateCaps();
      this.fluidHandlerLazyOptional.invalidate();
   }

   public AlchemistCauldronTile(BlockPos pWorldPosition, BlockState pBlockState) {
      super((BlockEntityType)BlockRegistry.ALCHEMIST_CAULDRON_TILE.get(), pWorldPosition, pBlockState);
      this.fluidInventory = new AlchemistCauldronTile.AlchemistCauldronFluidHandler();
   }

   public static void serverTick(Level level, BlockPos pos, BlockState blockState, AlchemistCauldronTile cauldronTile) {
      for (int i = 0; i < cauldronTile.inputItems.size(); i++) {
         ItemStack itemStack = (ItemStack)cauldronTile.inputItems.get(i);
         if (!itemStack.m_41619_() && cauldronTile.isBoiling(blockState)) {
            cauldronTile.cooktimes[i]++;
         } else {
            cauldronTile.cooktimes[i] = 0;
         }

         if (cauldronTile.cooktimes[i] > 100) {
            cauldronTile.tryMeltInput(itemStack);
            cauldronTile.cooktimes[i] = 0;
         }
      }

      RandomSource random = Utils.random;
      if (cauldronTile.isBoiling(blockState)) {
         float waterLevel = Mth.m_14179_(cauldronTile.getFluidAmount() / 1000.0F, 0.25F, 0.9F);
         MagicManager.spawnParticles(
            level,
            ParticleTypes.f_123772_,
            pos.m_123341_() + Mth.m_216283_(random, 0.2F, 0.8F),
            pos.m_123342_() + waterLevel,
            pos.m_123343_() + Mth.m_216283_(random, 0.2F, 0.8F),
            1,
            0.0,
            0.0,
            0.0,
            0.0,
            false
         );
      }
   }

   public ItemStack tryExecuteRecipeInteractions(Level level, ItemStack itemStack) {
      SimpleContainer fillRecipeInput = new SimpleContainer(new ItemStack[]{itemStack});
      RecipeManager recipeManager = level.m_7465_();
      Optional<FillAlchemistCauldronRecipe> fillRecipe = recipeManager.m_44015_(
         (RecipeType)RecipeRegistry.ALCHEMIST_CAULDRON_FILL_TYPE.get(), fillRecipeInput, level
      );
      if (fillRecipe.isEmpty() && FluidHelper.hasPotionContents(itemStack)) {
         FluidStack fluid;
         if (FluidHelper.isWater(itemStack)) {
            fluid = new FluidStack(Fluids.f_76193_, 250);
         } else {
            fluid = PotionFluid.from(itemStack);
         }

         fillRecipe = Optional.of(
            new FillAlchemistCauldronRecipe(
               IronsSpellbooks.id("generated"),
               Ingredient.m_43927_(new ItemStack[]{itemStack}),
               new ItemStack(Items.f_42590_),
               fluid,
               true,
               BuiltInRegistries.f_256894_.m_263177_(SoundEvents.f_11769_)
            )
         );
      }

      if (fillRecipe.isPresent()) {
         FillAlchemistCauldronRecipe recipe = fillRecipe.get();
         int amountThatCanFit = this.fluidInventory.fill(recipe.result(), FluidAction.SIMULATE);
         if ((!recipe.mustFitAll() || amountThatCanFit == recipe.result().getAmount()) && amountThatCanFit != 0) {
            this.fluidInventory.fill(recipe.result(), FluidAction.EXECUTE);
            this.m_6596_();
            level.m_247517_(null, this.m_58899_(), (SoundEvent)recipe.fillSound().m_203334_(), SoundSource.BLOCKS);
            return recipe.m_5874_(fillRecipeInput, level.m_9598_());
         }
      }

      FluidStack topFluid = this.fluidInventory.drain(1000, FluidAction.SIMULATE);
      EmptyAlchemistCauldronRecipe.Input emptyRecipeInput = new EmptyAlchemistCauldronRecipe.Input(itemStack, topFluid);
      Optional<EmptyAlchemistCauldronRecipe> emptyRecipe = recipeManager.m_44015_(
         (RecipeType)RecipeRegistry.ALCHEMIST_CAULDRON_EMPTY_TYPE.get(), emptyRecipeInput, level
      );
      if (emptyRecipe.isEmpty() && itemStack.m_150930_(Items.f_42590_)) {
         ItemStack potionStack = PotionFluid.from(topFluid);
         if (!potionStack.m_41619_()) {
            emptyRecipe = Optional.of(
               new EmptyAlchemistCauldronRecipe(
                  IronsSpellbooks.id("generated"),
                  Ingredient.f_43901_,
                  potionStack,
                  FluidHelper.copyWithAmount(topFluid, 250),
                  BuiltInRegistries.f_256894_.m_263177_(SoundEvents.f_11770_)
               )
            );
         }
      }

      if (emptyRecipe.isPresent()) {
         EmptyAlchemistCauldronRecipe recipe = emptyRecipe.get();
         this.fluidInventory.drain(recipe.fluid(), FluidAction.EXECUTE);
         level.m_247517_(null, this.m_58899_(), (SoundEvent)recipe.emptySound().m_203334_(), SoundSource.BLOCKS);
         this.m_6596_();
         return recipe.assemble(emptyRecipeInput, level.m_9598_());
      } else {
         return ItemStack.f_41583_;
      }
   }

   public InteractionResult handleUse(BlockState blockState, Level level, BlockPos pos, Player player, InteractionHand hand) {
      ItemStack itemStack = player.m_21120_(hand);
      ItemStack recipeResult = this.tryExecuteRecipeInteractions(level, itemStack);
      if (!recipeResult.m_41619_()) {
         player.m_21008_(hand, ItemUtils.m_41813_(player.m_21120_(hand), player, recipeResult));
         return InteractionResult.SUCCESS;
      }

      if (this.isValidInput(itemStack)) {
         if (!level.f_46443_) {
            for (int i = 0; i < this.inputItems.size(); i++) {
               ItemStack stack = (ItemStack)this.inputItems.get(i);
               if (stack.m_41619_()) {
                  ItemStack input = player.m_150110_().f_35937_ ? itemStack.m_41777_() : itemStack.m_41620_(1);
                  input.m_41764_(1);
                  this.inputItems.set(i, input);
                  player.m_21008_(hand, itemStack);
                  this.m_6596_();
                  break;
               }
            }
         }

         return InteractionResult.SUCCESS;
      } else {
         if ((itemStack.m_41619_() || player.m_6047_()) && hand.equals(InteractionHand.MAIN_HAND)) {
            for (ItemStack item : this.inputItems) {
               if (!item.m_41619_()) {
                  if (!level.f_46443_) {
                     ItemStack take = item.m_41620_(1);
                     if (player.m_21120_(hand).m_41619_()) {
                        player.m_21008_(hand, take);
                     } else if (!player.m_150109_().m_36054_(take)) {
                        player.m_36176_(take, false);
                     }

                     this.m_6596_();
                  }

                  return InteractionResult.SUCCESS;
               }
            }
         }

         return InteractionResult.CONSUME;
      }
   }

   public void tryMeltInput(ItemStack itemStack) {
      if (this.f_58857_ != null && this.f_58857_ instanceof ServerLevel serverLevel) {
         boolean var12 = false;
         boolean success = true;
         Optional byproduct = Optional.empty();
         if (itemStack.m_150930_((Item)ItemRegistry.SCROLL.get()) && this.fluidInventory.contains(FluidTags.f_13131_, 250)) {
            if (Utils.random.m_188501_() < (Double)ServerConfigs.SCROLL_RECYCLE_CHANCE.get()) {
               this.fluidInventory.drain(new FluidStack(Fluids.f_76193_, 250), FluidAction.EXECUTE);
               this.fluidInventory.fill(new FluidStack(getInkFromScroll(itemStack).fluid().get(), 250), FluidAction.EXECUTE);
            } else {
               success = false;
            }

            var12 = true;
         }

         if (!var12) {
            for (FluidStack fluid : this.fluidInventory.fluids()) {
               BrewAlchemistCauldronRecipe.Input input = new BrewAlchemistCauldronRecipe.Input(fluid, itemStack);
               Optional<BrewAlchemistCauldronRecipe> brewRecipeOpt = serverLevel.m_7465_()
                  .m_44015_((RecipeType)RecipeRegistry.ALCHEMIST_CAULDRON_BREW_TYPE.get(), input, serverLevel);
               if (brewRecipeOpt.isPresent()) {
                  BrewAlchemistCauldronRecipe recipe = brewRecipeOpt.get();
                  int totalNewFluid = recipe.results().stream().mapToInt(FluidStack::getAmount).sum();
                  if (this.fluidInventory.canFit(totalNewFluid - recipe.fluidIn().getAmount())
                     && this.fluidInventory.contains(recipe.fluidIn(), recipe.fluidIn().getAmount())) {
                     var12 = true;
                     this.fluidInventory.drain(recipe.fluidIn(), FluidAction.EXECUTE);
                     recipe.results().forEach(result -> this.fluidInventory.fill(result, FluidAction.EXECUTE));
                     byproduct = recipe.byproduct();
                  }
               }
            }
         }

         if (!var12 && this.isBrewable(itemStack)) {
            for (FluidStack fluid : this.fluidInventory.fluids()) {
               ItemStack potionGhostStack = PotionFluid.from(fluid);
               if (!potionGhostStack.m_41619_()) {
                  ItemStack potionResult = FluidHelper.getNonDestructiveBrewingResult(potionGhostStack, itemStack, serverLevel);
                  if (!potionResult.m_41619_()) {
                     FluidStack fluidResult = FluidHelper.copyWithAmount(PotionFluid.from(potionResult), fluid.getAmount());
                     this.fluidInventory.drain(fluid, FluidAction.EXECUTE);
                     this.fluidInventory.fill(fluidResult, FluidAction.EXECUTE);
                     var12 = true;
                  }
               }
            }
         }

         if (var12) {
            itemStack.m_41774_(1);
            if (byproduct.isPresent()) {
               for (int i = 0; i < this.inputItems.size(); i++) {
                  ItemStack stack = (ItemStack)this.inputItems.get(i);
                  if (stack.m_41619_()) {
                     ItemStack input = ((ItemStack)byproduct.get()).m_41620_(1);
                     this.inputItems.set(i, input);
                     break;
                  }
               }

               Vec3 pos = Vec3.m_82514_(this.m_58899_(), 1.0);
               Containers.m_18992_(this.f_58857_, pos.f_82479_, pos.f_82480_, pos.f_82481_, ((ItemStack)byproduct.get()).m_41620_(1));
            }

            this.m_6596_();
            if (success) {
               this.f_58857_.m_5594_(null, this.m_58899_(), SoundEvents.f_11772_, SoundSource.MASTER, 1.0F, 1.0F);
               this.f_58857_.markAndNotifyBlock(this.m_58899_(), this.f_58857_.m_46745_(this.m_58899_()), this.m_58900_(), this.m_58900_(), 1, 1);
            } else {
               this.f_58857_.m_5594_(null, this.m_58899_(), SoundEvents.f_11914_, SoundSource.MASTER, 1.0F, 1.0F);
            }
         }
      }
   }

   public boolean isValidInput(ItemStack itemStack) {
      return itemStack.m_150930_((Item)ItemRegistry.SCROLL.get())
         || this.isBrewable(itemStack)
         || this.f_58857_ != null
            && this.f_58857_
               .m_7465_()
               .m_44013_((RecipeType)RecipeRegistry.ALCHEMIST_CAULDRON_BREW_TYPE.get())
               .stream()
               .anyMatch(holder -> holder.reagent().test(itemStack));
   }

   public boolean isBrewable(ItemStack itemStack) {
      return (Boolean)ServerConfigs.ALLOW_CAULDRON_BREWING.get() && this.f_58857_ != null && FluidHelper.isBrewingIngredient(itemStack, this.f_58857_);
   }

   public static InkItem getInkFromScroll(ItemStack scrollStack) {
      ISpellContainer spellContainer = ISpellContainer.get(scrollStack);
      SpellData spellData = spellContainer.getSpellAtIndex(0);
      SpellRarity rarity = spellData.getSpell().getRarity(spellData.getLevel());
      return InkItem.getInkForRarity(rarity);
   }

   public void m_6596_() {
      super.m_6596_();
      if (this.f_58857_ != null) {
         this.f_58857_.m_7260_(this.f_58858_, this.m_58900_(), this.m_58900_(), 2);
      }
   }

   public boolean m_6542_(Player pPlayer) {
      return false;
   }

   public void m_142466_(CompoundTag tag) {
      Utils.loadAllItems(tag, this.inputItems, "Items");
      this.fluidInventory.load("Results", tag, this.f_58857_ == null ? null : this.f_58857_.m_9598_());
      super.m_142466_(tag);
   }

   protected void m_183515_(@Nonnull CompoundTag tag) {
      Utils.saveAllItems(tag, this.inputItems, "Items");
      this.fluidInventory.save("Results", tag, this.f_58857_ == null ? null : this.f_58857_.m_9598_());
      super.m_183515_(tag);
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.m_195640_(this);
   }

   public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
      this.handleUpdateTag(pkt.m_131708_());
      if (this.f_58857_ != null) {
         this.f_58857_.m_7260_(this.f_58858_, this.m_58900_(), this.m_58900_(), 3);
      }
   }

   public CompoundTag m_5995_() {
      CompoundTag tag = new CompoundTag();
      this.m_183515_(tag);
      return tag;
   }

   public void handleUpdateTag(CompoundTag tag) {
      this.inputItems.clear();
      this.fluidInventory.clear();
      if (tag != null) {
         this.m_142466_(tag);
      }
   }

   public void drops() {
      SimpleContainer simpleContainer = new SimpleContainer(this.inputItems.size());

      for (int i = 0; i < this.inputItems.size(); i++) {
         simpleContainer.m_6836_(i, (ItemStack)this.inputItems.get(i));
      }

      if (this.f_58857_ != null) {
         Containers.m_19002_(this.f_58857_, this.f_58858_, simpleContainer);
      }
   }

   public int[] m_7071_(Direction pSide) {
      return new int[]{0, 1, 2, 3};
   }

   public boolean m_7155_(int pIndex, ItemStack pItemStack, @Nullable Direction pDirection) {
      return pDirection != Direction.DOWN && this.isValidInput(pItemStack) && this.m_8020_(pIndex).m_41619_();
   }

   public boolean m_7157_(int pIndex, ItemStack pStack, Direction pDirection) {
      return pDirection == Direction.DOWN;
   }

   public void m_6211_() {
      this.inputItems.clear();
      this.fluidInventory.clear();
   }

   public int m_6643_() {
      return INPUT_SIZE;
   }

   public boolean m_7983_() {
      return this.inputItems.stream().allMatch(ItemStack::m_41619_);
   }

   public ItemStack m_8020_(int pSlot) {
      return pSlot >= 0 && pSlot <= this.inputItems.size() ? (ItemStack)this.inputItems.get(pSlot) : ItemStack.f_41583_;
   }

   public ItemStack m_7407_(int pSlot, int pAmount) {
      return ContainerHelper.m_18969_(this.inputItems, pSlot, pAmount);
   }

   public ItemStack m_8016_(int pSlot) {
      return pSlot >= 0 && pSlot <= this.inputItems.size() ? (ItemStack)this.inputItems.remove(pSlot) : ItemStack.f_41583_;
   }

   public void m_6836_(int pSlot, ItemStack pStack) {
      if (pSlot >= 0 && pSlot <= this.inputItems.size()) {
         this.inputItems.set(pSlot, pStack);
      }
   }

   public boolean isBoiling(BlockState blockState) {
      return this.getFluidAmount() >= 1;
   }

   public int getFluidAmount() {
      return this.fluidInventory.fluidAmount();
   }

   public class AlchemistCauldronFluidHandler implements IFluidHandler {
      IFluidTank[] tanks = new IFluidTank[]{
         new AlchemistCauldronTile.AlchemistCauldronFluidHandler.CallbackFluidTank(1000),
         new AlchemistCauldronTile.AlchemistCauldronFluidHandler.CallbackFluidTank(1000),
         new AlchemistCauldronTile.AlchemistCauldronFluidHandler.CallbackFluidTank(1000),
         new AlchemistCauldronTile.AlchemistCauldronFluidHandler.CallbackFluidTank(1000)
      };

      public int getTanks() {
         return this.tanks.length;
      }

      public FluidStack getFluidInTank(int tank) {
         return tank >= 0 && tank <= this.tanks.length
            ? (this.tanks[tank].getFluidAmount() == 0 ? FluidStack.EMPTY : this.tanks[tank].getFluid())
            : FluidStack.EMPTY;
      }

      public int getTankCapacity(int tank) {
         return 1000;
      }

      public int fluidAmount() {
         return this.fluids().stream().mapToInt(FluidStack::getAmount).sum();
      }

      public boolean canFit(int fluidAmount) {
         return fluidAmount + this.fluidAmount() <= 1000;
      }

      public boolean isFluidValid(int tank, FluidStack stack) {
         return tank >= 0 && tank <= this.tanks.length && this.tanks[tank].isFluidValid(stack);
      }

      public boolean isTankCompatible(IFluidTank tank, FluidStack stack) {
         return tank.isFluidValid(stack) && FluidHelper.isSameFluidSameComponents(tank.getFluid(), stack);
      }

      public void onContentsChanged() {
         AlchemistCauldronTile.this.m_6596_();
      }

      public int fill(FluidStack resource, FluidAction action) {
         if (resource.getFluid().m_205067_(ModTags.CAULDRON_FLUID_DISALLOW)) {
            return 0;
         }

         int resourceLocation = -1;
         int emptyLocation = -1;
         int remainingCapacity = 1000 - this.fluidAmount();
         if (remainingCapacity == 0) {
            return 0;
         }

         for (int i = 0; i < this.tanks.length; i++) {
            if (this.isTankCompatible(this.tanks[i], resource)) {
               resourceLocation = i;
               break;
            }

            if (emptyLocation == -1 && this.tanks[i].getFluid().isEmpty()) {
               emptyLocation = i;
            }
         }

         FluidStack copy = FluidHelper.copyWithAmount(resource, Math.min(remainingCapacity, resource.getAmount()));
         if (resourceLocation >= 0) {
            return this.tanks[resourceLocation].fill(copy, action);
         } else {
            return emptyLocation >= 0 ? this.tanks[emptyLocation].fill(copy, action) : 0;
         }
      }

      public FluidStack drain(FluidStack resource, FluidAction action) {
         for (int i = 0; i < this.tanks.length; i++) {
            IFluidTank tank = this.tanks[i];
            if (this.isTankCompatible(tank, resource)) {
               FluidStack result = tank.drain(resource, action);

               for (int j = i; j < this.tanks.length - 1; j++) {
                  for (int k = j + 1; k < this.tanks.length && this.tanks[j].getFluid().isEmpty() && !this.tanks[k].getFluid().isEmpty(); k++) {
                     IFluidTank tmp = this.tanks[j];
                     this.tanks[j] = this.tanks[k];
                     this.tanks[k] = tmp;
                  }
               }

               return result;
            }
         }

         return FluidStack.EMPTY;
      }

      public FluidStack drain(int maxDrain, FluidAction action) {
         for (int i = this.tanks.length - 1; i >= 0; i--) {
            IFluidTank tank = this.tanks[i];
            if (!tank.getFluid().isEmpty()) {
               return tank.drain(maxDrain, action);
            }
         }

         return FluidStack.EMPTY;
      }

      public boolean contains(FluidStack stack, int minAmount) {
         for (IFluidTank tank : this.tanks) {
            if (this.isTankCompatible(tank, stack)) {
               return tank.getFluidAmount() >= minAmount;
            }
         }

         return false;
      }

      public boolean contains(Holder<Fluid> fluid, int minAmount) {
         for (IFluidTank tank : this.tanks) {
            if (tank.getFluid().getFluid().equals(fluid.m_203334_())) {
               return tank.getFluidAmount() >= minAmount;
            }
         }

         return false;
      }

      public boolean contains(TagKey<Fluid> fluid, int minAmount) {
         for (IFluidTank tank : this.tanks) {
            if (tank.getFluid().getFluid().m_205067_(fluid)) {
               return tank.getFluidAmount() >= minAmount;
            }
         }

         return false;
      }

      public List<FluidStack> fluids() {
         return Arrays.stream(this.tanks).<FluidStack>map(IFluidTank::getFluid).filter(f -> !f.isEmpty()).toList();
      }

      public void clear() {
         for (IFluidTank tank : this.tanks) {
            tank.drain(tank.getCapacity(), FluidAction.EXECUTE);
         }
      }

      public void save(String name, CompoundTag tag, Provider access) {
         ListTag fluids = new ListTag();

         for (IFluidTank tank : this.tanks) {
            if (!tank.getFluid().isEmpty()) {
               fluids.add(tank.getFluid().writeToNBT(new CompoundTag()));
            }
         }

         tag.m_128365_(name, fluids);
      }

      public void load(String name, CompoundTag tag, Provider access) {
         if (tag.m_128425_(name, 9)) {
            ListTag fluids = tag.m_128437_(name, 10);
            int i = 0;

            try {
               for (Tag l : fluids) {
                  FluidStack stack = FluidStack.loadFluidStackFromNBT((CompoundTag)l);
                  this.tanks[i++].fill(stack, FluidAction.EXECUTE);
               }
            } catch (Exception e) {
               IronsSpellbooks.LOGGER.error("Alchemist Cauldron Handler Failed to load fluid, skipping: {}", e.getMessage());
            }
         }
      }

      public class CallbackFluidTank extends FluidTank {
         public CallbackFluidTank(int capacity) {
            super(capacity);
         }

         protected void onContentsChanged() {
            super.onContentsChanged();
            AlchemistCauldronFluidHandler.this.onContentsChanged();
         }
      }
   }
}
