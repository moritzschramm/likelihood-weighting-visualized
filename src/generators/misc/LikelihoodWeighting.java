/*
 * LikelihoodWeighting.java
 * Moritz Schramm, 2020 for the Animal project at TU Darmstadt.
 * Copying this file for educational purposes is permitted without further authorization.
 */
package generators.misc;

import algoanim.primitives.Graph;
import algoanim.properties.GraphProperties;
import algoanim.properties.SourceCodeProperties;
import algoanim.util.Node;
import algoanim.util.Offset;
import generators.misc.BNSamplingHelper.*;
import algoanim.primitives.Text;
import algoanim.properties.AnimationPropertiesKeys;
import algoanim.properties.TextProperties;
import algoanim.util.Coordinates;
import generators.framework.Generator;
import generators.framework.GeneratorType;

import java.awt.*;
import java.util.Arrays;
import java.util.Locale;
import algoanim.primitives.generators.Language;
import java.util.Hashtable;
import java.util.Random;

import generators.framework.ValidatingGenerator;
import generators.framework.properties.AnimationPropertiesContainer;
import algoanim.animalscript.AnimalScript;
import interactionsupport.models.MultipleChoiceQuestionModel;
import translator.Translator;

public class LikelihoodWeighting implements ValidatingGenerator {

    private Language lang;

    private Translator translator;
    private String resourceName;
    private Locale locale;

    private Random random;

    private Text header;

    private CodeLW code;
    private BayesNetLW bn;
    private InformationDisplayLW info;

    // iteration number, increased when sample() is called
    private int iteration = 0;
    private int numberOfIterations = 10;

    // variables and their sample counts
    private String[] vars;
    private String[] sampleVars;
    private Hashtable<String, Double> samples;
    private Hashtable<String, Double> normalizedSamples;

    // for questions
    private String WRONG_ASW;
    private String RIGHT_ASW;

    public LikelihoodWeighting(String resourceName, Locale locale) {
        this.resourceName = resourceName;
        this.locale = locale;

        translator = new Translator(resourceName, locale);
    }

    public void init() {
        lang = new AnimalScript("Likelihood Weighting", "Moritz Schramm, Moritz Andres", 800, 600);
        lang.setStepMode(true);
        lang.setInteractionType(Language.INTERACTION_TYPE_AVINTERACTION);

        random = new Random();

        iteration = 0;

        samples = new Hashtable<>();
        normalizedSamples = new Hashtable<>();

        code = new CodeLW(lang, translator);
        bn = new BayesNetLW(lang);
        info = new InformationDisplayLW(lang, bn, samples, normalizedSamples);


        RIGHT_ASW = translator.translateMessage("right_asw");
        WRONG_ASW = translator.translateMessage("wrong_asw");
    }

