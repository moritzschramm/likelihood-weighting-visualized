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
        return "desc";
    }

    public String getCodeExample(){
        return "code";
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