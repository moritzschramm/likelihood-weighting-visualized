/*
 * LikelihoodWeighting.java
 * Moritz Schramm, 2020 for the Animal project at TU Darmstadt.
 * Copying this file for educational purposes is permitted without further authorization.
 */
//package generators.misc;

import generators.framework.Generator;
import generators.framework.GeneratorType;
import java.util.Locale;
import algoanim.primitives.generators.Language;
import java.util.Hashtable;
import generators.framework.properties.AnimationPropertiesContainer;
import algoanim.animalscript.AnimalScript;

public class LikelihoodWeighting implements Generator {
    private Language lang;

    public void init(){
        lang = new AnimalScript("LikelihoodWeighting", "Moritz Schramm", 800, 600);
    }

    public String generate(AnimationPropertiesContainer props,Hashtable<String, Object> primitives) {
        
        return lang.toString();
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

}