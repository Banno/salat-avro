package models;
import java.util.*;
import org.objectweb.asm.*;
//import org.objectweb.asm.attrs.*;
public class MyRecord$Dump implements Opcodes {

public static byte[] dump () throws Exception {

ClassWriter cw = new ClassWriter(0);
FieldVisitor fv;
MethodVisitor mv;
AnnotationVisitor av0;

cw.visit(V1_5, ACC_PUBLIC + ACC_FINAL + ACC_SUPER, "models/MyRecord$", null, "scala/runtime/AbstractFunction3", new String[] { "scala/ScalaObject", "scala/Serializable" });

// ATTRIBUTE Scala
{
fv = cw.visitField(ACC_PUBLIC + ACC_FINAL + ACC_STATIC, "MODULE$", "Lmodels/MyRecord$;", null, null);
fv.visitEnd();
}
{
mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "<clinit>", "()V", null, null);
mv.visitCode();
mv.visitTypeInsn(NEW, "models/MyRecord$");
mv.visitMethodInsn(INVOKESPECIAL, "models/MyRecord$", "<init>", "()V");
mv.visitInsn(RETURN);
mv.visitMaxs(1, 0);
mv.visitEnd();
}
{
mv = cw.visitMethod(ACC_PUBLIC + ACC_FINAL, "toString", "()Ljava/lang/String;", null, null);
mv.visitCode();
mv.visitLdcInsn("MyRecord");
mv.visitInsn(ARETURN);
mv.visitMaxs(1, 1);
mv.visitEnd();
}
{
mv = cw.visitMethod(ACC_PUBLIC, "unapply", "(Lmodels/MyRecord;)Lscala/Option;", null, null);
mv.visitCode();
mv.visitVarInsn(ALOAD, 1);
Label l0 = new Label();
mv.visitJumpInsn(IFNONNULL, l0);
mv.visitFieldInsn(GETSTATIC, "scala/None$", "MODULE$", "Lscala/None$;");
Label l1 = new Label();
mv.visitJumpInsn(GOTO, l1);
mv.visitLabel(l0);
mv.visitTypeInsn(NEW, "scala/Some");
mv.visitInsn(DUP);
mv.visitTypeInsn(NEW, "scala/Tuple3");
mv.visitInsn(DUP);
mv.visitVarInsn(ALOAD, 1);
mv.visitMethodInsn(INVOKEVIRTUAL, "models/MyRecord", "x", "()Ljava/lang/String;");
mv.visitVarInsn(ALOAD, 1);
mv.visitMethodInsn(INVOKEVIRTUAL, "models/MyRecord", "y", "()I");
mv.visitMethodInsn(INVOKESTATIC, "scala/runtime/BoxesRunTime", "boxToInteger", "(I)Ljava/lang/Integer;");
mv.visitVarInsn(ALOAD, 1);
mv.visitMethodInsn(INVOKEVIRTUAL, "models/MyRecord", "z", "()Z");
mv.visitMethodInsn(INVOKESTATIC, "scala/runtime/BoxesRunTime", "boxToBoolean", "(Z)Ljava/lang/Boolean;");
mv.visitMethodInsn(INVOKESPECIAL, "scala/Tuple3", "<init>", "(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V");
mv.visitMethodInsn(INVOKESPECIAL, "scala/Some", "<init>", "(Ljava/lang/Object;)V");
mv.visitLabel(l1);
mv.visitInsn(ARETURN);
mv.visitMaxs(7, 2);
mv.visitEnd();
}
{
mv = cw.visitMethod(ACC_PUBLIC, "apply", "(Ljava/lang/String;IZ)Lmodels/MyRecord;", null, null);
mv.visitCode();
mv.visitTypeInsn(NEW, "models/MyRecord");
mv.visitInsn(DUP);
mv.visitVarInsn(ALOAD, 1);
mv.visitVarInsn(ILOAD, 2);
mv.visitVarInsn(ILOAD, 3);
mv.visitMethodInsn(INVOKESPECIAL, "models/MyRecord", "<init>", "(Ljava/lang/String;IZ)V");
mv.visitInsn(ARETURN);
mv.visitMaxs(5, 4);
mv.visitEnd();
}
{
mv = cw.visitMethod(ACC_PUBLIC, "readResolve", "()Ljava/lang/Object;", null, null);
mv.visitCode();
mv.visitFieldInsn(GETSTATIC, "models/MyRecord$", "MODULE$", "Lmodels/MyRecord$;");
mv.visitInsn(ARETURN);
mv.visitMaxs(1, 1);
mv.visitEnd();
}
{
mv = cw.visitMethod(ACC_PUBLIC + ACC_BRIDGE, "apply", "(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", null, null);
mv.visitCode();
mv.visitVarInsn(ALOAD, 0);
mv.visitVarInsn(ALOAD, 1);
mv.visitTypeInsn(CHECKCAST, "java/lang/String");
mv.visitVarInsn(ALOAD, 2);
mv.visitMethodInsn(INVOKESTATIC, "scala/runtime/BoxesRunTime", "unboxToInt", "(Ljava/lang/Object;)I");
mv.visitVarInsn(ALOAD, 3);
mv.visitMethodInsn(INVOKESTATIC, "scala/runtime/BoxesRunTime", "unboxToBoolean", "(Ljava/lang/Object;)Z");
mv.visitMethodInsn(INVOKEVIRTUAL, "models/MyRecord$", "apply", "(Ljava/lang/String;IZ)Lmodels/MyRecord;");
mv.visitInsn(ARETURN);
mv.visitMaxs(4, 4);
mv.visitEnd();
}
{
//mv = cw.visitMethod(ACC_PRIVATE, "<init>", "()V", null, null);
mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
mv.visitCode();
mv.visitVarInsn(ALOAD, 0);
mv.visitMethodInsn(INVOKESPECIAL, "scala/runtime/AbstractFunction3", "<init>", "()V");
mv.visitVarInsn(ALOAD, 0);
mv.visitFieldInsn(PUTSTATIC, "models/MyRecord$", "MODULE$", "Lmodels/MyRecord$;");
mv.visitInsn(RETURN);
mv.visitMaxs(1, 1);
mv.visitEnd();
}
cw.visitEnd();

return cw.toByteArray();
}
}
