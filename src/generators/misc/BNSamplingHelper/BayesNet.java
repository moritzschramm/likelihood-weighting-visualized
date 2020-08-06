package generators.misc.BNSamplingHelper;

import algoanim.primitives.Graph;
import algoanim.primitives.generators.Language;
import algoanim.properties.AnimationPropertiesKeys;
import algoanim.properties.GraphProperties;
import algoanim.util.Coordinates;
import algoanim.util.Node;
import generators.framework.properties.AnimationPropertiesContainer;

import java.awt.*;
import java.util.Arrays;
import java.util.Hashtable;

public class BayesNet {

    private Language lang;

    // graph and properties
    private Graph graph;
    private GraphProperties props;
    private int[][] adjacencyMatrix;
    public static Color HIGHLIGHT_COLOR;
    public static Color SELECT_COLOR;
    public static Color TRUE_COLOR;
    public static Color FALSE_COLOR;

    // variables
    private String [] vars;
    private String [] sampleVars;

    // hashtables, containing probabilities and values of random variables
    public Hashtable<String, Double> probabilities;
    public Hashtable<String, Boolean> values;


    public BayesNet(Language lang) {

        this.lang = lang;
        this.values = new Hashtable<>();
        this.probabilities = new Hashtable<>();
    }

    public void init(Hashtable<String, Object> primitives, GraphProperties props, String[] vars, String[] samplesVars) {

        this.graph = (Graph) primitives.get("graph");
        this.props = props;

        this.vars = vars;
        this.sampleVars = samplesVars;
        this.adjacencyMatrix = graph.getAdjacencyMatrix();

        for(String var: vars) {
            values.put(var, false);
        }

        // init colors
        HIGHLIGHT_COLOR = (Color) primitives.get("Highlight Color");
        SELECT_COLOR = (Color) primitives.get("Select Color");
        TRUE_COLOR = (Color) primitives.get("True Color");
        FALSE_COLOR = (Color) primitives.get("False Color");

        // init values and probabilities
        String[] v = (String[]) primitives.get("Values");
        int id = 0;
        for(String var: vars) {
            if(Arrays.asList(sampleVars).contains(var)) continue;
            boolean value = v[id].equals("true");
            id++;
            values.put(var, value);
        }

        int[][] p = (int[][]) primitives.get("Probabilities");

        for(int i = 0; i < vars.length; i++) {
            String var = int2node(i);
            String[] parents = parents(var);
            Hashtable<String, Boolean> values;
            switch(parents.length) {
                case 0:
                    probabilities.put(key(var), p[0][i] / 100.0);
                    break;

                case 1:
                    values = new Hashtable<>();
                    values.put(parents[0], true);
                    probabilities.put(key(var, null, values, parents), p[0][i] / 100.0);
                    values.put(parents[0], false);
                    probabilities.put(key(var, null, values, parents), p[1][i] / 100.0);
                    break;

                case 2:
                    values = new Hashtable<>();
                    values.put(parents[0], true);
                    values.put(parents[1], true);
                    probabilities.put(key(var, null, values, parents), p[0][i] / 100.0);
                    values.put(parents[0], true);
                    values.put(parents[1], false);
                    probabilities.put(key(var, null, values, parents), p[1][i] / 100.0);
                    values.put(parents[0], false);
                    values.put(parents[1], true);
                    probabilities.put(key(var, null, values, parents), p[2][i] / 100.0);
                    values.put(parents[0], false);
                    values.put(parents[1], false);
                    probabilities.put(key(var, null, values, parents), p[3][i] / 100.0);
                    break;

                default: // case 3
                    values = new Hashtable<>();
                    values.put(parents[0], true);
                    values.put(parents[1], true);
                    values.put(parents[2], true);
                    probabilities.put(key(var, null, values, parents), p[0][i] / 100.0);
                    values.put(parents[0], true);
                    values.put(parents[1], true);
                    values.put(parents[2], false);
                    probabilities.put(key(var, null, values, parents), p[1][i] / 100.0);
                    values.put(parents[0], true);
                    values.put(parents[1], false);
                    values.put(parents[2], true);
                    probabilities.put(key(var, null, values, parents), p[2][i] / 100.0);
                    values.put(parents[0], true);
                    values.put(parents[1], false);
                    values.put(parents[2], false);
                    probabilities.put(key(var, null, values, parents), p[3][i] / 100.0);

                    values.put(parents[0], false);
                    values.put(parents[1], true);
                    values.put(parents[2], true);
                    probabilities.put(key(var, null, values, parents), p[4][i] / 100.0);
                    values.put(parents[0], false);
                    values.put(parents[1], true);
                    values.put(parents[2], false);
                    probabilities.put(key(var, null, values, parents), p[5][i] / 100.0);
                    values.put(parents[0], false);
                    values.put(parents[1], false);
                    values.put(parents[2], true);
                    probabilities.put(key(var, null, values, parents), p[6][i] / 100.0);
                    values.put(parents[0], false);
                    values.put(parents[1], false);
                    values.put(parents[2], false);
                    probabilities.put(key(var, null, values, parents), p[7][i] / 100.0);
                    break;
            }
        }
    }

