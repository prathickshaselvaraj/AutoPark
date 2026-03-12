package gui;

import dao.BookingDAO;
import dao.ParkingSlotDAO;
import dao.VehicleDAO;
import models.Booking;
import models.SlotInfo;
import models.User;
import models.Vehicle;
import services.ParkingService;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import static gui.LoginFrame.*;

public class UserDashboard extends JFrame {

    private static final Color BG_SIDEBAR = new Color(15, 28, 44);
    private static final Color SLOT_FREE  = new Color(27, 94, 32);
    private static final Color SLOT_OCC   = new Color(183, 28, 28);
    private static final Color ROAD_COLOR = new Color(35, 50, 65);
    private static final Color TBL_BG     = new Color(10, 22, 35);
    private static final Color TBL_ALT    = new Color(14, 28, 44);
    private static final Color TBL_HDR    = new Color(20, 38, 58);
    private static final Color TBL_SEL    = new Color(30, 80, 130);
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd MMM yyyy  HH:mm");

    private final User           user;
    private final ParkingService parking = new ParkingService();
    private       JPanel         content;
    private       JLabel         statusBar;

    public UserDashboard(User user) {
        this.user = user;
        setTitle("AutoPark");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 720);
        setLocationRelativeTo(null);
        setUndecorated(true);
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG); root.setBorder(BorderFactory.createLineBorder(BORDER,1));
        root.add(topBar(), BorderLayout.NORTH);
        root.add(sidebar(), BorderLayout.WEST);
        content = new JPanel(new BorderLayout()); content.setBackground(BG);
        root.add(content, BorderLayout.CENTER);
        statusBar = new JLabel("  Ready");
        statusBar.setFont(new Font("Segoe UI",Font.PLAIN,11)); statusBar.setForeground(SUBTEXT);
        statusBar.setBackground(new Color(8,18,30)); statusBar.setOpaque(true);
        statusBar.setBorder(new EmptyBorder(4,12,4,12));
        root.add(statusBar, BorderLayout.SOUTH);
        setContentPane(root);
        go(dashboard());
    }

    private void go(JPanel p){content.removeAll();content.add(p,BorderLayout.CENTER);content.revalidate();content.repaint();}

    // ═══ TOP BAR ══════════════════════════════════════════════════════════════
    private JPanel topBar(){
        JPanel bar=new JPanel(new BorderLayout());bar.setBackground(new Color(8,18,30));bar.setBorder(new EmptyBorder(9,18,9,18));
        JLabel brand=new JLabel("AutoPark");brand.setFont(new Font("Segoe UI",Font.BOLD,18));brand.setForeground(ACCENT);
        JPanel right=new JPanel(new FlowLayout(FlowLayout.RIGHT,8,0));right.setOpaque(false);
        JLabel who=new JLabel(user.getFullName()+"  [USER]");who.setFont(new Font("Segoe UI",Font.PLAIN,12));who.setForeground(SUBTEXT);
        JButton logout=pill("Logout",DANGER,Color.WHITE);logout.addActionListener(e->{dispose();new LoginFrame().setVisible(true);});
        JButton min=flat("-");min.addActionListener(e->setState(ICONIFIED));
        JButton cls=flat("X");cls.setForeground(DANGER);cls.addActionListener(e->System.exit(0));
        right.add(who);right.add(logout);right.add(Box.createHorizontalStrut(4));right.add(min);right.add(cls);
        bar.add(brand,BorderLayout.WEST);bar.add(right,BorderLayout.EAST);drag(bar);return bar;
    }

    // ═══ SIDEBAR ══════════════════════════════════════════════════════════════
    private JPanel sidebar(){
        JPanel s=new JPanel();s.setBackground(BG_SIDEBAR);s.setPreferredSize(new Dimension(190,0));
        s.setLayout(new BoxLayout(s,BoxLayout.Y_AXIS));s.setBorder(new EmptyBorder(14,0,14,0));
        sec(s,"OVERVIEW");
        s.add(nav("Dashboard",    ()->go(dashboard())));
        s.add(nav("Parking Lot",  ()->go(parkingLot())));
        sec(s,"VEHICLE");
        s.add(nav("Park Vehicle", ()->go(parkPanel())));
        s.add(nav("Exit Vehicle", ()->go(exitPanel())));
        s.add(nav("Check Bill",   ()->go(billPanel())));
        sec(s,"BOOKINGS");
        s.add(nav("Pre-Book Slot",()->go(preBookPanel())));
        s.add(nav("My Bookings",  ()->go(myBookingsPanel())));
        s.add(Box.createVerticalGlue());return s;
    }
    private void sec(JPanel p,String t){JLabel l=new JLabel("  "+t);l.setFont(new Font("Segoe UI",Font.BOLD,9));l.setForeground(new Color(70,100,130));l.setMaximumSize(new Dimension(Integer.MAX_VALUE,22));l.setBorder(new EmptyBorder(12,10,4,0));p.add(l);}
    private JButton nav(String label,Runnable action){JButton b=new JButton("  "+label);b.setFont(new Font("Segoe UI",Font.PLAIN,13));b.setForeground(SUBTEXT);b.setBackground(BG_SIDEBAR);b.setBorderPainted(false);b.setFocusPainted(false);b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));b.setMaximumSize(new Dimension(Integer.MAX_VALUE,40));b.setHorizontalAlignment(SwingConstants.LEFT);b.setBorder(new EmptyBorder(10,18,10,10));b.addMouseListener(new MouseAdapter(){public void mouseEntered(MouseEvent e){b.setForeground(TEXT);b.setBackground(new Color(30,52,78));}public void mouseExited(MouseEvent e){b.setForeground(SUBTEXT);b.setBackground(BG_SIDEBAR);}});b.addActionListener(e->action.run());return b;}

    // ═══ DASHBOARD ════════════════════════════════════════════════════════════
    private JPanel dashboard(){
        JPanel root=new JPanel(new BorderLayout(0,14));root.setBackground(BG);root.setBorder(new EmptyBorder(20,20,20,20));
        JPanel hrow=new JPanel(new BorderLayout());hrow.setOpaque(false);hrow.setBorder(new EmptyBorder(0,0,14,0));
        hrow.add(h("Dashboard"),BorderLayout.WEST);hrow.add(sub("Welcome back, "+user.getFullName()+"!"),BorderLayout.EAST);
        JPanel cards=new JPanel(new GridLayout(1,3,12,0));cards.setOpaque(false);
        new SwingWorker<int[],Void>(){protected int[] doInBackground(){int occ=ParkingSlotDAO.getOccupiedSlotsCount();return new int[]{occ,AppConfig.TOTAL_SLOTS-occ,AppConfig.TOTAL_SLOTS};}protected void done(){try{int[]d=get();cards.add(statCard("Occupied",""+d[0],DANGER));cards.add(statCard("Available",""+d[1],SUCCESS));cards.add(statCard("Total",""+d[2],ACCENT));cards.revalidate();}catch(Exception x){x.printStackTrace();}}}.execute();
        JPanel lower=new JPanel(new GridLayout(1,2,14,0));lower.setOpaque(false);
        JPanel qa=card2("Quick Actions");JPanel qai=inner(qa);
        String[] ql={"Park a Vehicle","Exit Vehicle & Bill","Pre-Book a Slot","My Bookings"};
        Color[]  qc={ACCENT,new Color(255,152,0),new Color(100,181,246),SUCCESS};
        Runnable[] qr={()->go(parkPanel()),()->go(exitPanel()),()->go(preBookPanel()),()->go(myBookingsPanel())};
        for(int i=0;i<ql.length;i++){JButton b=makeBtn(ql[i],qc[i],new Color(10,30,50));b.setMaximumSize(new Dimension(Integer.MAX_VALUE,40));b.setFont(new Font("Segoe UI",Font.BOLD,13));b.setHorizontalAlignment(SwingConstants.LEFT);b.setBorder(new EmptyBorder(10,16,10,16));int fi=i;b.addActionListener(e->qr[fi].run());qai.add(b);qai.add(Box.createVerticalStrut(6));}
        lower.add(qa);
        JPanel ls=card2("Live Summary");JPanel lsi=inner(ls);
        new SwingWorker<List<SlotInfo>,Void>(){protected List<SlotInfo> doInBackground(){return ParkingSlotDAO.getAllSlotsInfo();}protected void done(){try{List<SlotInfo>sl=get();long occ=sl.stream().filter(SlotInfo::isOccupied).count();long free=sl.size()-occ;int pct=sl.size()>0?(int)(occ*100/sl.size()):0;lsi.add(sr("Total Slots",""+sl.size(),TEXT));lsi.add(sr("Occupied",""+occ,DANGER));lsi.add(sr("Available",""+free,SUCCESS));lsi.add(sr("Utilization",pct+"%",new Color(255,152,0)));lsi.add(sr("Rate","Rs "+AppConfig.RATE_PER_HOUR+"/hr",ACCENT));lsi.add(sr("Min Charge",AppConfig.MIN_CHARGE_MINUTES+" min",SUBTEXT));lsi.revalidate();}catch(Exception x){x.printStackTrace();}}}.execute();
        lower.add(ls);
        root.add(hrow,BorderLayout.NORTH);root.add(cards,BorderLayout.CENTER);root.add(lower,BorderLayout.SOUTH);return root;
    }
    private JPanel statCard(String label,String value,Color accent){JPanel c=new JPanel(new BorderLayout(0,4));c.setBackground(CARD);c.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0,3,0,0,accent),new EmptyBorder(18,18,18,18)));JLabel v=new JLabel(value);v.setFont(new Font("Segoe UI",Font.BOLD,42));v.setForeground(accent);JLabel l=new JLabel(label);l.setFont(new Font("Segoe UI",Font.PLAIN,11));l.setForeground(SUBTEXT);c.add(v,BorderLayout.CENTER);c.add(l,BorderLayout.SOUTH);return c;}

    // ═══ PARKING LOT ══════════════════════════════════════════════════════════
    private JPanel parkingLot(){
        JPanel root=new JPanel(new BorderLayout());root.setBackground(BG);root.setBorder(new EmptyBorder(20,20,20,20));
        JPanel top=new JPanel(new BorderLayout());top.setOpaque(false);top.setBorder(new EmptyBorder(0,0,12,0));
        top.add(h("Parking Lot  -  "+AppConfig.TOTAL_SLOTS+" Slots"),BorderLayout.WEST);
        JPanel ctrl=new JPanel(new FlowLayout(FlowLayout.RIGHT,10,0));ctrl.setOpaque(false);
        ctrl.add(legDot(SLOT_FREE," Free"));ctrl.add(legDot(SLOT_OCC," Occupied"));
        JButton ref=accentButton("Refresh");ref.setPreferredSize(new Dimension(100,30));ctrl.add(ref);top.add(ctrl,BorderLayout.EAST);
        JPanel grid=new JPanel();grid.setBackground(ROAD_COLOR);grid.setBorder(new EmptyBorder(14,14,14,14));
        JScrollPane scroll=new JScrollPane(grid);scroll.setBorder(BorderFactory.createLineBorder(BORDER,1));scroll.getVerticalScrollBar().setUnitIncrement(24);scroll.getViewport().setBackground(ROAD_COLOR);
        Runnable load=()->new SwingWorker<List<SlotInfo>,Void>(){protected List<SlotInfo> doInBackground(){return ParkingSlotDAO.getAllSlotsInfo();}protected void done(){try{List<SlotInfo>slots=get();grid.removeAll();int cols=10,rows=(int)Math.ceil((double)slots.size()/cols);grid.setLayout(new GridLayout(rows,cols,5,5));long occ=0,free=0;for(SlotInfo si:slots){grid.add(slotCell(si));if(si.isOccupied())occ++;else free++;}int rem=(cols-slots.size()%cols)%cols;for(int i=0;i<rem;i++){JPanel sp=new JPanel();sp.setOpaque(false);grid.add(sp);}grid.revalidate();grid.repaint();long fo=occ,ff=free;statusBar.setText("  Lot: "+fo+" occupied, "+ff+" free");}catch(Exception x){x.printStackTrace();}}}.execute();
        ref.addActionListener(e->load.run());load.run();
        root.add(top,BorderLayout.NORTH);root.add(scroll,BorderLayout.CENTER);return root;
    }
    private JPanel slotCell(SlotInfo si){boolean occ=si.isOccupied();JPanel c=new JPanel(new BorderLayout(0,1));c.setPreferredSize(new Dimension(92,66));c.setBackground(occ?SLOT_OCC:SLOT_FREE);c.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(occ?new Color(200,40,40):new Color(46,125,50),1),new EmptyBorder(3,3,3,3)));JLabel num=new JLabel("P"+si.getSlotNumber(),SwingConstants.CENTER);num.setFont(new Font("Segoe UI",Font.BOLD,9));num.setForeground(occ?new Color(255,205,210):new Color(165,214,167));JLabel mid=new JLabel(occ?"[CAR]":"",SwingConstants.CENTER);mid.setFont(new Font("Segoe UI",Font.PLAIN,8));mid.setForeground(new Color(255,200,200));String bt=occ?(si.getLicensePlate()!=null?si.getLicensePlate():"OCC"):"FREE";JLabel stat=new JLabel(bt,SwingConstants.CENTER);stat.setFont(new Font("Segoe UI",Font.BOLD,occ?8:9));stat.setForeground(occ?new Color(255,230,230):new Color(200,240,200));c.add(num,BorderLayout.NORTH);c.add(mid,BorderLayout.CENTER);c.add(stat,BorderLayout.SOUTH);return c;}
    private JPanel legDot(Color c,String text){JPanel p=new JPanel(new FlowLayout(FlowLayout.LEFT,4,0));p.setOpaque(false);JPanel d=new JPanel(){protected void paintComponent(Graphics g){g.setColor(c);g.fillRoundRect(0,0,getWidth(),getHeight(),4,4);}};d.setPreferredSize(new Dimension(13,13));d.setOpaque(false);JLabel l=new JLabel(text);l.setFont(new Font("Segoe UI",Font.PLAIN,11));l.setForeground(SUBTEXT);p.add(d);p.add(l);return p;}

    // ═══ PARK ═════════════════════════════════════════════════════════════════
    private JPanel parkPanel(){
        JPanel root=new JPanel(new BorderLayout(0,14));root.setBackground(BG);root.setBorder(new EmptyBorder(20,20,20,20));
        root.add(hrow("Park Vehicle","Register your vehicle and get a slot"),BorderLayout.NORTH);
        JPanel cols=new JPanel(new GridLayout(1,2,14,0));cols.setOpaque(false);
        JPanel left=formCard("Vehicle Details");JPanel fi=inner(left);
        JTextField pf=new JTextField();styleField(pf,"e.g. TN67AB1234");
        JComboBox<String>tb=new JComboBox<>(new String[]{"CAR","BIKE","TRUCK"});styleCombo(tb);
        JLabel res=resLbl();JButton btn=accentButton("Park Now");btn.setMaximumSize(new Dimension(Integer.MAX_VALUE,40));
        addF(fi,"License Plate",pf);addF(fi,"Vehicle Type",tb);fi.add(Box.createVerticalStrut(10));fi.add(btn);fi.add(res);
        btn.addActionListener(e->{String plate=pf.getText().trim().toUpperCase();if(plate.isEmpty()){setR(res,"Enter plate.",DANGER);return;}btn.setEnabled(false);setR(res,"Processing...",ACCENT);
            new SwingWorker<Integer,Void>(){protected Integer doInBackground(){if(VehicleDAO.getVehicleByLicensePlate(plate)!=null)return -1;if(ParkingSlotDAO.findAvailableSlot()==null)return -2;models.Vehicle v=new models.Vehicle(plate,(String)tb.getSelectedItem());v.setUserId(user.getId());return parking.parkVehicle(v);}
                protected void done(){try{Integer slot=get();if(slot==null||slot<=0)setR(res,"Failed.",DANGER);else if(slot==-1)setR(res,plate+" already parked!",DANGER);else if(slot==-2)setR(res,"Lot FULL!",DANGER);else{setR(res,plate+" at P"+slot,SUCCESS);pf.setText("");statusBar.setText("  Parked: "+plate+" -> P"+slot);}}catch(Exception x){setR(res,"Error.",DANGER);}finally{btn.setEnabled(true);}}}
                    .execute();});
        JPanel right=infoCard("Rates",new String[][]{{"Rate","Rs "+AppConfig.RATE_PER_HOUR+" / hr"},{"Per Min",String.format("Rs %.4f / min",AppConfig.RATE_PER_HOUR/60.0)},{"Min",""+AppConfig.MIN_CHARGE_MINUTES+" min"},{"30 min","-> Rs 25.00"},{"1 hr","-> Rs 50.00"},{"Payment","Cash on exit"}});
        cols.add(left);cols.add(right);root.add(cols,BorderLayout.CENTER);return root;
    }

    // ═══ EXIT ═════════════════════════════════════════════════════════════════
    private JPanel exitPanel(){
        JPanel root=new JPanel(new BorderLayout(0,14));root.setBackground(BG);root.setBorder(new EmptyBorder(20,20,20,20));
        root.add(hrow("Exit Vehicle & Bill","Process exit and get accurate bill"),BorderLayout.NORTH);
        JPanel cols=new JPanel(new GridLayout(1,2,14,0));cols.setOpaque(false);
        JPanel left=formCard("Exit Details");JPanel fi=inner(left);
        JTextField pf=new JTextField();styleField(pf,"e.g. TN67AB1234");
        JLabel res=resLbl();JTextArea ta=ta();
        JButton btn=accentButton("Exit & Generate Bill");btn.setMaximumSize(new Dimension(Integer.MAX_VALUE,40));
        addF(fi,"License Plate",pf);fi.add(Box.createVerticalStrut(10));fi.add(btn);fi.add(res);fi.add(Box.createVerticalStrut(6));
        JLabel rl=fieldLabel("Receipt");rl.setAlignmentX(LEFT_ALIGNMENT);rl.setBorder(new EmptyBorder(2,0,4,0));fi.add(rl);
        JScrollPane sc=new JScrollPane(ta);sc.setBorder(BorderFactory.createLineBorder(BORDER,1));sc.setAlignmentX(LEFT_ALIGNMENT);sc.setMaximumSize(new Dimension(Integer.MAX_VALUE,200));fi.add(sc);
        btn.addActionListener(e->{String plate=pf.getText().trim().toUpperCase();if(plate.isEmpty()){setR(res,"Enter plate.",DANGER);return;}btn.setEnabled(false);ta.setText("");setR(res,"Processing...",ACCENT);
            new SwingWorker<String,Void>(){protected String doInBackground()throws Exception{Vehicle v=VehicleDAO.getVehicleByLicensePlate(plate);if(v==null)return"NOT_FOUND";if(v.getExitTime()!=null)return"ALREADY_EXITED";if(!parking.removeVehicle(plate))return"FAILED";Vehicle ex=VehicleDAO.getVehicleByLicensePlate(plate);if(ex==null||ex.getExitTime()==null)return"FAILED";Duration d=Duration.between(ex.getEntryTime(),ex.getExitTime());long act=d.toMinutes(),bil=Math.max(AppConfig.MIN_CHARGE_MINUTES,act);return receipt(ex,d,act,bil,(bil/60.0)*AppConfig.RATE_PER_HOUR,false);}
                protected void done(){try{String r=get();switch(r){case"NOT_FOUND"->setR(res,"Not found.",DANGER);case"ALREADY_EXITED"->setR(res,"Already exited.",DANGER);case"FAILED"->setR(res,"Failed.",DANGER);default->{setR(res,"Done.",SUCCESS);ta.setText(r);pf.setText("");statusBar.setText("  Exited: "+plate);}}}catch(Exception x){setR(res,"Error.",DANGER);}finally{btn.setEnabled(true);}}}
                    .execute();});
        JPanel right=infoCard("Examples",new String[][]{{"Rate","Rs "+AppConfig.RATE_PER_HOUR+" / hr"},{"Min",""+AppConfig.MIN_CHARGE_MINUTES+" min"},{"30 min","-> Rs 25.00"},{"45 min","-> Rs 37.50"},{"1 hr","-> Rs 50.00"},{"2 hrs","-> Rs 100.00"}});
        cols.add(left);cols.add(right);root.add(cols,BorderLayout.CENTER);return root;
    }

    // ═══ BILL ═════════════════════════════════════════════════════════════════
    private JPanel billPanel(){
        JPanel root=new JPanel(new BorderLayout(0,14));root.setBackground(BG);root.setBorder(new EmptyBorder(20,20,20,20));
        root.add(hrow("Check Bill","Real-time estimate"),BorderLayout.NORTH);
        JPanel cols=new JPanel(new GridLayout(1,2,14,0));cols.setOpaque(false);
        JPanel left=formCard("Lookup");JPanel fi=inner(left);
        JTextField pf=new JTextField();styleField(pf,"e.g. TN67AB1234");
        JLabel res=resLbl();JTextArea ta=ta();
        JButton btn=accentButton("Calculate");btn.setMaximumSize(new Dimension(Integer.MAX_VALUE,40));
        addF(fi,"License Plate",pf);fi.add(Box.createVerticalStrut(10));fi.add(btn);fi.add(res);fi.add(Box.createVerticalStrut(6));
        JLabel rl=fieldLabel("Breakdown");rl.setAlignmentX(LEFT_ALIGNMENT);rl.setBorder(new EmptyBorder(2,0,4,0));fi.add(rl);
        JScrollPane sc=new JScrollPane(ta);sc.setBorder(BorderFactory.createLineBorder(BORDER,1));sc.setAlignmentX(LEFT_ALIGNMENT);sc.setMaximumSize(new Dimension(Integer.MAX_VALUE,200));fi.add(sc);
        btn.addActionListener(e->{String plate=pf.getText().trim().toUpperCase();if(plate.isEmpty()){setR(res,"Enter plate.",DANGER);return;}btn.setEnabled(false);ta.setText("");
            new SwingWorker<String,Void>(){protected String doInBackground(){Vehicle v=VehicleDAO.getVehicleByLicensePlate(plate);if(v==null)return"NOT_FOUND";boolean pk=v.getExitTime()==null;Duration d=Duration.between(v.getEntryTime(),pk?LocalDateTime.now():v.getExitTime());long act=d.toMinutes(),bil=Math.max(AppConfig.MIN_CHARGE_MINUTES,act);return receipt(v,d,act,bil,(bil/60.0)*AppConfig.RATE_PER_HOUR,pk);}
                protected void done(){try{String r=get();if("NOT_FOUND".equals(r))setR(res,"Not found.",DANGER);else{setR(res,"Calculated.",SUCCESS);ta.setText(r);}}catch(Exception x){setR(res,"Error.",DANGER);}finally{btn.setEnabled(true);}}}
                    .execute();});
        JPanel right=infoCard("Billing Info",new String[][]{{"Method","Per-minute"},{"Min",""+AppConfig.MIN_CHARGE_MINUTES+" min"},{"Rate","Rs "+AppConfig.RATE_PER_HOUR+"/hr"},{"Payment","Cash"}});
        cols.add(left);cols.add(right);root.add(cols,BorderLayout.CENTER);return root;
    }

    // ═══ PRE-BOOK ══════════════════════════════════════════════════════════════
    private JPanel preBookPanel() {
        JPanel root = new JPanel(new BorderLayout(0, 14));
        root.setBackground(BG); root.setBorder(new EmptyBorder(20, 20, 20, 20));
        root.add(hrow("Pre-Book a Slot", "Reserve a parking slot in advance"), BorderLayout.NORTH);

        JPanel cols = new JPanel(new GridLayout(1, 2, 14, 0)); cols.setOpaque(false);
        JPanel left = formCard("Booking Details"); JPanel fi = inner(left);

        // License plate
        JTextField pf = new JTextField(); styleField(pf, "e.g. TN67AB1234");

        // Vehicle type
        JComboBox<String> tb = new JComboBox<>(new String[]{"CAR","BIKE","TRUCK"}); styleCombo(tb);

        // Date + Time spinner
        SpinnerDateModel sdm = new SpinnerDateModel(new Date(), null, null, java.util.Calendar.HOUR_OF_DAY);
        JSpinner dtSpinner = new JSpinner(sdm);
        JSpinner.DateEditor dtEditor = new JSpinner.DateEditor(dtSpinner, "yyyy-MM-dd  HH:mm");
        dtSpinner.setEditor(dtEditor);
        dtSpinner.setBackground(new Color(15,30,48)); dtSpinner.setForeground(TEXT);
        dtEditor.getTextField().setBackground(new Color(15,30,48)); dtEditor.getTextField().setForeground(TEXT);
        dtEditor.getTextField().setFont(new Font("Segoe UI",Font.PLAIN,13));
        dtEditor.getTextField().setCaretColor(TEXT);
        dtSpinner.setBorder(BorderFactory.createLineBorder(BORDER,1));
        dtSpinner.setMaximumSize(new Dimension(Integer.MAX_VALUE,36)); dtSpinner.setAlignmentX(LEFT_ALIGNMENT);

        JLabel res = resLbl();
        JButton btn = accentButton("Confirm Booking"); btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        addF(fi, "License Plate", pf);
        addF(fi, "Vehicle Type",  tb);
        // date spinner manually added
        JLabel dl = fieldLabel("Scheduled Date & Time"); dl.setAlignmentX(LEFT_ALIGNMENT); dl.setBorder(new EmptyBorder(4,0,3,0));
        fi.add(dl); fi.add(dtSpinner); fi.add(Box.createVerticalStrut(6));
        fi.add(Box.createVerticalStrut(10)); fi.add(btn); fi.add(res);

        btn.addActionListener(e -> {
            String plate = pf.getText().trim().toUpperCase();
            if (plate.isEmpty()) { setR(res, "Enter license plate.", DANGER); return; }
            Date selected = (Date) dtSpinner.getValue();
            LocalDateTime scheduled = selected.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
            if (scheduled.isBefore(LocalDateTime.now())) { setR(res, "Please select a future date/time.", DANGER); return; }
            btn.setEnabled(false); setR(res, "Booking...", ACCENT);
            new SwingWorker<Boolean, Void>() {
                protected Boolean doInBackground() {
                    Booking b = new Booking(plate, (String)tb.getSelectedItem(), user.getId(), scheduled);
                    return BookingDAO.createBooking(b);
                }
                protected void done() {
                    try {
                        if (get()) { setR(res, "Booked! Show ID at arrival.", SUCCESS); pf.setText(""); statusBar.setText("  Pre-booking confirmed for "+plate); }
                        else setR(res, "Failed to book. Try again.", DANGER);
                    } catch (Exception x) { setR(res, "Error: "+x.getMessage(), DANGER); }
                    finally { btn.setEnabled(true); }
                }
            }.execute();
        });

        JPanel right = infoCard("Booking Rules", new String[][]{
                {"How it works",  "Reserve in advance"},
                {"On arrival",    "Show booking to admin"},
                {"Admin confirms","Vehicle gets parked"},
                {"Cancel",        "From My Bookings tab"},
                {"Payment",       "Cash on exit"},
                {"Note",          "Slot assigned on arrival"},
        });

        cols.add(left); cols.add(right);
        root.add(cols, BorderLayout.CENTER);
        return root;
    }

    // ═══ MY BOOKINGS ══════════════════════════════════════════════════════════
    private JPanel myBookingsPanel() {
        JPanel root = new JPanel(new BorderLayout(0, 14));
        root.setBackground(BG); root.setBorder(new EmptyBorder(20, 20, 20, 20));
        root.add(hrow("My Bookings", "Your pre-bookings — cancel any PENDING booking"), BorderLayout.NORTH);

        String[] cols = {"ID","License Plate","Type","Scheduled Entry","Booked On","Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = styledTable(model);
        table.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, val, sel, foc, r, c);
                setBackground(sel ? TBL_SEL : (r%2==0?TBL_BG:TBL_ALT));
                String s = val == null ? "" : val.toString();
                setForeground(switch(s){ case "PENDING" -> new Color(255,152,0); case "ARRIVED","CONFIRMED" -> SUCCESS; case "CANCELLED" -> DANGER; default -> TEXT; });
                setFont(new Font("Segoe UI",Font.BOLD,12)); setBorder(new EmptyBorder(0,8,0,8)); return this;
            }
        });
        JScrollPane scroll = new JScrollPane(table); scroll.setBorder(BorderFactory.createLineBorder(BORDER,1)); scroll.getViewport().setBackground(TBL_BG);

        Runnable load = () -> new SwingWorker<List<Booking>,Void>(){
            protected List<Booking> doInBackground(){return BookingDAO.getBookingsByUserId(user.getId());}
            protected void done(){try{List<Booking>bks=get();model.setRowCount(0);DateTimeFormatter df=DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");for(Booking b:bks){model.addRow(new Object[]{b.getId(),b.getLicensePlate(),b.getVehicleType(),b.getScheduledEntry()!=null?b.getScheduledEntry().format(df):"-",b.getBookingTime()!=null?b.getBookingTime().format(df):"-",b.getStatus()});}}catch(Exception x){x.printStackTrace();}}
        }.execute();

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT,10,0)); btns.setOpaque(false);
        JButton cancelBtn = makeBtn("Cancel Selected Booking", DANGER, Color.WHITE);
        cancelBtn.setFont(new Font("Segoe UI",Font.BOLD,11)); cancelBtn.setBorderPainted(false); cancelBtn.setFocusPainted(false);
        JButton refBtn = makeBtn("Refresh", new Color(30,60,90), TEXT); refBtn.setFont(new Font("Segoe UI",Font.BOLD,11)); refBtn.setBorderPainted(false); refBtn.setFocusPainted(false);
        JLabel actRes = new JLabel(" "); actRes.setFont(new Font("Segoe UI",Font.BOLD,12)); actRes.setForeground(SUBTEXT);
        btns.add(cancelBtn); btns.add(refBtn); btns.add(actRes);

        cancelBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { actRes.setText("Select a booking row first."); actRes.setForeground(DANGER); return; }
            int id = (int) model.getValueAt(row, 0);
            String status = model.getValueAt(row, 5).toString();
            if (!"PENDING".equals(status)) { actRes.setText("Only PENDING bookings can be cancelled."); actRes.setForeground(DANGER); return; }
            BookingDAO.updateStatus(id, "CANCELLED");
            load.run(); actRes.setText("Booking #"+id+" cancelled."); actRes.setForeground(SUCCESS);
        });
        refBtn.addActionListener(e -> load.run());
        load.run();

        root.add(scroll, BorderLayout.CENTER);
        root.add(btns, BorderLayout.SOUTH);
        return root;
    }

    // ═══ RECEIPT ══════════════════════════════════════════════════════════════
    private String receipt(Vehicle v,Duration d,long act,long bil,double fee,boolean est){return String.format("============================================\n           AUTOPARK RECEIPT\n============================================\n  Plate      : %s\n  Type       : %s\n  Entry      : %s\n  Exit       : %s\n--------------------------------------------\n  Actual     : %dh %dm  (%d min)\n  Min Charge : %d min\n  Billed     : %d min\n--------------------------------------------\n  Rate       : Rs %.2f / hr\n  Per min    : Rs %.4f / min\n  TOTAL %s: Rs %.2f\n============================================\n  Payment    : CASH\n============================================",v.getLicensePlate(),v.getType(),v.getEntryTime().format(FMT),v.getExitTime()!=null?v.getExitTime().format(FMT):"-- Still Parked --",d.toHours(),act%60,act,AppConfig.MIN_CHARGE_MINUTES,bil,AppConfig.RATE_PER_HOUR,AppConfig.RATE_PER_HOUR/60.0,est?"EST  ":"BILL ",fee);}

    // ═══ TABLE HELPER ═════════════════════════════════════════════════════════
    private JTable styledTable(DefaultTableModel model){JTable table=new JTable(model);table.setBackground(TBL_BG);table.setForeground(TEXT);table.setFont(new Font("Segoe UI",Font.PLAIN,12));table.setRowHeight(34);table.setShowGrid(false);table.setIntercellSpacing(new Dimension(0,0));table.setSelectionBackground(TBL_SEL);table.setSelectionForeground(Color.WHITE);table.getTableHeader().setBackground(TBL_HDR);table.getTableHeader().setForeground(SUBTEXT);table.getTableHeader().setFont(new Font("Segoe UI",Font.BOLD,11));table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0,0,1,0,BORDER));DefaultTableCellRenderer cr=new DefaultTableCellRenderer(){public Component getTableCellRendererComponent(JTable t,Object val,boolean sel,boolean foc,int r,int c){super.getTableCellRendererComponent(t,val,sel,foc,r,c);setBackground(sel?TBL_SEL:(r%2==0?TBL_BG:TBL_ALT));setForeground(sel?Color.WHITE:TEXT);setFont(new Font("Segoe UI",Font.PLAIN,12));setBorder(new EmptyBorder(0,8,0,8));return this;}};for(int i=0;i<model.getColumnCount();i++)table.getColumnModel().getColumn(i).setCellRenderer(cr);return table;}

    // ═══ UI HELPERS ═══════════════════════════════════════════════════════════
    private JLabel h(String t){JLabel l=new JLabel(t);l.setFont(new Font("Segoe UI",Font.BOLD,20));l.setForeground(TEXT);return l;}
    private JLabel sub(String t){JLabel l=new JLabel(t);l.setFont(new Font("Segoe UI",Font.PLAIN,12));l.setForeground(SUBTEXT);return l;}
    private JPanel hrow(String title,String subtitle){JPanel r=new JPanel(new BorderLayout());r.setOpaque(false);r.setBorder(new EmptyBorder(0,0,14,0));r.add(h(title),BorderLayout.WEST);r.add(sub(subtitle),BorderLayout.EAST);return r;}
    private JPanel card2(String title){JPanel o=new JPanel(new BorderLayout(0,8));o.setBackground(CARD);o.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER,1),new EmptyBorder(16,18,18,18)));JLabel t=new JLabel(title);t.setFont(new Font("Segoe UI",Font.BOLD,13));t.setForeground(ACCENT);t.setBorder(new EmptyBorder(0,0,8,0));JPanel i=new JPanel();i.setOpaque(false);i.setLayout(new BoxLayout(i,BoxLayout.Y_AXIS));o.add(t,BorderLayout.NORTH);o.add(i,BorderLayout.CENTER);return o;}
    private JPanel inner(JPanel card){return(JPanel)card.getComponent(1);}
    private JPanel sr(String k,String v,Color vc){JPanel r=new JPanel(new BorderLayout());r.setOpaque(false);r.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0,0,1,0,BORDER),new EmptyBorder(8,0,8,0)));JLabel kl=new JLabel(k);kl.setFont(new Font("Segoe UI",Font.PLAIN,12));kl.setForeground(SUBTEXT);JLabel vl=new JLabel(v);vl.setFont(new Font("Segoe UI",Font.BOLD,13));vl.setForeground(vc);r.add(kl,BorderLayout.WEST);r.add(vl,BorderLayout.EAST);return r;}
    private JPanel formCard(String title){JPanel o=new JPanel(new BorderLayout(0,10));o.setBackground(CARD);o.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER,1),new EmptyBorder(20,20,20,20)));JLabel t=new JLabel(title);t.setFont(new Font("Segoe UI",Font.BOLD,15));t.setForeground(TEXT);t.setBorder(new EmptyBorder(0,0,10,0));JPanel i=new JPanel();i.setOpaque(false);i.setLayout(new BoxLayout(i,BoxLayout.Y_AXIS));o.add(t,BorderLayout.NORTH);o.add(i,BorderLayout.CENTER);return o;}
    private void addF(JPanel p,String label,JComponent field){JLabel l=fieldLabel(label);l.setAlignmentX(LEFT_ALIGNMENT);l.setBorder(new EmptyBorder(4,0,3,0));field.setAlignmentX(LEFT_ALIGNMENT);field.setMaximumSize(new Dimension(Integer.MAX_VALUE,36));p.add(l);p.add(field);p.add(Box.createVerticalStrut(6));}
    private JPanel infoCard(String title,String[][] rows){JPanel o=new JPanel(new BorderLayout(0,0));o.setBackground(CARD);o.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER,1),new EmptyBorder(20,20,20,20)));JLabel t=new JLabel(title);t.setFont(new Font("Segoe UI",Font.BOLD,14));t.setForeground(ACCENT);t.setBorder(new EmptyBorder(0,0,12,0));JPanel i=new JPanel(new GridLayout(rows.length,1,0,0));i.setOpaque(false);for(String[]row:rows){JPanel r=new JPanel(new BorderLayout());r.setOpaque(false);r.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0,0,1,0,BORDER),new EmptyBorder(9,0,9,0)));JLabel k=new JLabel(row[0]);k.setFont(new Font("Segoe UI",Font.PLAIN,12));k.setForeground(SUBTEXT);JLabel v=new JLabel(row[1]);v.setFont(new Font("Segoe UI",Font.BOLD,12));v.setForeground(TEXT);r.add(k,BorderLayout.WEST);r.add(v,BorderLayout.EAST);i.add(r);}o.add(t,BorderLayout.NORTH);o.add(i,BorderLayout.CENTER);return o;}
    private JLabel resLbl(){JLabel l=new JLabel(" ");l.setFont(new Font("Segoe UI",Font.BOLD,12));l.setForeground(SUBTEXT);l.setAlignmentX(LEFT_ALIGNMENT);return l;}
    private void setR(JLabel l,String t,Color c){l.setText(t);l.setForeground(c);}
    private JTextArea ta(){JTextArea a=new JTextArea();a.setBackground(new Color(10,22,35));a.setForeground(TEXT);a.setFont(new Font("Monospaced",Font.PLAIN,12));a.setBorder(new EmptyBorder(8,10,8,10));a.setEditable(false);a.setLineWrap(true);a.setWrapStyleWord(true);return a;}
    private JButton makeBtn(String t,Color bg,Color fg){JButton b=new JButton(t);b.setBackground(bg);b.setForeground(fg);b.setFont(new Font("Segoe UI",Font.BOLD,12));b.setBorderPainted(false);b.setFocusPainted(false);b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));return b;}
    private JButton pill(String t,Color bg,Color fg){JButton b=new JButton(t);b.setBackground(bg);b.setForeground(fg);b.setFont(new Font("Segoe UI",Font.BOLD,11));b.setBorderPainted(false);b.setFocusPainted(false);b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));return b;}
    private JButton flat(String t){JButton b=new JButton(t);b.setFont(new Font("Segoe UI",Font.PLAIN,12));b.setForeground(SUBTEXT);b.setBackground(new Color(0,0,0,0));b.setBorderPainted(false);b.setFocusPainted(false);b.setContentAreaFilled(false);b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));return b;}
    private Point dragStart;
    private void drag(JPanel bar){bar.addMouseListener(new MouseAdapter(){public void mousePressed(MouseEvent e){dragStart=e.getPoint();}});bar.addMouseMotionListener(new MouseMotionAdapter(){public void mouseDragged(MouseEvent e){if(dragStart!=null){Point l=getLocation();setLocation(l.x+e.getX()-dragStart.x,l.y+e.getY()-dragStart.y);}}});}
}