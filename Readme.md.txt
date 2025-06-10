# 📊 Grade Calculator Java Application

A modern desktop application built using **Java Swing** and **FlatLaf** for calculating student grades based on subject marks. It supports real-time result calculation, dark/light theme toggling, and persistent result storage.

---

## 📁 Project Structure

```bash
GradeCalculatorApp/
│
├── Student.java             # Represents a student with name and marks
├── GradeCalculator.java     # Contains logic to calculate total, percentage, grade, and color
├── ResultStorage.java       # Interface for storing results (extensible)
├── FileResultStorage.java   # Concrete class that saves results to a text file
├── GradeCalculatorApp.java  # Main GUI class with Swing interface and event handling
├── student_results.txt      # Output file containing saved results
└── README.md                # Project documentation
