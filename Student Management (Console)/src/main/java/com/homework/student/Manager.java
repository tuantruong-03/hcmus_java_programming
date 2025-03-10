package com.homework.student;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Manager {
    private List<Entity> students;
    private final String filename;
    public Manager(String filename) {
        this.filename = filename;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            Object obj = ois.readObject();
            if (obj instanceof List) {
               this.students = (List<Entity>) obj;
               return;
            }
            this.students = new ArrayList<>();
        } catch(IOException | ClassNotFoundException ex) {
            System.out.println(ex.getMessage());
            this.students = new ArrayList<>();
        }
    }

    public boolean add(Entity student) {
        if (students.stream().anyMatch(s -> s.getId().equals(student.getId()))) {
            System.out.println("Student already exists with ID " + student.getId());
            return false;
        }
        students.add(student);
        saveToFile();
        return true;
    }

    public boolean update(String id, UpdateRequest updateStudentRequest) {
        for (Entity student : students) {
            if (student.getId().equals(id)) {
                if (updateStudentRequest.getName() != null && !updateStudentRequest.getName().isBlank()) {
                    student.setName(updateStudentRequest.getName());
                }
                if (updateStudentRequest.getScore() != null) {
                    student.setScore(updateStudentRequest.getScore());
                }
                if (updateStudentRequest.getImage() != null && !updateStudentRequest.getImage().isBlank()) {
                    student.setImage(updateStudentRequest.getImage());
                }
                if (updateStudentRequest.getAddress() != null && !updateStudentRequest.getAddress().isBlank()) {
                    student.setAddress(updateStudentRequest.getAddress());
                }
                if (updateStudentRequest.getNote() != null && !updateStudentRequest.getNote().isBlank()) {
                    student.setNote(updateStudentRequest.getNote());
                }
                saveToFile(); // Save the updated list to the file
                return true;
            }
        }
        return false; // Student not found
    }

    public boolean delete(String id) {
        boolean removed = students.removeIf(student -> student.getId().equals(id));
        if (removed) {
            saveToFile();
        }
        return removed;
    }

    public boolean exportToCSV(String csvFilename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(csvFilename, false))) {
            writer.println("ID,Name,Score,Image,Address,Note");
            for (Entity student : students) {
                writer.printf("%s,%s,%.2f,%s,%s,%s%n", student.getId(), student.getName(), student.getScore(),
                        student.getImage(), student.getAddress(), student.getNote());
            }
            writer.flush();
            System.out.println("Export successful!");
            return true;
        } catch (IOException e) {
            System.out.println("Error exporting CSV: " + e.getMessage());
            return false;
        }
    }

    public boolean importFromCSV(String csvFilename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFilename))) {
            String line;
            reader.readLine(); // Skip header
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 6) {
                    Entity student = new Entity(data[0], data[1], Double.parseDouble(data[2]), data[3], data[4], data[5]);
                    this.add(student);
                }
            }
            saveToFile();
            System.out.println("Import successful!");
            return true;
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error importing CSV: " + e.getMessage());
            return false;
        }
    }

    private void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(students);
        } catch (IOException ex) {
            System.out.println("Error saving to file: " + ex.getMessage());
        }
    }

    public void sortByIdAscending() {
        students.sort(Comparator.comparing(Entity::getId));
    }

    public void sortByIdDescending() {
        students.sort(Comparator.comparing(Entity::getId).reversed());
    }

    public void sortByScoreAscending() {
        students.sort(Comparator.comparing(Entity::getScore));
    }

    public void sortByScoreDescending() {
        students.sort(Comparator.comparing(Entity::getScore).reversed());
    }

    public void displayStudents() {
        for (Entity student : students) {
            System.out.println(student);
        }
    }

    public void initData() {
        List<Entity> students = List.of(
                new Entity("S001", "Alice Johnson", 8.5, "alice.jpg", "123 Main St", "Excellent student"),
                new Entity("S002", "Bob Smith", 7.2, "bob.jpg", "456 Oak St", "Needs improvement in math"),
                new Entity("S003", "Charlie Brown", 9.1, "charlie.jpg", "789 Pine St", "Great in science"),
                new Entity("S004", "David Wilson", 6.8, "david.jpg", "321 Birch St", "Struggles with history"),
                new Entity("S005", "Emma Davis", 8.9, "emma.jpg", "654 Cedar St", "Active in sports"),
                new Entity("S006", "Frank Miller", 7.5, "frank.jpg", "987 Maple St", "Good at coding"),
                new Entity("S007", "Grace Lee", 9.3, "grace.jpg", "741 Walnut St", "Top of the class"),
                new Entity("S008", "Henry Adams", 5.9, "henry.jpg", "852 Elm St", "Needs more practice"),
                new Entity("S009", "Ivy Thomas", 8.1, "ivy.jpg", "963 Spruce St", "Very creative"),
                new Entity("S010", "Jack White", 7.7, "jack.jpg", "147 Redwood St", "Enjoys group projects")
        );
        students.forEach(this::add);
    }
}
