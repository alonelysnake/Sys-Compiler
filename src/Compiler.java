import backend.Translator;
import backend.optimizer.Allocator;
import backend.schedule.BasicScheduler;
import backend.schedule.OptimizeScheduler;
import backend.schedule.Scheduler;
import error.AnalysisState;
import lexer.Lexer;
import lexer.token.Token;
import middle.BlockInfo;
import middle.LabelTable;
import middle.MiddleState;
import middle.instruction.FuncEntry;
import middle.instruction.INode;
import middle.optimizer.Optimizer;
import syntax.CompUnit;
import syntax.CompUnitParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class Compiler {
    public static void main(String[] args) {
        String inputfile;
        String outputFile;
        String errFile = "error.txt";
        String mipsFile = "mips.txt";
        boolean optimize = true;
        if (args.length > 0) {
            inputfile = args[1];
            outputFile = args[3];
        } else {
            inputfile = "./test/2021test/testfiles-only/c/testfile8.txt";
            inputfile = "testfile.txt";
            outputFile = "output.txt";
        }
        Lexer lexer = new Lexer(FileIO.readFile(inputfile));
        
        LinkedList<Token> tokens = lexer.tackle();
        
        CompUnitParser parser = new CompUnitParser(tokens);
        CompUnit unit = parser.parseCompUnit();
//        FileIO.writeParser(outputFile, unit);
        
        AnalysisState state = new AnalysisState();
        unit.analyse(state);
        
        FileIO.writeError(errFile, state.getErrorTable());
        MiddleState middleState = new MiddleState();
        BlockInfo info = unit.generateIcode(middleState);
        if (optimize) {
            info = optimize(info, middleState.getLabelTable());
        }
        FileIO.writeIR(outputFile, info.getFirst(), middleState.getLabelTable());// 写入中间代码
        Translator translator = new Translator(info.getFirst(), scheduler, middleState.getLabelTable());
        String out = translator.translate();
        FileIO.writeMIPS(mipsFile, out);
    }
    
    private static Scheduler scheduler = new BasicScheduler();
    
    public static BlockInfo optimize(BlockInfo info, LabelTable labelTable) {
        final INode first = info.getFirst();
        INode last = first;
        INode node = first;
        while (!(node instanceof FuncEntry)) {
            node = node.getNext();
        }
        HashMap<String, Allocator> allocators = new HashMap<>();
        while (node != null) {
            final FuncEntry funcEntry = (FuncEntry) node;
            node = node.getNext();
            INode begin = node;
            INode end = node;
            while (node != null && !(node instanceof FuncEntry)) {
                end = node;
                node = node.getNext();
            }
            // 优化中间代码
            boolean change = true;
            BlockInfo block = new BlockInfo(null, begin, end);
            while (change) {
                Optimizer optimizer = new Optimizer(block, labelTable);
                block = optimizer.optimize();
                change = optimizer.isChanged();
                last = block.getLast();
            }
            Allocator allocator = new Allocator(block, labelTable);
            allocator.optimize();
            allocators.put(funcEntry.getLabel(), allocator);
            //TODO 极端情况：为空的函数?
        }
        scheduler = new OptimizeScheduler(allocators);
        return new BlockInfo(null, first, last);
    }
}
