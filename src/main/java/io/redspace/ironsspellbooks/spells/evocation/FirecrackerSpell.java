package io.redspace.ironsspellbooks.spells.evocation;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.entity.spells.ExtendedFireworkRocket;
import java.util.List;
import java.util.Random;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class FirecrackerSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "firecracker");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.COMMON)
      .setSchoolResource(SchoolRegistry.EVOCATION_RESOURCE)
      .setMaxLevel(10)
      .setCooldownSeconds(1.5)
      .build();
   private static final int[] DYE_COLORS = new int[]{
      11546150, 6192150, 3949738, 8991416, 1481884, 15961002, 8439583, 16701501, 3847130, 13061821, 16351261, 16383998
   };

   @Override
   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(Component.m_237110_("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getSpellPower(spellLevel, caster), 1)}));
   }

   public FirecrackerSpell() {
      this.manaCostPerLevel = 2;
      this.baseSpellPower = 4;
      this.spellPowerPerLevel = 1;
      this.castTime = 0;
      this.baseManaCost = 20;
   }

   @Override
   public CastType getCastType() {
      return CastType.INSTANT;
   }

   @Override
   public DefaultConfig getDefaultConfig() {
      return this.defaultConfig;
   }

   @Override
   public ResourceLocation getSpellResource() {
      return this.spellId;
   }

   @Override
   public void onCast(Level world, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      Vec3 shootAngle = entity.m_20154_().m_82541_();
      Vec3 spawn = Utils.raycastForEntity(world, entity, this.getRange(spellLevel, entity), true).m_82450_().m_82546_(shootAngle.m_82490_(0.25));
      ExtendedFireworkRocket firework = new ExtendedFireworkRocket(
         world, this.randomFireworkRocket(), entity, spawn.f_82479_, spawn.f_82480_, spawn.f_82481_, true, this.getDamage(spellLevel, entity)
      );
      world.m_7967_(firework);
      firework.m_6686_(shootAngle.f_82479_, shootAngle.f_82480_, shootAngle.f_82481_, 0.0F, 0.0F);
      super.onCast(world, spellLevel, entity, castSource, playerMagicData);
   }

   private int getRange(int spellLevel, LivingEntity entity) {
      return 15 + (int)(this.getSpellPower(spellLevel, entity) * 2.0F);
   }

   private float getDamage(int spellLevel, LivingEntity entity) {
      return this.getSpellPower(spellLevel, entity);
   }

   private ItemStack randomFireworkRocket() {
      Random random = new Random();
      ItemStack rocket = new ItemStack(Items.f_42688_);
      CompoundTag properties = new CompoundTag();
      ListTag explosions = new ListTag();
      CompoundTag explosion = new CompoundTag();
      byte type = (byte)(random.nextInt(3) * 2);
      if (random.nextFloat() < 0.08F) {
         type = 3;
      }

      explosion.m_128344_("Type", type);
      if (random.nextInt(3) == 0) {
         explosion.m_128344_("Trail", (byte)1);
      }

      if (random.nextInt(3) == 0) {
         explosion.m_128344_("Flicker", (byte)1);
      }

      explosion.m_128385_("Colors", this.randomColors());
      explosions.add(explosion);
      properties.m_128365_("Explosions", explosions);
      properties.m_128344_("Flight", (byte)-1);
      rocket.m_41700_("Fireworks", properties);
      return rocket;
   }

   private int[] randomColors() {
      int[] colors = new int[3];
      Random random = new Random();

      for (int i = 0; i < colors.length; i++) {
         colors[i] = DYE_COLORS[random.nextInt(DYE_COLORS.length)];
      }

      return colors;
   }
}