    public String generate(AnimationPropertiesContainer props,Hashtable<String, Object> primitives) {

        // init vars and sample arrays
        vars = (String []) primitives.get("Variables");
        sampleVars = (String []) primitives.get("Non-evidence variables");

        // set seed
        random.setSeed((int) primitives.get("Seed"));

        // set number of iterations
        numberOfIterations = (int) primitives.get("NumberOfSamples");

        // init graph, probabilities and values
        /*GraphProperties graphProps = new GraphProperties();
        graphProps.set(AnimationPropertiesKeys.NAME, "graphProps");
        graphProps.set(AnimationPropertiesKeys.DIRECTED_PROPERTY, true);
        graphProps.set(AnimationPropertiesKeys.FILLED_PROPERTY, false);
        graphProps.set(AnimationPropertiesKeys.FILL_PROPERTY, Color.WHITE);
        graphProps.set(AnimationPropertiesKeys.EDGECOLOR_PROPERTY, Color.BLACK);
        graphProps.set(AnimationPropertiesKeys.ELEMHIGHLIGHT_PROPERTY, Color.BLACK);
        graphProps.set(AnimationPropertiesKeys.HIGHLIGHTCOLOR_PROPERTY, Color.GREEN);
        graphProps.set(AnimationPropertiesKeys.NODECOLOR_PROPERTY, Color.BLACK);
        graphProps.set(AnimationPropertiesKeys.WEIGHTED_PROPERTY, false);*/

        GraphProperties graphProps = (GraphProperties) props.getPropertiesByName("graphProps");
        bn.init(primitives, graphProps, vars, sampleVars);


        // header creation
        TextProperties headerProps = new TextProperties();
        headerProps.set(AnimationPropertiesKeys.FONT_PROPERTY, new Font(
                Font.SANS_SERIF, Font.BOLD | Font.ITALIC, 24));
        header = lang.newText(new Coordinates(20, 30), "Likelihood Weighting",
                "header", null, headerProps);

        lang.nextStep(translator.translateMessage("introTOC"));

        // show introduction text (creates new step)
        showIntro();

        // add source code (unhighlighted)
        SourceCodeProperties sourceCodeProps = new SourceCodeProperties();
        sourceCodeProps.set(AnimationPropertiesKeys.FONT_PROPERTY, new Font(
                Font.MONOSPACED, Font.PLAIN, 16));
        sourceCodeProps.set(AnimationPropertiesKeys.HIGHLIGHTCOLOR_PROPERTY, Color.RED);
        sourceCodeProps.set(AnimationPropertiesKeys.NAME, "sourceCode");
        code.init((SourceCodeProperties) props.getPropertiesByName("sourceCode"));
        //code.init(sourceCodeProps);
        code.add();

        // show additional information
        info.init(sampleVars, translator, primitives);
        info.add();

        // graph creation
        bn.add();

        iteration++;
        info.updateInformation(iteration);
        code.highlight(0);

        MultipleChoiceQuestionModel question1 = new MultipleChoiceQuestionModel("q1");
        String feedback_q1 = translator.translateMessage("q1_fb");
        question1.setPrompt(translator.translateMessage("q1_text"));
        question1.addAnswer(translator.translateMessage("q1_asw1"), 0, WRONG_ASW + feedback_q1);
        question1.addAnswer(translator.translateMessage("q1_asw2"), 0, WRONG_ASW + feedback_q1);
        question1.addAnswer(translator.translateMessage("q1_asw3"), 1, RIGHT_ASW + feedback_q1);
        lang.addMCQuestion(question1);

        lang.nextStep("1. Iteration");

        sample();

        for(int i = 0; i < numberOfIterations - 1; i++) {

            code.highlight(0);
            iteration++;
            info.updateInformation(iteration);
            lang.nextStep(iteration + ". Iteration");
            sample();
        }

        code.highlight(7);

        lang.nextStep();

        code.unhighlight(7);
        code.highlight(8);

        lang.nextStep(translator.translateMessage("outroTOC"));

        showOutro();


        lang.finalizeGeneration();

        return lang.toString();
    }

    private void showIntro() {

        TextProperties props = new TextProperties();
        props.set(AnimationPropertiesKeys.FONT_PROPERTY, new Font(
                Font.SANS_SERIF, Font.PLAIN, 16));

        String text = translator.translateMessage("intro");

        final int lineBreakSize = 16 + 3;
        String[] parts = text.split("\n");
        Text[] intro_ts = new Text[parts.length];

        int lineCounter = 0;
        for(String textPart : parts){
            int yOffset = lineBreakSize * lineCounter;
            Text intro = lang.newText(new Coordinates(20, 70 + yOffset), textPart, null, null, props);
            intro_ts[lineCounter] = intro;
            lineCounter++;
        }

        lang.nextStep();

        for(Text intro : intro_ts)
            intro.hide();
    }

