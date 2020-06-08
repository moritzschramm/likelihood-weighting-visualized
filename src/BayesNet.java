import algoanim.primitives.Graph;
import algoanim.primitives.generators.Language;
import algoanim.properties.AnimationPropertiesKeys;
import algoanim.properties.GraphProperties;
import algoanim.util.Coordinates;
import algoanim.util.Node;

import java.awt.*;
import java.util.Arrays;
import java.util.Hashtable;

public class BayesNet {

    public static final String A = "A";
    public static final String B = "B";
    public static final String X = "X";
    public static final String Y = "Y";

    private Language lang;

    private Graph g;


    // hashtables, containing probabilities and values of random variables
    public Hashtable<String, Double> probabilities;
    public Hashtable<String, Boolean> values;


    public BayesNet(Language lang) {

        this.lang = lang;
        this.values = new Hashtable<>();
        this.probabilities = new Hashtable<>();

        values.put(A, false);
        values.put(B, false);
        values.put(X, false);
        values.put(Y, false);
    }

    public void setProbabilitiesAndValues(Hashtable<String, Object> primitives) {

        probabilities.put("P(Y)", (double) primitives.get("P(Y)"));
        probabilities.put("P(X | Y=true)", (double) primitives.get("P(X | Y=true)"));                   // P(X | Y=true) = P(X=true | Y=true)
        probabilities.put("P(X | Y=false)", (double) primitives.get("P(X | Y=false)"));
        probabilities.put("P(A | Y=true)", (double) primitives.get("P(A | Y=true)"));
        probabilities.put("P(A | Y=false)", (double) primitives.get("P(A | Y=false)"));
        probabilities.put("P(B | A=true, X=true)", (double) primitives.get("P(B | A=true, X=true)"));
        probabilities.put("P(B | A=true, X=false)", (double) primitives.get("P(B | A=true, X=false)"));
        probabilities.put("P(B | A=false, X=true)", (double) primitives.get("P(B | A=false, X=true)"));
        probabilities.put("P(B | A=false, X=false)", (double) primitives.get("P(B | A=false, X=false)"));

        values.put(A, (boolean) primitives.get(A));
        values.put(B, (boolean) primitives.get(B));
    }

    public void add() {

        int[][] adjacencyMatrix = new int[4][4];
        for(int i = 0; i < adjacencyMatrix.length; i++)
            for(int j = 0; j < adjacencyMatrix.length; j++)
                adjacencyMatrix[i][j] = 0;

        adjacencyMatrix[0][1] = 1;
        adjacencyMatrix[0][2] = 1;
        adjacencyMatrix[1][3] = 1;
        adjacencyMatrix[2][3] = 1;

        Node[] nodes = new Node[4];
        nodes[0] = new Coordinates(150, 100);
        nodes[1] = new Coordinates(50, 150);
        nodes[2] = new Coordinates(250, 150);
        nodes[3] = new Coordinates(150, 200);

        GraphProperties graphProps = new GraphProperties();
        graphProps.set(AnimationPropertiesKeys.DIRECTED_PROPERTY, true);
        graphProps.set(AnimationPropertiesKeys.FILLED_PROPERTY, false);
        graphProps.set(AnimationPropertiesKeys.FILL_PROPERTY, Color.WHITE);
        graphProps.set(AnimationPropertiesKeys.EDGECOLOR_PROPERTY, Color.BLACK);
        graphProps.set(AnimationPropertiesKeys.ELEMHIGHLIGHT_PROPERTY, Color.BLACK);
        graphProps.set(AnimationPropertiesKeys.HIGHLIGHTCOLOR_PROPERTY, Color.GREEN);
        graphProps.set(AnimationPropertiesKeys.NODECOLOR_PROPERTY, Color.BLACK);
        graphProps.set(AnimationPropertiesKeys.WEIGHTED_PROPERTY, false);

        g = lang.newGraph("bn", adjacencyMatrix, nodes, new String[]{Y, A, X, B}, null, graphProps);
    }

    public void highlightNode(String node, Color color) {

        g.setNodeHighlightFillColor(node2int(node), color, null, null);
        g.highlightNode(node2int(node), null, null);
    }

    public void unhighlightNode(String node) {

        g.unhighlightNode(node2int(node), null, null);
    }

    private int node2int(String node) {
        switch (node) {
            case A: return 1;
            case B: return 3;
            case X: return 2;
            case Y: return 0;
            default: return -1;
        }
    }

    public String[] parents(String var) {

        switch (var) {
            case A: return new String[]{Y};
            case B: return new String[]{A,X};
            case X: return new String[]{Y};
            case Y: return new String[]{};
            default: return new String[]{};
        }
    }

    public String[] children(String var) {

        switch (var) {
            case A: return new String[]{B};
            case B: return new String[]{};
            case X: return new String[]{B};
            case Y: return new String[]{A,X};
            default: return new String[]{};
        }
    }

    public String key(final String var) { return key(var, new String[]{}); }
    public String key(final String var, final String... evidence) {

        StringBuilder sb = new StringBuilder();
        sb.append("P(");
        sb.append(var);

        if(evidence.length > 0) {
            sb.append(" | ");
        }

        Arrays.sort(evidence);

        for(int i = 0; i < evidence.length; i++) {

            if(i != 0) sb.append(", ");

            sb.append(evidence[i]);
            sb.append("=");
            sb.append(values.get(evidence[i]));
        }

        sb.append(")");

        return sb.toString();
    }
}
