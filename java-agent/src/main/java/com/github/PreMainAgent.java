package com.github;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

/**
 * create by ljhrot
 * create at 2021/12/21 23:24
 */
public class PreMainAgent {

    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("In premain static function");
        System.out.println("Input arguments['agentArgs']: " + agentArgs);
        System.out.println("Input arguments['inst']: " + inst);
        inst.addTransformer(new SimpleTransformer(), true);
    }

    private static class SimpleTransformer implements ClassFileTransformer {

        @Override
        public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
            System.out.println("In SimpleTransformer class, transform function");
            System.out.println("Input arguments['loader']: " + loader);
            System.out.println("Input arguments['className']: " + className);
            System.out.println("Input arguments['classBeingRedefined']: " + classBeingRedefined);
            System.out.println("Input arguments['protectionDomain']: " + protectionDomain);
            return classfileBuffer;
        }
    }
}