    public void add() {

        int size = graph.getSize();
        Node[] nodes = new Node[size];
        String[] nodeLabels = new String[size];
        for (int i = 0; i < size; i++) {
            nodes[i] = graph.getNode(i);
            nodeLabels[i] = graph.getNodeLabel(i);
        }

        graph = lang.newGraph("bn", graph.getAdjacencyMatrix(), nodes, nodeLabels, graph.getDisplayOptions(), props);

        // highlight evidence vars
        for(String evidence: vars) {
            if(Arrays.asList(sampleVars).contains(evidence)) continue;
            highlightNode(evidence, values.get(evidence) ? TRUE_COLOR : FALSE_COLOR);
        }
    }

    public void highlightNode(String node, Color color) {

        graph.setNodeHighlightFillColor(node2int(node), color, null, null);
        graph.highlightNode(node2int(node), null, null);
    }

    public void unhighlightNode(String node) {

        graph.unhighlightNode(node2int(node), null, null);
    }

    public int node2int(String node) {
        for(int i = 0; i < vars.length; i++) {
            if(vars[i].equals(node)) return i;
        }

        return -1;
    }

    public String int2node(int id) {

        return vars[id];
    }

    public String[] parents(String var) {

        int id = node2int(var);
        String[] tmp = new String[vars.length];
        int count = 0;

        for(int i = 0; i < adjacencyMatrix.length; i++) {
            if(adjacencyMatrix[i][id] != 0) {
                tmp[count] = int2node(i);
                count++;
            }
        }
        String[] parents = new String[count];
        for(int k = 0; k < count; k++) parents[k] = tmp[k];

        return parents;
    }

    public String[] children(String var) {

        int id = node2int(var);
        String[] tmp = new String[vars.length];
        int count = 0;

        for(int i = 0; i < adjacencyMatrix[id].length; i++) {
            if(adjacencyMatrix[id][i] != 0) {
                tmp[count] = int2node(i);
                count++;
            }
        }
        String[] children = new String[count];
        for(int k = 0; k < count; k++) children[k] = tmp[k];

        return children;
    }

    public String key(final String var) { return key(var, null, new String[]{}); }
    public String key(final String var, final String... evidence) { return key(var, null, evidence); }
    public String key(final String var, final Boolean value, final String... evidence) {
        return key(var, value, null, evidence);
    }
    public String key(final String var, final Boolean value, Hashtable<String, Boolean> values, final String... evidence) {

        StringBuilder sb = new StringBuilder();
        sb.append("P(");
        sb.append(var);

        if(value != null) {
            sb.append("=");
            sb.append(value);
        }

        if(evidence.length > 0) {
            sb.append(" | ");
        }

        Arrays.sort(evidence);

        if(values == null) values = this.values;

        for(int i = 0; i < evidence.length; i++) {

            if(i != 0) sb.append(", ");

            sb.append(evidence[i]);
            sb.append("=");
            sb.append(values.get(evidence[i]) == null ? false : values.get(evidence[i]));
        }

        sb.append(")");

        return sb.toString();
    }
}
