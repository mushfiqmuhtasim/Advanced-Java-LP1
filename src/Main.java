import java.util.*;

enum Department {
    CSE, EEE, BBA
}

class Student {
    private int id;
    private String name;
    private Department department;
    private int[] marks;

    public Student(int id, String name, Department department, int m1, int m2, int m3) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.marks = new int[]{m1, m2, m3};
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public Department getDepartment() { return department; }
    public int[] getMarks() { return marks; }

    @Override
    public String toString() {
        return id + ", " + name + ", " + department + ", " + Arrays.toString(marks);
    }
}

class TotalMarksThread extends Thread {
    private Student student;

    public TotalMarksThread(Student student) {
        this.student = student;
    }

    @Override
    public void run() {
        int total = 0;
        for (int m : student.getMarks()) {
            total += m;
        }
        System.out.println("Total Marks for " + student.getName() + ": " + total);
    }
}

class GradeUtil {
    public static String letterGrade(double avg) {
        if (avg >= 90) return "A+";
        else if (avg >= 85) return "A";
        else if (avg >= 80) return "B+";
        else if (avg >= 75) return "B";
        else if (avg >= 70) return "C+";
        else if (avg >= 65) return "C";
        else if (avg >= 60) return "D+";
        else if (avg >= 50) return "D";
        else return "F";
    }

    public static double gradePoint(String grade) {
        switch (grade) {
            case "A+": return 4.00;
            case "A":  return 3.75;
            case "B+": return 3.50;
            case "B":  return 3.25;
            case "C+": return 3.00;
            case "C":  return 2.75;
            case "D+": return 2.50;
            case "D":  return 2.25;
            default:   return 0.00;
        }
    }

    public static double average(Student s) {
        int total = 0;
        for (int m : s.getMarks()) total += m;
        return total / 3.0;
    }
}

class AverageMarksThread extends Thread {
    private Student student;

    public AverageMarksThread(Student student) {
        this.student = student;
    }

    @Override
    public void run() {
        double avg = GradeUtil.average(student);
        String grade = GradeUtil.letterGrade(avg);
        double gp = GradeUtil.gradePoint(grade);

        System.out.println(
                "Average Marks for " + student.getName() + ": " + String.format("%.2f", avg) + "\n" +
                        "Letter Grade for " + student.getName() + ": " + grade + "\n" +
                        "Grade Point for " + student.getName() + ": " + String.format("%.2f", gp));
    }
}

class DepartmentGradeCountThread extends Thread {
    private List<Student> students;
    private static final String[] GRADES = {"A+", "A", "B+", "B", "C+", "C", "D+", "D", "F"};

    public DepartmentGradeCountThread(List<Student> students) {
        this.students = students;
    }

    @Override
    public void run() {
        Map<Department, Map<String, Integer>> counts = new EnumMap<>(Department.class);
        for (Department d : Department.values()) {
            Map<String, Integer> gradeMap = new LinkedHashMap<>();
            for (String g : GRADES) gradeMap.put(g, 0);
            counts.put(d, gradeMap);
        }

        for (Student s : students) {
            String grade = GradeUtil.letterGrade(GradeUtil.average(s));
            Map<String, Integer> gradeMap = counts.get(s.getDepartment());
            gradeMap.put(grade, gradeMap.get(grade) + 1);
        }

        StringBuilder sb = new StringBuilder("Department-Wise Grade Count:");
        for (Department d : Department.values()) {
            sb.append("\n").append(d).append(": ");
            boolean first = true;
            for (Map.Entry<String, Integer> e : counts.get(d).entrySet()) {
                if (!first) sb.append(", ");
                sb.append(e.getKey()).append("+").append(e.getValue());
                first = false;
            }
        }
        System.out.println(sb);
    }
}

public class Main {
    public static void main(String[] args) throws InterruptedException {
        List<Student> students = new ArrayList<>();
        students.add(new Student(101, "Rahim",  Department.CSE, 80, 75, 90));
        students.add(new Student(102, "Karim",  Department.CSE, 60, 65, 70));
        students.add(new Student(103, "Ayesha", Department.EEE, 90, 95, 92));
        students.add(new Student(104, "Nusrat", Department.EEE, 50, 55, 60));
        students.add(new Student(105, "Sakib",  Department.BBA, 75, 80, 85));
        students.add(new Student(106, "Mim",    Department.BBA, 40, 45, 50));

        System.out.println("Student Information:");
        for (Student s : students) {
            System.out.println(s);
        }

        TotalMarksThread t1 = new TotalMarksThread(students.get(0));
        AverageMarksThread t2 = new AverageMarksThread(students.get(0));
        DepartmentGradeCountThread t3 = new DepartmentGradeCountThread(students);

        t1.start();
        t2.start();
        t3.start();

        t1.join();
        t2.join();
        t3.join();

        System.out.println("Result processing completed.");
    }
}