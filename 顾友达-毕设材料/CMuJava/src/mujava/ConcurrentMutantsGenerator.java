/*
 * @author: GuYouda
 * @date: 2018/03/22
 * @time: 01:29
 * @des: 并发变异算子生成，调用其他变异算子规则生成变异体
 */

package mujava;

import mujava.op.concurrence.util.TimeUtil;
import mujava.op.util.CodeChangeLog;
import mujava.op.util.Mutator;
import mujava.util.Debug;
import openjava.mop.Environment;
import openjava.ptree.ClassDeclaration;
import openjava.ptree.ClassDeclarationList;
import openjava.ptree.CompilationUnit;
import openjava.ptree.ParseTreeException;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ConcurrentMutantsGenerator extends MutantsGenerator {

    // 并发变异算子数组
    private String[] concurrentOp;

    public ConcurrentMutantsGenerator(File f) {
        super(f);
        concurrentOp = MutationSystem.conm_operators;
    }

    public ConcurrentMutantsGenerator(File f, boolean debug_flag) {
        super(f, debug_flag);
        concurrentOp = MutationSystem.conm_operators;
    }

    public ConcurrentMutantsGenerator(File f, String[] operator_list) {
        super(f, operator_list);
        concurrentOp = operator_list;
    }

    public ConcurrentMutantsGenerator(File f, String[] operator_list, boolean debug_flag) {
        super(f, operator_list, debug_flag);
        concurrentOp = operator_list;
    }

    /**
     * 生成变异体
     */
    @Override
    void genMutants() {
        if (comp_unit == null) {
            System.err.println(original_file + " is skipped.");
        }
        ClassDeclarationList cdecls = comp_unit.getClassDeclarations();

        if (cdecls == null || cdecls.size() == 0) {
            return;
        }

        if (concurrentOp != null && concurrentOp.length > 0) {
            Debug.println("* Generating class mutants");
            MutationSystem.clearPreviousConcurrentMutants();
            MutationSystem.MUTANT_PATH = MutationSystem.CONCURRENT_MUTANT_PATH;
            CodeChangeLog.openLogFile();
            genConcurrentMutants(cdecls);
            CodeChangeLog.closeLogFile();
        }
    }

    /**
     * 生成变异体
     *
     * @param cdecls ClassDeclarationList
     */
    private void genConcurrentMutants(ClassDeclarationList cdecls) {
        for (int j = 0; j < cdecls.size(); ++j) {
            ClassDeclaration cdecl = cdecls.get(j);

            if (cdecl.getName().equals(MutationSystem.CLASS_NAME)) {
                String qname = file_env.toQualifiedName(cdecl.getName());
                try {
                    mujava.op.util.Mutator mutant_op;

                    for (String mutator : concurrentOp) {
                        try {
                            System.out.println("========start: " + mutator + "=============" + TimeUtil.getCurrentTime());
                            String MUTATOR_OP_PATH = "mujava.op.concurrence."; //并发变异算子所在路径
                            Debug.println("Analysis " + MUTATOR_OP_PATH + mutator);

                            /*
                            MuJava原设计会导致大量冗余代码
                            针对并发变异体生成利用反射机制进行并发变异类的初始化,
                            避免大量冗余代码
                            详细冗余情况可参考：
                            mujava.TraditionalMutantsGenerator.java
                            mujava.ClassMutantsGenerator.java
                            */
                            Class clazz = Class.forName(MUTATOR_OP_PATH + mutator);
                            //获取有参构造函数
                            Constructor mutantor = clazz.getConstructor(Environment.class,
                                    ClassDeclaration.class,
                                    CompilationUnit.class);
                            mutant_op = (Mutator) mutantor.newInstance(file_env, cdecl, comp_unit);
                            comp_unit.accept(mutant_op);
                            System.out.println("========stop: " + mutator + "=============" + TimeUtil.getCurrentTime());
                        } catch (ClassNotFoundException | InstantiationException
                                | IllegalAccessException | NoSuchMethodException
                                | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (ParseTreeException e) {
                    System.err.println("Encountered errors during generating mutants.");
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 编译变异体
     */
    @Override
    public void compileMutants() {
        if (concurrentOp != null && concurrentOp.length > 0) {
            Debug.println("* Compiling class mutants into bytecode");
            MutationSystem.MUTANT_PATH = MutationSystem.CONCURRENT_MUTANT_PATH;
            super.compileMutants();
        }
    }
}
