import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

import static java.awt.Color.RED;

public class GG8680 extends JFrame{
    public static void main(String[] args){
        new GG8680();
    }

    private JPanel buttonPanel = new JPanel();
    private DrawingPanel drawingPanel = new DrawingPanel();

    private JRadioButton addVertex = new JRadioButton("Add Vertex");
    private JRadioButton addEdge = new JRadioButton("Add Edge");
    private JRadioButton removeVertex = new JRadioButton("Remove Vertex");
    private JRadioButton removeEdge = new JRadioButton("Remove Edge");
    private JRadioButton moveVertex = new JRadioButton("Move Vertex");

    private JButton addAllEdges = new JButton("Add All Edges");
    private JButton connectedComponents = new JButton("Connected Components");
    private JButton showCutVertices = new JButton("Show Cut Vertices");
    private JButton help = new JButton("Help");

    private ButtonGroup group = new ButtonGroup();

    private Listen listen = new Listen();


    private GG8680(){
        addButtons();
        buttonListeners();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

        setLayout(new BorderLayout());
        add(buttonPanel, BorderLayout.WEST);
        add(drawingPanel, BorderLayout.CENTER);
        Mouse m = new Mouse();
        drawingPanel.addMouseListener(m);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1000, 1000);
        setVisible(true);
    }

    private void addButtons(){
        group.add(addVertex);
        group.add(addEdge);
        group.add(removeVertex);
        group.add(removeEdge);
        group.add(moveVertex);

        buttonPanel.add(addVertex);
        buttonPanel.add(addEdge);
        buttonPanel.add(removeVertex);
        buttonPanel.add(removeEdge);
        buttonPanel.add(moveVertex);

        buttonPanel.add(addAllEdges);
        buttonPanel.add(connectedComponents);
        buttonPanel.add(showCutVertices);
        buttonPanel.add(help);
    }

    private class Listen implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if(e.getSource() == addAllEdges){
                addAllEdges();
                releaseRB();
            }else if(e.getSource() == connectedComponents){
                drawingPanel.connectedComponents();
                drawingPanel.repaint();
                releaseRB();
            }else if(e.getSource() == showCutVertices){
                drawingPanel.cutVertices();
                drawingPanel.resetEdgeColors();
                repaint();
                releaseRB();
            }else if(e.getSource() == help){
                releaseRB();
                helpWindow();
            }
        }
        private void addAllEdges(){
            if(drawingPanel.dotList.size() <= 1){
                return;
            }
            for(int i = drawingPanel.dotList.size() - 1; i >= 0; i--){
                for(int j = drawingPanel.dotList.size() - 1; j >= 0; j--) {
                    Edge newEdge = new Edge(drawingPanel.dotList.get(i), drawingPanel.dotList.get(j));
                    newEdge.setColor(Color.BLUE);
                    if(i == j) continue;
                    if (drawingPanel.edgeExists(newEdge)) continue;
                    drawingPanel.addEdge(newEdge);
                }
            }
            drawingPanel.repaint();
        }
        //Method to release all radio buttons
        private void releaseRB(){
            group.clearSelection();
        }
        //Method to open a small window to show how to use it.
        private void helpWindow(){
            JFrame jFrame = new JFrame("Help");
            JPanel panel = new JPanel();
            String instructions =
                    "Add Vertex: Press this then press the screen anywhere to place a vertex on the graph\n"
                            + "Add edge: Press this then any two vertices to connect them\n"
                            + "Remove Vertex: Press this to remove a vertex (This will also remove any edge connected to this vertex.\n"
                            + "Remove Edge: Press this to then remove the desired edge in the graph.\n"
                            + "More Vertex: Press this to move a vertex point to another spot on the graph.\n"
                            + "Add All edges: A shortcut that addDot in all possible edges between pairs of vertices.\n"
                            + "Connected Components: Makes the gui show the different components of the graph in different colors\n"
                            + "Show cut Vertices: Makes the gui highlight all cut vertices of the graph\n";
            TextArea txt = new TextArea(instructions);
            panel.add(txt);
            jFrame.setSize(600, 250);
            jFrame.getContentPane().add(panel);
            jFrame.setVisible(true);
        }

    }

    private void buttonListeners(){
        addAllEdges.addActionListener(listen);
        connectedComponents.addActionListener(listen);
        showCutVertices.addActionListener(listen);
        help.addActionListener(listen);
    }

    private class Mouse extends MouseAdapter{
        @Override
        public void mouseClicked(MouseEvent e) {
            Dot newDot = new Dot(e.getX(), e.getY());

            if(addVertex.isSelected()){
                if(!drawingPanel.isClose(newDot)) {
                    drawingPanel.addDot(newDot);
                    drawingPanel.repaint();
                }
            }else if(addEdge.isSelected()){
                Dot temp = drawingPanel.findDot(newDot);
                if(temp != null && !drawingPanel.isWorkingOnEdge()){
                    disableForAddEdge();
                    drawingPanel.setWorkingOnEdge(true);
                    drawingPanel.setRecent(temp);
                    temp.setColor(Color.GREEN);
                    drawingPanel.repaint();
                    return;
                }
                if(drawingPanel.getRecent() != null) {
                    drawingPanel.getRecent().setColor(Color.RED);
                }
                if(temp == null) return;
                drawingPanel.addEdge(drawingPanel.getRecent(), temp);
                drawingPanel.setRecent(null);
                drawingPanel.setWorkingOnEdge(false);
                drawingPanel.repaint();
                enableForAddEdge();
            }else if(removeVertex.isSelected()){
                Dot temp = drawingPanel.findDot(newDot);
                if(temp != null){
                    drawingPanel.checkForEdges(temp);
                    drawingPanel.removeDot(temp);
                    drawingPanel.resetDotColors();
                    drawingPanel.repaint();
                }
            }else if(removeEdge.isSelected()){
                drawingPanel.destroyEdge(newDot);
                drawingPanel.repaint();
            }else if(moveVertex.isSelected()){
                Dot temp = drawingPanel.findDot(newDot);
                if(drawingPanel.getRecent() != null){
                    if(!drawingPanel.isClose(newDot)) {
                        drawingPanel.getRecent().setColor(Color.RED);
                        drawingPanel.newLocation(drawingPanel.getRecent(), newDot);
                        drawingPanel.repaint();
                        drawingPanel.setRecent(null);
                        enableForMoveRB();
                    }
                }else{
                    drawingPanel.setRecent(temp);
                    Objects.requireNonNull(temp).setColor(Color.GREEN);
                    drawingPanel.repaint();
                    disableForMoveRB();
                }
            }
        }

        private void disableForMoveRB(){
            addVertex.setEnabled(false);
            addEdge.setEnabled(false);
            removeVertex.setEnabled(false);
            removeEdge.setEnabled(false);
        }

        private void enableForMoveRB(){
            addVertex.setEnabled(true);
            addEdge.setEnabled(true);
            removeVertex.setEnabled(true);
            removeEdge.setEnabled(true);
        }

        private void disableForAddEdge(){
            addVertex.setEnabled(false);
            removeVertex.setEnabled(false);
            removeEdge.setEnabled(false);
            moveVertex.setEnabled(false);
        }

        private void enableForAddEdge(){
            addVertex.setEnabled(true);
            removeVertex.setEnabled(true);
            removeEdge.setEnabled(true);
            moveVertex.setEnabled(true);
        }
    }

    private class DrawingPanel extends JPanel{
        private ArrayList<Dot> dotList;
        private ArrayList<Edge> edgeList;
        private ArrayList<Edge> shape;
        private ArrayList<Edge> holdEdges;
        private boolean workingOnEdge = false;
        private Dot recent = null;
        private Random rand = new Random();
        int total = 0;

        private DrawingPanel(){
            dotList = new ArrayList<>();
            edgeList = new ArrayList<>();
            shape = new ArrayList<>();
            holdEdges = new ArrayList<>();
        }

        @Override
        public void paint(Graphics g){
            super.paint(g);
            for(Dot dot : dotList){
                dot.draw(g);
            }
            for(Edge edge : edgeList){
                edge.drawEdge(g);
            }

        }

        private void addDot(Dot d){
            dotList.add(d);
        }

        private void removeDot(Dot d){
            dotList.remove(d);
        }
        //Might cause an error check in the future.
        private void addEdge(Dot start, Dot end){
            Edge newEdge = new Edge(start, end);
            if(!edgeExists(newEdge) && isClose(start) && isClose(end)){
                newEdge.setColor(Color.BLUE);
                edgeList.add(newEdge);
            }
        }

        private void addEdge(Edge e){
            edgeList.add(e);
        }

        private void newLocation(Dot existingDot, Dot newDot){
            existingDot.setX((int)newDot.getX());
            existingDot.setY((int)newDot.getY());
        }

        private Dot findDot(Dot dot){
            for(Dot d : dotList){
                if(inVicinity(d, dot)){
                    return d;
                }
            }
            return null;
        }

        private Edge findEdge(Dot d1, Dot d2){
            for(Edge e : edgeList){
                if((inVicinity(e.getFirst(), d1) && inVicinity(e.getSecond(), d2)
                        || (inVicinity(e.getSecond(), d2) && inVicinity(e.getFirst(), d1)))){
                    return e;
                }
            }
            return null;
        }

        private boolean isClose(Dot dot){
            for(Dot d : dotList){
                if(inVicinity(d, dot)){
                    return true;
                }
            }
            return false;
        }

        private boolean inVicinity(Dot existingPoint, Dot newPoint){
            double leftX = existingPoint.getX() - 25;
            double rightX = existingPoint.getX() + 25;
            double upY = existingPoint.getY() - 25;
            double downY = existingPoint.getY() + 25;
            if(leftX < 0){
                leftX = 0;
            }
            if(upY < 0){
                upY = 0;
            }
            return newPoint.getX() <= rightX && newPoint.getX() >= leftX && newPoint.getY() <= downY && newPoint.getY() >= upY;
        }

        private boolean edgeExists(Edge e){
            for(Edge edge : edgeList){
                if((inVicinity(edge.getFirst(), e.getFirst()) && inVicinity(edge.getSecond(), e.getSecond()))
                        || (inVicinity(edge.getFirst(), e.getSecond()) && inVicinity(edge.getSecond(), e.getFirst()))){
                    return true;
                }
            }
            return false;
        }

        private void destroyEdge(Dot d){
            for(Edge e : edgeList){
                if(removeEdge(e, d)){
                    return;
                }
            }
        }

        private boolean removeEdge(Edge e, Dot d){
            double xValues = (e.getSecond().getX() - e.getFirst().getX());
            if(xValues == 0) xValues = 1;
            double m = (e.getSecond().getY() - e.getFirst().getY()) / xValues;
            double x = e.getFirst().getX();
            double b = e.getFirst().getY() - (m * x);
            if(e.getFirst().getX() >= e.getSecond().getX() && e.getFirst().getY() <= e.getSecond().getY()
                    || e.getFirst().getX() >= e.getSecond().getX() && e.getFirst().getY() >= e.getSecond().getY()){
                for(; x>= e.getSecond().getX(); x--){
                    double y = (m * x) + b;
                    Dot temp = new Dot(x, y);
                    if(inVicinity(temp, d)){
                        edgeList.remove(e);
                        return true;
                    }
                }
            }
            for(; x <= e.getSecond().getX(); x++){
                double y = (m * x) + b;
                Dot temp = new Dot(x, y);
                if(inVicinity(temp, d)){
                    edgeList.remove(e);
                    return true;
                }
            }
            return false;
        }

        private void checkForEdges(Dot d){
            ArrayList<Edge> temp = new ArrayList<>();
            for(int i = edgeList.size() - 1; i >= 0; i--){
                if(inVicinity(edgeList.get(i).getFirst(), d) || inVicinity(edgeList.get(i).getSecond(), d)){
                    temp.add(edgeList.get(i));
                }
            }

            int x = temp.size() - 1;
            for(int i = x; i >= 0; i--){
                edgeList.remove(temp.get(i));
            }
        }

        private void checkForEdgesForCutVertex(Dot d){
            ArrayList<Edge> temp = new ArrayList<>();
            for(int i = edgeList.size() - 1; i >= 0; i--){
                if(inVicinity(edgeList.get(i).getFirst(), d) || inVicinity(edgeList.get(i).getSecond(), d)){
                    temp.add(edgeList.get(i));
                    holdEdges.add(edgeList.get(i));
                }
            }

            int x = temp.size() - 1;
            for(int i = x; i >= 0; i--){
                edgeList.remove(temp.get(i));
            }
        }

        private void connectedComponents(){
            shape.clear();
            resetVisitedDotsAndEdges();
            int q = 0;
            for(Dot d : dotList){
                if(!d.isVisited()){
                    recAllDots(d, shape);
                    shape.add(null);
                }
            }
            Color color = generateColor();
            for(int i = 0; i < shape.size() - 1; i++){
                if(shape.get(i) != null){
                    shape.get(i).setColor(color);
                }else{
                    color = generateColor();
                }
            }
        }

        private void resetVisitedDotsAndEdges(){
            for(Dot dot : dotList){
                dot.setVisited(false);
            }
            for(Edge edge : edgeList){
                edge.setVisited(false);
            }
        }

        private Color generateColor(){
            float a = rand.nextFloat();
            float b = rand.nextFloat();
            float c = rand.nextFloat();
            return new Color(a, b, c);
        }

        private void recAllDots(Dot d, ArrayList<Edge> shape){
            if(d.isVisited()) return;
            ArrayList<Dot> nei = new ArrayList<>();
            d.setVisited(true);
            findNeighbors(d, nei);
            if(nei.size() == 0){
                shape.add(new Edge(d, d));
                return;
            }
            for(Dot n : nei){
                if(n != null){
                    Edge newEdge = findEdge(d, n);
                    if(newEdge != null && !newEdge.isVisited()){
                        newEdge.setVisited(true);
                        shape.add(newEdge);
                    }
                }
                if(n != null) {
                    recAllDots(n, shape);
                }
            }
        }

        private void findNeighbors(Dot d, ArrayList<Dot> values){
            for(Edge e : edgeList){
                if(inVicinity(d, e.getFirst())){
                    values.add(e.getSecond());
                }
                if(inVicinity(d, e.getSecond())) {
                    values.add(e.getFirst());
                }
            }
        }

        private int numComp(){
            this.total = 0;
            for(int i = shape.size() - 1; i >= 0; i--){
                if(shape.get(i) == null){
                    total++;
                }
            }
            return total;
        }

        private void cutVertices(){
            connectedComponents();
            int oldNumComp = numComp();
            resetVisitedDotsAndEdges();
            int s = dotList.size();
            for(int i = s - 1; i >= 0; i--){
                double x = dotList.get(i).getX();
                double y = dotList.get(i).getY();
                checkForEdgesForCutVertex(dotList.get(i));
                removeDot(dotList.get(i));
                connectedComponents();
                int newNumCom = numComp();
                resetVisitedDotsAndEdges();
                Dot newDot = new Dot(x, y);
                if(newNumCom > oldNumComp){
                    newDot.setColor(Color.GREEN);
                }
                dotList.add(newDot);
                recreateEdges(newDot);
                holdEdges.clear();
            }
        }

        private void resetEdgeColors(){
            for(Edge e : edgeList){
                e.setColor(Color.BLUE);
            }
        }

        private void resetDotColors(){
            for(Dot d : dotList){
                d.setColor(Color.RED);
            }
        }

        private void recreateEdges(Dot dot){
            for(Edge e : holdEdges){
                if(inVicinity(e.getFirst(), dot)){
                    edgeList.add(new Edge(dot, e.getSecond()));
                }else if(inVicinity(e.getSecond(), dot)){
                    edgeList.add(new Edge(dot, e.getFirst()));
                }
            }
        }

        private Dot getRecent() {
            return recent;
        }

        private void setRecent(Dot recent) {
            this.recent = recent;
        }

        private boolean isWorkingOnEdge() {
            return workingOnEdge;
        }

        private void setWorkingOnEdge(boolean workingOnEdge) {
            this.workingOnEdge = workingOnEdge;
        }
    }

    private class Dot{
        private double x;
        private double y;
        private Color color = RED;
        private boolean visited;

        private Dot(double x, double y) {
            this.x = x;
            this.y = y;
            visited = false;
        }

        private void draw(Graphics g){
            g.setColor(color);
            g.fillOval((int)x, (int)y, 10, 10);
        }

        private void setX(int x) {
            this.x = x;
        }

        private void setY(int y) {
            this.y = y;
        }

        private double getX() {
            return x;
        }

        private double getY() {
            return y;
        }

        private void setColor(Color color) {
            this.color = color;
        }

        private boolean isVisited() {
            return visited;
        }

        private void setVisited(boolean visited) {
            this.visited = visited;
        }
    }

    private class Edge{
        private Dot first;
        private Dot second;
        private Color color;
        private boolean visited;

        private Edge(Dot start, Dot end){
            this.first = start;
            this.second = end;
        }

        private void drawEdge(Graphics g){
            Graphics2D g2 = (Graphics2D) g;
            g2.setStroke(new BasicStroke(2));
            g.setColor(color);
            g.drawLine((int)first.getX(), (int)first.getY(), (int)second.getX(), (int)second.getY());
        }

        private Dot getFirst() {
            return first;
        }

        private Dot getSecond() {
            return second;
        }

        private void setColor(Color c){
            color = c;
        }

        private boolean isVisited() {
            return visited;
        }

        private void setVisited(boolean visited) {
            this.visited = visited;
        }
    }
    //This is correct
}
