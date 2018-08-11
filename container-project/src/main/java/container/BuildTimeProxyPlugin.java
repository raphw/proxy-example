package container;

import net.bytebuddy.build.Plugin;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;

import java.io.File;
import java.io.IOException;

public class BuildTimeProxyPlugin implements Plugin {

    public DynamicType.Builder<?> apply(DynamicType.Builder<?> builder, TypeDescription typeDescription) {
        DynamicType proxy = ProxyFactory.make(typeDescription);
        try {
            // TODO: Needs better mechanism in Plugin API or type builder API as attachment types are already supported.
            proxy.saveIn(new File("user-project/target/classes"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return builder.annotateType(AnnotationDescription.Builder.ofType(BuildTimeProxy.class)
                .define("type", proxy.getTypeDescription())
                .build());
    }

    public boolean matches(TypeDescription target) {
        return target.getDeclaredAnnotations().isAnnotationPresent(Bean.class);
    }
}
