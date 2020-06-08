/*
 * LikelihoodWeighting.java
 * Moritz Schramm, 2020 for the Animal project at TU Darmstadt.
 * Copying this file for educational purposes is permitted without further authorization.
 */
//package generators.misc;

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

    public void init(){
        lang = new AnimalScript("LikelihoodWeighting", "Moritz Schramm", 800, 600);

        lang.setStepMode(true);
        lang.setInteractionType(Language.INTERACTION_TYPE_AVINTERACTION);

        random = new Random();

        iteration = 0;

        code = new Code(lang);
        bn = new BayesNet(lang);
        info = new InformationDisplay(lang, bn);
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


        return lang.toString();
    }

    private void showIntro() {


    }

    public void likelihoodWeighting() {

        // TODO
    }

    public int[] weightedSample() {

        // TODO
        return null;
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
        //animal.main.Animal.startGeneratorWindow(generator);



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