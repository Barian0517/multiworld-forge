package me.isaiah.multiworld;

import net.minecraft.world.level.ServerWorldProperties;
import java.lang.reflect.Method;

public class TestProps {
    public static void test() {
        for (Method m : ServerWorldProperties.class.getMethods()) {
            System.out.println(m.getReturnType().getName() + " " + m.getName());
        }
    }
}
