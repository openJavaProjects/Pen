import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import javax.imageio.ImageIO;

/**
 * Pen v2.1
 * Developed by Shadow Technologies Software SLU (2023 - 2025)
 * Fully functional Open Source Paint Clone
 */
public class Pen extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final String VERSION = "2.1";

    private CanvasArea canvas;
    private String currentTool = "Pen";
    private Color currentColor = Color.BLACK;
    private int brushSize = 4;
    private JToolBar toolBar;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Pen().setVisible(true));
    }

    public Pen() {
        setTitle("Pen " + VERSION + " - Shadow Technologies Software SLU");
        setSize(1300, 900);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                confirmExit();
            }
        });

        initializeMainWindow();
    }

    private void initializeMainWindow() {
        canvas = new CanvasArea();
        setLayout(new BorderLayout());
        add(canvas, BorderLayout.CENTER);

        toolBar = createToolBar();
        add(toolBar, BorderLayout.NORTH);

        setJMenuBar(createMenuBar());

        setCustomCursor("Pen");
    }

    // =========================
    // Menu Bar
    // =========================
    private JMenuBar createMenuBar() {
        JMenuBar bar = new JMenuBar();

        JMenu file = new JMenu("File");
        JMenuItem newItem = new JMenuItem("New");
        JMenuItem openItem = new JMenuItem("Open Image");
        JMenuItem saveItem = new JMenuItem("Save");
        JMenuItem exitItem = new JMenuItem("Exit");

        newItem.addActionListener(e -> canvas.clearCanvas());
        openItem.addActionListener(e -> importImage());
        saveItem.addActionListener(e -> saveImage());
        exitItem.addActionListener(e -> confirmExit());

        file.add(newItem);
        file.add(openItem);
        file.add(saveItem);
        file.addSeparator();
        file.add(exitItem);

        JMenu edit = new JMenu("Edit");
        JMenuItem undoItem = new JMenuItem("Undo");
        JMenuItem redoItem = new JMenuItem("Redo");
        JMenuItem clearItem = new JMenuItem("Clear All");
        JMenuItem eraseItem = new JMenuItem("Eraser Tool");

        undoItem.addActionListener(e -> canvas.undo());
        redoItem.addActionListener(e -> canvas.redo());
        clearItem.addActionListener(e -> canvas.clearCanvas());
        eraseItem.addActionListener(e -> selectTool("Eraser"));

        edit.add(undoItem);
        edit.add(redoItem);
        edit.add(clearItem);
        edit.add(eraseItem);

        JMenu view = new JMenu("View");
        JCheckBoxMenuItem gridItem = new JCheckBoxMenuItem("Show Grid");
        gridItem.addActionListener(e -> { canvas.showGrid = gridItem.isSelected(); canvas.repaint(); });
        view.add(gridItem);

        JMenu help = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Pen v2.1\nDeveloped by Shadow Technologies Software SLU (2023-2025)\nEpic Open Source Paint Clone",
                "About Pen", JOptionPane.INFORMATION_MESSAGE));
        help.add(aboutItem);

        bar.add(file);
        bar.add(edit);
        bar.add(view);
        bar.add(help);

        return bar;
    }

    // =========================
    // Toolbar
    // =========================
    private JToolBar createToolBar() {
        JToolBar bar = new JToolBar();
        bar.setFloatable(false);

        String[] tools = {"Pen","Thin Pen","Thick Pen","Dashed Pen","Spray","Highlighter",
                "Eraser","Line","Rectangle","Circle","Selector","Text","Insert Image"};

        for(String t:tools){
            JButton btn = new JButton(t);
            btn.setPreferredSize(new Dimension(100,50));
            btn.setIcon(generateIcon(t));
            btn.setToolTipText(t);
            btn.addActionListener(e -> {
                if(t.equals("Insert Image")) importImage();
                else selectTool(t);
            });
            bar.add(btn);
        }

        bar.addSeparator();

        JButton colorBtn = new JButton("Color");
        colorBtn.addActionListener(e -> {
            Color c = JColorChooser.showDialog(this,"Select Color",currentColor);
            if(c!=null) currentColor=c;
        });
        bar.add(colorBtn);

        JLabel sizeLabel = new JLabel("Size");
        bar.add(sizeLabel);

        JSlider sizeSlider = new JSlider(1,50,brushSize);
        sizeSlider.setPreferredSize(new Dimension(120,30));
        sizeSlider.addChangeListener((ChangeEvent e) -> brushSize=sizeSlider.getValue());
        bar.add(sizeSlider);

        return bar;
    }

    private void selectTool(String t){
        currentTool=t;
        setCustomCursor(t);
        updateToolbarColors();
    }

    private void updateToolbarColors() {
        for(Component comp:toolBar.getComponents()){
            if(comp instanceof JButton){
                JButton b = (JButton) comp;
                if(currentTool.equals(b.getToolTipText())) b.setBackground(Color.GREEN);
                else b.setBackground(null);
            }
        }
    }

    private Icon generateIcon(String tool){
        BufferedImage img = new BufferedImage(32,32,BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setColor(Color.BLACK);
        switch(tool){
            case "Pen": g2.drawLine(4,28,28,4); break;
            case "Thin Pen": g2.drawLine(16,28,16,4); break;
            case "Thick Pen": g2.setStroke(new BasicStroke(5)); g2.drawLine(4,28,28,4); break;
            case "Dashed Pen": g2.setStroke(new BasicStroke(2,BasicStroke.CAP_BUTT,BasicStroke.JOIN_MITER,10,new float[]{5,5},0)); g2.drawLine(4,28,28,4); break;
            case "Spray": for(int i=0;i<20;i++){ int x=4+new Random().nextInt(24); int y=4+new Random().nextInt(24); g2.fillOval(x,y,1,1); } break;
            case "Highlighter": g2.setColor(new Color(255,255,0,150)); g2.fillRect(4,4,24,24); break;
            case "Eraser": g2.setColor(Color.WHITE); g2.fillRect(4,4,24,24); g2.setColor(Color.BLACK); g2.drawRect(4,4,24,24); break;
            case "Line": g2.drawLine(4,28,28,4); break;
            case "Rectangle": g2.drawRect(4,4,24,24); break;
            case "Circle": g2.drawOval(4,4,24,24); break;
            case "Selector": g2.drawRect(4,4,24,24); g2.drawLine(4,4,28,28); g2.drawLine(28,4,4,28); break;
            case "Text": g2.setFont(new Font("SansSerif",Font.BOLD,16)); g2.drawString("T",8,24); break;
            case "Insert Image": g2.drawRect(4,4,24,24); g2.drawLine(4,4,28,28); g2.drawLine(4,28,28,4); break;
        }
        g2.dispose();
        return new ImageIcon(img);
    }

    private void setCustomCursor(String tool){
        BufferedImage img = new BufferedImage(16,16,BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setColor(Color.BLACK);
        switch(tool){
            case "Pen": g2.drawLine(0,15,15,0); break;
            case "Eraser": g2.fillRect(0,0,16,16); break;
            case "Selector": g2.drawLine(8,0,8,16); g2.drawLine(0,8,16,8); break;
            case "Text": g2.drawString("T",2,14); break;
            default: g2.drawLine(0,15,15,0);
        }
        g2.dispose();
        Cursor c = Toolkit.getDefaultToolkit().createCustomCursor(img,new Point(0,0),tool);
        canvas.setCursor(c);
    }

    private void confirmExit(){
        int res = JOptionPane.showConfirmDialog(this,"Do you want to exit? Make sure you have saved your image because you cannot undone on restarting the program.","Exit",JOptionPane.YES_NO_OPTION);
        if(res==JOptionPane.YES_OPTION) System.exit(0);
    }

    private void importImage(){
        try{
            JFileChooser chooser = new JFileChooser();
            if(chooser.showOpenDialog(this)==JFileChooser.APPROVE_OPTION){
                File file = chooser.getSelectedFile();
                BufferedImage img = ImageIO.read(file);
                canvas.setBackgroundImage(img);
            }
        }catch(Exception e){ JOptionPane.showMessageDialog(this,"Error importing image","Error",JOptionPane.ERROR_MESSAGE);}
    }

    private void saveImage(){
        try{
            BufferedImage img = canvas.exportImage();
            JFileChooser chooser = new JFileChooser();
            if(chooser.showSaveDialog(this)==JFileChooser.APPROVE_OPTION){
                File file=chooser.getSelectedFile();
                if(!file.getName().toLowerCase().endsWith(".png")) file=new File(file.getAbsolutePath()+".png");
                ImageIO.write(img,"png",file);
                JOptionPane.showMessageDialog(this,"Saved: "+file.getName());
            }
        }catch(Exception e){ JOptionPane.showMessageDialog(this,"Error saving image","Error",JOptionPane.ERROR_MESSAGE);}
    }

    // =====================
    // CanvasArea Implementation
    // =====================
    class CanvasArea extends JPanel {
        private static final long serialVersionUID = 1L;
		private java.util.List<ShapeRecord> shapes = new ArrayList<>();
        private Point startPoint=null;
        private ShapeRecord selectedShape=null;
        private BufferedImage backgroundImage=null;
        boolean showGrid=false;

        CanvasArea(){
            setBackground(Color.WHITE);

            addMouseListener(new MouseAdapter(){
                public void mousePressed(MouseEvent e){
                    if(currentTool.equals("Selector")){
                        for(ShapeRecord s:shapes){
                            if(s.contains(e.getPoint())){
                                selectedShape=s;
                                s.offsetStart=e.getPoint();
                                return;
                            }
                        }
                    } else if(currentTool.equals("Text")){
                        startPoint=e.getPoint();
                        ShapeRecord textShape = new ShapeRecord("Text",startPoint,startPoint,currentColor,brushSize);
                        shapes.add(textShape);
                        editTextShape(textShape);
                        repaint();
                    } else startPoint=e.getPoint();
                }
                public void mouseReleased(MouseEvent e){
                    if(!currentTool.equals("Selector") && startPoint!=null && !currentTool.equals("Text")){
                        shapes.add(new ShapeRecord(currentTool,startPoint,e.getPoint(),currentColor,brushSize));
                        startPoint=null;
                        repaint();
                    }
                    if(currentTool.equals("Selector")) selectedShape=null;
                }
            });

            addMouseMotionListener(new MouseMotionAdapter(){
                public void mouseDragged(MouseEvent e){
                    if(currentTool.equals("Selector") && selectedShape!=null && selectedShape.offsetStart!=null){
                        int dx = e.getX()-selectedShape.offsetStart.x;
                        int dy = e.getY()-selectedShape.offsetStart.y;
                        selectedShape.move(dx,dy);
                        selectedShape.offsetStart = e.getPoint();
                        repaint();
                    } else if(startPoint!=null && !currentTool.equals("Text")){
                        shapes.add(new ShapeRecord(currentTool,startPoint,e.getPoint(),currentColor,brushSize));
                        startPoint=e.getPoint();
                        repaint();
                    }
                }
            });
        }

        void clearCanvas(){ shapes.clear(); backgroundImage=null; repaint(); }
        void setBackgroundImage(BufferedImage img){ backgroundImage=img; repaint(); }
        BufferedImage exportImage(){ BufferedImage img = new BufferedImage(getWidth(),getHeight(),BufferedImage.TYPE_INT_ARGB); paint(img.getGraphics()); return img; }
        void undo(){ if(!shapes.isEmpty()){ shapes.remove(shapes.size()-1); repaint();} }
        void redo(){ /* Optional redo implementation */ }

        public void paintComponent(Graphics g){
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            if(backgroundImage!=null) g2.drawImage(backgroundImage,0,0,getWidth(),getHeight(),null);
            if(showGrid){
                g2.setColor(Color.LIGHT_GRAY);
                int step=20;
                for(int x=0;x<getWidth();x+=step) g2.drawLine(x,0,x,getHeight());
                for(int y=0;y<getHeight();y+=step) g2.drawLine(0,y,getWidth(),y);
            }
            for(ShapeRecord s:shapes) s.draw(g2);
        }
    }

    // =====================
    // ShapeRecord Implementation
    // =====================
    class ShapeRecord{
        String type;
        Point p1,p2;
        Color color;
        int size;
        Point offsetStart=null;
        String text="Text";
        Font font = new Font("SansSerif",Font.PLAIN,16);
        Random random = new Random();

        ShapeRecord(String t, Point a, Point b, Color c, int s){
            type=t; p1=new Point(a); p2=new Point(b); color=c; size=s;
        }

        boolean contains(Point p){
            return new Rectangle(Math.min(p1.x,p2.x),Math.min(p1.y,p2.y),
                    Math.abs(p2.x-p1.x),Math.abs(p2.y-p1.y)).contains(p);
        }

        void move(int dx,int dy){ p1.translate(dx,dy); p2.translate(dx,dy); }

        void draw(Graphics g){
            Graphics2D g2=(Graphics2D) g;
            g2.setStroke(new BasicStroke(size));
            g2.setColor(color);
            switch(type){
                case "Pen": g2.drawLine(p1.x,p1.y,p2.x,p2.y); break;
                case "Thin Pen": g2.setStroke(new BasicStroke(1)); g2.drawLine(p1.x,p1.y,p2.x,p2.y); break;
                case "Thick Pen": g2.setStroke(new BasicStroke(8)); g2.drawLine(p1.x,p1.y,p2.x,p2.y); break;
                case "Dashed Pen": float[] dash={4f,4f}; g2.setStroke(new BasicStroke(size,BasicStroke.CAP_BUTT,BasicStroke.JOIN_MITER,10f,dash,0f)); g2.drawLine(p1.x,p1.y,p2.x,p2.y); break;
                case "Spray": for(int i=0;i<20;i++){ int x=p1.x+random.nextInt(size)-size/2; int y=p1.y+random.nextInt(size)-size/2; g2.fillOval(x,y,1,1);} break;
                case "Highlighter": g2.setColor(new Color(color.getRed(),color.getGreen(),color.getBlue(),150)); g2.fillRect(Math.min(p1.x,p2.x),Math.min(p1.y,p2.y),Math.abs(p2.x-p1.x),Math.abs(p2.y-p1.y)); break;
                case "Eraser": g2.setColor(Color.WHITE); g2.fillRect(p1.x,p1.y,size,size); break;
                case "Line": g2.drawLine(p1.x,p1.y,p2.x,p2.y); break;
                case "Rectangle": g2.drawRect(Math.min(p1.x,p2.x),Math.min(p1.y,p2.y),Math.abs(p2.x-p1.x),Math.abs(p2.y-p1.y)); break;
                case "Circle": int d=Math.max(Math.abs(p2.x-p1.x),Math.abs(p2.y-p1.y)); g2.drawOval(Math.min(p1.x,p2.x),Math.min(p1.y,p2.y),d,d); break;
                case "Text": g2.setFont(font); g2.drawString(text,p1.x,p1.y); break;
            }
        }
    }

    private void editTextShape(ShapeRecord textShape) {
        JPanel panel = new JPanel(new GridLayout(3,2));
        JTextField tf = new JTextField(textShape.text != null ? textShape.text : "Text");
        JButton colorBtn = new JButton("Color");
        colorBtn.addActionListener(e -> {
            Color c = JColorChooser.showDialog(this, "Select Text Color", textShape.color);
            if(c!=null) textShape.color = c;
        });
        String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        JComboBox<String> fontBox = new JComboBox<>(fonts);
        fontBox.setSelectedItem(textShape.font != null ? textShape.font.getFamily() : "SansSerif");

        panel.add(new JLabel("Text:")); panel.add(tf);
        panel.add(new JLabel("Font:")); panel.add(fontBox);
        panel.add(colorBtn);

        int res = JOptionPane.showConfirmDialog(this, panel, "Edit Text", JOptionPane.OK_CANCEL_OPTION);
        if(res == JOptionPane.OK_OPTION){
            textShape.text = tf.getText();
            textShape.font = new Font((String)fontBox.getSelectedItem(), Font.PLAIN, textShape.font != null ? textShape.font.getSize() : 16);
        }
    }
}

