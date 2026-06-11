import java.lang.reflect.Method;
public class print_methods {
    public static void main(String[] args) throws Exception {
        Class<?> clazz = Class.forName("net.minecraft.world.gen.chunk.ChunkGenerator");
        for (Method m : clazz.getDeclaredMethods()) {
            System.out.println(m.getName() + " " + m.getParameterCount());
            for (Class<?> p : m.getParameterTypes()) {
                System.out.println("  - " + p.getName());
            }
        }
    }
}
