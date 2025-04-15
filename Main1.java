import java.util.*;

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
        return marks.get("EEE") + marks.get("EOC") + marks.get("MFC") + marks.get("JAVA");
    }
}

class BST {
    Student root;

    public void insert(Student student) {
        root = insertRec(root, student);
    }

    private Student insertRec(Student root, Student student) {
        if (root == null) return student;

        if (student.rollNo < root.rollNo)
            root.left = insertRec(root.left, student);
        else
            root.right = insertRec(root.right, student);

        return root;
    }

    public void delete(int rollNo) {
        root = deleteRec(root, rollNo);
    }

    private Student deleteRec(Student root, int rollNo) {
        if (root == null) return null;

        if (rollNo < root.rollNo)
            root.left = deleteRec(root.left, rollNo);
        else if (rollNo > root.rollNo)
            root.right = deleteRec(root.right, rollNo);
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
        while (node.left != null)
            node = node.left;
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
    Map<String, List<Student>> subjectQueues = new HashMap<>();

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
            subjectQueues.get(subject).removeIf(s -> s.name.equals(student.name));
        }
    }

    public List<Student> getStudentsBySubject(String subject) {
        return subjectQueues.getOrDefault(subject, new ArrayList<>());
    }
}

class ExamSystem {
    BST bst = new BST();
    PriorityQueueSystem pq = new PriorityQueueSystem();
    Scanner scanner = new Scanner(System.in);

    public void run() {
        while (true) {
            System.out.println("\nEnter 'query', 'add', 'delete', or 'exit':");
            String action = scanner.nextLine().trim();

            if (action.equalsIgnoreCase("query")) {
                System.out.println("Enter roll number, name, or subject:");
                String query = scanner.nextLine();
                handleQuery(query);
            } else if (action.equalsIgnoreCase("add")) {
                addStudent();
            } else if (action.equalsIgnoreCase("delete")) {
                try {
                    System.out.println("Enter roll number to delete:");
                    int roll = Integer.parseInt(scanner.nextLine());
                    deleteStudent(roll);
                } catch (Exception e) {
                    System.out.println("Invalid roll number.");
                }
            } else if (action.equalsIgnoreCase("exit")) {
                System.out.println("Exiting...");
                scanner.close();
                break;
            } else {
                System.out.println("Invalid input.");
            }
        }
    }

    void addStudent() {
        try {
            System.out.print("Enter name: ");
            String name = scanner.nextLine();
            System.out.print("Enter roll no: ");
            int roll = Integer.parseInt(scanner.nextLine());
            Map<String, Integer> marks = new HashMap<>();
            for (String subject : Arrays.asList("EEE", "EOC", "MFC", "JAVA")) {
                System.out.print("Enter marks for " + subject + ": ");
                marks.put(subject, Integer.parseInt(scanner.nextLine()));
            }
            Student s = new Student(name, roll, marks);
            bst.insert(s);
            pq.addStudent(s);
            System.out.println("✅ Student added successfully.");
        } catch (Exception e) {
            System.out.println("⚠️ Invalid input. Please try again.");
        }
    }

    void deleteStudent(int roll) {
        Student s = bst.searchByRollNo(roll);
        if (s != null) {
            bst.delete(roll);
            pq.removeStudent(s);
            System.out.println("🗑️ Deleted successfully.");
        } else {
            System.out.println("❌ Student not found.");
        }
    }

    void handleQuery(String query) {
        try {
            int roll = Integer.parseInt(query);
            Student s = bst.searchByRollNo(roll);
            displayStudent(s);
        } catch (NumberFormatException e) {
            Student s = bst.searchByName(query);
            if (s != null) {
                displayStudent(s);
            } else if (pq.subjectQueues.containsKey(query.toUpperCase())) {
                List<Student> list = pq.getStudentsBySubject(query.toUpperCase());
                if (list.isEmpty()) {
                    System.out.println("No students found for subject " + query);
                } else {
                    System.out.println("🏆 Students sorted by " + query + " marks:");
                    for (Student st : list) {
                        System.out.println(st.name + ": " + st.marks.get(query.toUpperCase()));
                    }
                }
            } else {
                System.out.println("❌ Invalid query.");
            }
        }
    }

    void displayStudent(Student s) {
        if (s == null) {
            System.out.println("❌ Student not found.");
            return;
        }
        System.out.println("📋 Results for " + s.name + " (Roll No: " + s.rollNo + "):");
        for (String subject : s.marks.keySet()) {
            System.out.println(subject + ": " + s.marks.get(subject));
        }
        System.out.println("Total Marks: " + s.getTotalMarks());
    }
}

public class Main1 {
    public static void main(String[] args) {
        ExamSystem system = new ExamSystem();
        system.run();
    }
}
