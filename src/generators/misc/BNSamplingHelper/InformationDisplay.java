package generators.misc.BNSamplingHelper;

import algoanim.animalscript.AnimalScript;
import algoanim.primitives.Text;
import algoanim.primitives.generators.Language;
import algoanim.properties.AnimationPropertiesKeys;
import algoanim.properties.GraphProperties;
import algoanim.properties.TextProperties;
import algoanim.util.Coordinates;
import algoanim.util.Node;
import algoanim.util.Offset;
import translator.Translator;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Hashtable;
import java.util.Locale;

public class InformationDisplay {

    private Language lang;
    private BayesNet bn;
    private String[] sampleVars;
    private String of;
    private String normValue;
    private String probs;

    private Translator translator;
    private Color highlight;
    private Color select;
    private Color trueColor;
    private Color falseColor;

    private Text iterationDisplay;
    private Text sampleDisplay;
    private Text normalizedSampleDisplay;
    private Text varDisplay;
    private Text childVarDisplay;
    private Text probabilityDisplay;

    private Hashtable<String, Integer> samples;
    private Hashtable<String, Double> normalizedSamples;


    public InformationDisplay(Language lang, BayesNet bn,
                              Hashtable<String, Integer> samples, Hashtable<String, Double> normalizedSamples) {

        this.lang = lang;
        this.bn = bn;
        this.samples = samples;
        this.normalizedSamples = normalizedSamples;
    }

    public void init(String[] sampleVars, Translator translator, Hashtable<String, Object> primitives) {

        this.sampleVars = sampleVars;
        this.of = translator.translateMessage("of");
        this.normValue = translator.translateMessage("normValue");
        this.probs = "";

        this.translator = translator;
        this.highlight = (Color) primitives.get("Highlight Color");
        this.select = (Color) primitives.get("Select Color");
        this.trueColor = (Color) primitives.get("True Color");
        this.falseColor = (Color) primitives.get("False Color");
    }

    public void add() {

        TextProperties props = new TextProperties();
        props.set(AnimationPropertiesKeys.FONT_PROPERTY, new Font(
                Font.SANS_SERIF, Font.PLAIN, 16));

        iterationDisplay = lang.newText(new Offset(50, 0, "explanation", AnimalScript.DIRECTION_NE), "Iteration: 0",
                "iterationDisplay", null, props);
        sampleDisplay = lang.newText(new Offset(0, 25, "iterationDisplay",
                AnimalScript.DIRECTION_NW), getSampleCount("Sample (true, false) " + of + " "),
                "sampleXDisplay", null, props);

        normalizedSampleDisplay =
                lang.newText(new Offset(0, 25, "sampleXDisplay",
                                AnimalScript.DIRECTION_NW),
                        getSampleCount(normValue+" (true, false) " + of + " "),
                        "normalizedSampleXDisplay", null, props);


        GraphProperties graphProps = new GraphProperties();
        graphProps.set(AnimationPropertiesKeys.EDGECOLOR_PROPERTY, Color.BLACK);
        graphProps.set(AnimationPropertiesKeys.FILL_PROPERTY, highlight);

        lang.newGraph("legendHighlight", new int[1][1], new Node[]{new Offset(10, 10, "normalizedSampleXDisplay", AnimalScript.DIRECTION_SW)}, new String[]{""}, null, graphProps);
        lang.newText(new Offset(5, 0, "legendHighlight", AnimalScript.DIRECTION_NE), "= Var", "legendHighlightText", null, props);

        graphProps.set(AnimationPropertiesKeys.FILL_PROPERTY, select);

        lang.newGraph("legendSelect", new int[1][1], new Node[]{new Offset(25, 0, "legendHighlightText", AnimalScript.DIRECTION_NE)}, new String[]{""}, null, graphProps);
        lang.newText(new Offset(5, 0, "legendSelect", AnimalScript.DIRECTION_NE), "= ChildVar", "legendSelectText", null, props);

        graphProps.set(AnimationPropertiesKeys.FILL_PROPERTY, trueColor);

        lang.newGraph("legendTrue", new int[1][1], new Node[]{new Offset(25, 0, "legendSelectText", AnimalScript.DIRECTION_NE)}, new String[]{""}, null, graphProps);
        lang.newText(new Offset(5, 0, "legendTrue", AnimalScript.DIRECTION_NE), "= True", "legendTrueText", null, props);

        graphProps.set(AnimationPropertiesKeys.FILL_PROPERTY, falseColor);

        lang.newGraph("legendFalse", new int[1][1], new Node[]{new Offset(25, 0, "legendTrueText", AnimalScript.DIRECTION_NE)}, new String[]{""}, null, graphProps);
        lang.newText(new Offset(5, 0, "legendFalse", AnimalScript.DIRECTION_NE), "= False", "legendFalseText", null, props);


        props.set(AnimationPropertiesKeys.FONT_PROPERTY, new Font(Font.SANS_SERIF, Font.BOLD, 16));

        varDisplay = lang.newText(new Offset(0, 60, "normalizedSampleXDisplay",
                AnimalScript.DIRECTION_NW), "", "varDisplay", null, props);
        probabilityDisplay = lang.newText(new Offset(0, 25, "varDisplay",
                AnimalScript.DIRECTION_NW), "", "probabilityDisplay", null, props);
        childVarDisplay = lang.newText(new Offset(0, 25, "probabilityDisplay",
                AnimalScript.DIRECTION_NW), "", "childVarDisplay", null, props);

    }

