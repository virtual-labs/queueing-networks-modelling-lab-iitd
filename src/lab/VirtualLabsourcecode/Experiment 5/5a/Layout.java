
import java.util.LinkedList;
import java.util.Random;
import java.awt.image.BufferedImage;
import java.awt.*;
import java.awt.Point;
import java.applet.*;
import java.awt.event.*;
import java.awt.event.ActionListener;

class Job {                                  //Job containing each jobs Arrival and Departure Time


    int size;
    double ArrivalTime;
    double WaitingTime;

    Job(int s, double t) {
        size = s;
        ArrivalTime = t;
    }
}

class Queue {                                //Queue containing List of Jobs


    LinkedList<Job> l = new LinkedList();
}

class Server {                               //Server containing each Server's Current Status


    boolean Occupied = false;
    int capacity;
    double ServiceTime;
    LinkedList<Job> CurrJobs = new LinkedList();

    Server(int c) {
        capacity = c;
    }
}

class Servers {                              //Servers containing an array of Servers


    int num;
    Server[] ArrayofServer;

    Servers(int n, int c) {
        num = n;
        ArrayofServer = new Server[num];
        for (int i = 0; i < num; i++) {
            ArrayofServer[i] = new Server(c);
        }
    }

    public int isFree() {
        int count = 0;
        for (int i = 0; i < num; i++) {
            if (ArrayofServer[i].Occupied == false) {
                count++;
            }
        }
        return count;
    }
}

class Event {

    int Type;
    Job j;
    int ServerNo;
    double time;
}


public class Layout extends Applet implements ActionListener, ItemListener, Runnable {

    //Input Parameters
    static double lambda;
    static double mu;
    static double arrPara1,  arrPara2;
    static double depPara1,  depPara2;
    static String arrDist;
    static String depDist;
    static int BatchSize=1;
    static int PartialBatch =0;
    static int QueuingDisc;
    static int SystemCapacity;
    static int NumberofServers;
    static int OrbitSize;
    static double alphaRetrial;
    //Internal Constansts
    static final int Arrival = 0;
    static final int Departure = 1;
    static final int Retrial = 2;
    static final int LIFO = 0;
    static final int FIFO = 1;
    static final int RAND = 2;
    //RunTime Measures
    double MeanNoCustomersSys;
    double MeanNoCustomersQue;
    double MeanWaitingTimeQue;
    double MeanResponseTime;
    double ResponseTime;
    double Utilisation;
    double Throughput;
    double BlockingProbability;
    double MeanWaiting;
    double summ1, summ2, summ3;
    int CustomersServed;
    int CustomersEntered;


    //temporary variables
    double left;
    int a = 100;
    double rho = 0;
    int NoSteadyState;
    int infiServers;
    // for error
    int etype;
    int x;
    int b;
    int d;
    //   Steady State Measures
    static String SteadyMeanNoCustomersSys;
    static String SteadyMeanNoCustomersQue;
    static String SteadyMeanWaitingTimeQue;
    static String SteadyMeanResponseTime;
    static String SteadyThroughput;
    static String SteadyBlockingProbability;
    static String SteadyMeanWaiting;
    static String SteadyUtilisation;
    //Runtime Variables
    double time = 0.0;
    static int SystemSize = 0;
    Boolean playing = false, started = false;
    Thread t;

    public static double GenerateArrival(double l) {
        double time = 0.0;
        if (arrDist.equals("M")) {
            time = -Math.log(Math.random()) / l;
        } else if (arrDist.equals("Uniform")) {
            time = contDistributions.Uniform(arrPara1, arrPara2);
        } else if (arrDist.equals("Gaussian")) {
            time = contDistributions.Gaussian(arrPara1, arrPara2);
        } else if (arrDist.equals("Deterministic")) {
            time = contDistributions.Deterministic(arrPara1);
        } else if (arrDist.equals("Weibull")) {
            time = contDistributions.Weibull(arrPara1, arrPara2);
        } else if (arrDist.equals("Pereto")) {
            time = contDistributions.Pereto(arrPara1, arrPara2);
        } else if (arrDist.equals("Gamma")) {
            time = contDistributions.Gamma(arrPara1, arrPara2);
        } else if (arrDist.equals("Erlang")) {
            time = contDistributions.Erlang((int)(arrPara1), arrPara2);
        } else if (arrDist.equals("Lognormal")) {
            time = contDistributions.LogNormal(arrPara1, arrPara2);
        }
        return time;
    }

    public static double GenerateDeparture(double l) {
        double time = 0.0;
        if (depDist.equals("M")) {
            time = -Math.log(Math.random()) / l;
        } else if (depDist.equals("Uniform")) {
            time = contDistributions.Uniform(depPara1, depPara2);
        } else if (depDist.equals("Gaussian")) {
            time = contDistributions.Gaussian(depPara1, depPara2);
        } else if (depDist.equals("Deterministic")) {
            time = contDistributions.Deterministic(depPara1);
        } else if (depDist.equals("Weibull")) {
            time = contDistributions.Weibull(depPara1, depPara2);
        } else if (depDist.equals("Pereto")) {
            time = contDistributions.Pereto(depPara1, depPara2);
        } else if (depDist.equals("Gamma")) {
            time = contDistributions.Gamma(depPara1, depPara2);
        } else if (depDist.equals("Erlang")) {
            time = contDistributions.Erlang((int)(depPara1), depPara2);
        } else if (depDist.equals("Lognormal")) {
            time = contDistributions.LogNormal(depPara1, depPara2);
        }
        return time;
    }

    public static double GenerateWaiting(double l) {
        double time = -Math.log(Math.random()) / l;
        return time;
    }

    public int GetNumberofArrivals() {    
        return 1;
    }

