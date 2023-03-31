package com.phy.probelm;

import heros.DefaultSeeds;
import heros.FlowFunction;
import heros.FlowFunctions;
import heros.InterproceduralCFG;
import heros.flowfunc.Identity;
import heros.flowfunc.KillAll;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.transform.Source;

import org.apache.commons.lang3.StringUtils;

import soot.EquivalentValue;
import soot.Local;
import soot.NullType;
import soot.Scene;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.DefinitionStmt;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.Jimple;
import soot.jimple.ReturnStmt;
import soot.jimple.ReturnVoidStmt;
import soot.jimple.Stmt;
import soot.jimple.internal.JimpleLocal;
import soot.jimple.toolkits.ide.DefaultJimpleIFDSTabulationProblem;
import soot.toolkits.scalar.Pair;

public class IFDSescape
        extends DefaultJimpleIFDSTabulationProblem<Pair<Value, Stmt>, InterproceduralCFG<Unit, SootMethod>> {

    public IFDSescape(InterproceduralCFG<Unit, SootMethod> icfg) {
        super(icfg);

    }



    @Override
    protected FlowFunctions<Unit, Pair<Value, Stmt>, SootMethod> createFlowFunctionsFactory() {
        return new FlowFunctions<Unit, Pair<Value, Stmt>, SootMethod>() {

            @Override
            public FlowFunction<Pair<Value, Stmt>> getCallFlowFunction(Unit arg0, SootMethod arg1) {
                if (arg1.toString().equals("<java.lang.Object: void <clinit>()>"))
                    return Identity.v();
                // System.out.println(arg0+" "+arg1);
                final Stmt statement = (Stmt) arg0;
                return new FlowFunction<Pair<Value, Stmt>>() {
                    @Override
                    public Set<Pair<Value, Stmt>> computeTargets(Pair<Value, Stmt> arg0) {
                        LinkedHashSet<Pair<Value, Stmt>> res = new LinkedHashSet<Pair<Value, Stmt>>();
                        res.add(arg0);
                        for (Value v : statement.getInvokeExpr().getArgs()) {
                            if(!StringUtils.isNumeric(v.toString()))
                                res.add(new Pair<Value, Stmt>(v, statement));
                        }
                        return res;
                    }
                };
            }

            @Override
            public FlowFunction<Pair<Value, Stmt>> getCallToReturnFlowFunction(Unit arg0, Unit arg1) {
                if (arg0 instanceof DefinitionStmt) {
                    final DefinitionStmt assignment = (DefinitionStmt) arg0;
                    if (assignment.getLeftOp().toString().contains("<")) {
                        return new FlowFunction<Pair<Value, Stmt>>() {
                            @Override
                            public Set<Pair<Value, Stmt>> computeTargets(Pair<Value, Stmt> arg0) {
                                LinkedHashSet<Pair<Value, Stmt>> res = new LinkedHashSet<Pair<Value, Stmt>>();
                                res.add(arg0);
                                if(!StringUtils.isNumeric(assignment.getRightOp().toString()))
                                    res.add(new Pair<Value, Stmt>(assignment.getRightOp(), assignment));
                                return res;
                            }
                        };
                    }

                }
                return Identity.v();
            }

            @Override
            public FlowFunction<Pair<Value, Stmt>> getNormalFlowFunction(Unit arg0, Unit arg1) {
                if (arg0 instanceof DefinitionStmt) {
                    final DefinitionStmt assignment = (DefinitionStmt) arg0;
                    if (assignment.getLeftOp().toString().contains("<")) {
                        return new FlowFunction<Pair<Value, Stmt>>() {
                            @Override
                            public Set<Pair<Value, Stmt>> computeTargets(Pair<Value, Stmt> arg0) {
                                LinkedHashSet<Pair<Value, Stmt>> res = new LinkedHashSet<Pair<Value, Stmt>>();
                                res.add(arg0);
                                if(!StringUtils.isNumeric(assignment.getRightOp().toString()))
                                    res.add(new Pair<Value, Stmt>(assignment.getRightOp(), assignment));
                                return res;
                            }
                        };
                    }

                }
                return Identity.v();
            }

            @Override
            public FlowFunction<Pair<Value, Stmt>> getReturnFlowFunction(Unit arg0, SootMethod arg1, Unit arg2,
                    Unit arg3) {
                if (arg1.toString().equals("<java.lang.Object: void <clinit>()>"))
                    return Identity.v();
                final ReturnStmt statement = (ReturnStmt) arg2;
                return new FlowFunction<Pair<Value, Stmt>>() {
                    @Override
                    public Set<Pair<Value, Stmt>> computeTargets(Pair<Value, Stmt> arg0) {
                        LinkedHashSet<Pair<Value, Stmt>> res = new LinkedHashSet<Pair<Value, Stmt>>();
                        res.add(arg0);
                        if(!StringUtils.isNumeric(statement.getOp().toString()))
                            res.add(new Pair<Value, Stmt>(statement.getOp(), statement));
                        return res;
                    }
                };

            }
        };
    }
    

    @Override
    public Map<Unit, Set<Pair<Value, Stmt>>> initialSeeds() {
        return DefaultSeeds.make(Collections.singleton(Scene.v().getMainMethod().getActiveBody().getUnits().getFirst()),zeroValue());
    }

    @Override
    protected Pair<Value, Stmt> createZeroValue() {
        Pair<Value, Stmt> res = new Pair<Value, Stmt>(new JimpleLocal("<<zero>>", NullType.v()),null);
        return res;
    }
}