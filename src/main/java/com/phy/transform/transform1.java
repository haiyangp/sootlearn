package com.phy.transform;

import heros.IFDSTabulationProblem;
import heros.InterproceduralCFG;
import heros.solver.IFDSSolver;
import soot.Body;
import soot.Scene;
import soot.SceneTransformer;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import java.util.*;
import soot.jimple.DefinitionStmt;
import soot.jimple.Stmt;

import com.phy.probelm.*;
import soot.jimple.toolkits.ide.icfg.JimpleBasedInterproceduralCFG;


import soot.toolkits.scalar.Pair;
import soot.util.Chain;


public class transform1 extends SceneTransformer {

    @Override
    protected void internalTransform(String arg0, Map < String, String > arg1) {
        JimpleBasedInterproceduralCFG icfg = new JimpleBasedInterproceduralCFG();

        IFDSTabulationProblem < Unit, Pair<Value, Stmt>, SootMethod, InterproceduralCFG < Unit, SootMethod >> problem = new IFDSescape(icfg);
        IFDSSolver < Unit, Pair<Value, Stmt>,
            SootMethod, InterproceduralCFG < Unit, SootMethod >> solver =
            new IFDSSolver < Unit, Pair<Value, Stmt>, SootMethod,
            InterproceduralCFG < Unit, SootMethod >> (problem);

        System.out.println("Starting solver");
        solver.solve(); //在控制流图上做数据分析

        System.out.println("Done");
        System.out.println("get result");

        Chain < SootClass > scs = Scene.v().getClasses();

        for (SootClass sc: scs) {
            if (sc.getName() != "com.test.App")
                continue;
            for (SootMethod sm: sc.getMethods()) {
                if(!sm.getName().equals("main")&&!sm.getName().equals("foo")){
                    continue;
                }
                Body b=sm.retrieveActiveBody();
                for(Unit u:b.getUnits()){
                    if(u.toString().equals("return"))
                        System.out.print(solver.resultsAt(u));
                }
            }
        }
    }

}