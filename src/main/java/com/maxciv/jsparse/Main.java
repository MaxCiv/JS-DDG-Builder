package com.maxciv.jsparse;

import jdk.nashorn.api.tree.*;

import java.io.File;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {
// Create a new parser instance
        Parser parser = Parser.create();
        File sourceFile = new File(args[0]);

        // Parse given source File using parse method.
        // Pass a diagnostic listener to print error messages.
        CompilationUnitTree cut = parser.parse(sourceFile,
                System.out::println);

        System.out.println(cut);
        if (cut != null) {
            // call Tree.accept method passing a SimpleTreeVisitor
            cut.accept(new SimpleTreeVisitorES5_1<Void, Void>() {
                // visit method for 'with' statement
                public Void visitWith(WithTree wt, Void v) {
                    // print warning on 'with' statement
                    System.out.println("Warning: using 'with' statement!");
                    return null;
                }

            }, null);
        }
    }
}
