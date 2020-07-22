/*
 * LikelihoodWeighting.java
 * Moritz Schramm, 2020 for the Animal project at TU Darmstadt.
 * Copying this file for educational purposes is permitted without further authorization.
 */
package generators.misc;

import generators.misc.BNSamplingHelper.*;
import algoanim.primitives.Text;
import algoanim.properties.AnimationPropertiesKeys;
import algoanim.properties.TextProperties;
import algoanim.util.Coordinates;
import generators.framework.Generator;
import generators.framework.GeneratorType;

import java.awt.*;
import java.util.Locale;
import algoanim.primitives.generators.Language;
import java.util.Hashtable;
import java.util.Random;

import generators.framework.ValidatingGenerator;
import generators.framework.properties.AnimationPropertiesContainer;
import algoanim.animalscript.AnimalScript;

public class LikelihoodWeighting implements ValidatingGenerator {

    private Language lang;

    private Random random;

    private Text header;

    private Code code;
    private BayesNet bn;
    private InformationDisplay info;

    // iteration number, increased when likelihoodWeighting() is called
    private int iteration = 0;

    private Hashtable<String, double[]> samples;
    private double[] normalizedSamplesX;

    public void init(){
        lang = new AnimalScript("LikelihoodWeighting", "Moritz Schramm", 800, 600);

        lang.setStepMode(true);
        lang.setInteractionType(Language.INTERACTION_TYPE_AVINTERACTION);

        random = new Random();

        iteration = 0;

        samples = new Hashtable<>();
        normalizedSamplesX = new double[2];

        code = new Code(lang);
        bn = new BayesNet(lang);
        info = new InformationDisplay(lang, bn, samples, normalizedSamplesX);
    }

    public String generate(AnimationPropertiesContainer props,Hashtable<String, Object> primitives) {

        // set seed
        random.setSeed((int) primitives.get("Seed"));

        // init probabilities and values
        bn.setProbabilitiesAndValues(primitives);


        // header creation
        TextProperties headerProps = new TextProperties();
        headerProps.set(AnimationPropertiesKeys.FONT_PROPERTY, new Font(
                Font.SANS_SERIF, Font.BOLD, 24));
        header = lang.newText(new Coordinates(20, 30), "Likelihood Weighting",
                "header", null, headerProps);

        lang.nextStep("Einleitung");

        // show introduction text (creates new step)
        showIntro();

        // graph creation
        bn.add();

        // show additional information
        info.add();

        // highlight evidence vars (value won't be changing, highlight color will stay the same)
        bn.highlightNode(BayesNet.A, bn.values.get(BayesNet.A) ? Color.GREEN : Color.RED);
        bn.highlightNode(BayesNet.B, bn.values.get(BayesNet.B) ? Color.GREEN : Color.RED);

        // add source code (unhighlighted)
        code.add();

        lang.nextStep("1. Iteration");

        code.highlight(0);


        lang.nextStep();


        likelihoodWeighting();

        int SAMPLES = 9;

        for(int i = 0; i < SAMPLES; i++) {

            code.highlight(0);
            lang.nextStep();
            likelihoodWeighting();
        }

        lang.nextStep("Zusammenfassung");

        showOutro();


        lang.finalizeGeneration();


        return lang.toString();
    }

    private void showIntro() {

        TextProperties props = new TextProperties();
        props.set(AnimationPropertiesKeys.FONT_PROPERTY, new Font(
                Font.SANS_SERIF, Font.PLAIN, 16));

        String text = getDescription();

        Text intro = lang.newText(new Coordinates(20, 80), text, null, null, props);

        lang.nextStep();

        intro.hide();
    }

    private void showOutro() {

        lang.hideAllPrimitives();
        header.show();

        TextProperties props = new TextProperties();
        props.set(AnimationPropertiesKeys.FONT_PROPERTY, new Font(
                Font.SANS_SERIF, Font.PLAIN, 16));

        String text = "outro";       // TODO add summary (StringBuilder,..)

        Text outro = lang.newText(new Coordinates(20, 80), text, null, null, props);

        lang.nextStep();
    }