    private void showOutro() {

        lang.hideAllPrimitives();
        header.show();

        TextProperties props = new TextProperties();
        props.set(AnimationPropertiesKeys.FONT_PROPERTY, new Font(
                Font.SANS_SERIF, Font.PLAIN, 16));

        String text = translator.translateMessage("outro");


        final int lineBreakSize = 16 + 3;  // font size + gap
        String[] parts = text.split("\n");
        Text[] outro_ts = new Text[parts.length];

        int lineCounter = 0;
        for(String textPart : parts){
            int yOffset = lineBreakSize * lineCounter;
            Text outro = lang.newText(new Coordinates(20, 70 + yOffset), textPart, "outroline"+lineCounter, null, props);
            outro_ts[lineCounter] = outro;
            lineCounter++;
        }

        Text iterationDisplay = lang.newText(new Offset(0, 30, "outroline"+(lineCounter-1), AnimalScript.DIRECTION_NW), "Iteration: "+iteration,
                "iterationDisplayOutro", null, props);

        Text propTrueDisplay = lang.newText(new Offset(0, 30, "iterationDisplayOutro", AnimalScript.DIRECTION_NW),
                info.getSampleCount("Sample (true, false) " + translator.translateMessage("of") + " "),
                "propTrueDisplayOutro", null, props);

        Text propFalseDisplay = lang.newText(new Offset(0, 30, "propTrueDisplayOutro", AnimalScript.DIRECTION_NW),
                info.getNormalizedSampleCount(translator.translateMessage("normValue")+" (true, false) " + translator.translateMessage("of") + " "),
                "propFalseDisplayOutro", null, props);

        lang.nextStep();
    }


    /* algorithm */
    public void sample() {

        code.unhighlight(0);
        code.unhighlight(7);
        code.highlight(1);

        double weight = 1.0;

        for(String var: sampleVars) {

            bn.unhighlightNode(var);
        }

        info.updateVars(null, null, weight, null);

        lang.nextStep();

        for(String var: vars) {

            code.unhighlight(1);
            code.highlight(2);

            info.updateVars(var, null, weight, -1.0);

            bn.highlightNode(var, BayesNetLW.HIGHLIGHT_COLOR);

            double p = bn.probabilities.get(bn.key(var, bn.parents(var)));

            lang.nextStep();

            code.unhighlight(2);

            if(Arrays.asList(sampleVars).contains(var)) {       // is non-evidence variable

                code.highlight(5);

                lang.nextStep();

                code.unhighlight(5);
                code.highlight(6);

                boolean sample = createSampleValue(p);

                bn.values.put(var, sample);

                bn.highlightNode(var, bn.values.get(var) ? BayesNetLW.TRUE_COLOR : BayesNetLW.FALSE_COLOR);

                lang.nextStep();

                code.unhighlight(6);

            } else {    // is evidence variable

                code.highlight(3);

                lang.nextStep();

                code.unhighlight(3);
                code.highlight(4);

                weight *= p;

                info.updateVars(var, null, weight, p);

                bn.highlightNode(var, bn.values.get(var) ? BayesNetLW.TRUE_COLOR : BayesNetLW.FALSE_COLOR);

                lang.nextStep();

                code.unhighlight(4);
            }

        }

        code.highlight(7);

        for(String var: sampleVars) {

            increaseSampleCount(var, bn.values.get(var), weight);
        }

        info.updateInformation(iteration);

        lang.nextStep();

        code.unhighlight(7);
    }

    private boolean createSampleValue(double p) {

        return random.nextDouble() <= p;
    }

    private void increaseSampleCount(String var, boolean value, double weight) {

        String key = var + (value ? "=true" : "=false");
        samples.put(key, (samples.get(key) == null ? 0 : samples.get(key)) + weight);

        double trueVal = samples.get(var+"=true") == null ? 0 : samples.get(var+"=true");
        double falseVal = samples.get(var+"=false") == null ? 0 : samples.get(var+"=false");
        double sum = trueVal + falseVal;
        sum = sum == 0 ? 1 : sum;
        normalizedSamples.put(var+"=true", trueVal / sum);
        normalizedSamples.put(var+"=false", falseVal / sum);
    }

    public String getName() {
        return "Likelihood Weighting";
    }

    public String getAlgorithmName() {
        return "Likelihood Weighting";
    }

    public String getAnimationAuthor() {
        return "Moritz Schramm, Moritz Andres";
    }