    //awt Components
    protected int canvasWidth = 700,  canvasHeight = 600;
    protected Image buffImage = new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_ARGB);
    protected SimulationCanvas canvass;
    Graphics g = buffImage.getGraphics();
    Label  label2,  label4, label6, label7, label9,label12, label13,label15;
   
    Button startButton;
    Button resetButton;
    Choice ArrivalDist;
    Choice DepartureDist;
    TextField arrDistPara1;
    TextField arrDistPara2;
    TextField depDistPara1;
    TextField depDistPara2;
    TextField arrBulkPara1;
    TextField arrBulkPara2;
    TextField depBulkSize;
    TextField servers;
    TextField systemCapacity;
    
    
    

   
    CheckboxGroup ArrivalType;
    Checkbox radioA1;
    Checkbox radioA2;
    CheckboxGroup DepartureType;
    Checkbox radioD1;
    Checkbox radioD2;
    CheckboxGroup serviceType;
    Checkbox radio3;
    Checkbox radio4;
    Checkbox radio5;
    
    
   
    Color GridColor = Color.BLACK;
    Color BackgroundColor = Color.WHITE;
    Color PathColor = Color.BLUE;
    Font font1 = new Font("Arial", Font.PLAIN, 12);
    Font font2 = new Font("Arial", Font.BOLD, 12);
    Font font3 = new Font("Times New Roman", Font.PLAIN, 20);
    Font font4 = new Font("Arial", Font.BOLD, 20);
    Font font5 = new Font("CASTELLER", Font.BOLD, 28);
    Font font6 = new Font("Arial", Font.ITALIC, 14);
    int MaxTime, sizeInterval, MaxSize;
    double timeInterval;
    double scale;
    double yscale;

    public void PaintInitialGrid(Graphics g) {
        g.setColor(BackgroundColor);
        g.fillRect(1, 1, 700, 309);
        g.setColor(GridColor);
        g.drawLine(50, 270, 650, 270);
        g.drawLine(50, 270, 50, 100);
        g.setColor(Color.RED);
        g.drawLine(50, 100, 650, 100);

        g.setColor(GridColor);
        g.setFont(font1);
        double maxRepresentableTime = (time < MaxTime) ? MaxTime : time;
        scale = time < MaxTime ? 1 : MaxTime / time;
        timeInterval = (time < MaxTime ? 2 : 2 * (time / 20));
        for (int i = 0; i <= (int) maxRepresentableTime; i += timeInterval) {
            int xPos = (int) (i * 600 / maxRepresentableTime) + 50;
            g.drawLine(xPos, 270, xPos, 278);
            g.drawString(String.valueOf(i), xPos - 7, 290);
        }
        g.setFont(font2);
        g.drawString("Time [units]", 330, 305);
        g.drawString("System Size", 20, 70);

        if (SystemSize > MaxSize) {

            MaxSize = MaxSize + 10;
            yscale = (double) 10 / (double) MaxSize;
        }

        double maxRepresentableSize = MaxSize;
        sizeInterval = (int) MaxSize / 5;
        for (int i = 0; i <= (int) maxRepresentableSize; i += sizeInterval) {
            int yPos = 270 - (int) (i * 170 / maxRepresentableSize);
            g.drawLine(42, yPos, 50, yPos);
            g.drawString(String.valueOf(i), 30, yPos);
        }
        g.setColor(Color.WHITE);
        g.fillRect(412, 313, 108, 35);
        g.setColor(Color.BLACK);
        g.setFont(font6);
        g.drawString("(till t=" + (int) (time + 1) + ")", 440, 344);
        g.drawString("(Theoretical)", 537, 344);
        g.setColor(Color.DARK_GRAY);
        g.setFont(font3);
        g.drawString("Run Time", 430, 330);
        g.drawString("Steady State", 535, 330);

        g.setColor(Color.WHITE);
        g.fillRect(412, 351, 107, 200);
        g.setColor(Color.GRAY);
        g.setFont(font3);
        g.drawString("" + MeanNoCustomersSys, 450, 370);
        g.drawString("" + MeanNoCustomersQue, 450, 400);
        g.drawString("" + MeanWaiting, 450, 430);
        g.drawString("" + MeanResponseTime, 450, 460);
        g.drawString("" + Utilisation, 450, 490);
        g.drawString("" + Throughput, 450, 520);
        g.drawString("" + BlockingProbability, 450, 550);
        g.drawString("Customers Entered = " + CustomersEntered, 120, 50);
        g.drawString("Customers Served = " + CustomersServed, 400, 50);

        g.drawString("No. Of Customers In System " + SystemSize, 200, 95);


        canvass.repaint();

    }

    public void Join(Graphics g, Point p1, Point p2) {
        g.setColor(PathColor);

        g.drawLine((int) ((p1.x - 50) * scale + 50), (int) (270 - (270 - p1.y) * yscale), (int) ((p2.x - 50) * scale + 50), (int) (270 - (270 - p2.y) * yscale));
    }

    public void DrawPath(Graphics g, LinkedList<Point> l) {
        for (int i = 0; i < l.size() - 1; i++) {
            Join(g, l.get(i), l.get(i + 1));
        }
    }

    
    public void HandleError(int e) {


        g.setColor(Color.RED);
        Font ErrorFont = new Font("Arial", Font.BOLD, 22);
        g.setFont(ErrorFont);

        if (e == 11) {
            g.drawString("*Error In Input In Arrival Data", a, b + d * x);
            x++;
        }
        if (e == 12) {
            g.drawString("*Inputs Should Be Positive", a, b + d * x);
            x++;
        }
        if (e == 81) {
            g.drawString("*Error In Input In Arrival Parameter 1", a, b + d * x);
            x++;
        }
        if (e == 82) {
            g.drawString("Arrival Parameter 1 Should Be Positive", a, b + d * x);
            x++;
        }
        if (e == 91) {
            g.drawString("*Error In Input In Arrival Parameter 2", a, b + d * x);
            x++;
        }
        if (e == 92) {
            g.drawString("Arrival Parameter 2 Should Be Positive", a, b + d * x);
            x++;
        }
        if (e == 21) {
            g.drawString("*Error in Input In Departure", a, b + d * x);
            x++;
        }
        if (e == 22) {
            g.drawString("*Inputs Should Be Positive", a, b + d * x);
            x++;
        }
        if (e == 31) {
            g.drawString("*Error In Input in Batch Size", a, b + d * x);
            x++;
        }
        if (e == 32) {
            g.drawString("*Batch Size Should Be Positive", a, b + d * x);
            x++;
        }
        if (e == 41) {
            g.drawString("*Error In Input in System Capacity", a, b + d * x);
            x++;
        }
        if (e == 42) {
            g.drawString("*System Capacity Should Be Positive", a, b + d * x);
            x++;
        }
        if (e == 51) {
            g.drawString("*Error In Input in Number of Servers", a, b + d * x);
            x++;
        }
        if (e == 52) {
            g.drawString("*Number of Servers Should Be Positive", a, b + d * x);
            x++;
        }
        if (e == 61) {
            g.drawString("*Error In Input in Orbit Size", a, b + d * x);
            x++;
        }
        if (e == 62) {
            g.drawString("*Orbit Size Should Be Positive", a, b + d * x);
            x++;
        }
        if (e == 71) {
            g.drawString("*Error In input in Retrail Parameter", a, b + d * x);
            x++;
        }
        if (e == 72) {
            g.drawString("*Retrial Parameter Should Be Positive", a, b + d * x);
            x++;
        }

        if (e == 101) {
            g.drawString("*System Capacity Can't be less than number of Servers", a, b + d * x);
            x++;
        }
        canvass.repaint();
    }
    
    
    
    public void init() {
        this.setLayout(new BorderLayout(5, 5));
        this.setBackground(Color.WHITE);

        canvass = new SimulationCanvas(buffImage);
        canvass.setPreferredSize(new Dimension(canvasWidth, canvasHeight));
        canvass.setBackground(Color.DARK_GRAY);
        this.add("Center", canvass);


        startButton = new Button("Start");
        startButton.addActionListener(this);
        resetButton = new Button("Reset");
        resetButton.addActionListener(this);
        resetButton.enable(false);

       
        ArrivalDist = new Choice();
        ArrivalDist.addItem("Uniform");
        ArrivalDist.addItem("Gaussian");
        ArrivalDist.addItem("Deterministic");
        ArrivalDist.addItem("Weibull");
        ArrivalDist.addItem("Pereto");
        ArrivalDist.addItem("Gamma");
        ArrivalDist.addItem("Erlang");
        ArrivalDist.addItem("Lognormal");
        ArrivalDist.addItemListener(this);
        ArrivalDist.enable(false);
        DepartureDist = new Choice();
        DepartureDist.addItem("Uniform");
        DepartureDist.addItem("Gaussian");
        DepartureDist.addItem("Deterministic");
        DepartureDist.addItem("Weibull");
        DepartureDist.addItem("Pereto");
        DepartureDist.addItem("Gamma");
        DepartureDist.addItem("Erlang");
        DepartureDist.addItem("Lognormal");
        DepartureDist.addItemListener(this);
        DepartureDist.enable(true);
        arrDistPara1 = new TextField("", 5);
        arrDistPara1.enable(true);
        arrDistPara2 = new TextField("", 5);
        arrDistPara2.enable(false);
        depDistPara1 = new TextField("", 5);
        depDistPara1.enable(true);
        depDistPara2 = new TextField("", 5);
        depDistPara2.enable(true);
        
        label12 = new Label();
        label12.setText("Arrival Distribution :");
        label12.enable(true);
        

        label2 = new Label();
        label2.setText("Parameters :");
        label2.enable(false);
        arrBulkPara1 = new TextField("", 5);
        arrBulkPara1.enable(false);
     
        arrBulkPara2 = new TextField("", 5);
        arrBulkPara2.enable(false);

        label4 = new Label();
        label4.setText("Departure Distribution :");
        
       
        depBulkSize = new TextField("", 5);
        depBulkSize.enable(false);

        label6 = new Label();
        label6.setText("Number of Servers :");
        servers = new TextField("1", 5);
        servers.enable(false);
        label7 = new Label();
        label7.setText("Capacity of the System :");
        systemCapacity = new TextField("", 5);
        systemCapacity.enable(true);

        
      

        label13 = new Label();
        label13.setText("Parameters :");
        label13.enable(true);
        label15 = new Label();
        label15.setText("Parameters :");
        label15.enable(true);

        ArrivalType = new CheckboxGroup();
        radioA1 = new Checkbox("M", ArrivalType, true);
        radioA2 = new Checkbox("G", ArrivalType, false);
        radioA1.enable(true);
        radioA2.enable(true);
        radioA1.addItemListener(this);
        radioA2.addItemListener(this);

        DepartureType = new CheckboxGroup();
        radioD1 = new Checkbox("M", DepartureType, false);
        radioD2 = new Checkbox("G", DepartureType, true);
        radioD1.enable(false);
        radioD2.enable(true);
        radioD1.addItemListener(this);
        radioD2.addItemListener(this);


        label9 = new Label();
        label9.setText("Queueing Discipline :");
        serviceType = new CheckboxGroup();
        radio3 = new Checkbox("FIFO", serviceType, true);
        radio4 = new Checkbox("LIFO", serviceType, false);
        radio5 = new Checkbox("Random", serviceType, false);

        
        
     
        

        Panel p1 = new Panel();
        p1.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 0, 5, 0);

        c.gridx = 1;
        c.gridy = 0;
        p1.add(startButton, c);
        c.gridx = 2;
        p1.add(resetButton, c);

        c.gridx = 1;
        c.gridy = 1;
        p1.add(radioA1, c);
        c.gridy = 2;
        p1.add(radioA2, c);

        c.gridx = 0;
        c.gridy = 3;
        
        c.gridx = 1;
        p1.add(ArrivalDist, c);

        c.gridx =0;
        c.gridy = 4;
        c.anchor = c.EAST;
        p1.add(label13,c);
        c.anchor = c.CENTER;
        c.gridx = 1;
        p1.add(arrDistPara1,c);
        c.gridx = 2;
        p1.add(arrDistPara2,c);

        c.gridx=0;
        c.gridy=3;
        p1.add(label12,c);
        
        
        

        c.gridx = 1;
        c.gridy = 7;
        p1.add(radioD1, c);
        c.gridy = 8;
        p1.add(radioD2, c);

        c.gridx = 0;
        c.gridy = 9;
        p1.add(label4, c);
        c.gridx = 1;
        p1.add(DepartureDist, c);

        c.gridx =0;
        c.gridy = 10;
        c.anchor = c.EAST;
        p1.add(label15,c);
        c.anchor = c.CENTER;
        c.gridx = 1;
        p1.add(depDistPara1,c);
        c.gridx = 2;
        p1.add(depDistPara2,c);

        

        c.anchor = c.WEST;
        c.gridx = 1;
        c.gridy = 12;
       
        c.anchor = c.EAST;
        c.gridx = 1;
     
        c.anchor = c.CENTER;
        c.gridx = 2;
  
        c.gridx = 0;
        c.gridy = 13;
        p1.add(label6, c);
        c.gridx = 1;
        p1.add(servers, c);
        c.gridx = 0;
        c.gridy = 14;
        p1.add(label7, c);
        c.gridx = 1;
        p1.add(systemCapacity, c);
        c.gridx = 0;
        c.gridy = 15;
        p1.add(label9, c);
        c.anchor = c.WEST;
        c.gridx = 1;
        p1.add(radio3, c);
        c.anchor = c.EAST;
        c.gridx = 1;
        p1.add(radio4, c);
        c.anchor = c.EAST;
        c.gridx = 2;
        p1.add(radio5, c);
        c.anchor = c.CENTER;
        
       
      
       
        this.add("East", p1);
        canvass.repaint();
    }

    private static double truncate(double x) {
        long y = (long) (x * 1000);
        return (double) y / 1000;
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == startButton) {
            if (startButton.getLabel().equals("Start")) {
                if (started == false) {
                    x = 0;
                    b = 350;
                    d = 25;
                    try {
                        if(radioA1.getState()) arrDist="M";
                            else arrDist=ArrivalDist.getSelectedItem();
                        if(radioD1.getState()) depDist="M";
                            else depDist=DepartureDist.getSelectedItem();
                        if(radioA1.getState()){
                            lambda = Double.parseDouble(arrDistPara1.getText());

                        }
                        if (radioA2.getState()) {
                            arrPara1 = Double.parseDouble(arrDistPara1.getText());
                            if(!ArrivalDist.getSelectedItem().equals("Deterministic"))
                            arrPara2 = Double.parseDouble(arrDistPara2.getText());
                            else arrPara2=1;
                            lambda =1;
                        }
                        if(radioD1.getState()){
                            mu = Double.parseDouble(depDistPara1.getText());
                        }
                        if (radioD2.getState()) {
                            depPara1 = Double.parseDouble(depDistPara1.getText());
                            if(!DepartureDist.getSelectedItem().equals("Deterministic"))
                                depPara2 = Double.parseDouble(depDistPara2.getText());
                            else depPara2 = 1;
                            mu=1;
                        }
                    } catch (NumberFormatException ee) {
                        etype = 11;
                        HandleError(etype);
                        lambda = 1;
                    }
                    if (lambda <= 0 && radioA1.getState()) {
                        etype = 12;
                        HandleError(etype);
                    }
                    if(radioA1.getState()){
                    radioD1.enable(false);
                    }
                    if(radioD1.getState()){
                    radioA1.enable(false);
                    }

                    
                    try {
                        mu = Double.parseDouble(depDistPara1.getText());
                    } catch (NumberFormatException ee) {
                        etype = 21;
                        HandleError(etype);
                        mu = 1;
                    }
                    if (mu <= 0) {
                        etype = 22;
                        HandleError(etype);
                    }


                     
                       
                    

                    try {
                        String temp = systemCapacity.getText();
                        if (!temp.equalsIgnoreCase("INF")) {
                            SystemCapacity = Integer.parseInt(temp);
                        } else {
                            SystemCapacity = Integer.MAX_VALUE;
                        }
                    } catch (NumberFormatException ee) {
                        etype = 41;
                        HandleError(etype);
                        SystemCapacity = 1;
                    }
                    if (SystemCapacity <= 0) {
                        etype = 42;
                        HandleError(etype);
                    }

                    try {
                        String temp = servers.getText();
                        if (!temp.equalsIgnoreCase("INF")) {
                            NumberofServers = Integer.parseInt(temp);
                            infiServers = 0;
                        } else {
                            infiServers = 1;
                            NumberofServers = 1000;
                        }
                    } catch (NumberFormatException ee) {
                        etype = 51;
                        HandleError(etype);
                        NumberofServers = 1;
                    }
                    if (NumberofServers <= 0) {
                        etype = 52;
                        HandleError(etype);
                    }

                    left = SystemCapacity - NumberofServers;

                    if (radio3.getState()) {
                        QueuingDisc = FIFO;
                    } else if (radio4.getState()) {
                        QueuingDisc = LIFO;
                    } else if (radio5.getState()) {
                        QueuingDisc = RAND;
                    }

                     

                    arrDistPara1.enable(false);
                    arrDistPara2.enable(false);
                    depDistPara1.enable(false);
                    depDistPara2.enable(false);
                    label13.enable(false);
                    label15.enable(false);
                    radioA1.enable(false);
                    radioA2.enable(false);
                    radioD1.enable(false);
                    radioD2.enable(false);
                   
                    ArrivalDist.enable(false);
                    arrBulkPara1.enable(false);
                    arrBulkPara2.enable(false);
                    DepartureDist.enable(false);
                    depBulkSize.enable(false);
                    servers.enable(false);
                    systemCapacity.enable(false);
                    
                    radio3.enable(false);
                    radio4.enable(false);
                    radio5.enable(false);
                   
                   
                    label2.enable(false);
                   
                    label4.enable(false);
                   
                    label6.enable(false);
                    label7.enable(false);
                    
                    label9.enable(false);
                   
                    label12.enable(false);



                    if (SystemCapacity < NumberofServers && SystemCapacity != Integer.MAX_VALUE) {
                        etype = 101;
                        HandleError(etype);
                    }
                    resetButton.enable(true);
                    canvass.repaint();
                    if (!(etype == 0)) {
                        g.drawString("CLICK ON RESET BUTTON AND CORRECT INPUTS", 10, b + d * x);
                    } else {
                        t = new Thread(this);
                        started = true;
                        t.start();
                        startButton.setLabel("Pause");
                        playing = true;
                        canvass.repaint();
                    }
                }
                playing = true;
                startButton.setLabel("Pause");

                canvass.repaint();
            } else if (startButton.getLabel().equals("Pause")) {
                playing = false;

                startButton.setLabel("Start");
                canvass.repaint();
            }
        }
        if (e.getSource() == resetButton) {
            if (t != null) {
                t.stop();
                t = null;
            }
            systemCapacity.enable(true);
            radioA1.enable(true);
            radioA2.enable(true);
            radioD1.enable(true);
            radioD2.enable(true);
            if(radioA1.getState()) {
                arrDistPara1.enable(true);
            }
            if (radioA2.getState()) {
                ArrivalDist.enable(true);
                if (ArrivalDist.getSelectedItem().equals("Uniform")) {
                    arrDistPara1.enable(true);
                    arrDistPara2.enable(true);
                }
                if (ArrivalDist.getSelectedItem().equals("Gaussian")) {
                    arrDistPara1.enable(true);
                    arrDistPara2.enable(true);
                }
                if (ArrivalDist.getSelectedItem().equals("Deterministic")) {
                    arrDistPara1.enable(true);
                    arrDistPara2.enable(false);
                }
                if (ArrivalDist.getSelectedItem().equals("Weibull")) {
                    arrDistPara1.enable(true);
                    arrDistPara2.enable(true);
                }
                if (ArrivalDist.getSelectedItem().equals("Pereto")) {
                    arrDistPara1.enable(true);
                    arrDistPara2.enable(true);
                }
                if (ArrivalDist.getSelectedItem().equals("Gamma")) {
                    arrDistPara1.enable(true);
                    arrDistPara2.enable(true);
                }
                if (ArrivalDist.getSelectedItem().equals("Erlang")) {
                    arrDistPara1.enable(true);
                    arrDistPara2.enable(true);
                }
                if (ArrivalDist.getSelectedItem().equals("Lognormal")) {
                    arrDistPara1.enable(true);
                    arrDistPara2.enable(true);
                }
            }
            if(radioD1.getState()) {
                depDistPara1.enable(true);
            }
            if (radioD2.getState()) {
                DepartureDist.enable(true);
                if (DepartureDist.getSelectedItem().equals("Uniform")) {
                    depDistPara1.enable(true);
                    depDistPara2.enable(true);
                }
                if (DepartureDist.getSelectedItem().equals("Gaussian")) {
                    depDistPara1.enable(true);
                    depDistPara2.enable(true);
                }
                if (DepartureDist.getSelectedItem().equals("Deterministic")) {
                    depDistPara1.enable(true);
                    depDistPara2.enable(false);
                }
                if (DepartureDist.getSelectedItem().equals("Weibull")) {
                    depDistPara1.enable(true);
                    depDistPara2.enable(true);
                }
                if (DepartureDist.getSelectedItem().equals("Pereto")) {
                    depDistPara1.enable(true);
                    depDistPara2.enable(true);
                }
                if (DepartureDist.getSelectedItem().equals("Gamma")) {
                    depDistPara1.enable(true);
                    depDistPara2.enable(true);
                }
                if (DepartureDist.getSelectedItem().equals("Erlang")) {
                    depDistPara1.enable(true);
                    depDistPara2.enable(true);
                }
                if (DepartureDist.getSelectedItem().equals("Lognormal")) {
                    depDistPara1.enable(true);
                    depDistPara2.enable(true);
                }
            }
            

            

            DepartureDist.enable(true);
           
           
           

           
           
            label13.enable(true);
            label15.enable(true);
            label12.enable(true);
            label4.enable(true);
            label6.enable(true);
            label7.enable(true);
            label9.enable(true);
            radio3.enable(true);
            radio4.enable(true);
            radio5.enable(true);
           

            started = false;
            playing = false;
            resetButton.enable(false);

            time = 0.0;
            SystemSize = 0;
            etype = 0;
            startButton.setLabel("Start");
            g.setColor(Color.DARK_GRAY);
            g.fillRect(1, 1, 10000, 1000);
            canvass.repaint();
        }
    }

    /*public void CalculateSteadyStatemeasures() {

    if (OrbitSize == 0 && BatchSize == 1 && !bulkArrival.getState()) {

    //     M/M/1/inf

    if (SystemCapacity == Integer.MAX_VALUE && NumberofServers == 1) {
    if (lambda >= mu) {
    NoSteadyState = 1;
    } else {
    rho = lambda / mu;
    SteadyMeanNoCustomersSys = Double.toString(truncate(rho / (1 - rho)));
    SteadyMeanNoCustomersQue = Double.toString(truncate(rho * rho / (1 - rho)));
    SteadyMeanWaitingTimeQue = Double.toString(truncate(rho / (mu - lambda)));
    SteadyUtilisation = Double.toString(truncate(rho));
    SteadyMeanResponseTime = Double.toString(truncate(1 / (mu - lambda)));
    SteadyBlockingProbability = Double.toString(0.000);
    SteadyThroughput = Double.toString(truncate(lambda));

    }
    } //           M/M/1/k
    else if (!(SystemCapacity == Integer.MAX_VALUE) && NumberofServers == 1) {

    rho = lambda / mu;
    double Ls, Pb;
    double lambdaEff = lambda * (1 - ((1 - rho) * Math.pow(rho, SystemCapacity) / (1 - Math.pow(rho, SystemCapacity + 1))));
    if (rho == 1) {
    Ls = SystemCapacity / 2;
    Pb = 1 / (SystemCapacity + 1);
    } else {
    Ls = (rho / (1 - rho)) - ((SystemCapacity + 1) * (Math.pow(rho, SystemCapacity + 1)) / (1 - Math.pow(rho, SystemCapacity + 1)));
    Pb = ((1 - rho) * (Math.pow(rho, SystemCapacity)) / (1 - Math.pow(rho, SystemCapacity + 1)));
    }
    SteadyMeanNoCustomersSys = Double.toString(truncate(Ls));
    double Lq = Ls - lambdaEff / mu;
    SteadyMeanNoCustomersQue = Double.toString(truncate(Lq));
    SteadyMeanWaitingTimeQue = Double.toString(truncate(Lq / lambdaEff));
    SteadyUtilisation = Double.toString(truncate(rho));
    SteadyBlockingProbability = Double.toString(truncate(Pb));
    SteadyThroughput = Double.toString(truncate(lambda * (1 - Pb)));
    SteadyMeanResponseTime = Double.toString(truncate(Ls / lambdaEff));
    } //    M/M/c/inf
    else if ((SystemCapacity == Integer.MAX_VALUE) && !(NumberofServers == 1) && infiServers != 1) {
    if (lambda <= NumberofServers * mu) {
    double Po, temp = 0;

    rho = lambda / (NumberofServers * mu);
    double r = lambda / mu;




    for (int i = 0; i <= NumberofServers - 1; i++) {
    temp += (Math.pow(r, i) / factorial(i));

    }
    Po = 1 / (temp + (Math.pow(r, NumberofServers) / (factorial(NumberofServers) * (1 - rho))));
    double Lq = Math.pow(r, NumberofServers) * rho * Po / (factorial(NumberofServers) * (1 - rho) * (1 - rho));
    SteadyMeanNoCustomersSys = Double.toString(truncate(r + Lq));
    SteadyMeanNoCustomersQue = Double.toString(truncate(Lq));
    SteadyMeanWaitingTimeQue = Double.toString(truncate(Lq / (mu * NumberofServers * rho)));
    SteadyBlockingProbability = Double.toString(0.000);
    SteadyMeanResponseTime = Double.toString(truncate(1 / mu + Lq / (mu * NumberofServers * rho)));
    SteadyThroughput = Double.toString(truncate(lambda));
    SteadyUtilisation = Double.toString(truncate(rho));
    } else {
    NoSteadyState = 1;
    }
    } //    M/M/c/k
    else if (!(SystemCapacity == Integer.MAX_VALUE) && !(infiServers == 1) && (NumberofServers != SystemCapacity)) {
    rho = lambda / (mu * NumberofServers);
    double r = lambda / mu;
    double Po, temp = 0, Pk;
    for (int ii = 0; ii <= NumberofServers - 1; ii++) {
    temp += Math.pow(r, ii) / factorial(ii);
    }
    if (rho != 1) {
    Po = 1 / (temp + (Math.pow(r, NumberofServers) * (1 - Math.pow(rho, left + 1)) / (factorial(NumberofServers) * (1 - rho))));
    } else {
    Po = 1 / (temp + ((left + 1) * (1 - rho) / (factorial(NumberofServers))));
    }

    Pk = Po * (Math.pow(r, SystemCapacity)) / (factorial(NumberofServers) * Math.pow(NumberofServers, left));
    double Lq = Po * Math.pow(r, NumberofServers) * rho * (1 - Math.pow(rho, left + 1) - (1 - rho) * (left + 1) * Math.pow(rho, left)) / (factorial(NumberofServers) * (1 - rho) * (1 - rho));
    SteadyMeanNoCustomersQue = Double.toString(truncate(Lq));
    SteadyMeanNoCustomersSys = Double.toString(truncate(Lq + r * (1 - Pk)));
    SteadyMeanWaitingTimeQue = Double.toString(truncate(Lq / (lambda * (1 - Pk))));
    SteadyUtilisation = Double.toString(truncate(rho));
    double Pb = Po * Math.pow(r, SystemCapacity) / (factorial(NumberofServers) * Math.pow(NumberofServers, left));
    SteadyBlockingProbability = Double.toString(truncate(Pb));

    SteadyMeanResponseTime = Double.toString(truncate((Lq + r * (1 - Pk)) / (lambda * (1 - Pk))));
    SteadyThroughput = Double.toString(truncate(lambda * (1 - Pk)));
    } //     M/M/c/c
    else if (NumberofServers == SystemCapacity) {
    rho = lambda / (mu * SystemCapacity);

    double r = lambda / rho;
    double temp = 0;
    for (int iii = 0; iii <= SystemCapacity; iii++) {
    temp += Math.pow(r, iii) / factorial(iii);
    }
    double Pb = Math.pow(r, SystemCapacity) / (factorial(SystemCapacity) * temp);
    SteadyBlockingProbability = Double.toString(truncate(Pb));
    SteadyThroughput = Double.toString(truncate(lambda * (1 - Pb)));


    } else if (infiServers == 1 && SystemCapacity == Integer.MAX_VALUE) {
    rho = lambda / mu;
    SteadyMeanNoCustomersSys = Double.toString(truncate(rho));
    SteadyMeanNoCustomersQue = Double.toString(truncate(0.000));
    SteadyThroughput = Double.toString(truncate(lambda));
    SteadyMeanResponseTime = Double.toString(truncate(1 / mu));
    SteadyUtilisation = Double.toString(truncate(rho));
    SteadyBlockingProbability = Double.toString(truncate(0.000));
    SteadyMeanWaitingTimeQue = Double.toString(truncate(0.000));

    }
    }



    }*/

    int factorial(int a) {
        int fact = 1;
        for (int i = 1; i <= a; i++) {
            fact = fact * i;

        }
        return fact;
    }

    public void itemStateChanged(ItemEvent e) {
        if (e.getSource() == ArrivalDist) {
            if (ArrivalDist.getSelectedItem().equals("Uniform")) {
                arrDistPara1.enable(true);
                arrDistPara2.enable(true);
            }
            if (ArrivalDist.getSelectedItem().equals("Gaussian")) {
                arrDistPara1.enable(true);
                arrDistPara2.enable(true);
            }
            if (ArrivalDist.getSelectedItem().equals("Deterministic")) {
                arrDistPara1.enable(true);
                arrDistPara2.enable(false);
            }
            if (ArrivalDist.getSelectedItem().equals("Weibull")) {
                arrDistPara1.enable(true);
                arrDistPara2.enable(true);
            }
            if (ArrivalDist.getSelectedItem().equals("Pereto")) {
                arrDistPara1.enable(true);
                arrDistPara2.enable(true);
            }
            if (ArrivalDist.getSelectedItem().equals("Gamma")) {
                arrDistPara1.enable(true);
                arrDistPara2.enable(true);
            }
            if (ArrivalDist.getSelectedItem().equals("Erlang")) {
                arrDistPara1.enable(true);
                arrDistPara2.enable(true);
            }
            if (ArrivalDist.getSelectedItem().equals("Lognormal")) {
                arrDistPara1.enable(true);
                arrDistPara2.enable(true);
            }
        }
        if (e.getSource() == DepartureDist) {
            if (DepartureDist.getSelectedItem().equals("Uniform")) {
                depDistPara1.enable(true);
                depDistPara2.enable(true);
            }
            if (DepartureDist.getSelectedItem().equals("Gaussian")) {
                depDistPara1.enable(true);
                depDistPara2.enable(true);
            }
            if (DepartureDist.getSelectedItem().equals("Deterministic")) {
                depDistPara1.enable(true);
                depDistPara2.enable(false);
            }
            if (DepartureDist.getSelectedItem().equals("Weibull")) {
                depDistPara1.enable(true);
                depDistPara2.enable(true);
            }
            if (DepartureDist.getSelectedItem().equals("Pereto")) {
                depDistPara1.enable(true);
                depDistPara2.enable(true);
            }
            if (DepartureDist.getSelectedItem().equals("Gamma")) {
                depDistPara1.enable(true);
                depDistPara2.enable(true);
            }
            if (DepartureDist.getSelectedItem().equals("Erlang")) {
                depDistPara1.enable(true);
                depDistPara2.enable(true);
            }
            if (DepartureDist.getSelectedItem().equals("Lognormal")) {
                depDistPara1.enable(true);
                depDistPara2.enable(true);
            }
        }
      
             
        
       
      
        if (e.getSource() == radioA1) {
            if (radioA1.getState() == true) {
                ArrivalDist.enable(false);
                arrDistPara1.enable(true);
                arrDistPara2.enable(false);
                radioD1.enable(false);
                radioD2.setState(true);
            }
        }
        if (e.getSource() == radioA2) {
            if (radioA2.getState() == true) {
                ArrivalDist.enable(true);
                arrDistPara1.enable(true);
                arrDistPara2.enable(true);
                radioD1.enable(true);
                if (ArrivalDist.getSelectedItem().equals("Deterministic")) {
                    arrDistPara2.enable(false);
                }
            }
        }
        if (e.getSource() == radioD1) {
            if (radioD1.getState() == true) {
                DepartureDist.enable(false);
                depDistPara1.enable(true);
                depDistPara2.enable(false);
                radioA1.enable(false);
                radioA2.setState(true);
            }
        }
        if (e.getSource() == radioD2) {
            if (radioD2.getState() == true) {
                DepartureDist.enable(true);
                depDistPara1.enable(true);
                depDistPara2.enable(true);
                radioA1.enable(true);
                if (DepartureDist.getSelectedItem().equals("Deterministic")) {
                    depDistPara2.enable(false);
                }
            }
        }
    }

    public void run() {
        Queue q = new Queue();
        Servers s = new Servers(NumberofServers, BatchSize);
       
        LinkedList<Event> EventQueue = new LinkedList();
        LinkedList<Point> points = new LinkedList();
        Event Test = new Event();
        Test.Type = Arrival;
        Test.time = GenerateArrival(lambda);
        Test.j = new Job(GetNumberofArrivals(), Test.time);
        NoSteadyState = 0;
        SteadyMeanNoCustomersSys = "N/A";
        SteadyMeanNoCustomersQue = "N/A";
        SteadyMeanWaitingTimeQue = "N/A";
        SteadyMeanResponseTime = "N/A";
        SteadyUtilisation = "N/A";
        SteadyThroughput = "N/A";
        SteadyBlockingProbability = "N/A";
        SteadyMeanWaiting = "N/A";
       
        EventQueue.push(Test);
        int prevX = 50, nextX = 50, nextY = 270;
        //printing initial grid
        g.setColor(Color.WHITE);
        g.fillRect(1, 310, 700, 300);

        g.setColor(Color.BLACK);
        g.setFont(font4);
        g.drawRoundRect(50, 310, 600, 250, 10, 10);
        g.drawRoundRect(51, 311, 598, 248, 10, 10);
        g.drawLine(50, 350, 650, 350);
        g.drawLine(50, 348, 650, 348);
        g.drawLine(50, 349, 650, 349);
        g.drawLine(410, 310, 410, 560);
        g.drawLine(411, 310, 411, 560);
        g.drawLine(412, 310, 412, 560);
        g.drawLine(520, 310, 520, 560);
        g.drawLine(521, 310, 521, 560);
        g.drawLine(522, 310, 522, 560);

        g.setColor(Color.DARK_GRAY);
        g.setFont(font5);
        g.drawString("Performance Measures", 80, 340);

        if (NoSteadyState == 1) {
            g.drawString("Not a Stable System", 200, 600);
        }

        g.setColor(Color.GRAY);
        g.setFont(font3);
        g.drawString("Throughput  ", 90, 520);
        g.drawString("Utilisation  ", 90, 490);
        g.drawString("Mean Sojurn Time In System ", 90, 460);
        g.drawString("Mean Waiting Time In Queue  ", 90, 430);
        g.drawString("Blocking Probability  ", 90, 550);
        g.drawString("Mean No. of Customers in Queue ", 90, 400);
        g.drawString("Mean No. of Customers in the System  ", 90, 370);
        g.drawString(SteadyMeanNoCustomersSys, 580, 370);
        g.drawString(SteadyMeanNoCustomersQue, 580, 400);
        g.drawString(SteadyMeanWaitingTimeQue, 580, 430);
        g.drawString(SteadyMeanResponseTime, 580, 460);
        g.drawString(SteadyUtilisation, 580, 490);
        g.drawString(SteadyThroughput, 580, 520);
        g.drawString(SteadyBlockingProbability, 580, 550);

        MaxTime = 20;
        sizeInterval = 4;
        MaxSize = 10;

        yscale = 1;
        MeanNoCustomersSys = 0;
        MeanNoCustomersQue = 0;
        MeanWaitingTimeQue = 0;
        MeanResponseTime = 0;
        ResponseTime = 0;
        Utilisation = 0;
        Throughput = 0;
        summ1 = 0;
        summ2 = 0;
        summ3 = 0;
        CustomersServed = 0;
        CustomersEntered = 0;
      
        MeanWaiting = 0;
        //     System.out.println("  " + MeanResponseTime);
        int count = 0;
        if (radioA1.getState() == false && radioA2.getState() == true && radioD1.getState() == false && radioD2.getState() == true) {
            System.out.println("GG");
            while (true) {
                if (!playing) {
                    try {
                        t.sleep(100);
                    } catch (InterruptedException ie) {
                    }
                    continue;
                }
                count++;
                System.out.println("counter= " + count);

                nextX = (int) (600 * time / (MaxTime)) + 50;
                nextY = 270 - (int) (170 * SystemSize / 10);
                points.addLast(new Point(prevX, nextY));
                points.addLast(new Point(nextX, nextY));
                prevX = nextX;

                System.out.print("System Size : " + SystemSize + " ");
                System.out.print("queue Size : " + q.l.size() + " ");
                System.out.print("Event queue " + EventQueue.size() + " ");

                Event e = EventQueue.pollFirst(); //Get the Event with min time

                time = time + e.time;

                for (int ii = 0; ii < EventQueue.size(); ii++) {
                    EventQueue.get(ii).time = EventQueue.get(ii).time - e.time;
                }

          

                if (e.Type == Arrival) {
                    System.out.println("Arrival at : " + time + " of " + e.j.size);
                    if (SystemCapacity - SystemSize >= e.j.size) {          //Add in queue if space is available

                        for (int i = 1; i <= e.j.size; i++) {
                            Job j = new Job(1, 0.0);
                            q.l.push(j);
                            SystemSize++;
                            CustomersEntered++;
                        }
                    } 
                    Event newev = new Event();                           //Generate the Event for Next Arrival

                    newev.Type = Arrival;                                //And Put it in the EventQueue

                    newev.time = GenerateArrival(lambda);
                    newev.j = new Job(GetNumberofArrivals(), newev.time);

                    if (EventQueue.size() == 0) {
                        EventQueue.add(newev);
                    } else {
                        int eqsize = EventQueue.size();
                        int ii;
                        for (ii = 0; ii < eqsize; ii++) {
                            if (EventQueue.get(ii).time > newev.time) {
                                EventQueue.add(ii, newev);
                                break;
                            }
                        }
                        if (ii == eqsize) {
                            EventQueue.add(ii, newev);
                        }
                    }
                } else if (e.Type == Departure) {
                    SystemSize = SystemSize - s.ArrayofServer[e.ServerNo].CurrJobs.size();
                    System.out.println("Deaparture at : " + time + " of " + s.ArrayofServer[e.ServerNo].CurrJobs.size() + "at server no " + e.ServerNo);
                    CustomersServed += s.ArrayofServer[e.ServerNo].CurrJobs.size();
                    s.ArrayofServer[e.ServerNo].Occupied = false;
                    s.ArrayofServer[e.ServerNo].CurrJobs.clear();
                } 

                if (s.isFree() > 0) //Assign Servers to Jobs
                {
                    if (PartialBatch == 1) {
                        for (int i = 0; i < s.num; i++) {                   //Check which servers are free

                            if (s.ArrayofServer[i].Occupied == false) {
                                for (int k = 0; k < s.ArrayofServer[i].capacity; k++) {
                                    if (q.l.size() > 0) {
                                        Job j = null;
                                        if (QueuingDisc == FIFO) {
                                            j = q.l.pollLast();             //Queueing Discipline

                                        }
                                        if (QueuingDisc == LIFO) {
                                            j = q.l.pollFirst();
                                        }
                                        if (QueuingDisc == RAND) {
                                            Random r = new Random();
                                            int temp = r.nextInt(q.l.size());
                                            j = q.l.remove(temp);
                                        }
                                        s.ArrayofServer[i].CurrJobs.addFirst(j);
                                    }
                                    if (s.ArrayofServer[i].CurrJobs.size() > 0) {
                                        Event newEvent = new Event();
                                        newEvent.Type = Departure;
                                        newEvent.time = GenerateDeparture(mu);
                                        newEvent.ServerNo = i;
                                        s.ArrayofServer[i].ServiceTime = newEvent.time;
                                        int eqsize = EventQueue.size();
                                        int c;
                                        if (eqsize == 0) {
                                            EventQueue.add(newEvent);
                                        } else {
                                            for (c = 0; c < eqsize; c++) {
                                                if (EventQueue.get(c).time > newEvent.time) {
                                                    EventQueue.add(c, newEvent);
                                                    break;
                                                }
                                            }
                                            if (c == eqsize) {
                                                EventQueue.add(c, newEvent);
                                            }
                                        }
                                    }
                                }
                                if (s.ArrayofServer[i].CurrJobs.size() > 0) {
                                    s.ArrayofServer[i].Occupied = true;
                                }
                            }

                        }
                    } else if (PartialBatch == 0) {
                        for (int i = 0; i < s.num; i++) {                   //Check which servers are free

                            if (s.ArrayofServer[i].Occupied == false) {
                                if (q.l.size() >= s.ArrayofServer[i].capacity) {
                                    for (int k = 0; k < s.ArrayofServer[i].capacity; k++) {
                                        Job j = null;
                                        if (QueuingDisc == FIFO) {
                                            j = q.l.pollLast();             //Queueing Discipline

                                        }
                                        if (QueuingDisc == LIFO) {
                                            j = q.l.pollFirst();
                                        }
                                        if (QueuingDisc == RAND) {
                                            Random r = new Random();
                                            int temp = r.nextInt(q.l.size());
                                            j = q.l.remove(temp);
                                        }
                                        s.ArrayofServer[i].CurrJobs.addFirst(j);
                                    }
                                }
                                if (s.ArrayofServer[i].CurrJobs.size() > 0) {
                                    Event newEvent = new Event();
                                    newEvent.Type = Departure;
                                    newEvent.time = GenerateDeparture(mu);
                                    newEvent.ServerNo = i;
                                    s.ArrayofServer[i].ServiceTime = newEvent.time;
                                    int eqsize = EventQueue.size();
                                    int c;
                                    if (eqsize == 0) {
                                        EventQueue.add(newEvent);
                                    } else {
                                        for (c = 0; c < eqsize; c++) {
                                            if (EventQueue.get(c).time > newEvent.time) {
                                                EventQueue.add(c, newEvent);
                                                break;
                                            }
                                        }
                                        if (c == eqsize) {
                                            EventQueue.add(c, newEvent);
                                        }
                                    }
                                }

                                if (s.ArrayofServer[i].CurrJobs.size() > 0) {
                                    s.ArrayofServer[i].Occupied = true;
                                }
                            }
                        }
                    }
                }

                if (SystemSize - q.l.size() > 0) {
                    ResponseTime += (SystemSize - q.l.size()) * e.time;
                }
                summ1 += SystemSize * e.time;
                summ2 += q.l.size() * e.time;
                MeanNoCustomersSys = summ1 / time;
                MeanNoCustomersSys = truncate(MeanNoCustomersSys);
                MeanNoCustomersQue = summ2 / time;
                MeanNoCustomersQue = truncate(MeanNoCustomersQue);
                Utilisation = ResponseTime / (time * NumberofServers * BatchSize);
                Utilisation = truncate(Utilisation);
                Throughput = CustomersServed / time;
                Throughput = truncate(Throughput);
                /*if (CustomersServed == 0) {
                MeanResponseTime = 0;
                } else {
                MeanResponseTime = (ResponseTime / CustomersServed);
                }*/
                MeanResponseTime = summ1 / CustomersEntered;
                MeanResponseTime = truncate(MeanResponseTime);
            
                BlockingProbability = truncate(BlockingProbability);
                MeanWaiting = summ2 / CustomersEntered;
                MeanWaiting = truncate(MeanWaiting);

                PaintInitialGrid(g);
                DrawPath(g, points);

                try {
                    t.sleep(80);
                } catch (InterruptedException z) {
                }

            }
        } else if (radioA1.getState() == true && radioA2.getState() == false && radioD1.getState() == false && radioD2.getState() == true) {
            System.out.println("M-G");
            while (true) {
                if (!playing) {
                    try {
                        t.sleep(100);
                    } catch (InterruptedException ie) {
                    }
                    continue;
                }
                count++;
                System.out.println("counter= " + count);

                nextX = (int) (600 * time / (MaxTime)) + 50;
                nextY = 270 - (int) (170 * SystemSize / 10);
                points.addLast(new Point(prevX, nextY));
                points.addLast(new Point(nextX, nextY));
                prevX = nextX;

                System.out.print("System Size : " + SystemSize + " ");
                System.out.print("queue Size : " + q.l.size() + " ");
                System.out.print("Event queue " + EventQueue.size() + " ");

                Event e = EventQueue.pollFirst(); //Get the Event with min time

                time = time + e.time;
                for (int ii = 0; ii < EventQueue.size(); ii++) // removing all events except departure
                {
                    Event ne = EventQueue.get(ii);
                    if (ne.Type == Departure) {
                        ne.time = EventQueue.get(ii).time - e.time;
                    } else {
                        EventQueue.remove(ii);
                    }



                }



              

                if (e.Type == Arrival) {
                    System.out.println("Arrival at : " + time + " of " + e.j.size);
                    if (SystemCapacity - SystemSize >= e.j.size) {          //Add in queue if space is available

                        for (int i = 1; i <= e.j.size; i++) {
                            Job j = new Job(1, 0.0);
                            q.l.push(j);
                            SystemSize++;
                            CustomersEntered++;
                        }
                    } 
               
                } else if (e.Type == Departure) {
                    SystemSize = SystemSize - s.ArrayofServer[e.ServerNo].CurrJobs.size();
                    System.out.println("Deaparture at : " + time + " of " + s.ArrayofServer[e.ServerNo].CurrJobs.size() + "at server no " + e.ServerNo);
                    CustomersServed += s.ArrayofServer[e.ServerNo].CurrJobs.size();
                    s.ArrayofServer[e.ServerNo].Occupied = false;
                    s.ArrayofServer[e.ServerNo].CurrJobs.clear();
                } 

                if (s.isFree() > 0) //Assign Servers to Jobs
                {
                    if (PartialBatch == 1) {
                        for (int i = 0; i < s.num; i++) {                   //Check which servers are free

                            if (s.ArrayofServer[i].Occupied == false) {
                                for (int k = 0; k < s.ArrayofServer[i].capacity; k++) {
                                    if (q.l.size() > 0) {
                                        Job j = null;
                                        if (QueuingDisc == FIFO) {
                                            j = q.l.pollLast();             //Queueing Discipline

                                        }
                                        if (QueuingDisc == LIFO) {
                                            j = q.l.pollFirst();
                                        }
                                        if (QueuingDisc == RAND) {
                                            Random r = new Random();
                                            int temp = r.nextInt(q.l.size());
                                            j = q.l.remove(temp);
                                        }
                                        s.ArrayofServer[i].CurrJobs.addFirst(j);
                                    }
                                    if (s.ArrayofServer[i].CurrJobs.size() > 0) {
                                        Event newEvent = new Event();
                                        newEvent.Type = Departure;
                                        newEvent.time = GenerateDeparture(mu);
                                        newEvent.ServerNo = i;
                                        s.ArrayofServer[i].ServiceTime = newEvent.time;
                                        int eqsize = EventQueue.size();
                                        int c;
                                        if (eqsize == 0) {
                                            EventQueue.add(newEvent);
                                        } else {
                                            for (c = 0; c < eqsize; c++) {
                                                if (EventQueue.get(c).time > newEvent.time) {
                                                    EventQueue.add(c, newEvent);
                                                    break;
                                                }
                                            }
                                            if (c == eqsize) {
                                                EventQueue.add(c, newEvent);
                                            }
                                        }
                                    }
                                }
                                if (s.ArrayofServer[i].CurrJobs.size() > 0) {
                                    s.ArrayofServer[i].Occupied = true;
                                }
                            }

                        }
                    } else if (PartialBatch == 0) {
                        for (int i = 0; i < s.num; i++) {                   //Check which servers are free

                            if (s.ArrayofServer[i].Occupied == false) {
                                if (q.l.size() >= s.ArrayofServer[i].capacity) {
                                    for (int k = 0; k < s.ArrayofServer[i].capacity; k++) {
                                        Job j = null;
                                        if (QueuingDisc == FIFO) {
                                            j = q.l.pollLast();             //Queueing Discipline

                                        }
                                        if (QueuingDisc == LIFO) {
                                            j = q.l.pollFirst();
                                        }
                                        if (QueuingDisc == RAND) {
                                            Random r = new Random();
                                            int temp = r.nextInt(q.l.size());
                                            j = q.l.remove(temp);
                                        }
                                        s.ArrayofServer[i].CurrJobs.addFirst(j);
                                    }
                                }
                                if (s.ArrayofServer[i].CurrJobs.size() > 0) {
                                    Event newEvent = new Event();
                                    newEvent.Type = Departure;
                                    newEvent.time = GenerateDeparture(mu);
                                    newEvent.ServerNo = i;
                                    s.ArrayofServer[i].ServiceTime = newEvent.time;
                                    int eqsize = EventQueue.size();
                                    int c;
                                    if (eqsize == 0) {
                                        EventQueue.add(newEvent);
                                    } else {
                                        for (c = 0; c < eqsize; c++) {
                                            if (EventQueue.get(c).time > newEvent.time) {
                                                EventQueue.add(c, newEvent);
                                                break;
                                            }
                                        }
                                        if (c == eqsize) {
                                            EventQueue.add(c, newEvent);
                                        }
                                    }
                                }

                                if (s.ArrayofServer[i].CurrJobs.size() > 0) {
                                    s.ArrayofServer[i].Occupied = true;
                                }
                            }
                        }
                    }
                }

                if (SystemSize - q.l.size() > 0) {
                    ResponseTime += (SystemSize - q.l.size()) * e.time;
                }
                summ1 += SystemSize * e.time;
                summ2 += q.l.size() * e.time;
                MeanNoCustomersSys = summ1 / time;
                MeanNoCustomersSys = truncate(MeanNoCustomersSys);
                MeanNoCustomersQue = summ2 / time;
                MeanNoCustomersQue = truncate(MeanNoCustomersQue);
                Utilisation = ResponseTime / (time * NumberofServers * BatchSize);
                Utilisation = truncate(Utilisation);
                Throughput = CustomersServed / time;
                Throughput = truncate(Throughput);
                /*if (CustomersServed == 0) {
                MeanResponseTime = 0;
                } else {
                MeanResponseTime = (ResponseTime / CustomersServed);
                }*/
                MeanResponseTime = summ1 / CustomersEntered;
                MeanResponseTime = truncate(MeanResponseTime);
               
                BlockingProbability = truncate(BlockingProbability);
                MeanWaiting = summ2 / CustomersEntered;
                MeanWaiting = truncate(MeanWaiting);

                PaintInitialGrid(g);
                DrawPath(g, points);



                Event nextArrival = new Event();                           //Generate the Event for Next Arrival

                nextArrival.Type = Arrival;
                nextArrival.time = GenerateArrival(lambda);
                nextArrival.j = new Job(GetNumberofArrivals(), nextArrival.time);
                for (int k = 0; k <= EventQueue.size(); k++) {
                    if (k == EventQueue.size()) {
                        EventQueue.add(k, nextArrival);
                        break;
                    } else if (EventQueue.get(k).time > nextArrival.time) {
                        EventQueue.add(k, nextArrival);
                        break;
                    }
                }

               





                try {
                    t.sleep(200);
                } catch (InterruptedException z) {
                }

            }
        } else if (radioA1.getState() == false && radioA2.getState() == true && radioD1.getState() == true && radioD2.getState() == false) {
            System.out.println("G-M");
            while (true) {
                if (!playing) {
                    try {
                        t.sleep(100);
                    } catch (InterruptedException ie) {
                    }
                    continue;
                }
                count++;
                System.out.println("counter= " + count);

                nextX = (int) (600 * time / (MaxTime)) + 50;
                nextY = 270 - (int) (170 * SystemSize / 10);
                points.addLast(new Point(prevX, nextY));
                points.addLast(new Point(nextX, nextY));
                prevX = nextX;

                System.out.print("System Size : " + SystemSize + " ");
                System.out.print("queue Size : " + q.l.size() + " ");
                System.out.print("Event queue " + EventQueue.size() + " ");

                Event e = EventQueue.pollFirst(); //Get the Event with min time

                time = time + e.time;
                for (int ii = 0; ii < EventQueue.size(); ii++) // removing all events except departure
                {
                    Event ne = EventQueue.get(ii);
                    if (ne.Type == Arrival) {
                        ne.time = EventQueue.get(ii).time - e.time;
                    } else {
                        EventQueue.remove(ii);
                    }



                }



               

                if (e.Type == Arrival) {
                    System.out.println("Arrival at : " + time + " of " + e.j.size);
                    if (SystemCapacity - SystemSize >= e.j.size) {          //Add in queue if space is available

                        for (int i = 1; i <= e.j.size; i++) {
                            Job j = new Job(1, 0.0);
                            q.l.push(j);
                            SystemSize++;
                            CustomersEntered++;
                        }
                    } 
                    Event newev = new Event();                           //Generate the Event for Next Arrival

                    newev.Type = Arrival;                                //And Put it in the EventQueue

                    newev.time = GenerateArrival(lambda);
                    newev.j = new Job(GetNumberofArrivals(), newev.time);

                    if (EventQueue.size() == 0) {
                        EventQueue.add(newev);
                    } else {
                        int eqsize = EventQueue.size();
                        int ii;
                        for (ii = 0; ii < eqsize; ii++) {
                            if (EventQueue.get(ii).time > newev.time) {
                                EventQueue.add(ii, newev);
                                break;
                            }
                        }
                        if (ii == eqsize) {
                            EventQueue.add(ii, newev);
                        }
                    }
                } else if (e.Type == Departure) {
                    SystemSize = SystemSize - s.ArrayofServer[e.ServerNo].CurrJobs.size();
                    System.out.println("Deaparture at : " + time + " of " + s.ArrayofServer[e.ServerNo].CurrJobs.size() + "at server no " + e.ServerNo);
                    CustomersServed += s.ArrayofServer[e.ServerNo].CurrJobs.size();
                    s.ArrayofServer[e.ServerNo].Occupied = false;
                    s.ArrayofServer[e.ServerNo].CurrJobs.clear();
                } 

                if (s.isFree() > 0) //Assign Servers to Jobs
                {
                    if (PartialBatch == 1) {
                        for (int i = 0; i < s.num; i++) {                   //Check which servers are free

                            if (s.ArrayofServer[i].Occupied == false) {
                                for (int k = 0; k < s.ArrayofServer[i].capacity; k++) {
                                    if (q.l.size() > 0) {
                                        Job j = null;
                                        if (QueuingDisc == FIFO) {
                                            j = q.l.pollLast();             //Queueing Discipline

                                        }
                                        if (QueuingDisc == LIFO) {
                                            j = q.l.pollFirst();
                                        }
                                        if (QueuingDisc == RAND) {
                                            Random r = new Random();
                                            int temp = r.nextInt(q.l.size());
                                            j = q.l.remove(temp);
                                        }
                                        s.ArrayofServer[i].CurrJobs.addFirst(j);
                                    }
                                }
                                if (s.ArrayofServer[i].CurrJobs.size() > 0) {
                                    s.ArrayofServer[i].Occupied = true;
                                }
                            }
                        }
                    } else if (PartialBatch == 0) {
                        for (int i = 0; i < s.num; i++) {                   //Check which servers are free

                            if (s.ArrayofServer[i].Occupied == false) {
                                if (q.l.size() >= s.ArrayofServer[i].capacity) {
                                    for (int k = 0; k < s.ArrayofServer[i].capacity; k++) {
                                        Job j = null;
                                        if (QueuingDisc == FIFO) {
                                            j = q.l.pollLast();             //Queueing Discipline

                                        }
                                        if (QueuingDisc == LIFO) {
                                            j = q.l.pollFirst();
                                        }
                                        if (QueuingDisc == RAND) {
                                            Random r = new Random();
                                            int temp = r.nextInt(q.l.size());
                                            j = q.l.remove(temp);
                                        }
                                        s.ArrayofServer[i].CurrJobs.addFirst(j);
                                    }
                                }
                            }
                            if (s.ArrayofServer[i].CurrJobs.size() > 0) {
                                s.ArrayofServer[i].Occupied = true;
                            }
                        }
                    }
                }

                if (SystemSize - q.l.size() > 0) {
                    ResponseTime += (SystemSize - q.l.size()) * e.time;
                }
                summ1 += SystemSize * e.time;
                summ2 += q.l.size() * e.time;
                MeanNoCustomersSys = summ1 / time;
                MeanNoCustomersSys = truncate(MeanNoCustomersSys);
                MeanNoCustomersQue = summ2 / time;
                MeanNoCustomersQue = truncate(MeanNoCustomersQue);
                Utilisation = ResponseTime / (time * NumberofServers * BatchSize);
                Utilisation = truncate(Utilisation);
                Throughput = CustomersServed / time;
                Throughput = truncate(Throughput);
                /*if (CustomersServed == 0) {
                MeanResponseTime = 0;
                } else {
                MeanResponseTime = (ResponseTime / CustomersServed);
                }*/
                MeanResponseTime = summ1 / CustomersEntered;
                MeanResponseTime = truncate(MeanResponseTime);
               
                BlockingProbability = truncate(BlockingProbability);
                MeanWaiting = summ2 / CustomersEntered;
                MeanWaiting = truncate(MeanWaiting);

                PaintInitialGrid(g);
                DrawPath(g, points);



                /*Event nextArrival = new Event();                           //Generate the Event for Next Arrival

                nextArrival.Type = Arrival;
                nextArrival.time = GenerateArrival(lambda);
                nextArrival.j = new Job(GetNumberofArrivals(), nextArrival.time);
                for (int k = 0; k <= EventQueue.size(); k++) {
                if (k == EventQueue.size()) {
                EventQueue.add(k, nextArrival);
                break;
                } else if (EventQueue.get(k).time > nextArrival.time) {
                EventQueue.add(k, nextArrival);
                break;
                }
                }*/

                for (int i = 0; i < s.num; i++) {                          //fill departures in the EventQueue

                    double minTime = 0;
                    int OccupiedServers = 0;
                    Event newe = new Event();
                    newe.Type = Departure;
                    if (s.ArrayofServer[i].Occupied == true) {
                        OccupiedServers++;
                        s.ArrayofServer[i].ServiceTime = GenerateDeparture(mu);
                        if (OccupiedServers == 1) {
                            minTime = s.ArrayofServer[i].ServiceTime;
                            newe.ServerNo = i;
                            newe.time = minTime;
                        } else if (s.ArrayofServer[i].ServiceTime < minTime) {
                            minTime = s.ArrayofServer[i].ServiceTime;
                            newe.ServerNo = i;
                            newe.time = minTime;
                        }
                    }
                    if (OccupiedServers > 0) {
                        for (int k = 0; k <= EventQueue.size(); k++) {
                            if (k == EventQueue.size()) {
                                EventQueue.add(k, newe);
                                break;
                            } else if (EventQueue.get(k).time > newe.time) {
                                EventQueue.add(k, newe);
                                break;
                            }
                        }
                    }
                }


                try {
                    t.sleep(800);
                } catch (InterruptedException z) {
                }

            }
        }

    }

    public void stop() {
        t.stop();
    }
}