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
    public void add(Entity student) {
        students.add(student);
        saveToFile();
    }

    private void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(students);
        } catch (IOException ex) {
            System.out.println("Error saving to file: " + ex.getMessage());
        }
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

    public void exportToCSV(String csvFilename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(csvFilename, false))) {
            writer.println("ID,Name,Score,Image,Address,Note");
            for (Entity student : students) {
                writer.printf("%s,%s,%.2f,%s,%s,%s%n", student.getId(), student.getName(), student.getScore(),
                        student.getImage(), student.getAddress(), student.getNote());
            }
            writer.flush();
            System.out.println("Export successful!");
        } catch (IOException e) {
            System.out.println("Error exporting CSV: " + e.getMessage());
        }
    }

    public void importFromCSV(String csvFilename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFilename))) {
            String line;
            reader.readLine(); // Skip header
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 6) {
                    students.add(new Entity(data[0], data[1], Double.parseDouble(data[2]), data[3], data[4], data[5]));
                }
            }
            saveToFile();
            System.out.println("Import successful!");
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error importing CSV: " + e.getMessage());
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
}
