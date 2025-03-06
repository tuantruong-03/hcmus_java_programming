package com.homework;

import com.homework.student.Manager;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Manager manager = new Manager("students.dat");
        Console console = new Console(manager, scanner);
//        List<Entity> students = List.of(
//                new Entity("S001", "Alice Johnson", 8.5, "alice.jpg", "123 Main St", "Excellent student"),
//                new Entity("S002", "Bob Smith", 7.2, "bob.jpg", "456 Oak St", "Needs improvement in math"),
//                new Entity("S003", "Charlie Brown", 9.1, "charlie.jpg", "789 Pine St", "Great in science"),
//                new Entity("S004", "David Wilson", 6.8, "david.jpg", "321 Birch St", "Struggles with history"),
//                new Entity("S005", "Emma Davis", 8.9, "emma.jpg", "654 Cedar St", "Active in sports"),
//                new Entity("S006", "Frank Miller", 7.5, "frank.jpg", "987 Maple St", "Good at coding"),
//                new Entity("S007", "Grace Lee", 9.3, "grace.jpg", "741 Walnut St", "Top of the class"),
//                new Entity("S008", "Henry Adams", 5.9, "henry.jpg", "852 Elm St", "Needs more practice"),
//                new Entity("S009", "Ivy Thomas", 8.1, "ivy.jpg", "963 Spruce St", "Very creative"),
//                new Entity("S010", "Jack White", 7.7, "jack.jpg", "147 Redwood St", "Enjoys group projects")
//        );
//        students.forEach(manager::add);
        while (true) {
            System.out.println("\nStudent Management System");
            System.out.println("1. Add Student");
            System.out.println("2. Update Student");
            System.out.println("3. Delete Student");
            System.out.println("4. View Students (Sort by ID / Score)");
            System.out.println("5. Export to CSV");
            System.out.println("6. Import from CSV");
            System.out.println("7. Exit");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    console.addStudent();
                    break;
                case 2:
                    console.updateStudent();
                    break;
                case 3:
                    console.deleteStudent();
                    break;
                case 4:
                    console.viewStudents();
                    break;
                case 5:
                    manager.exportToCSV("students.csv");
                    break;
                case 6:
                    manager.importFromCSV("students.csv");
                    break;
                case 7:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid choice. Please enter a valid option.");
            }
        }
    }
}
