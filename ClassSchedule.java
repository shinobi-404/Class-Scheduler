import java.util.Scanner;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;



public class ClassSchedule {

    public static ArrayList<Lecture> readLectures(String fileName) {
        // creates ArrayList for Lecture
        ArrayList<Lecture> lectures = new ArrayList<Lecture>();

        File inputFile = new File(fileName);
        boolean rLabs = false;
        // Opens The File For Reading
        try (BufferedReader r = new BufferedReader(new FileReader(inputFile))) {
            String ftxt;
            // Read File Line By Line
            while ((ftxt = r.readLine()) != null) {
                // Splits The Lines (organize)
                String[] subject = ftxt.trim().split(",");

                if (rLabs) {
                    if (subject.length != 2) {
                        rLabs = false;
                    } else {
                        int crn = Integer.parseInt(subject[0].trim());
                        String roomNum = subject[1].trim();
                        lectures.get(lectures.size() - 1).addLab(crn, roomNum);
                        continue;
                    }
                }
                // takes in CRN, Prefix, Class, ClassType
                int crn = Integer.parseInt(subject[0].trim());
                String prefix = subject[1].trim();
                String className = subject[2].trim();
                String classType = subject[3].trim();
                // Check If Lecture is Online
                if (subject[4].trim().equalsIgnoreCase("ONLINE")) {
                    // Create A New Online Lecture
                    lectures.add(new Lecture(crn, prefix, className, classType));
                } else {

                    String buildingCode = subject[4].trim();
                    String roomNum = subject[5].trim();
                    boolean isLabs = false;

                    if (subject[6].trim().equalsIgnoreCase("YES")) {
                        // if it has Labs change boolean to true
                        isLabs = true;
                        rLabs = true;
                    }
                    // Adds Lectrue
                    lectures.add(new Lecture(crn, prefix, className, classType, buildingCode, roomNum, isLabs));
                }
            }
        } catch (IOException | IndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        return lectures;
    }

    // Main
    public static void main(String[] args) {

        // Scan for user input
        Scanner myScan = new Scanner(System.in);

        ArrayList<Lecture> lectures = readLectures("lec.txt");
        // Find And Print Online Courses
        int onlineNum = 0;
        for (Lecture lecture : lectures) {
            if (lecture.isOnline()) {
                onlineNum++;
            }
        }
        System.out.printf("\n-   There are %d online lectures offered.\n", onlineNum);

        // Asks user to enter the class
        System.out.print("-   Enter the classroom: ");
        String roomNum = myScan.nextLine();

        for (Lecture lecture : lectures) {
            if (!lecture.isOnline()) {
                // Check Lecture
                if (lecture.getRoomNum().equalsIgnoreCase(roomNum)) {
                    System.out.println("\n   The crns held in "+ roomNum +" are: "+ lecture.getCRN());
                }

                if (lecture.isLabs()) {
                    // Check Labs
                    ArrayList<Lab> labs = lecture.getLabs();
                    for (Lab lab : labs) {
                        if (lab.getRoomNum().equalsIgnoreCase(roomNum)) {
                            System.out.println("\n   The crns held in "+ roomNum +" are: "+ lab.getCRN());
                        }
                    }
                }
            }
        }
        
        myScan.close();

        // Creates a LecuresOnly.txt
        String outputFileName = "lecturesOnly.txt";
        try {
            PrintWriter w = new PrintWriter(outputFileName);

            // Create Online Lectures
            w.write("Online Lectures\n");
            for (Lecture lecture : lectures) {
                if (lecture.isOnline()) {
                    w.write(lecture.toString() + "\n");
                }
            }

            // Create Lectures With No Labs
            w.write("\nLectures With No Labs\n");
            for (Lecture lecture : lectures) {
                if (!lecture.isOnline() && !lecture.isLabs()) {
                    w.write(lecture.toString() + "\n");
                }
            }
            w.close();
            System.out.printf("-   %s is created.\n", outputFileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}


// Lab Class
class Lab {
    // variables
    private int crn;
    private String roomNum;

    // Constructor
    public Lab(int crn, String roomNum) {
        this.crn = crn;
        this.roomNum = roomNum;
    }

    // Setters & Getters
    public int getCRN() {
        return crn;
    }
    // public void setCRN(){
    //     this.crn = crn;
    // }

    public String getRoomNum() {
        return roomNum;
    }
    // public void setRoomNum(){
    //     this.roomNum = roomNum;
    // }
   
}

// Lecture Class
class Lecture {
    // Variables
    private int crn;
    private String prefix;
    private String className;
    private String classType;
    private String buildingCode;
    private String roomNum;
    private boolean isOnline;
    private boolean isLabs;
    private ArrayList<Lab> labs;

    // Constructors, Lecture Is Online Online
    public Lecture(int crn, String prefix, String className, String classType) {
        this(crn, prefix, className, classType, null, null, false);
        this.isOnline = true;
    }

    // Constructor
    public Lecture(int crn, String prefix, String className, String classType, String buildingCode, String roomNum,
            boolean isLabs) {
        this.crn = crn;
        this.prefix = prefix;
        this.className = className;
        this.classType = classType;
        this.buildingCode = buildingCode;
        this.roomNum = roomNum;
        this.isLabs = isLabs;
        
        // If Lecture Has Labs
        if (this.isLabs) {
            labs = new ArrayList<Lab>();
        }
    }

    // Add A Lab
    public void addLab(int crn, String roomNum) {
        labs.add(new Lab(crn, roomNum));
    }

    public boolean isOnline() {
        return isOnline;
    }

    public boolean isLabs() {
        return isLabs;
    }

    public String getRoomNum() {
        return roomNum;
    }

    public ArrayList<Lab> getLabs() {
        return labs;
    }


    public int getCRN() {
        return crn;
    }

    // To String
    @Override
    public String toString() {
        if (isOnline()) {
            return String.format("%d, %s, %s, %s, Online",
                    crn, prefix, className, classType);
        } else {
            return String.format("%d, %s, %s, %s, %s, %s, %s",
                    crn, prefix, className, classType, buildingCode, roomNum, isLabs() ? "Yes" : "No");
        }
    }
}