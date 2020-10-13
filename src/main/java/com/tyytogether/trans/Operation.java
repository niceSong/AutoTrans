package com.tyytogether.trans;

import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.util.CheckClassAdapter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

@FunctionalInterface
public interface Operation {
    Object apply();
}

class MyClassAdapter2 extends CheckClassAdapter {

    public MyClassAdapter2(ClassVisitor cv) {
        super(cv);
    }
    @Override
    public void visitEnd() {
        cv.visitField(Opcodes.ACC_PUBLIC, "age", Type.getDescriptor(int.class), null, null);
    }
}