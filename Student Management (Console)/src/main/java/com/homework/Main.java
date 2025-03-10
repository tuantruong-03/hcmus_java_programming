package com.homework;

import com.homework.student.Manager;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Manager manager = new Manager("students.dat");
        Console console = new Console(manager, scanner);

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
