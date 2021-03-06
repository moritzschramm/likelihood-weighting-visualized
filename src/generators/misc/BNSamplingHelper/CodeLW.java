package generators.misc.BNSamplingHelper;

import algoanim.animalscript.AnimalScript;
import algoanim.primitives.SourceCode;
import algoanim.primitives.generators.Language;
import algoanim.properties.AnimationPropertiesKeys;
import algoanim.properties.SourceCodeProperties;
import algoanim.util.Coordinates;

import java.awt.*;

import algoanim.util.Offset;
import translator.Translator;

public class CodeLW {

    public final int INDENTATION_WIDTH = 2;

    private Language lang;
    private Translator translator;
    private SourceCode sc;
    private SourceCode exp;
    private SourceCodeProperties scp;

    public CodeLW(Language lang, Translator translator) {
        this.lang = lang;
        this.translator = translator;
    }

    public void init(SourceCodeProperties scp) {

        this.scp = scp;
    }

    public void add() {add(20, 50);}
    public void add(int x, int y) {

        SourceCodeProperties expCodeProps = new SourceCodeProperties();
        expCodeProps.set(AnimationPropertiesKeys.FONT_PROPERTY, new Font(
                Font.SANS_SERIF, Font.ITALIC, 16));
        expCodeProps.set(AnimationPropertiesKeys.HIGHLIGHTCOLOR_PROPERTY, scp.get(AnimationPropertiesKeys.HIGHLIGHTCOLOR_PROPERTY));
        expCodeProps.set(AnimationPropertiesKeys.COLOR_PROPERTY, scp.get(AnimationPropertiesKeys.COLOR_PROPERTY));

        exp = lang.newSourceCode(new Coordinates(x, y), "explanation", null, expCodeProps);
        exp.addCodeLine("1. "+translator.translateMessage("line0"), null, 0, null);
        exp.addCodeLine("2. "+translator.translateMessage("line1"), null, 0, null);
        exp.addCodeLine("3. "+translator.translateMessage("line2"), null, 0, null);
        exp.addCodeLine("4. "+translator.translateMessage("line3"), null, 0, null);
        exp.addCodeLine("5. "+translator.translateMessage("line4"), null, 0, null);
        exp.addCodeLine("6. "+translator.translateMessage("line5"), null, 0, null);
        exp.addCodeLine("7. "+translator.translateMessage("line6"), null, 0, null);
        exp.addCodeLine("8. "+translator.translateMessage("line7"), null, 0, null);
        exp.addCodeLine("9. "+translator.translateMessage("line8"), null, 0, null);

        scp.set(AnimationPropertiesKeys.FONT_PROPERTY, new Font(
                Font.MONOSPACED, Font.PLAIN, 16));
        sc = lang.newSourceCode(new Offset(0, 10, "explanation", AnimalScript.DIRECTION_SW), "sourceCode",
                null, scp);

        sc.addCodeLine("for i = 1 to NumberOfSamples:", null, 0*INDENTATION_WIDTH, null);                    // 0
        sc.addCodeLine("w = 1.0", null, 1*INDENTATION_WIDTH, null);                                         // 1
        sc.addCodeLine("for each Var in Vars:", null, 1*INDENTATION_WIDTH, null);                            // 2
        sc.addCodeLine("if Var is in EvidenceVars:", null, 2*INDENTATION_WIDTH, null);                       // 3
        sc.addCodeLine("w = w * P( Var = Values[Var] | parents(Var) )", null, 3*INDENTATION_WIDTH, null);    // 4
        sc.addCodeLine("else:", null, 2*INDENTATION_WIDTH, null);                                            // 5
        sc.addCodeLine("Values[Var] = sample(P(Var | parents(Var))", null, 3*INDENTATION_WIDTH, null);       // 6
        sc.addCodeLine("addWeightForEachSampleVar(Vars, Values, w);", null, 2*INDENTATION_WIDTH, null);          // 7
        sc.addCodeLine("return normalizedSamples()", null, 0*INDENTATION_WIDTH, null);                       // 8
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