    /* algorithm */
    public void likelihoodWeighting() {

        iteration++;
        info.updateInformation(iteration);

        code.unhighlight(0);
        code.highlight(1);

        double w = 1;

        for(String var: bn.list()) {

            if(var.equals(BayesNet.A) || var.equals(BayesNet.B)) {

                double p = bn.probabilities.get(bn.key(var, bn.parents(var)));

                if(!bn.values.get(var)) p = 1 - p;

                w *= p;

            } else {

                double p = bn.probabilities.get(bn.key(var, bn.parents(var)));

                for(String child: bn.children(var)) {

                    p *= bn.probabilities.get(bn.key(child, bn.parents(child)));
                }

                boolean sample = createSampleValue(p);

                bn.values.put(var, sample);
            }
        }

        for(String var: bn.list()) {

            double [] tmp = samples.get(var);
            tmp[bn.values.get(var) ? 1 : 0] += w;
            samples.put(var, tmp);
        }
        normalize();

        info.updateInformation(iteration);
    }

    private boolean createSampleValue(double p) {

        return random.nextDouble() <= p;
    }

    private void normalize() {

        double sum = samples.get(BayesNet.X)[0] + samples.get(BayesNet.X)[1];
        normalizedSamplesX[0] = samples.get(BayesNet.X)[0] / sum;
        normalizedSamplesX[1] = samples.get(BayesNet.X)[1] / sum;
    }

    public String getName() {
        return "LikelihoodWeighting";
    }

    public String getAlgorithmName() {
        return "Likelihood Weighting";
    }

    public String getAnimationAuthor() {
        return "Moritz Schramm";
    }

    public String getDescription(){
        return "Spezialfall von Importance Sampling, zugeschnitten auf Inferenz in Bayesschen Netzen. Erzeugt Ergebnisse die konsistent zu Evidenz e sind. ";
    }

    public String getCodeExample(){
        return "function likelihoodWeighting(Var, evidence, N):"
                +"\n"
                +"    for j = 1 to N:"
                +"\n"
                +"        x, w = weightedSample(evidence)"
                +"\n"
                +"        W[x] = W[x] + w"
                +"\n"
                +"return normalize(W)"
                +"\n"
                +"\n"
                +"function weightedSample(evidence):"
                +"\n"
                +"    w = 1; x mit e init....//fixme"
                +"\n"
                +"    foreach Var in Vars:"
                +"\n"
                +"        if Var is in evidence//fixme:"
                +"\n"
                +"            w = w * P( Var = x | parents(Var) )"
                +"\n"
                +"        else:"
                +"\n"
                +"            x[i] = sample(P(Var | parents(Var))"
                +"\n"
                +"return x, w";
    }

    public String getFileExtension(){
        return "asu";
    }

    public Locale getContentLocale() {
        return Locale.GERMAN;
    }

    public GeneratorType getGeneratorType() {
        return new GeneratorType(GeneratorType.GENERATOR_TYPE_MORE);
    }

    public String getOutputLanguage() {
        return Generator.PSEUDO_CODE_OUTPUT;
    }

    public boolean validateInput(AnimationPropertiesContainer props, Hashtable<String, Object> primitives) {

        for(String key: primitives.keySet()) {

            if(key.equals("A") || key.equals("B") || key.equals("Seed")) continue;

            double v = (double) primitives.get(key);

            if(v < 0.0 || v > 1.0) return false;
        }

        return true;
    }

    public static void main(String[] args) {

        Generator generator = new LikelihoodWeighting();
        generator.init();

        if(args[0].equals("generator")) {

            animal.main.Animal.startGeneratorWindow(generator);

        } else if (args[0].equals("animation")) {

            Hashtable<String, Object> primitives = new Hashtable<>();
            primitives.put("Seed", 1234);

            primitives.put("P(Y)", 0.8);
            primitives.put("P(X | Y=true)", 0.4);
            primitives.put("P(X | Y=false)", 0.7);
            primitives.put("P(A | Y=true)", 0.1);
            primitives.put("P(A | Y=false)", 0.2);
            primitives.put("P(B | A=true, X=true)", 0.9);
            primitives.put("P(B | A=true, X=false)", 0.99);
            primitives.put("P(B | A=false, X=true)", 0.3);
            primitives.put("P(B | A=false, X=false)", 0.6);

            primitives.put("A", false);
            primitives.put("B", true);

            System.out.println(generator.generate(null, primitives));
        }
    }
}