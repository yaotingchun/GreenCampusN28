/* 
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;
import core.*;
import structure.Area;
import Control.Admin;

public class FloorPlanApp extends JFrame {

    // ─── Data model ──────────────────────────────────────────────────────────
    // VisualAppliance: wraps core.Appliance with GUI positioning
    static class VisualAppliance {
        final core.Appliance coreAppliance;
        int x, y;  // Visual position on canvas
        
        VisualAppliance(core.Appliance appliance, int x, int y) {
            this.coreAppliance = appliance;
            this.x = x;
            this.y = y;
        }
        
        String getType() {
            if (coreAppliance instanceof Fan) return "FAN";
            if (coreAppliance instanceof LightDevice) return "LIGHT";
            if (coreAppliance instanceof AirConditioner) return "AC";
            return "UNKNOWN";
        }
        
        String getCode() {
            return coreAppliance.getApplianceID();
        }
        
        String getStatus() {
            return coreAppliance.getStatus();
        }
        
        void cycleStatus(Admin admin) {
            String current = coreAppliance.getStatus();
            if ("OFF".equalsIgnoreCase(current)) {
                coreAppliance.turnOn(admin);
            } else if ("ON".equalsIgnoreCase(current)) {
                coreAppliance.setStatus("FAULT");
            } else {
                coreAppliance.turnOff(admin);
            }
        }
    }

    enum ApplianceType { FAN, LIGHT, AC }
    enum ApplianceStatus { ACTIVE, FAULT, OFF }

    static final Color STATUS_ACTIVE = new Color(0x4DA6FF);
    static final Color STATUS_FAULT  = new Color(0xE05252);
    static final Color STATUS_OFF    = new Color(0xB0B0B0);

    static class Room {
        String id, label;
        int x, y, w, h;
        Color fill;
        Room(String id, String label, int x, int y, int w, int h, Color fill) {
            this.id = id; this.label = label;
            this.x = x; this.y = y; this.w = w; this.h = h; this.fill = fill;
        }
        boolean contains(int px, int py) {
            return px >= x && px < x + w && py >= y && py < y + h;
        }
    }

    // ─── Floor layout constants ───────────────────────────────────────────────
    // The building is organised into 4 floors, each drawn as a separate row.
    // Rooms are placed to faithfully replicate the hand-drawn sketch (Image 1).
    // All coordinates are in the canvas coordinate system (pixels).

    static final int CANVAS_W = 1200;
    static final int CANVAS_H = 1200;

    static final Color COL_CLASSROOM   = new Color(0xFFF4FB);
    static final Color COL_LECTURE     = new Color(0xF0F4FF);
    static final Color COL_LOUNGE      = new Color(0xFFF8EC);
    static final Color COL_STUDY       = new Color(0xF0FFF4);
    static final Color COL_OFFICE      = new Color(0xFBF4FF);
    static final Color COL_MEETING     = new Color(0xFFF0F4);
    static final Color COL_COMMON      = new Color(0xF0FAFF);
    static final Color COL_TOILET      = new Color(0xF5F5F5);
    static final Color COL_CORRIDOR    = new Color(0xFCFCFC);
    static final Color COL_FLOOR       = new Color(0xF9F0FF);

    // ─── Room definitions (matching the 4-floor sketch, scaled 1.33x) ───────
    // Floor 1  y=13..287
    // Floor 2  y=300..600
    // Floor 3  y=613..813
    // Floor 4  y=827..1040
    static final Room[] ROOMS = {
        // ── FLOOR 1 ──────────────────────────────────────────────────────────
        // Row A: 3 classrooms + toilet (top row of floor 1)
        new Room("F1-CLA1", "Classroom",   27,  13, 260, 120, COL_CLASSROOM),
        new Room("F1-COR1", "Corridor",   287,  13,  53, 120, COL_CORRIDOR),
        new Room("F1-CLA2", "Classroom",  340,  13, 253, 120, COL_CLASSROOM),
        new Room("F1-CLA3", "Classroom",  593,  13, 313, 120, COL_CLASSROOM),
        new Room("F1-TOI1", "Toilet",     907,  13,  80, 120, COL_TOILET),
        // Corridor between rows
        new Room("F1-CORR", "Corridor",    27, 133, 960, 40,  COL_CORRIDOR),
        // Row B: classroom + toilet + 2 lecture halls (bottom row of floor 1)
        new Room("F1-CLA4", "Classroom",   27, 173, 193, 113, COL_CLASSROOM),
        new Room("F1-TOI2", "Toilet",     220, 173,  80, 113, COL_TOILET),
        new Room("F1-LEC1", "Lecture Hall",300, 173, 313, 113, COL_LECTURE),
        new Room("F1-LEC2", "Lecture Hall",613, 173, 373, 113, COL_LECTURE),

        // ── FLOOR 2 ──────────────────────────────────────────────────────────
        // Left: student lounge + study/meeting
        new Room("F2-LNG",  "Student Lounge",  27, 300, 280, 200, COL_LOUNGE),
        new Room("F2-STD",  "Study/Meeting Rm", 27, 507, 280, 80,  COL_STUDY),
        // Right top: 3 classrooms + toilet
        new Room("F2-CLA1", "Classroom",  313, 300, 187, 127, COL_CLASSROOM),
        new Room("F2-CLA2", "Classroom",  500, 300, 187, 127, COL_CLASSROOM),
        new Room("F2-CLA3", "Classroom",  687, 300, 187, 127, COL_CLASSROOM),
        new Room("F2-TOI1", "Toilet",     873, 300, 113, 127, COL_TOILET),
        // Corridor
        new Room("F2-CORR", "Corridor",   313, 433, 673, 33,  COL_CORRIDOR),
        // Right bottom: toilet + 3 classrooms
        new Room("F2-TOI2", "Toilet",     313, 467,  93, 120, COL_TOILET),
        new Room("F2-CLA4", "Classroom",  407, 467, 200, 120, COL_CLASSROOM),
        new Room("F2-CLA5", "Classroom",  607, 467, 200, 120, COL_CLASSROOM),
        new Room("F2-CLA6", "Classroom",  807, 467, 180, 120, COL_CLASSROOM),

        // ── FLOOR 3 ──────────────────────────────────────────────────────────
        // Left block: meeting room + office
        new Room("F3-MTG1", "Meeting Room",  27, 613, 227, 133, COL_MEETING),
        new Room("F3-OFF1", "Office",       253, 613, 133, 133, COL_OFFICE),
        // Right block: 5 offices + toilet
        new Room("F3-OFF2", "Office",       387, 613, 133, 133, COL_OFFICE),
        new Room("F3-OFF3", "Office",       520, 613, 133, 133, COL_OFFICE),
        new Room("F3-OFF4", "Office",       653, 613, 133, 133, COL_OFFICE),
        new Room("F3-OFF5", "Office",       787, 613, 133, 133, COL_OFFICE),
        new Room("F3-OFF6", "Office",       920, 613, 133, 133, COL_OFFICE),
        new Room("F3-TOI1", "Toilet",      1053, 613, 113, 133, COL_TOILET),

        // ── FLOOR 4 ──────────────────────────────────────────────────────────
        // Left: common area
        new Room("F4-CMN",  "Common Area",   27, 827, 313, 147, COL_COMMON),
        // Right: toilet + meeting room + 3 offices
        new Room("F4-TOI1", "Toilet",       347, 827,  93, 147, COL_TOILET),
        new Room("F4-MTG1", "Meeting Room", 440, 827, 213, 147, COL_MEETING),
        new Room("F4-OFF1", "Office",       653, 827, 160, 147, COL_OFFICE),
        new Room("F4-OFF2", "Office",       813, 827, 160, 147, COL_OFFICE),
        new Room("F4-OFF3", "Office",       973, 827, 200, 147, COL_OFFICE),
    };

    // Room-code prefix map
    static String roomPrefix(String roomId) {
        String lbl = roomId.toUpperCase();
        if (lbl.contains("CLA"))    return "CLA";
        if (lbl.contains("LEC"))    return "LEC";
        if (lbl.contains("LNG"))    return "LNG";
        if (lbl.contains("STD"))    return "STD";
        if (lbl.contains("MTG"))    return "MTG";
        if (lbl.contains("OFF"))    return "OFF";
        if (lbl.contains("CMN"))    return "CMN";
        if (lbl.contains("TOI"))    return "TOI";
        if (lbl.contains("COR"))    return "COR";
        return "GEN";
    }

    static String typePrefix(ApplianceType t) {
        return switch (t) { case FAN -> "FAN"; case LIGHT -> "LT"; case AC -> "AC"; };
    }

    // ─── State ────────────────────────────────────────────────────────────────
    final List<VisualAppliance> appliances = new ArrayList<>();
    final Map<String, Integer> counters = new HashMap<>();
    boolean placing = false;
    ApplianceType placeType = ApplianceType.FAN;
    Admin admin = new Admin("GreenCampusAdmin", "admin123");  // Default admin for appliance control

    // ─── UI refs ──────────────────────────────────────────────────────────────
    JTabbedPane floorTabs;
    FloorCanvas[] floorCanvases;
    JTable table;
    DefaultTableModel tableModel;
    JLabel hintLabel;
    JComboBox<String> typeCombo;

    // ─── Entry point ──────────────────────────────────────────────────────────
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FloorPlanApp app = new FloorPlanApp();
            app.setVisible(true);
        });
    }

    FloorPlanApp() {
        super("Building Floor Plan — Appliance Manager");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(6, 6));
        getContentPane().setBackground(new Color(0xF5F0FF));

        // ── Top toolbar ──
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        toolbar.setBackground(new Color(0xF5F0FF));
        toolbar.setBorder(BorderFactory.createEmptyBorder(4, 8, 2, 8));

        typeCombo = new JComboBox<>(new String[]{"Fan", "Light", "Air Conditioner"});
        styleCombo(typeCombo);

        JButton addBtn = styledButton("+ Place Appliance", new Color(0xC065C0));
        JButton clearBtn = styledButton("Clear All", new Color(0xE05252));

        hintLabel = new JLabel(" ");
        hintLabel.setFont(new Font("SansSerif", Font.ITALIC, 11));
        hintLabel.setForeground(Color.BLACK);

        toolbar.add(new JLabel("Type:"));
        toolbar.add(typeCombo);
        toolbar.add(addBtn);
        toolbar.add(clearBtn);
        toolbar.add(hintLabel);
        add(toolbar, BorderLayout.NORTH);

        // ── Floor canvases (separate tabs for first/second/third floors) ──
        floorCanvases = new FloorCanvas[3];
        floorCanvases[0] = new FloorCanvas(13, 274, "FLOOR 1");
        floorCanvases[1] = new FloorCanvas(300, 300, "FLOOR 2");
        floorCanvases[2] = new FloorCanvas(613, 200, "FLOOR 3");

        floorTabs = new JTabbedPane();
        floorTabs.addTab("First Floor", new JScrollPane(floorCanvases[0]));
        floorTabs.addTab("Second Floor", new JScrollPane(floorCanvases[1]));
        floorTabs.addTab("Third Floor", new JScrollPane(floorCanvases[2]));
        add(floorTabs, BorderLayout.CENTER);

        // (default appliances will be loaded after UI table is created)

        // ── Registry table ──
        String[] cols = {"Code", "Type", "Room", "Status", "Cycle Status", "Remove"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        styleTable(table);

        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (row < 0 || row >= appliances.size()) return;
                if (col == 4) { // Cycle
                    appliances.get(row).cycleStatus(admin);
                    refreshTable(); for (FloorCanvas fc : floorCanvases) fc.repaint();
                } else if (col == 5) { // Remove
                    appliances.remove(row);
                    refreshTable(); for (FloorCanvas fc : floorCanvases) fc.repaint();
                }
            }
        });

        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setPreferredSize(new Dimension(CANVAS_W, 180));
        tableScroll.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.BLACK),
            "Appliance Registry",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("SansSerif", Font.BOLD, 12),
            Color.BLACK
        ));

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(new Color(0xF5F0FF));
        bottom.setBorder(BorderFactory.createEmptyBorder(4, 8, 8, 8));
        bottom.add(tableScroll, BorderLayout.CENTER);

        // Legend
        JPanel legend = buildLegend();
        bottom.add(legend, BorderLayout.NORTH);
        add(bottom, BorderLayout.SOUTH);

        // ── Actions ──
        addBtn.addActionListener(e -> {
            placing = true;
            String sel = (String) typeCombo.getSelectedItem();
            placeType = sel.startsWith("Fan") ? ApplianceType.FAN
                      : sel.startsWith("Air") ? ApplianceType.AC
                      : ApplianceType.LIGHT;
            floorCanvases[floorTabs.getSelectedIndex()].setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
            hintLabel.setText("Click inside a room to place " + sel);
        });

        clearBtn.addActionListener(e -> {
            appliances.clear(); counters.clear(); placing = false;
            hintLabel.setText(" ");
            if (floorCanvases != null) for (FloorCanvas fc : floorCanvases) fc.setCursor(Cursor.getDefaultCursor());
            refreshTable(); if (floorCanvases != null) for (FloorCanvas fc : floorCanvases) fc.repaint();
        });

        pack();
        setLocationRelativeTo(null);
        // Populate defaults now that the table model exists
        initDefaultAppliances();
        addBtn.setEnabled(true);
        hintLabel.setText("Default appliances loaded — click room to add more");
        // final refresh
        refreshTable();
    }

    // ─── Canvas ───────────────────────────────────────────────────────────────
    class FloorCanvas extends JPanel {
        final int viewY;
        final int viewH;
        final String label;

        FloorCanvas(int viewY, int viewH, String label) {
            this.viewY = viewY;
            this.viewH = viewH;
            this.label = label;
            setBackground(Color.WHITE);
            setPreferredSize(new Dimension(CANVAS_W, viewH));

            addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) {
                    int gx = e.getX();
                    int gy = e.getY() + viewY; // global coords
                    if (placing) {
                        Room room = roomAt(gx, gy);
                        if (room == null) return;
                        String code = generateCode(placeType, room);
                        appliances.add(new Appliance(code, room.id, room.label, placeType, gx, gy));
                        placing = false;
                        hintLabel.setText(" ");
                        setCursor(Cursor.getDefaultCursor());
                        refreshTable(); repaint();
                    } else {
                        Appliance ap = applianceNear(gx, gy);
                        if (ap != null) {
                            ap.status = ap.nextStatus();
                            refreshTable(); repaint();
                        }
                    }
                }
            });

            addMouseMotionListener(new MouseMotionAdapter() {
                @Override public void mouseMoved(MouseEvent e) {
                    int gx = e.getX();
                    int gy = e.getY() + viewY;
                    Appliance ap = applianceNear(gx, gy);
                    if (ap != null && !placing) {
                        setToolTipText(ap.code + " | " + ap.type + " | " + ap.status);
                        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    } else {
                        setToolTipText(null);
                        setCursor(placing ? Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR) : Cursor.getDefaultCursor());
                    }
                }
            });
        }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            drawFloorBand(g2, label, 5, 0, viewH);

            for (Room r : ROOMS) {
                if (r.y >= viewY && r.y < viewY + viewH) drawRoom(g2, r, viewY);
            }

            for (VisualAppliance ap : appliances) {
                if (ap.y >= viewY && ap.y < viewY + viewH) drawAppliance(g2, ap, viewY);
            }
        }

        void drawFloorBand(Graphics2D g2, String label, int x, int y, int h) {
            g2.setColor(new Color(0xEEDDFF));
            g2.fillRoundRect(x, y, CANVAS_W - 10, h, 14, 14);
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(2f));
            g2.drawRoundRect(x, y, CANVAS_W - 10, h, 14, 14);

            g2.setFont(new Font("SansSerif", Font.BOLD, 11));
            g2.setColor(Color.BLACK);
            g2.drawString(label, x + 6, y + 13);
        }

        void drawRoom(Graphics2D g2, Room r, int offsetY) {
            int rx = r.x;
            int ry = r.y - offsetY;
            g2.setColor(r.fill);
            g2.fillRect(rx, ry, r.w, r.h);
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRect(rx, ry, r.w, r.h);
            if (r.id.contains("COR")) return;
            g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
            g2.setColor(Color.BLACK);
            FontMetrics fm = g2.getFontMetrics();
            String lbl = r.label;
            List<String> lines = wrapText(lbl, r.w - 6, fm);
            int totalH = lines.size() * fm.getHeight();
            int startY = ry + (r.h - totalH) / 2 + fm.getAscent();
            for (String line : lines) {
                int tw = fm.stringWidth(line);
                g2.drawString(line, rx + (r.w - tw) / 2, startY);
                startY += fm.getHeight();
            }
            drawFurnitureHint(g2, r, offsetY);
        }

        void drawFurnitureHint(Graphics2D g2, Room r, int offsetY) {
            g2.setColor(new Color(0xDDAACE, true));
            g2.setStroke(new BasicStroke(0.8f));
            String id = r.id;
            int rx = r.x;
            int ry = r.y - offsetY;

            if (id.contains("CLA") || id.contains("LEC")) {
                int deskY = ry + r.h - 18;
                int startX = rx + 8;
                int cols = Math.max(1, (r.w - 16) / 18);
                for (int i = 0; i < Math.min(cols, 6); i++) g2.drawRect(startX + i * 18, deskY, 12, 8);
                int wY = ry + 4;
                for (int i = 0; i < Math.min(cols, 4); i++) g2.drawRect(startX + i * 22, wY, 14, 10);
            } else if (id.contains("OFF")) {
                g2.drawRect(rx + r.w/2 - 18, ry + r.h/2 + 4, 30, 18);
                g2.drawRect(rx + 4, ry + 6, 20, 12);
            } else if (id.contains("MTG")) {
                g2.drawRect(rx + 14, ry + r.h/2 - 8, r.w - 28, 16);
                int seats = Math.max(1, (r.w - 28) / 18);
                for (int i = 0; i < seats; i++) {
                    g2.drawRect(rx + 14 + i * 18, ry + r.h/2 - 18, 12, 8);
                    g2.drawRect(rx + 14 + i * 18, ry + r.h/2 + 10, 12, 8);
                }
            } else if (id.contains("LNG")) {
                g2.drawOval(rx + r.w/2 - 20, ry + r.h/2 - 20, 38, 38);
                g2.drawOval(rx + 12, ry + 25, 16, 20);
                g2.drawOval(rx + r.w - 30, ry + 25, 16, 20);
                g2.drawOval(rx + 12, ry + r.h - 50, 16, 20);
                g2.drawOval(rx + r.w - 30, ry + r.h - 50, 16, 20);
            } else if (id.contains("CMN")) {
                g2.fillOval(rx + 20, ry + 20, 40, 25);
                g2.drawRect(rx + 10, ry + r.h - 30, 30, 16);
                g2.drawRect(rx + 50, ry + r.h - 30, 30, 16);
            } else if (id.contains("STD")) {
                g2.drawRect(rx + 14, ry + 12, r.w - 28, 14);
            }
        }

    // Helper: convert core.Appliance status string to Color
    static Color getStatusColor(String status) {
        if ("ON".equalsIgnoreCase(status)) return STATUS_ACTIVE;
        if ("FAULT".equalsIgnoreCase(status)) return STATUS_FAULT;
        return STATUS_OFF;
    }

        void drawAppliance(Graphics2D g2, VisualAppliance ap, int offsetY) {
            int x = ap.x, y = ap.y - offsetY;
            Color c = getStatusColor(ap.getStatus());
            String typeStr = ap.getType();
            int R = 10;
            g2.setStroke(new BasicStroke(1.5f));

            switch (typeStr) {
                case "FAN" -> {
                    g2.setColor(c);
                    g2.fillOval(x - R, y - R, R * 2, R * 2);
                    g2.setColor(Color.DARK_GRAY);
                    g2.drawOval(x - R, y - R, R * 2, R * 2);
                    g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 160));
                    for (int deg = 0; deg < 360; deg += 90) {
                        double rad = Math.toRadians(deg);
                        int bx = (int)(x + Math.cos(rad) * R * 0.55);
                        int by = (int)(y + Math.sin(rad) * R * 0.55);
                        g2.fillOval(bx - 4, by - 3, 8, 5);
                    }
                    g2.setColor(Color.DARK_GRAY);
                    g2.fillOval(x - 3, y - 3, 6, 6);
                }
                case "LIGHT" -> {
                    g2.setColor(c);
                    g2.fillOval(x - R + 2, y - R, R * 2 - 4, R * 2 - 4);
                    g2.setColor(Color.DARK_GRAY);
                    g2.drawOval(x - R + 2, y - R, R * 2 - 4, R * 2 - 4);
                    g2.setColor(c.darker());
                    for (int deg = 0; deg < 360; deg += 45) {
                        double rad = Math.toRadians(deg);
                        g2.drawLine(
                            (int)(x + Math.cos(rad) * (R + 1)),
                            (int)(y + Math.sin(rad) * (R + 1)),
                            (int)(x + Math.cos(rad) * (R + 4)),
                            (int)(y + Math.sin(rad) * (R + 4))
                        );
                    }
                }
                case "AC" -> {
                    g2.setColor(c);
                    g2.fillRoundRect(x - R - 4, y - 6, (R + 4) * 2, 12, 4, 4);
                    g2.setColor(Color.DARK_GRAY);
                    g2.drawRoundRect(x - R - 4, y - 6, (R + 4) * 2, 12, 4, 4);
                    g2.setColor(new Color(80, 80, 80));
                    for (int vx = x - R; vx < x + R; vx += 4) g2.drawLine(vx, y - 4, vx + 2, y + 3);
                    g2.setColor(c.darker());
                    g2.fillOval(x - 3, y - R - 6, 6, 6);
                    g2.setColor(Color.DARK_GRAY);
                    g2.drawOval(x - 3, y - R - 6, 6, 6);
                }
            }

            // Code label
            g2.setFont(new Font("Monospaced", Font.BOLD, 8));
            g2.setColor(Color.DARK_GRAY);
            FontMetrics fm = g2.getFontMetrics();
            String code = ap.getCode();
            int tw = fm.stringWidth(code);
            g2.setColor(new Color(255, 255, 255, 200));
            g2.fillRoundRect(x - tw / 2 - 2, y + R + 1, tw + 4, 10, 3, 3);
            g2.setColor(Color.BLACK);
            g2.drawString(code, x - tw / 2, y + R + 10);
        }
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    Room roomAt(int px, int py) {
        for (Room r : ROOMS) {
            if (r.id.contains("COR")) continue; // don't place in corridors
            if (r.contains(px, py)) return r;
        }
        return null;
    }

    VisualAppliance applianceNear(int px, int py) {
        for (VisualAppliance ap : appliances) {
            if (Math.hypot(ap.x - px, ap.y - py) < 14) return ap;
        }
        return null;
    }

    String generateCode(ApplianceType type, Room room) {
        String key = typePrefix(type) + "-" + roomPrefix(room.id);
        int n = counters.getOrDefault(key, 0) + 1;
        counters.put(key, n);
        return String.format("%s-%s-%02d", typePrefix(type), roomPrefix(room.id), n);
    }

    // Create a real core.Appliance object based on type
    core.Appliance createCoreAppliance(ApplianceType type, String code) {
        String status = "ON";  // Default to ON
        return switch (type) {
            case FAN -> new Fan(code, status);
            case LIGHT -> new LightDevice(code, status);
            case AC -> new AirConditioner(code, status);
        };
    }

    static List<String> wrapText(String text, int maxW, FontMetrics fm) {
        List<String> lines = new ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder cur = new StringBuilder();
        for (String w : words) {
            String test = cur.isEmpty() ? w : cur + " " + w;
            if (fm.stringWidth(test) <= maxW) {
                cur = new StringBuilder(test);
            } else {
                if (!cur.isEmpty()) lines.add(cur.toString());
                cur = new StringBuilder(w);
            }
        }
        if (!cur.isEmpty()) lines.add(cur.toString());
        return lines;
    }

    void refreshTable() {
        tableModel.setRowCount(0);
        for (Appliance ap : appliances) {
            tableModel.addRow(new Object[]{
                ap.code,
                ap.type.toString(),
                ap.roomLabel,
                ap.status.toString(),
                "→ " + ap.nextStatus(),
                "✕ Remove"
            });
        }
        // Color rows by status
        table.repaint();
    }

    // Populate the map with default appliances placed to strictly follow the floor plan sketch.
    // Appliances positioned at edges/corners to avoid covering room labels.
    void initDefaultAppliances() {
        appliances.clear(); counters.clear();
        
        // ── FLOOR 1 ──
        // Top row classrooms: 2 lights (top corners), 1 AC (top right), 1 fan (top left)
        addDefaultAppliance(roomById("F1-CLA1"), ApplianceType.LIGHT, 40, 15);
        addDefaultAppliance(roomById("F1-CLA1"), ApplianceType.LIGHT, 220, 15);
        addDefaultAppliance(roomById("F1-CLA1"), ApplianceType.AC, 230, 100);
        addDefaultAppliance(roomById("F1-CLA1"), ApplianceType.FAN, 30, 100);
        
        addDefaultAppliance(roomById("F1-CLA2"), ApplianceType.LIGHT, 30, 15);
        addDefaultAppliance(roomById("F1-CLA2"), ApplianceType.LIGHT, 220, 15);
        addDefaultAppliance(roomById("F1-CLA2"), ApplianceType.AC, 235, 100);
        addDefaultAppliance(roomById("F1-CLA2"), ApplianceType.FAN, 30, 100);
        
        addDefaultAppliance(roomById("F1-CLA3"), ApplianceType.LIGHT, 40, 15);
        addDefaultAppliance(roomById("F1-CLA3"), ApplianceType.LIGHT, 270, 15);
        addDefaultAppliance(roomById("F1-CLA3"), ApplianceType.AC, 290, 100);
        addDefaultAppliance(roomById("F1-CLA3"), ApplianceType.FAN, 30, 100);
        
        addDefaultAppliance(roomById("F1-TOI1"), ApplianceType.LIGHT, 40, 15);
        
        // Corridor
        addDefaultAppliance(roomById("F1-CORR"), ApplianceType.FAN, 480, 20);
        
        // Bottom row
        addDefaultAppliance(roomById("F1-CLA4"), ApplianceType.LIGHT, 30, 15);
        addDefaultAppliance(roomById("F1-CLA4"), ApplianceType.AC, 160, 90);
        addDefaultAppliance(roomById("F1-TOI2"), ApplianceType.LIGHT, 40, 15);
        
        // Lecture halls: 3 AC units each spread across bottom
        addDefaultAppliance(roomById("F1-LEC1"), ApplianceType.AC, 60, 95);
        addDefaultAppliance(roomById("F1-LEC1"), ApplianceType.AC, 155, 95);
        addDefaultAppliance(roomById("F1-LEC1"), ApplianceType.AC, 250, 95);
        
        addDefaultAppliance(roomById("F1-LEC2"), ApplianceType.AC, 80, 95);
        addDefaultAppliance(roomById("F1-LEC2"), ApplianceType.AC, 185, 95);
        addDefaultAppliance(roomById("F1-LEC2"), ApplianceType.AC, 290, 95);
        
        // ── FLOOR 2 ──
        // Student Lounge: 8 fans arranged around edges
        addDefaultAppliance(roomById("F2-LNG"), ApplianceType.FAN, 15, 20);
        addDefaultAppliance(roomById("F2-LNG"), ApplianceType.FAN, 265, 20);
        addDefaultAppliance(roomById("F2-LNG"), ApplianceType.FAN, 15, 180);
        addDefaultAppliance(roomById("F2-LNG"), ApplianceType.FAN, 265, 180);
        addDefaultAppliance(roomById("F2-LNG"), ApplianceType.FAN, 140, 15);
        addDefaultAppliance(roomById("F2-LNG"), ApplianceType.FAN, 140, 185);
        addDefaultAppliance(roomById("F2-LNG"), ApplianceType.LIGHT, 80, 100);
        addDefaultAppliance(roomById("F2-LNG"), ApplianceType.LIGHT, 200, 100);
        
        // Study/Meeting room
        addDefaultAppliance(roomById("F2-STD"), ApplianceType.FAN, 30, 15);
        addDefaultAppliance(roomById("F2-STD"), ApplianceType.LIGHT, 240, 15);
        
        // Right side classrooms: 1 light (top), 1 AC (bottom)
        addDefaultAppliance(roomById("F2-CLA1"), ApplianceType.LIGHT, 93, 20);
        addDefaultAppliance(roomById("F2-CLA1"), ApplianceType.AC, 160, 100);
        
        addDefaultAppliance(roomById("F2-CLA2"), ApplianceType.LIGHT, 93, 20);
        addDefaultAppliance(roomById("F2-CLA2"), ApplianceType.AC, 160, 100);
        
        addDefaultAppliance(roomById("F2-CLA3"), ApplianceType.LIGHT, 93, 20);
        addDefaultAppliance(roomById("F2-CLA3"), ApplianceType.AC, 160, 100);
        
        addDefaultAppliance(roomById("F2-TOI1"), ApplianceType.LIGHT, 56, 20);
        
        // Bottom right
        addDefaultAppliance(roomById("F2-TOI2"), ApplianceType.LIGHT, 45, 20);
        
        addDefaultAppliance(roomById("F2-CLA4"), ApplianceType.LIGHT, 100, 20);
        addDefaultAppliance(roomById("F2-CLA4"), ApplianceType.AC, 175, 90);
        
        addDefaultAppliance(roomById("F2-CLA5"), ApplianceType.LIGHT, 100, 20);
        addDefaultAppliance(roomById("F2-CLA5"), ApplianceType.AC, 175, 90);
        
        addDefaultAppliance(roomById("F2-CLA6"), ApplianceType.LIGHT, 90, 20);
        addDefaultAppliance(roomById("F2-CLA6"), ApplianceType.AC, 155, 90);
        
        // ── FLOOR 3 ──
        // Meeting room: lights at edges, AC at corner
        addDefaultAppliance(roomById("F3-MTG1"), ApplianceType.LIGHT, 60, 20);
        addDefaultAppliance(roomById("F3-MTG1"), ApplianceType.LIGHT, 160, 20);
        addDefaultAppliance(roomById("F3-MTG1"), ApplianceType.AC, 190, 100);
        addDefaultAppliance(roomById("F3-MTG1"), ApplianceType.FAN, 30, 100);
        
        // Offices: 1 AC each at bottom right corner
        addDefaultAppliance(roomById("F3-OFF1"), ApplianceType.AC, 105, 105);
        addDefaultAppliance(roomById("F3-OFF2"), ApplianceType.AC, 105, 105);
        addDefaultAppliance(roomById("F3-OFF3"), ApplianceType.AC, 105, 105);
        addDefaultAppliance(roomById("F3-OFF4"), ApplianceType.AC, 105, 105);
        addDefaultAppliance(roomById("F3-OFF5"), ApplianceType.AC, 105, 105);
        addDefaultAppliance(roomById("F3-OFF6"), ApplianceType.AC, 105, 105);
        
        addDefaultAppliance(roomById("F3-TOI1"), ApplianceType.LIGHT, 56, 20);
        
        refreshTable();
    }
    
    // Helper to find room by ID
    Room roomById(String id) {
        for (Room r : ROOMS) {
            if (r.id.equals(id)) return r;
        }
        return null;
    }

    void addDefaultAppliance(Room r, ApplianceType type, int relX, int relY) {
        int gx = r.x + Math.max(8, Math.min(r.w - 8, relX));
        int gy = r.y + Math.max(8, Math.min(r.h - 8, relY));
        String code = generateCode(type, r);
        core.Appliance realAppliance = createCoreAppliance(type, code);
        VisualAppliance visual = new VisualAppliance(realAppliance, gx, gy);
        appliances.add(visual);
    }

    void styleTable(JTable t) {
        t.setRowHeight(24);
        t.setFont(new Font("Monospaced", Font.PLAIN, 11));
        t.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 11));
        t.getTableHeader().setBackground(new Color(0xEEDDFF));
        t.getTableHeader().setForeground(Color.BLACK);
        t.setSelectionBackground(new Color(0xDDAAFF));
        t.setGridColor(new Color(0xDDCCEE));
        t.setShowGrid(true);

        // Custom renderer for status column
        t.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable tbl, Object val, boolean sel, boolean foc, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(tbl, val, sel, foc, row, col);
                if (row < appliances.size()) {
                    String status = appliances.get(row).getStatus();
                    Color statusColor = "ON".equalsIgnoreCase(status) ? new Color(0x2E7D32) :
                                        "FAULT".equalsIgnoreCase(status) ? new Color(0xC62828) :
                                        new Color(0x555555);
                    lbl.setForeground(statusColor);
                    lbl.setFont(new Font("SansSerif", Font.BOLD, 11));
                }
                return lbl;
            }
        });

        // Action columns styling
        for (int c : new int[]{4, 5}) {
            t.getColumnModel().getColumn(c).setCellRenderer(new DefaultTableCellRenderer() {
                @Override public Component getTableCellRendererComponent(
                        JTable tbl, Object val, boolean sel, boolean foc, int row, int col) {
                    JLabel lbl = (JLabel) super.getTableCellRendererComponent(tbl, val, sel, foc, row, col);
                    lbl.setForeground(col == 5 ? new Color(0xC62828) : Color.BLACK);
                    lbl.setHorizontalAlignment(CENTER);
                    lbl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    return lbl;
                }
            });
        }

        t.getColumnModel().getColumn(0).setPreferredWidth(130);
        t.getColumnModel().getColumn(1).setPreferredWidth(80);
        t.getColumnModel().getColumn(2).setPreferredWidth(130);
        t.getColumnModel().getColumn(3).setPreferredWidth(80);
        t.getColumnModel().getColumn(4).setPreferredWidth(100);
        t.getColumnModel().getColumn(5).setPreferredWidth(80);
    }

    JButton styledButton(String text, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setForeground(fg);
        btn.setBackground(Color.WHITE);
        btn.setBorder(BorderFactory.createLineBorder(fg, 1));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    void styleCombo(JComboBox<String> cb) {
        cb.setFont(new Font("SansSerif", Font.PLAIN, 12));
        cb.setBackground(Color.WHITE);
        cb.setForeground(Color.BLACK);
    }

    JPanel buildLegend() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
        p.setBackground(new Color(0xF5F0FF));
        p.add(legendItem(STATUS_ACTIVE, "Active (blue)"));
        p.add(legendItem(STATUS_FAULT,  "Fault (red)"));
        p.add(legendItem(STATUS_OFF,    "Off (grey)"));
        JLabel tip = new JLabel("  Click appliance on plan or table to cycle status");
        tip.setFont(new Font("SansSerif", Font.ITALIC, 11));
        tip.setForeground(Color.BLACK);
        p.add(tip);
        return p;
    }

    JPanel legendItem(Color c, String label) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        item.setBackground(new Color(0xF5F0FF));
        JPanel dot = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(c);
                g.fillOval(1, 1, 12, 12);
                g.setColor(Color.GRAY);
                ((Graphics2D)g).drawOval(1, 1, 12, 12);
            }
        };
        dot.setPreferredSize(new Dimension(15, 15));
        dot.setBackground(new Color(0xF5F0FF));
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lbl.setForeground(new Color(0x555555));
        item.add(dot);
        item.add(lbl);
        return item;
    }
}
*/