    public void updateInformation(int iteration) {

        iterationDisplay.setText("Iteration: " + iteration, null, null);
        sampleDisplay.setText(getSampleCount("Sample (true, false) " + of + " "), null, null);
        normalizedSampleDisplay.setText(getNormalizedSampleCount(normValue+" (true, false) " + of + " "), null, null);
    }

    public void updateVars(String var, String childVar, Double resProbability, Double probability) {

        DecimalFormat df = new DecimalFormat("0.0###", new DecimalFormatSymbols(Locale.ENGLISH));

        if (var != null) varDisplay.setText("Var = " + var, null, null);
        else varDisplay.setText("", null, null);

        if (childVar != null) childVarDisplay.setText("ChildVar = " + childVar, null, null);
        else childVarDisplay.setText("", null, null);


        if (probability != null && resProbability != null) {

            if(probability != -1.0) {
                probs += probs.length() == 0 ? "" : " x ";
                probs += df.format(probability);
            }

            String tmp = probs + (probs.length() > 0 ? " = " : "");

            probabilityDisplay.setText("p = " + tmp + df.format(resProbability), null, null);

        } else if (resProbability != null) {
            probs = df.format(resProbability);
            probabilityDisplay.setText("p = " + df.format(resProbability), null, null);
        } else {
            probs = "";
            probabilityDisplay.setText("", null, null);
        }
    }

    public String getSampleCount(final String prefix) {

        StringBuilder sb = new StringBuilder();
        sb.append(prefix);

        String conc = "";
        for(String var: sampleVars) {
            sb.append(conc);
            conc = ", ";
            sb.append(var);
        }

        sb.append(": ");

        conc = "";
        for(String var: sampleVars) {
            sb.append(conc);
            conc = ", ";

            sb.append("(");
            sb.append(samples.get(var+"=true") == null ? 0 : samples.get(var+"=true"));
            sb.append(", ");
            sb.append(samples.get(var+"=false") == null ? 0 : samples.get(var+"=false"));
            sb.append(")");
        }

        return sb.toString();
    }

    public String getNormalizedSampleCount(final String prefix) {

        DecimalFormat df = new DecimalFormat("0.0##", new DecimalFormatSymbols(Locale.ENGLISH));
        StringBuilder sb = new StringBuilder();
        sb.append(prefix);

        String conc = "";
        for(String var: sampleVars) {
            sb.append(conc);
            conc = ", ";
            sb.append(var);
        }

        sb.append(": ");

        conc = "";
        for(String var: sampleVars) {
            sb.append(conc);
            conc = ", ";

            sb.append("(");
            sb.append(normalizedSamples.get(var+"=true") == null ? 0 : df.format(normalizedSamples.get(var+"=true")));
            sb.append(", ");
            sb.append(normalizedSamples.get(var+"=false") == null ? 0 : df.format(normalizedSamples.get(var+"=false")));
            sb.append(")");
        }

        return sb.toString();
    }
}
