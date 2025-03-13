class Student {
    String name;
    int rollNo;
    int EEE, EOC, MFC, OOPS;
    Student left, right;

   
    public Student(String name, int rollNo, int EEE, int EOC, int MFC, int OOPS) {
        this.name = name;
        this.rollNo = rollNo;
        this.EEE = EEE;  
        this.EOC = EOC;
        this.MFC = MFC;
        this.OOPS = OOPS;
        this.left = this.right = null;
    }
    

    public int getTotalMarks() {
        return EEE + EOC + MFC + OOPS;
    }
}

class BST {
    private Student root;

    public void insert(String name, int rollNo, int EEE, int EOC, int MFC, int OOPS) {
        Student student = new Student(name, rollNo, EEE, EOC, MFC, OOPS);
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
        if (root == null) return root;
        if (rollNo < root.rollNo) root.left = deleteRec(root.left, rollNo);
        else if (rollNo > root.rollNo) root.right = deleteRec(root.right, rollNo);
        else {
            if (root.left == null) return root.right;
            if (root.right == null) return root.left;
            Student temp = minValueNode(root.right);
            root.rollNo = temp.rollNo;
            root.name = temp.name;
            root.EEE = temp.EEE;
            root.EOC = temp.EOC;
            root.MFC = temp.MFC;
            root.OOPS = temp.OOPS;
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
        return rollNo < root.rollNo ? searchRollNoRec(root.left, rollNo) : searchRollNoRec(root.right, rollNo);
    }

    public void inorderTraversal() {
        inorderRec(root);
    }

    private void inorderRec(Student root) {
        if (root != null) {
            inorderRec(root.left);
            System.out.println("Roll No: " + root.rollNo + ", Name: " + root.name + ", Total Marks: " + root.getTotalMarks());
            inorderRec(root.right);
        }
    }
}

public class Main {
    public static void main(String[] args) {
        BST bst = new BST();
        bst.insert("Alice", 10, 90, 85, 88, 92);
        bst.insert("Bob", 5, 75, 80, 78, 85);
        bst.insert("Charlie", 15, 82, 87, 80, 89);
        
        System.out.println("Inorder Traversal of BST:");
        bst.inorderTraversal();
        
        System.out.println("\nDeleting Roll No 10");
        bst.delete(10);
        
        System.out.println("Inorder Traversal after Deletion:");
        bst.inorderTraversal();
    }
}