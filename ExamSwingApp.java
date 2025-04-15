package app;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


class Student {
    String name;
    int rollNo;
    Map<String, Integer> marks;
    Student left, right;

    public Student(String name, int rollNo, Map<String, Integer> marks) {
        this.name = name;
        this.rollNo = rollNo;
        this.marks = marks;
        this.left = null;
        this.right = null;
    }

    public int getTotalMarks() {
        return marks.getOrDefault("EEE", 0) + marks.getOrDefault("EOC", 0) +
               marks.getOrDefault("MFC", 0) + marks.getOrDefault("JAVA", 0);
    }
}

class BST {
    Student root;

    public void insert(Student student) {
        root = insertRec(root, student);
    }

    private Student insertRec(Student root, Student student) {
        if (root == null) return student;
        if (student.rollNo < root.rollNo) root.left = insertRec(root.left, student);
        else root.right = insertRec(root.right, student);
        return root;
    }

    public void delete(int rollNo) {
        root = deleteRec(root, rollNo);
    }

    private Student deleteRec(Student root, int rollNo) {
        if (root == null) return null;
        if (rollNo < root.rollNo) root.left = deleteRec(root.left, rollNo);
        else if (rollNo > root.rollNo) root.right = deleteRec(root.right, rollNo);
        else {
            if (root.left == null) return root.right;
            if (root.right == null) return root.left;
            Student temp = minValueNode(root.right);
            root.rollNo = temp.rollNo;
            root.name = temp.name;
            root.marks = temp.marks;
            root.right = deleteRec(root.right, temp.rollNo);
        }
        return root;
    }

    private Student minValueNode(Student node) {
        while (node.left != null) node = node.left;
        return node;
    }

    public Student searchByRollNo(int rollNo) {
        return searchRollNoRec(root, rollNo);
    }

    private Student searchRollNoRec(Student root, int rollNo) {
        if (root == null || root.rollNo == rollNo) return root;
        if (rollNo < root.rollNo) return searchRollNoRec(root.left, rollNo);
        return searchRollNoRec(root.right, rollNo);
    }

    public Student searchByName(String name) {
        return searchNameRec(root, name);
    }

    private Student searchNameRec(Student root, String name) {
        if (root == null) return null;
        if (root.name.equalsIgnoreCase(name)) return root;
        Student left = searchNameRec(root.left, name);
        if (left != null) return left;
        return searchNameRec(root.right, name);
    }
}

class PriorityQueueSystem {
    private Map<String, List<Student>> subjectQueues = new HashMap<>();

    public PriorityQueueSystem() {
        for (String subject : Arrays.asList("EEE", "EOC", "MFC", "JAVA")) {
            subjectQueues.put(subject, new ArrayList<>());
        }
    }

    public void addStudent(Student student) {
        for (String subject : subjectQueues.keySet()) {
            subjectQueues.get(subject).add(student);
            subjectQueues.get(subject).sort((a, b) -> b.marks.get(subject) - a.marks.get(subject));
        }
    }

    public void removeStudent(Student student) {
        for (String subject : subjectQueues.keySet()) {
            subjectQueues.get(subject).removeIf(s -> s.rollNo == student.rollNo);
        }
    }

    public List<Student> getStudentsBySubject(String subject) {
        return subjectQueues.getOrDefault(subject, new ArrayList<>());
    }

    public boolean hasSubject(String subject) {
        return subjectQueues.containsKey(subject);
    }
}

public class ExamSwingApp extends JFrame {
    private static final long serialVersionUID = 1L;

    private JComboBox<String> actionBox;
    private JPanel inputPanel;
    private JTextArea outputArea;

    private final BST bst = new BST();
    private final PriorityQueueSystem pq = new PriorityQueueSystem();

    public ExamSwingApp() {
        setTitle("Exam Result Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Action: "));
        actionBox = new JComboBox<>(new String[]{"Query", "Add Student", "Delete Student"});
        topPanel.add(actionBox);
        JButton submitBtn = new JButton("Submit");
        topPanel.add(submitBtn);
        add(topPanel, BorderLayout.NORTH);

        inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(0, 2, 10, 5));
        add(inputPanel, BorderLayout.CENTER);

        outputArea = new JTextArea(10, 40);
        outputArea.setFont(new Font("monospaced", Font.PLAIN, 12));
        outputArea.setEditable(false);
        add(new JScrollPane(outputArea), BorderLayout.SOUTH);

        submitBtn.addActionListener(e -> updateInputFields());

        setVisible(true);
    }

    private void updateInputFields() {
        inputPanel.removeAll();
        String action = (String) actionBox.getSelectedItem();

        if (action.equals("Query")) {
            JTextField queryField = new JTextField();
            inputPanel.add(new JLabel("Name / Roll No / Subject:"));
            inputPanel.add(queryField);
            JButton searchBtn = new JButton("Search");
            inputPanel.add(searchBtn);
            searchBtn.addActionListener(e -> runQuery(queryField.getText()));
        } else if (action.equals("Add Student")) {
            JTextField name = new JTextField(), roll = new JTextField();
            JTextField eee = new JTextField(), eoc = new JTextField(), mfc = new JTextField(), java = new JTextField();
            inputPanel.add(new JLabel("Name:")); inputPanel.add(name);
            inputPanel.add(new JLabel("Roll No:")); inputPanel.add(roll);
            inputPanel.add(new JLabel("EEE:")); inputPanel.add(eee);
            inputPanel.add(new JLabel("EOC:")); inputPanel.add(eoc);
            inputPanel.add(new JLabel("MFC:")); inputPanel.add(mfc);
            inputPanel.add(new JLabel("JAVA:")); inputPanel.add(java);
            JButton addBtn = new JButton("Add");
            inputPanel.add(addBtn);
            addBtn.addActionListener(e -> {
                try {
                    Map<String, Integer> marks = new HashMap<>();
                    marks.put("EEE", Integer.parseInt(eee.getText()));
                    marks.put("EOC", Integer.parseInt(eoc.getText()));
                    marks.put("MFC", Integer.parseInt(mfc.getText()));
                    marks.put("JAVA", Integer.parseInt(java.getText()));
                    Student s = new Student(name.getText(), Integer.parseInt(roll.getText()), marks);
                    bst.insert(s);
                    pq.addStudent(s);
                    outputArea.setText("Student added successfully.");
                } catch (Exception ex) {
                    outputArea.setText("Invalid input.");
                }
            });
        } else if (action.equals("Delete Student")) {
            JTextField rollDel = new JTextField();
            inputPanel.add(new JLabel("Roll No:")); inputPanel.add(rollDel);
            JButton delBtn = new JButton("Delete");
            inputPanel.add(delBtn);
            delBtn.addActionListener(e -> {
                try {
                    int roll = Integer.parseInt(rollDel.getText());
                    Student s = bst.searchByRollNo(roll);
                    if (s != null) {
                        bst.delete(roll);
                        pq.removeStudent(s);
                        outputArea.setText("Student with Roll No " + roll + " deleted.");
                    } else {
                        outputArea.setText("Student not found.");
                    }
                } catch (Exception ex) {
                    outputArea.setText("Invalid input.");
                }
            });
        }

        inputPanel.revalidate();
        inputPanel.repaint();
    }

    private void runQuery(String input) {
        try {
            int roll = Integer.parseInt(input);
            Student s = bst.searchByRollNo(roll);
            displayStudent(s);
        } catch (NumberFormatException e) {
            Student s = bst.searchByName(input);
            if (s != null) {
                displayStudent(s);
            } else if (pq.hasSubject(input.toUpperCase())) {
                List<Student> list = pq.getStudentsBySubject(input.toUpperCase());
                if (list.isEmpty()) {
                    outputArea.setText("No students found for subject " + input + ".");
                } else {
                    StringBuilder sb = new StringBuilder("Students sorted by " + input + " marks:\n");
                    for (Student st : list) {
                        sb.append(st.name).append(": ").append(st.marks.get(input.toUpperCase())).append("\n");
                    }
                    outputArea.setText(sb.toString());
                }
            } else {
                outputArea.setText("Invalid query.");
            }
        }
    }

    private void displayStudent(Student s) {
        if (s == null) {
            outputArea.setText("Student not found.");
            return;
        }
        StringBuilder sb = new StringBuilder("Results for " + s.name + " (Roll No: " + s.rollNo + "):\n");
        for (Map.Entry<String, Integer> entry : s.marks.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        sb.append("Total Marks: ").append(s.getTotalMarks());
        outputArea.setText(sb.toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ExamSwingApp::new);
    }
}
