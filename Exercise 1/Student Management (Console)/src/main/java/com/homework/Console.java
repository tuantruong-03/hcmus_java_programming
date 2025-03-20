package com.homework;

import com.homework.student.Entity;
import com.homework.student.Manager;
import com.homework.student.UpdateRequest;

import java.nio.DoubleBuffer;
import java.util.Scanner;

public class Console {
    private final Manager manager;
    private final Scanner scanner;
    public Console(Manager manager, Scanner scanner) {
        this.manager = manager;
        this.scanner = scanner;
    }

    public void addStudent() {
        System.out.print("Enter ID: ");
        String id = scanner.nextLine();
        System.out.print("Enter Name: ");
        String name = scanner.nextLine();
        double score;
        while (true) {
            System.out.print("Enter Score (0-10): ");
            if (scanner.hasNextDouble()) {
                score = scanner.nextDouble();
                if (score >= 0 && score <= 10) {
                    scanner.nextLine(); // Consume break line
                    break;
                }
                System.out.println("Invalid score! Please enter a value between 0 and 10.");
            } else {
                System.out.println("Invalid input! Please enter a numeric value.");
                scanner.next();
            }
        }
        System.out.print("Enter Image Path: ");
        String image = scanner.nextLine();
        System.out.print("Enter Address: ");
        String address = scanner.nextLine();
        System.out.print("Enter Note: ");
        String note = scanner.nextLine();
        if (manager.add(new Entity(id, name, score, image, address, note))) {
            System.out.println("Student added successfully!");
        }
    }

    public void updateStudent() {
        System.out.print("Enter Student ID to update: ");
        String id = scanner.nextLine();
        System.out.print("Enter new Name (or press Enter to skip): ");
        String newName = scanner.nextLine();
        Double newScore;
        while (true) {
            System.out.print("Enter Score (0-10) or press Enter to skip: ");
            String input = scanner.nextLine().trim(); // Read input and trim whitespace

            if (input.isEmpty()) {
                newScore = null; // User chose to skip
                break;
            }

            try {
                newScore = Double.parseDouble(input);
                if (newScore >= 0 && newScore <= 10) {
                    break;
                } else {
                    System.out.println("Invalid score! Please enter a value between 0 and 10.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a numeric value.");
            }
        }
        System.out.print("Enter new Image Path (or press Enter to skip): ");
        String newImage = scanner.nextLine();
        System.out.print("Enter new Address (or press Enter to skip): ");
        String newAddress = scanner.nextLine();
        System.out.print("Enter new Note (or press Enter to skip): ");
        String newNote = scanner.nextLine();

        UpdateRequest updateRequest = new UpdateRequest.Builder()
                .name(newName)
                .score(newScore)
                .image(newImage)
                .address(newAddress)
                .note(newNote).build();

        if (manager.update(id, updateRequest)) {
            System.out.println("Student updated successfully!");
        } else {
            System.out.println("Student ID not found!");
        }
    }


    public void deleteStudent() {
        System.out.print("Enter Student ID to delete: ");
        String id = scanner.nextLine();
        if (manager.delete(id)) {
            System.out.println("Student deleted successfully!");
        } else {
            System.out.println("Student ID not found!");
        }
    }

    public void viewStudents() {
        System.out.println("\nSort by:");
        System.out.println("1. ID Ascending");
        System.out.println("2. ID Descending");
        System.out.println("3. Score Ascending");
        System.out.println("4. Score Descending");
        System.out.print("Choose an option: ");

        int sortChoice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        switch (sortChoice) {
            case 1:
                manager.sortByIdAscending();
                break;
            case 2:
                manager.sortByIdDescending();
                break;
            case 3:
                manager.sortByScoreAscending();
                break;
            case 4:
                manager.sortByScoreDescending();
                break;
            default:
                System.out.println("Invalid choice. Showing default list.");
        }

        manager.displayStudents();
    }

    public boolean importStudentsFromCSV() {
        System.out.print("Enter filename to import (or press Enter for default 'import.csv'): ");
        String fileName = scanner.nextLine().trim();
        if (fileName.isBlank()) {
            fileName = "import.csv";
        } else if (!fileName.toLowerCase().endsWith(".csv")) {
            fileName += ".csv";
        }

        if (manager.importFromCSV(fileName)) {
            System.out.println("Students imported successfully from " + fileName);
            return true;
        } else {
            System.out.println("Failed to import students from " + fileName);
            return false;
        }
    }

    public boolean exportStudentsToCSV() {
        String fileName;
        while (true) {
            System.out.print("Enter filename to export (or press Enter for default 'export.csv'): ");
            fileName = scanner.nextLine().trim();

            if (fileName.isBlank()) {
                fileName = "export.csv";
            } else if (!fileName.toLowerCase().endsWith(".csv")) {
                fileName += ".csv";
            }

            if (fileName.equalsIgnoreCase("import.csv") || fileName.equalsIgnoreCase("import")) {
                System.out.println("Invalid filename. 'import.csv' is not allowed. Please choose a different name.");
            } else {
                break;
            }
        }

        if (manager.exportToCSV(fileName)) {
            System.out.println("Students exported successfully to " + fileName);
            return true;
        } else {
            System.out.println("Failed to export students to " + fileName);
            return false;
        }
    }

}
