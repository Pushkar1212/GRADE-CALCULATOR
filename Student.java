import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

// -------- Student class --------
class Student {
    private String name;
    private int[] marks = new int[5];
    private static final String[] subjects = {"Math", "Physics", "Chemistry", "History", "Geography"};

    public Student(String name, int[] marks) {
        this.name = name;
        this.marks = marks;
    }

    public String getName() {
        return name;
    }

    public int[] getMarks() {
        return marks;
    }

    public static String[] getSubjects() {
        return subjects;
    }
}

// -------- Interface for result storage --------
interface ResultStorage {
    void saveResult(Student student, int total, double percentage, String grade);
}

// -------- File-based result storage --------
class FileResultStorage implements ResultStorage {
    private final String filename;

    public FileResultStorage(String filename) {
        this.filename = filename;
    }

    @Override
    public void saveResult(Student student, int total, double percentage, String grade) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filename, true))) {
            pw.println("Name: " + student.getName());
            String[] subjects = Student.getSubjects();
            for (int i = 0; i < subjects.length; i++) {
                pw.println(subjects[i] + ": " + student.getMarks()[i]);
            }
            pw.println("Total: " + total);
            pw.printf("Percentage: %.2f%%\n", percentage);
            pw.println("Grade: " + grade);
            pw.println("-----");
        } catch (IOException e) {
            System.out.println("Error saving result to file.");
        }
    }
}

// -------- Grade calculation logic --------
class GradeCalculator {
    public static int getTotal(Student student) {
        int sum = 0;
        for (int mark : student.getMarks()) sum += mark;
        return sum;
    }

    public static double getPercentage(Student student) {
        return getTotal(student) / 5.0;
    }

    public static String getGrade(double percentage) {
        if (percentage >= 90) return "A+";
        else if (percentage >= 80) return "A";
        else if (percentage >= 70) return "B";
        else if (percentage >= 60) return "C";
        else if (percentage >= 50) return "D";
        else return "F";
    }

    public static Color getGradeColor(String grade) {
        return switch (grade) {
            case "A+" -> Color.GREEN.darker();
            case "A" -> Color.GREEN;
            case "B" -> Color.BLUE;
            case "C" -> Color.ORANGE;
            case "D" -> Color.PINK;
            default -> Color.RED;
        };
    }
}

// -------- GUI class --------
class GradeCalculatorApp extends JFrame implements ActionListener {
    private JTextField nameField;
    private JTextField[] markFields = new JTextField[5];
    private JLabel resultLabel;
    private ResultStorage storage;
    private JToggleButton toggleTheme;

    public GradeCalculatorApp() {
        setTitle("Modern Grade Calculator");
        setSize(450, 520);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Top panel for name and theme toggle
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        JPanel namePanel = new JPanel(new GridLayout(1, 2, 5, 5));
        namePanel.setBorder(BorderFactory.createTitledBorder("Student Info"));

        namePanel.add(new JLabel("Student Name:"));
        nameField = new JTextField();
        namePanel.add(nameField);

        toggleTheme = new JToggleButton("Dark Mode");
        toggleTheme.addActionListener(e -> toggleLookAndFeel());

        topPanel.add(namePanel, BorderLayout.CENTER);
        topPanel.add(toggleTheme, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Center panel for subjects and marks
        JPanel centerPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        centerPanel.setBorder(BorderFactory.createTitledBorder("Enter Marks"));

        String[] subjects = Student.getSubjects();
        for (int i = 0; i < subjects.length; i++) {
            centerPanel.add(new JLabel(subjects[i] + ":"));
            markFields[i] = new JTextField();
            centerPanel.add(markFields[i]);
        }
        add(centerPanel, BorderLayout.CENTER);

        // Bottom panel for button and result
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));

        JButton calcButton = new JButton("Calculate Grade");
        calcButton.addActionListener(this);
        calcButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        bottomPanel.add(calcButton);

        bottomPanel.add(Box.createVerticalStrut(15));

        resultLabel = new JLabel("Enter marks and click Calculate", SwingConstants.CENTER);
        resultLabel.setFont(new Font("Arial", Font.BOLD, 14));
        resultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        bottomPanel.add(resultLabel);

        add(bottomPanel, BorderLayout.SOUTH);

        storage = new FileResultStorage("student_results.txt");

        setVisible(true);
    }

    private void toggleLookAndFeel() {
        try {
            if (toggleTheme.isSelected()) {
                UIManager.setLookAndFeel(new FlatDarkLaf());
            } else {
                UIManager.setLookAndFeel(new FlatLightLaf());
            }
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            String name = nameField.getText().trim();
            if (name.isEmpty()) throw new Exception("Student name is required.");

            int[] marks = new int[5];
            for (int i = 0; i < marks.length; i++) {
                marks[i] = Integer.parseInt(markFields[i].getText().trim());
                if (marks[i] < 0 || marks[i] > 100) throw new NumberFormatException();
            }

            Student student = new Student(name, marks);
            int total = GradeCalculator.getTotal(student);
            double percentage = GradeCalculator.getPercentage(student);
            String grade = GradeCalculator.getGrade(percentage);
            Color gradeColor = GradeCalculator.getGradeColor(grade);

            String resultText = "<html>Student: " + name +
                    "<br>Total: " + total + "/500" +
                    "<br>Percentage: " + String.format("%.2f", percentage) + "%" +
                    "<br>Grade: " + grade + "</html>";

            resultLabel.setText(resultText);
            resultLabel.setForeground(gradeColor);

            storage.saveResult(student, total, percentage, grade);

        } catch (NumberFormatException ex) {
            resultLabel.setText("Marks must be numeric and between 0-100.");
            resultLabel.setForeground(Color.RED);
        } catch (Exception ex) {
            resultLabel.setText(ex.getMessage());
            resultLabel.setForeground(Color.RED);
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            System.err.println("Failed to initialize FlatLaf");
        }
        SwingUtilities.invokeLater(GradeCalculatorApp::new);
    }
}