    public String getDescription(){
        return translator.translateMessage("description");
    }

    public String getCodeExample(){
        return "function likelihoodWeighting(Vars, EvidenceVars, Values, NumberOfSamples):"
                +"\n"
                +"    for i = 1 to NumberOfSamples:"
                +"\n"
                +"        w = 1.0;"
                +"\n"
                +"        for each Var in Vars:"
                +"\n"
                +"            if Var is in EvidenceVars:"
                +"\n"
                +"                w = w * P( Var = Values[Var] | parents(Var) )"
                +"\n"
                +"            else:"
                +"\n"
                +"                Values[Var] = sample(P(Var | parents(Var))"
                +"\n"
                +"        addWeightForEachSampleVar(Vars, Values, w);"
                +"\n"
                +"return normalizedSamples()";
    }

    public String getFileExtension(){
        return "asu";
    }

    public Locale getContentLocale() {
        return locale;
    }

    public GeneratorType getGeneratorType() {
        return new GeneratorType(GeneratorType.GENERATOR_TYPE_MORE);
    }

    public String getOutputLanguage() {
        return Generator.PSEUDO_CODE_OUTPUT;
    }

    public boolean validateInput(AnimationPropertiesContainer props, Hashtable<String, Object> primitives) {

        int seed = (int) primitives.get("Seed");

        int numberOfIterations = (int) primitives.get("NumberOfSamples");

        if(seed <= 0)
            throw new IllegalArgumentException("Seed must be greater than 0.");
        if(numberOfIterations <= 0)
            throw new IllegalArgumentException("NumberOfSamples must be greater than 0.");

        String[] vars = (String []) primitives.get("Variables");
        String[] sampleVars = (String []) primitives.get("Non-evidence variables");
        String[] values = (String []) primitives.get("Values");

        if(vars.length > 4)
            throw new IllegalArgumentException("Length of Variables must not be greater than 4.");

        if(sampleVars.length > vars.length)
            throw new IllegalArgumentException("Length of Non-evidence variables cannot be greater than length of Variables");

        if(sampleVars.length < 1)
            throw new IllegalArgumentException("There must be at least one Non-evidence variable");

        if(vars.length - sampleVars.length != values.length)
            throw new IllegalArgumentException("Array 'Values' has an invalid form");

        int[][] p = (int[][]) primitives.get("Probabilities");

        for(int i = 0; i < p.length; i++) {
            for(int j = 0; j < p[i].length; j++) {
                if(p[i][j] < 0 || p[i][j] > 100)
                    throw new IllegalArgumentException("Probabilities table has invalid entries (< 0 or > 100).");
            }
        }

        BayesNetLW bn;
        try {
            bn = new BayesNetLW(new AnimalScript("Likelihood Weighting", "Moritz Schramm, Moritz Andres", 800, 600));
            bn.init(primitives, null, vars, sampleVars);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid Probability table for given graph");
        }

        if(bn.graph.getNodes().length != vars.length)
            throw new IllegalArgumentException("Variables do not match graph nodes");

        for(int i = 0; i < bn.graph.getSize(); i++) {
            String label = bn.graph.getNodeLabel(i);
            if( ! Arrays.asList(vars).contains(label))
                throw new IllegalArgumentException("Invalid Variables for given graph");
        }

        for(String var: sampleVars) {
            if( ! Arrays.asList(vars).contains(var))
                throw new IllegalArgumentException("Invalid Sample Variables");
        }

        for(String var: vars) {
            int parents = bn.parents(var).length;
            int children = bn.children(var).length;

            if(parents == 0 && children == 0)
                throw new IllegalArgumentException("Invalid graph");
        }

        return true;
    }

