package io.redspace.ironsspellbooks.util;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.Tuple;

public class CodeTimer {
   private final List<Tuple<String, Long>> timing = new ArrayList<>();

   public CodeTimer() {
      this.add("START");
   }

   public void add(String name) {
      this.timing.add(new Tuple(name, System.nanoTime()));
   }

   public String getOutput(String delimiter) {
      StringBuilder sb = new StringBuilder();
      long itemDelta = 0L;
      long totalDelta = 0L;

      for (int i = 0; i < this.timing.size(); i++) {
         Tuple<String, Long> item = this.timing.get(i);
         if (i > 0) {
            Tuple<String, Long> lastItem = this.timing.get(i - 1);
            itemDelta = (Long)item.m_14419_() - (Long)lastItem.m_14419_();
            totalDelta += itemDelta;
            sb.append(
               String.format(
                  "%s%s%s%s%f%s%f\n", lastItem.m_14418_(), delimiter, item.m_14418_(), delimiter, itemDelta / 1000000.0, delimiter, totalDelta / 1000000.0
               )
            );
         }
      }

      return sb.toString();
   }

   @Override
   public String toString() {
      return this.getOutput("\t");
   }
}
