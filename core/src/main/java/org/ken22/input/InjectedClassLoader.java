package org.ken22.input;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class InjectedClassLoader {

    private final InjectedClass injectedClass;
    // what we're doing is very dangerous and insecure, but it can really help with performance
    public InjectedClassLoader(String functionDeclaration) {
        try {
            this.injectedClass = loadInjectedClass(functionDeclaration);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public InjectedClass getInjectedClass() {
        return injectedClass;
    }

    private InjectedClass loadInjectedClass(String functionDeclaration) throws IOException,
        ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

        String exampleFunctionDeclaration = "Math.pow(x, y)";
        // DO NOT CHANGE THIS
        String source = "package injected;\n" +
            "import org.ken22.input.InjectedClass;\n" +
            "\n" +
            "public class ExpressionClass implements InjectedClass {   \n" +
            "    public double evaluate(double x, double y) {\n" +
            "        return " + functionDeclaration + ";\n" +
            "    }\n" +
            "}\n";

        Path sourceRootPath = Paths.get(System.getProperty("user.dir"));
        sourceRootPath = sourceRootPath.getParent();
        sourceRootPath = sourceRootPath.resolve("core/src/main/java/org/ken22/input/injected");

        System.out.println("sourceRootPath = " + sourceRootPath);
        File root = Files.createTempDirectory(sourceRootPath, null).toFile();
        File sourceFile = new File(root, "injected/ExpressionClass.java");
        sourceFile.getParentFile().mkdirs();
        Files.write(sourceFile.toPath(), source.getBytes(StandardCharsets.UTF_8));
        System.out.println("File has been written to: " + sourceFile.getAbsolutePath());

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        compiler.run(null, null, null, sourceFile.getPath());
        System.out.println("compiled class");


        URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] { root.toURI().toURL() });
        URL[] urls = classLoader.getURLs();

        for(URL url : urls) {
            System.out.println(url.getFile());
        }
        Class<?> cls = Class.forName("injected.ExpressionClass", true, classLoader);
        Object instance = cls.getDeclaredConstructor().newInstance();
        if (instance instanceof InjectedClass) {
            System.out.println("Applying operation: ");
            System.out.println("Result= " + ((InjectedClass) instance).evaluate(2, 3));
            System.out.println("ITS AN INSTANCE OF INJECTED CLASS");
            return (InjectedClass) instance;
        } else {
            throw new RuntimeException("Something is very wrong");
        }
    }

}
