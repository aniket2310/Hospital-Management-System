package HospitalManagementSystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class HospitalManagementSystem {

private static final String url ="jdbc:mysql://localhost:3306/hospital";

private static final String username = "root";

private static final String password = "2310";

    public static void main(String[] args) {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch (Exception e){
            e.printStackTrace();
        }
        Scanner scanner = new Scanner(System.in);
        try {
            Connection connection = DriverManager.getConnection(url,username,password);
            Patient patient = new Patient(connection,scanner);
            Doctor doctor = new Doctor(connection);
            while (true){
                System.out.println(" ★ HOSPITAL MANAGEMENT SYSTEM ★\n");
                System.out.println("1. Add Patients");
                System.out.println("2. View Patients");
                System.out.println("3. View Doctors");
                System.out.println("4. Book Appoinment");
                System.out.println("5. Exit");
                System.out.println("Enter Your Choice : ");

                int choice = scanner.nextInt();

                switch (choice){

                    case 1 :
                        //add patients
                        patient.addPatient();
                        System.out.println();
                        break;
                    case 2 :
                        //view patients
                        patient.viewPatients();
                        System.out.println();
                        break;
                    case 3 :
                        //view doctors
                        doctor.viewDoctors();
                        System.out.println();
                        break;
                    case 4 :
                        //book appoinment
                        bookAppointment(patient, doctor, connection, scanner);
                        System.out.println();
                        break;
                    case 5 :
                        System.out.println("THANK YOU! FOR USING HOSPITAL MANAGEMENT SYSTEM!!");
                        return;

                    default:
                        System.out.println("Entered Choice Is Please Enter Valid Choice : ");
                        break;
                }


            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }
        public static void bookAppointment(Patient patient,Doctor doctor,Connection connection,Scanner scanner){

            System.out.println("Enter Patient Id : ");
            int patientId = scanner.nextInt();
            System.out.println("Enter Doctor Id : ");
            int doctorId = scanner.nextInt();
            System.out.println("Enter appointment date (YYYY-MM-DD) : ");
            String appointmentDate = scanner.next();

            if(patient.getPatientById(patientId) && doctor.getPatientById(doctorId)){

                if (checkDoctorAvailability(doctorId,appointmentDate,connection)){
                    String appointmentQuery = "INSERT INTO appointments(patient_id,doctors_id, appointment_date)VALUES(? ,? ,?)";
                    try{
                        PreparedStatement preparedStatement = connection.prepareStatement(appointmentQuery);
                        preparedStatement.setInt(1,patientId);
                        preparedStatement.setInt(2,doctorId);
                        preparedStatement.setString(3,appointmentDate);
                        int rowsAffected = preparedStatement.executeUpdate();
                        if (rowsAffected>0){
                            System.out.println("Appointment Booked..!");
                        }else {
                            System.out.println("Failed To Booked Appointment");
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }else {
                    System.out.println("Doctor Not Available On This Date...!");
                }

            }else {
                System.out.println("Either Doctor Or Patient Doesn't Exist...!");
            }

        }


    public static boolean checkDoctorAvailability(int doctorId, String appointmentDate, Connection connection){
        String query = "SELECT COUNT(*) FROM appointments WHERE doctors_id = ? AND appointment_date = ?";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, doctorId);
            preparedStatement.setString(2, appointmentDate);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                int count = resultSet.getInt(1);
                if(count==0){
                    return true;
                }else{
                    return false;
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
}