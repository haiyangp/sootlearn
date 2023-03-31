package com.phy;

import soot.Scene;

import soot.SootClass;
import soot.SootMethod;
import soot.PackManager;
import soot.Transform;
import soot.options.Options;

import java.util.*;

import com.phy.transform.transform1;


// 基于soot的逃逸分析，主要内容为IFDSescape.java中对逃逸分析问题的定义

// 新增更改

public class App {

       public static void main(String[] args) {
              
              String classPath = "D:\\work\\PA\\dataflow\\demo\\target\\classes";
              String mainClass = "com.test.App";
              initialSoot(classPath, mainClass);


              PackManager.v().getPack("wjtp").add(new Transform("wjtp.herosifds", new transform1()));
              PackManager.v().runPacks();
                            
       }

       public static void initialSoot(String classPath, String mainClass) {

              // 设置字节码路径和主类
              Scene.v().extendSootClassPath(mainClass);
              Scene.v().setSootClassPath(classPath);
              Options.v().set_process_dir(Arrays.asList(classPath));

              //排除java内部类
              List < String > excludeList = new ArrayList < > ();
              excludeList.add("java.");
              excludeList.add("javax.");
              excludeList.add("sun.");
              Options.v().set_exclude(excludeList);

              Options.v().set_no_bodies_for_excluded(true);
              // Enable whole-program mode
              Options.v().set_whole_program(true);
              //Options.v().set_app(true);
              Options.v().set_allow_phantom_refs(true);

              // Call-graph options
              Options.v().setPhaseOption("cg", "safe-newinstance:true");
              Options.v().setPhaseOption("cg.cha", "enabled:false");

              // Enable SPARK call-graph construction
              Options.v().setPhaseOption("cg.spark", "enabled:true");
              Options.v().setPhaseOption("cg.spark", "verbose:true");
              Options.v().setPhaseOption("cg.spark", "on-fly-cg:true");

              //尝试关闭空指针的优化
              Options.v().setPhaseOption("jop.cpf", "enabled:false");

              // 保留源代码行号
              Options.v().setPhaseOption("jb", "use-original-names:true");
              Options.v().set_keep_line_number(true);

              Scene.v().extendSootClassPath("C:\\Program Files\\Java\\jdk1.8.0_172\\jre\\lib\\rt.jar");
              Scene.v().extendSootClassPath("C:\\Program Files\\Java\\jdk1.8.0_172\\jre\\lib\\jce.jar");
              Scene.v().loadNecessaryClasses();

              Options.v().set_main_class(mainClass);
              SootClass c = Scene.v().loadClassAndSupport(mainClass);

              c.setApplicationClass();

              //设置分析入口
              List < SootMethod > entryPoints = new ArrayList < SootMethod > ();
              SootMethod entryPoint = c.getMethodByName("main");
              entryPoints.add(entryPoint);
              Scene.v().setEntryPoints(entryPoints);
       }

}