    public static void main(String[] args) {

        Generator generator = new LikelihoodWeighting("resources/likelihoodweighting", Locale.GERMANY);
        generator.init();

        if (args[0].equals("generator")) {

            animal.main.Animal.startGeneratorWindow(generator);

        } else if (args[0].equals("animation")) {

            Hashtable<String, Object> primitives = new Hashtable<>();
            AnimationPropertiesContainer props = new AnimationPropertiesContainer();

            primitives.put("Seed", 1234);
            primitives.put("NumberOfSamples", 10);
            primitives.put("Variables", new String[]{"Y", "A", "X", "B"});
            primitives.put("Non-evidence variables", new String[]{"Y", "X"});
            primitives.put("Values", new String[]{"true", "false"});

            GraphProperties graphProps = new GraphProperties();
            graphProps.set(AnimationPropertiesKeys.NAME, "graphProps");
            graphProps.set(AnimationPropertiesKeys.DIRECTED_PROPERTY, true);
            graphProps.set(AnimationPropertiesKeys.FILLED_PROPERTY, false);
            graphProps.set(AnimationPropertiesKeys.FILL_PROPERTY, Color.WHITE);
            graphProps.set(AnimationPropertiesKeys.EDGECOLOR_PROPERTY, Color.BLACK);
            graphProps.set(AnimationPropertiesKeys.ELEMHIGHLIGHT_PROPERTY, Color.BLACK);
            graphProps.set(AnimationPropertiesKeys.HIGHLIGHTCOLOR_PROPERTY, Color.GREEN);
            graphProps.set(AnimationPropertiesKeys.NODECOLOR_PROPERTY, Color.BLACK);
            graphProps.set(AnimationPropertiesKeys.WEIGHTED_PROPERTY, false);

            int[][] adjacencyMatrix = new int[4][4];
            for (int i = 0; i < adjacencyMatrix.length; i++)
                for (int j = 0; j < adjacencyMatrix.length; j++)
                    adjacencyMatrix[i][j] = 0;

            adjacencyMatrix[0][1] = 1;
            adjacencyMatrix[0][2] = 1;
            adjacencyMatrix[1][3] = 1;
            adjacencyMatrix[2][3] = 1;

            Node[] nodes = new Node[4];
            int offsetX = 600;
            int offsetY = 180;
            nodes[0] = new Coordinates(offsetX + 150, offsetY + 100);
            nodes[1] = new Coordinates(offsetX + 50, offsetY + 150);
            nodes[2] = new Coordinates(offsetX + 250, offsetY + 150);
            nodes[3] = new Coordinates(offsetX + 150, offsetY + 200);

            Language lang = new AnimalScript("Gibbs Sampling", "Moritz Schramm, Moritz Andres", 800, 600);
            Graph graph = lang.newGraph("bn", adjacencyMatrix, nodes, new String[]{"Y", "A", "X", "B"}, null, graphProps);

            primitives.put("graph", graph);

            int[][] p = new int[4][4];

            p[0][0] = 30;   // P(Y)
            p[0][1] = 10;   // P(A|Y=true)
            p[1][1] = 20;   // P(A|Y=false)
            p[0][2] = 40;   // P(X|Y=true)
            p[1][2] = 70;   // P(X|Y=false)
            p[0][3] = 90;   // P(B | A=true, X=true)
            p[1][3] = 99;   // P(B | A=true, X=false)
            p[2][3] = 30;   // P(B | A=false, X=true)
            p[3][3] = 60;   // P(B | A=false, X=false)

            primitives.put("Probabilities", p);

            primitives.put("Highlight Color", Color.GRAY);
            primitives.put("Select Color", Color.LIGHT_GRAY);
            primitives.put("True Color", Color.GREEN);
            primitives.put("False Color", Color.RED);

            SourceCodeProperties sourceCodeProps = new SourceCodeProperties();
            sourceCodeProps.set(AnimationPropertiesKeys.FONT_PROPERTY, new Font(
                    Font.MONOSPACED, Font.PLAIN, 16));
            sourceCodeProps.set(AnimationPropertiesKeys.HIGHLIGHTCOLOR_PROPERTY, Color.RED);
            sourceCodeProps.set(AnimationPropertiesKeys.NAME, "sourceCode");

            props.add(sourceCodeProps);

            System.out.println(generator.generate(props, primitives));
        }
    }
}