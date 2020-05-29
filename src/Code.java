import algoanim.primitives.SourceCode;
import algoanim.primitives.generators.Language;
import algoanim.properties.AnimationPropertiesKeys;
import algoanim.properties.SourceCodeProperties;
import algoanim.util.Coordinates;

import java.awt.*;

public class Code {

    public final int INDENTATION_WIDTH = 2;

    private Language lang;
    private SourceCode sc;
    private SourceCode exp;

    public Code(Language lang) {
        this.lang = lang;
    }

    public void add() {

        SourceCodeProperties sourceCodeProps = new SourceCodeProperties();
        sourceCodeProps.set(AnimationPropertiesKeys.FONT_PROPERTY, new Font(
                Font.MONOSPACED, Font.PLAIN, 16));
        sourceCodeProps.set(AnimationPropertiesKeys.HIGHLIGHTCOLOR_PROPERTY, Color.RED);

        sc = lang.newSourceCode(new Coordinates(0, 350), "sourceCode",
                null, sourceCodeProps);

        exp = lang.newSourceCode(new Coordinates(550, 350), "explanation", null, sourceCodeProps);

        sc.addCodeLine("for i = 1 to NumberOfSamples:", null, 0*INDENTATION_WIDTH, null);                         // 0
        exp.addCodeLine("// starte neue Iteration", null, 0, null);
        sc.addCodeLine("for each Var in NonevidenceVars:", null, 1*INDENTATION_WIDTH, null);                      // 1
        exp.addCodeLine("// wähle Zufallsvariable, deren Wert nicht bekannt ist, aus", null, 0, null);
        sc.addCodeLine("p = P( Var | parents(Var) )", null, 2*INDENTATION_WIDTH, null);                           // 2
        exp.addCodeLine("// Die Wahrscheinlichkeit P( Var | markov-blanket(Var) ) = P(Var | parents(Var) ) * ...", null, 0, null);
        sc.addCodeLine("for each ChildVar in children(Var):", null, 2*INDENTATION_WIDTH, null);                   // 3
        exp.addCodeLine("// ... * P( ChildVar | parents(ChildVar) )  für jeden Kindknoten ...", null, 0, null);
        sc.addCodeLine("p = p * P( ChildVar | parents(ChildVar) )", null, 3*INDENTATION_WIDTH, null);             // 4
        exp.addCodeLine("// ... wobei diese bedingten Wahrscheinlichkeiten bekannt sind", null, 0, null);
        sc.addCodeLine("sampleValue = createValueGivenProbability(p)", null, 2*INDENTATION_WIDTH, null);          // 5
        exp.addCodeLine("// basierend auf der berechneten Wahrscheinlichkeit 'p', erzeuge einen Wert für die ...", null, 0, null);
        sc.addCodeLine("increaseSampleCount(Var, sampleValue)", null, 2*INDENTATION_WIDTH, null);                 // 6
        exp.addCodeLine("// ... gewählte Zufallsvariable und speichere den Wert in einer Liste", null, 0, null);
        sc.addCodeLine("return normalize(Samples)", null, 0*INDENTATION_WIDTH, null);                             // 7
        exp.addCodeLine("// normalisiere die Liste und gib das Ergebnis zurück", null, 0, null);
    }

    public void highlight(int lineNo) {

        sc.highlight(lineNo);
        exp.highlight(lineNo);
    }

    public void unhighlight(int lineNo) {

        sc.unhighlight(lineNo);
        exp.unhighlight(lineNo);
    }
}
