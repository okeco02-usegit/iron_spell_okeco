package io.redspace.ironsspellbooks.api.util;

public interface IMusicHandler {
   void init();

   void stop();

   void tick();

   boolean isDone();

   void hardStop();

   void triggerResume();
}
