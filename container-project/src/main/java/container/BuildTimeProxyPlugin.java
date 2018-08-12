package container;

import net.bytebuddy.build.Plugin;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.Duplication;
import net.bytebuddy.implementation.bytecode.TypeCreation;
import net.bytebuddy.implementation.bytecode.member.MethodInvocation;
import net.bytebuddy.implementation.bytecode.member.MethodReturn;

import java.io.File;
import java.io.IOException;

import static net.bytebuddy.matcher.ElementMatchers.isConstructor;

public class BuildTimeProxyPlugin implements Plugin {

    public DynamicType.Builder<?> apply(DynamicType.Builder<?> builder, TypeDescription typeDescription) {
        DynamicType proxy = ProxyFactory.make(typeDescription);
        try {
            // TODO: Needs better mechanism in Plugin API or type builder API as attachment types are already supported.
            proxy.saveIn(new File("user-project/target/classes"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return builder.implement(BuildTimeProxy.class).intercept(new Implementation.Simple(
                TypeCreation.of(proxy.getTypeDescription()),
                Duplication.SINGLE,
                MethodInvocation.invoke(proxy.getTypeDescription()
                        .getDeclaredMethods()
                        .filter(isConstructor())
                        .getOnly()),
                MethodReturn.REFERENCE
        ));
    }

    public boolean matches(TypeDescription target) {
        return target.getDeclaredAnnotations().isAnnotationPresent(Bean.class);
    }
}
