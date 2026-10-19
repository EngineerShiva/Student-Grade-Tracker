import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

/**
 * Single-file runnable Student Grade Tracker.
 * Save as StudentGradeTrackerFull.java
 * Compile: javac StudentGradeTrackerFull.java
 * Run:     java StudentGradeTrackerFull
 */
public class StudentGradeTrackerFull extends JFrame {

    // Inner Student model
    static class Student {
        private final String name;
        private final String id;
        private final ArrayList<Double> grades = new ArrayList<>();

        public Student(String name, String id) {
            this.name = name;
            this.id = id;
        }
        public String getName() { return name; }
        public String getId() { return id; }
        public ArrayList<Double> getGrades() { return grades; }
        public void addGrade(double g) {
            if (g < 0 || g > 100) throw new IllegalArgumentException("Grade must be between 0 and 100");
            grades.add(g);
        }
        public int getSubjectCount() { return grades.size(); }
        public double getAverage() {
            if (grades.isEmpty()) return 0.0;
            double s = 0; for (double d : grades) s += d; return s / grades.size();
        }
        public double getHighest() {
            if (grades.isEmpty()) return 0.0;
            double m = grades.get(0); for (double d : grades) if (d > m) m = d; return m;
        }
        public double getLowest() {
            if (grades.isEmpty()) return 0.0;
            double m = grades.get(0); for (double d : grades) if (d < m) m = d; return m;
        }
        public String getLetterGrade() {
            double avg = getAverage();
            if (avg >= 90) return "A";
            if (avg >= 80) return "B";
            if (avg >= 70) return "C";
            if (avg >= 60) return "D";
            return "F";
        }
        public String gradesAsArrayString() {
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < grades.size(); i++) {
                sb.append(String.format("%.2f", grades.get(i)));
                if (i < grades.size() - 1) sb.append(", ");
            }
            sb.append("]");
            return sb.toString();
        }
    }

    // UI fields & data
    private final ArrayList<Student> students = new ArrayList<>();
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"Student ID","Name","Subjects","Grades","Average","Highest","Lowest","Letter Grade"}, 0) {
        @Override public boolean isCellEditable(int r,int c){return false;}
    };
    private final JTable table = new JTable(tableModel);
    private final JTextField nameField = new JTextField(12);
    private final JTextField idField = new JTextField(8);
    private final JTextField gradeField = new JTextField(6);
    private final JComboBox<String> studentSelector = new JComboBox<>();
    private final JTextArea summaryArea = new JTextArea();

    public StudentGradeTrackerFull() {
        setTitle("Student Grade Tracker - Single File");
        setSize(1100,680);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10,10));
        add(createInputPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createSummaryPanel(), BorderLayout.SOUTH);
        setLocationRelativeTo(null);
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Student Management"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6,6,6,6); gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx=0; gbc.gridy=0; panel.add(new JLabel("Student Name:"), gbc);
        gbc.gridx=1; panel.add(nameField, gbc);
        gbc.gridx=2; panel.add(new JLabel("Student ID:"), gbc);
        gbc.gridx=3; panel.add(idField, gbc);
        gbc.gridx=4; JButton addStudentBtn = new JButton("Add Student"); addStudentBtn.addActionListener(e->addStudent()); panel.add(addStudentBtn, gbc);

        gbc.gridx=0; gbc.gridy=1; panel.add(new JLabel("Select Student:"), gbc);
        gbc.gridx=1; studentSelector.setPreferredSize(new Dimension(260,26)); panel.add(studentSelector, gbc);
        gbc.gridx=2; panel.add(new JLabel("Grade (0-100):"), gbc);
        gbc.gridx=3; panel.add(gradeField, gbc);
        gbc.gridx=4; JButton addGradeBtn = new JButton("Add Grade"); addGradeBtn.addActionListener(e->addGradeToSelected()); panel.add(addGradeBtn, gbc);

        gbc.gridx=0; gbc.gridy=2; JButton viewBtn = new JButton("View Grades"); viewBtn.addActionListener(e->viewGradesPopup()); panel.add(viewBtn,gbc);
        gbc.gridx=1; JButton refreshBtn = new JButton("Refresh Table"); refreshBtn.addActionListener(e->updateTable()); panel.add(refreshBtn,gbc);
        gbc.gridx=2; JButton summaryBtn = new JButton("Generate Summary"); summaryBtn.addActionListener(e->generateSummary()); panel.add(summaryBtn,gbc);
        gbc.gridx=3; JButton clearBtn = new JButton("Clear All"); clearBtn.addActionListener(e->clearAll()); panel.add(clearBtn,gbc);
        gbc.gridx=4; JButton deleteBtn = new JButton("Delete Student"); deleteBtn.addActionListener(e->deleteSelectedStudent()); panel.add(deleteBtn,gbc);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Student Records"));
        table.setRowHeight(26);
        table.getTableHeader().setFont(new Font("SansSerif",Font.BOLD,12));
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Summary Report"));
        summaryArea.setEditable(false); summaryArea.setFont(new Font("Monospaced",Font.PLAIN,12));
        summaryArea.setBackground(new Color(250,250,250));
        JScrollPane sp = new JScrollPane(summaryArea); sp.setPreferredSize(new Dimension(0,170));
        panel.add(sp, BorderLayout.CENTER);
        return panel;
    }

    // Actions
    private void addStudent() {
        String name = nameField.getText().trim(); String id = idField.getText().trim();
        if (name.isEmpty()||id.isEmpty()){ JOptionPane.showMessageDialog(this,"Enter both name and ID","Input Error",JOptionPane.ERROR_MESSAGE); return;}
        for (Student s: students) if (s.getId().equals(id)){ JOptionPane.showMessageDialog(this,"Student ID already exists","Duplicate ID",JOptionPane.ERROR_MESSAGE); return;}
        Student s = new Student(name,id); students.add(s); studentSelector.addItem(name+" ("+id+")"); nameField.setText(""); idField.setText(""); updateTable();
        JOptionPane.showMessageDialog(this,"Student added.","Success",JOptionPane.INFORMATION_MESSAGE);
    }

    private void addGradeToSelected() {
        int idx = studentSelector.getSelectedIndex(); if (idx==-1){ JOptionPane.showMessageDialog(this,"Select a student","Selection Error",JOptionPane.ERROR_MESSAGE); return;}
        String txt = gradeField.getText().trim(); if (txt.isEmpty()){ JOptionPane.showMessageDialog(this,"Enter a grade","Input Error",JOptionPane.ERROR_MESSAGE); return;}
        try { double g = Double.parseDouble(txt); students.get(idx).addGrade(g); gradeField.setText(""); updateTable(); JOptionPane.showMessageDialog(this,"Grade added.","Success",JOptionPane.INFORMATION_MESSAGE);}
        catch(NumberFormatException ex){ JOptionPane.showMessageDialog(this,"Invalid grade format","Format Error",JOptionPane.ERROR_MESSAGE);}
        catch(IllegalArgumentException ex){ JOptionPane.showMessageDialog(this,ex.getMessage(),"Value Error",JOptionPane.ERROR_MESSAGE);}
    }

    private void viewGradesPopup() {
        int idx = studentSelector.getSelectedIndex(); if (idx==-1){ JOptionPane.showMessageDialog(this,"Select a student","Selection Error",JOptionPane.ERROR_MESSAGE); return;}
        Student s = students.get(idx); ArrayList<Double> g = s.getGrades();
        StringBuilder sb = new StringBuilder(); sb.append("Grades for ").append(s.getName()).append(" (").append(s.getId()).append(")\n\n");
        if (g.isEmpty()) sb.append("No grades entered yet."); else { for (int i=0;i<g.size();i++) sb.append(String.format("%2d. %.2f\n", i+1, g.get(i))); sb.append("\nAverage: ").append(String.format("%.2f", s.getAverage())); sb.append("\nHighest: ").append(String.format("%.2f", s.getHighest())); sb.append("\nLowest: ").append(String.format("%.2f", s.getLowest())); sb.append("\nLetter: ").append(s.getLetterGrade()); }
        JTextArea ta = new JTextArea(sb.toString()); ta.setEditable(false); ta.setFont(new Font("Monospaced",Font.PLAIN,12));
        JScrollPane sp = new JScrollPane(ta); sp.setPreferredSize(new Dimension(380,320));
        JOptionPane.showMessageDialog(this, sp, "View Grades", JOptionPane.INFORMATION_MESSAGE);
    }

    private void updateTable() {
        tableModel.setRowCount(0);
        for (Student s : students) {
            tableModel.addRow(new Object[]{
                    s.getId(),
                    s.getName(),
                    s.getSubjectCount(),
                    s.gradesAsArrayString(),
                    String.format("%.2f", s.getAverage()),
                    s.getSubjectCount()==0? "N/A": String.format("%.2f", s.getHighest()),
                    s.getSubjectCount()==0? "N/A": String.format("%.2f", s.getLowest()),
                    s.getLetterGrade()
            });
        }
    }

    private void generateSummary() {
        if (students.isEmpty()) { summaryArea.setText("No students to summarize."); return; }
        StringBuilder sb = new StringBuilder(); sb.append("══════════ STUDENT SUMMARY ══════════\n\n"); sb.append(String.format("Total Students: %d\n\n", students.size()));
        double classAvgSum = 0; double classHigh = 0; double classLow = 100; int studentsWithGrades = 0;
        for (Student s : students) {
            sb.append(String.format("Student: %s (ID: %s)\n", s.getName(), s.getId()));
            sb.append(String.format("  Subjects: %d\n", s.getSubjectCount()));
            sb.append("  Grades  : " + s.gradesAsArrayString() + "\n");
            sb.append(String.format("  Average : %.2f\n", s.getAverage()));
            sb.append(String.format("  Highest : %s\n", s.getSubjectCount()==0? "N/A": String.format("%.2f", s.getHighest())));
            sb.append(String.format("  Lowest  : %s\n", s.getSubjectCount()==0? "N/A": String.format("%.2f", s.getLowest())));
            sb.append(String.format("  Letter  : %s\n", s.getLetterGrade()));
            sb.append("  -----------------------------------\n");
            if (s.getSubjectCount()>0){ classAvgSum += s.getAverage(); studentsWithGrades++; if (s.getHighest()>classHigh) classHigh = s.getHighest(); if (s.getLowest()<classLow) classLow = s.getLowest(); }
        }
        if (studentsWithGrades>0) { sb.append("\nClass Average: ").append(String.format("%.2f", classAvgSum / studentsWithGrades)).append("\n"); sb.append("Class Highest: ").append(String.format("%.2f", classHigh)).append("\n"); sb.append("Class Lowest: ").append(String.format("%.2f", classLow)).append("\n"); }
        summaryArea.setText(sb.toString());
    }

    private void clearAll() {
        int c = JOptionPane.showConfirmDialog(this,"Clear all data?","Confirm",JOptionPane.YES_NO_OPTION);
        if (c!=JOptionPane.YES_OPTION) return;
        students.clear(); studentSelector.removeAllItems(); updateTable(); summaryArea.setText(""); nameField.setText(""); idField.setText(""); gradeField.setText("");
    }

    private void deleteSelectedStudent() {
        int idx = studentSelector.getSelectedIndex(); if (idx==-1){ JOptionPane.showMessageDialog(this,"Select a student","Selection Error",JOptionPane.ERROR_MESSAGE); return;}
        int c = JOptionPane.showConfirmDialog(this,"Delete selected student?","Confirm Delete",JOptionPane.YES_NO_OPTION); if (c!=JOptionPane.YES_OPTION) return;
        students.remove(idx); studentSelector.removeItemAt(idx); updateTable(); summaryArea.setText("");
    }

    // main
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
            StudentGradeTrackerFull app = new StudentGradeTrackerFull();
            app.setVisible(true);
        });
    }
}
