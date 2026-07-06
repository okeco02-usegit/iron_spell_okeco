package io.redspace.ironsspellbooks.api.backwards_compat;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import java.util.function.Function;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

public class CodecHelper {
   public static <T> Codec<T> withAlternative(Codec<T> primary, Codec<? extends T> alternative) {
      return Codec.either(primary, alternative).xmap(either -> either.map(Function.identity(), Function.identity()), Either::left);
   }

   public static <T> T get(Codec<T> codec, Tag rawDataTag) {
      return (T)((Pair)codec.decode(NbtOps.f_128958_, rawDataTag).getOrThrow(false, IronsSpellbooks.LOGGER::error)).getFirst();
   }

   public static <T> T getOrElse(Codec<T> codec, Tag rawDataTag, T backup) {
      return (T)codec.decode(NbtOps.f_128958_, rawDataTag).get().map(Pair::getFirst, pair -> backup);
   }

   public static <T> T get(Codec<T> codec, ItemStack stack, String nbt) {
      return get(codec, stack.m_41784_().m_128423_(nbt));
   }

   public static <T> T getOrElse(ItemStack stack, String nbt, Codec<T> codec, T empty) {
      return stack.m_41782_() && stack.m_41784_().m_128441_(nbt) ? get(codec, stack.m_41784_().m_128423_(nbt)) : empty;
   }

   public static <T> T getWithLegacy(Codec<T> codec, ItemStack stack, String nbt, String legacyNbt, Codec<T> legacyCodec) {
      CompoundTag tag = stack.m_41784_();
      if (tag.m_128441_(nbt)) {
         return get(codec, tag.m_128423_(nbt));
      }

      T data = get(legacyCodec, tag.m_128423_(legacyNbt));
      tag.m_128473_(legacyNbt);
      tag.m_128365_(nbt, (Tag)codec.encode(data, NbtOps.f_128958_, NbtOps.f_128958_.empty()).getOrThrow(false, IronsSpellbooks.LOGGER::error));
      return data;
   }

   public static <T> T getOrElseWithLegacy(ItemStack stack, String nbt, Codec<T> codec, T empty, String legacyNbt, Codec<T> legacyCodec) {
      return hasWithLegacy(stack, nbt, legacyNbt) ? getWithLegacy(codec, stack, nbt, legacyNbt, legacyCodec) : empty;
   }

   public static boolean hasWithLegacy(ItemStack stack, String nbt, String legacyNbt) {
      return stack.m_41782_() && (stack.m_41784_().m_128441_(nbt) || stack.m_41784_().m_128441_(legacyNbt));
   }

   public static <T> void set(ItemStack stack, String nbt, Codec<T> codec, T data) {
      stack.m_41784_().m_128365_(nbt, (Tag)codec.encode(data, NbtOps.f_128958_, NbtOps.f_128958_.empty()).getOrThrow(false, IronsSpellbooks.LOGGER::error));
   }

   public static boolean has(ItemStack stack, String nbt) {
      return stack.m_41782_() && stack.m_41784_().m_128441_(nbt);
   }

   public static <E> Codec<E> createLegacyCodec(final Function<Tag, E> decoder) {
      return Codec.of(Encoder.error("Legacy codec should never write!"), new Decoder<E>() {
         public <T> DataResult<Pair<E, T>> decode(DynamicOps<T> ops, T input) {
            try {
               E data = decoder.apply((Tag)input);
               return DataResult.success(Pair.of(data, input));
            } catch (Exception e) {
               return DataResult.error(e::getMessage);
            }
         }
      });
   }
}
