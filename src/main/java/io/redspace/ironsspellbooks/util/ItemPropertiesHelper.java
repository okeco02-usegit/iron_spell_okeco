package io.redspace.ironsspellbooks.util;

import net.minecraft.world.item.Item.Properties;

public class ItemPropertiesHelper {
   public static Properties equipment() {
      return new Properties();
   }

   public static Properties equipment(int stackSize) {
      return equipment().m_41487_(stackSize);
   }

   public static Properties material() {
      return new Properties();
   }

   public static Properties material(int stackSize) {
      return material().m_41487_(stackSize);
   }

   public static Properties hidden() {
      return new Properties();
   }

   public static Properties hidden(int stackSize) {
      return hidden().m_41487_(stackSize);
   }
}
