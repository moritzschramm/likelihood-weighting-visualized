package generators.misc.BNSamplingHelper;

import algoanim.animalscript.AnimalScript;
import algoanim.primitives.Text;
import algoanim.primitives.generators.Language;
import algoanim.properties.AnimationPropertiesKeys;
import algoanim.properties.TextProperties;
import algoanim.util.Coordinates;
import algoanim.util.Offset;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class InformationDisplay {

    private Language lang;
    private BayesNet bn;

    private Text iterationDisplay;
    private Text sampleXDisplay;
    private Text normalizedSampleXDisplay;
    private Text varDisplay;
    private Text childVarDisplay;
    private Text probabilityDisplay;

    private int[] samplesX;
    private int[] samplesY;
    private double[] normalizedSamplesX;
    private double[] normalizedSamplesY;


    public InformationDisplay(Language lang, BayesNet bn, int[] samplesX, int[] samplesY, double[] normalizedSamplesX, double[] normalizedSamplesY) {

        this.lang = lang;
        this.bn = bn;
        this.samplesX = samplesX;
        this.samplesY = samplesY;
        this.normalizedSamplesX = normalizedSamplesX;
        this.normalizedSamplesY = normalizedSamplesY;
    }



    public void add() {

        TextProperties props = new TextProperties();
        props.set(AnimationPropertiesKeys.FONT_PROPERTY, new Font(
                Font.SANS_SERIF, Font.PLAIN, 16));

        iterationDisplay = lang.newText(new Coordinates(350, 70), "Iteration: 0",
                "iterationDisplay", null, props);
        sampleXDisplay = lang.newText(new Offset(0, 25, "iterationDisplay",
                AnimalScript.DIRECTION_NW), "Samples X: (" + samplesX[1] + ", " + samplesX[0] + ")",
                "sampleXDisplay", null, props);

        normalizedSampleXDisplay =
                lang.newText(new Offset(0, 25, "sampleXDisplay",
                        AnimalScript.DIRECTION_NW),
                        bn.key(BayesNet.X, BayesNet.A, BayesNet.B)+" = (" + normalizedSamplesX[1] + ", " +normalizedSamplesX[0] + ")",
                        "normalizedSampleXDisplay", null, props);

        props.set(AnimationPropertiesKeys.FONT_PROPERTY, new Font(Font.SANS_SERIF, Font.BOLD, 16));

        varDisplay = lang.newText(new Offset(0, 50, "normalizedSampleXDisplay",
                AnimalScript.DIRECTION_NW), "", "varDisplay", null, props);
        probabilityDisplay = lang.newText(new Offset(0, 25, "varDisplay",
                AnimalScript.DIRECTION_NW), "", "probabilityDisplay", null, props);
        childVarDisplay = lang.newText(new Offset(0, 25, "probabilityDisplay",
                AnimalScript.DIRECTION_NW), "", "childVarDisplay", null, props);

    }

    public void updateInformation(int iteration) {

        DecimalFormat df = new DecimalFormat("0.0##", new DecimalFormatSymbols(Locale.ENGLISH));

        iterationDisplay.setText("Iteration: " + iteration, null, null);
        sampleXDisplay.setText("Samples X: (" + samplesX[1] + ", " + samplesX[0] + ")", null, null);
        normalizedSampleXDisplay
                .setText(bn.key(BayesNet.X, BayesNet.A, BayesNet.B)+" = ("+ df.format(normalizedSamplesX[1])
                        + ", " + df.format(normalizedSamplesX[0]) + ")", null, null);
    }

    public void updateVars(String var, String childVar, Double probability) {

        DecimalFormat df = new DecimalFormat("0.0###", new DecimalFormatSymbols(Locale.ENGLISH));

        if(var != null ) varDisplay.setText("Var = " + var, null, null);
        else varDisplay.setText("", null, null);

        if(childVar != null) childVarDisplay.setText("ChildVar = " + childVar, null, null);
        else childVarDisplay.setText("", null, null);

        if(probability != null) probabilityDisplay.setText("p = " + df.format(probability), null, null);
        else probabilityDisplay.setText("", null, null);
    }
}
