package dedux.internal;

import org.junit.Test;

import static org.junit.Assert.*;

public class ReflectUtilsTest {


    @SuppressWarnings("WeakerAccess")
    public static class PublicNoConstructor {}

    private static class PrivateNoConstructor {}

    @SuppressWarnings("WeakerAccess")
    static class NoConstructor {}


    @Test
    public void inner_public_no_constructor_instance_created() {
        assertNotNull(ReflectUtils.newInstance(PublicNoConstructor.class));
    }

    @Test
    public void inner_private_no_constructor_fails() {
        try {
            ReflectUtils.newInstance(PrivateNoConstructor.class);
            assertTrue(false);
        } catch (Throwable t) {
            assertTrue(true);
        }
    }

    @Test
    public void inner_no_constructor_fails() {
        try {
            ReflectUtils.newInstance(NoConstructor.class);
            assertTrue(false);
        } catch (Throwable t) {
            assertTrue(true);
        }
    }

    @Test
    public void public_no_constructor() {
        assertNotNull(ReflectUtils.newInstance(ReflectUtilsPublic.class));
    }

    public static class MultipleConstructorsHasEmpty {
        public MultipleConstructorsHasEmpty() {}
        public MultipleConstructorsHasEmpty(byte b) {}
    }

    @Test
    public void multiple_constructors_empty_present() {
        assertNotNull(ReflectUtils.newInstance(MultipleConstructorsHasEmpty.class));
    }

    public static class MultipleConstructorNoEmpty {
        public MultipleConstructorNoEmpty(byte b) {}
        public MultipleConstructorNoEmpty(char c) {}
    }

    @Test
    public void multiple_constructors_no_empty_present() {
        try {
            ReflectUtils.newInstance(MultipleConstructorNoEmpty.class);
            assertTrue(false);
        } catch (Throwable t) {
            assertTrue(true);
        }
    }
}