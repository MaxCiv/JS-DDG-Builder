package com.maxciv.jsparse;

import com.maxciv.jsparse.cfg.ActionBlockCFG;
import com.maxciv.jsparse.cfg.BlockCFG;
import com.maxciv.jsparse.cfg.FunctionBlockCFG;
import jdk.nashorn.api.tree.*;

import java.io.File;

public class Main {

    public static void main(String[] args) throws Exception {
        Parser parser = Parser.create();
        File sourceFile = new File(args[0]);

        CompilationUnitTree cut = parser.parse(sourceFile, System.out::println);

        System.out.println(cut);
        if (cut != null) {
            Visitor visitor = new Visitor();
            cut.accept(visitor, null);

            FunctionBlockCFG functionBlockCFG = visitor.functionBlockCFG;
            System.out.println(" ");
        }
    }
}
