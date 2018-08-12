package container;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;

import static net.bytebuddy.matcher.ElementMatchers.isDeclaredBy;

public class ProxyFactory {

    @SuppressWarnings("unchecked")
    public <T> T proxy(T instance) {
        if (!instance.getClass().isAnnotationPresent(Bean.class)) {
            throw new IllegalArgumentException();
        }
        if (instance instanceof BuildTimeProxy) {
            System.out.println("Use build-time proxy");
            return (T) ((BuildTimeProxy) instance).proxy();
        } else {
            try {
                System.out.println("Generate runtime proxy");
                return (T) make(TypeDescription.ForLoadedType.of(instance.getClass()))
                        .load(instance.getClass().getClassLoader())
                        .getLoaded()
                        .getConstructor()
                        .newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    static DynamicType.Unloaded<?> make(TypeDescription type) {
        return new ByteBuddy()
                .subclass(type)
                .method(isDeclaredBy(type))
                .intercept(MethodDelegation.to(ProxyFactory.class))
                .make();
    }

    public static void intercept() {
        System.out.println("Intercepted");
    }
}
