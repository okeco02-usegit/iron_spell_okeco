package io.redspace.ironsspellbooks.entity.spells.summoned_weapons;

import io.redspace.ironsspellbooks.entity.mobs.goals.melee.AttackAnimationData;
import io.redspace.ironsspellbooks.entity.mobs.wizards.GenericAnimatedWarlockAttackGoal;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import java.util.List;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;

public class SummonedSwordEntity extends SummonedWeaponEntity {
   public static Builder prepareAttributes() {
      return LivingEntity.m_21183_()
         .m_22268_(Attributes.f_22282_, 1.0)
         .m_22268_(Attributes.f_22281_, 5.0)
         .m_22268_(Attributes.f_22276_, 25.0)
         .m_22268_(Attributes.f_22277_, 40.0)
         .m_22268_(Attributes.f_22280_, 1.5)
         .m_22268_((Attribute)ForgeMod.ENTITY_REACH.get(), 4.0)
         .m_22268_(Attributes.f_22279_, 0.5);
   }

   public SummonedSwordEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   public SummonedSwordEntity(Level level, LivingEntity owner) {
      this((EntityType<? extends PathfinderMob>)EntityRegistry.SUMMONED_SWORD.get(), level);
      this.setSummoner(owner);
   }

   @Override
   public GenericAnimatedWarlockAttackGoal<SummonedSwordEntity> makeAttackGoal() {
      return new GenericAnimatedWarlockAttackGoal<>(this, 1.5, 0, 20)
         .setMoveset(
            List.of(new AttackAnimationData(36, "summoned_sword_basic_swing", 20), new AttackAnimationData(52, "summoned_sword_basic_dual_swing", 20, 35))
         );
   }
}
