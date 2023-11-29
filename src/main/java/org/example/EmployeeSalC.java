package org.example;

import org.example.utility.DBConn;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class EmployeeSalC extends JFrame {
    double gSal, totalSal, etfDeductedBal, advance, loans, netSal;

    private JLabel mainLabel;
    private JPanel mainPanel;
    private JLabel ename;
    private JTextField txtEname;
    private JTextField totalSalary;
    private JButton btnCalculate;
    private JTextField txtEtf;
    private JTextField txtLoans;
    private JCheckBox chkLoan;
    private JCheckBox chkAdvance;
    private JTextField txtAdvance;
    private JTextField txtNetsalary;
    private JButton clearButton;
    private JTextField txtGrossSalary;
    private JComboBox cmbNames;
    private JButton btnCalGross;
    private JTextField txtYear;
    private JComboBox cmbMonth;

    private final double ETF = 1280;
    private double grossSalary = 0;

    private final double basicSalary = 16000.00;



    //cash flows
    final double earlyCash = 400.00;
    final double baseCash = 600.00; //for 5 hours
    final double midCash = 500.00; //for next 3 hours
    final double OT = 200.00;

    Connection conn;

    //meta data with default constructor
    public EmployeeSalC() {
        setContentPane(mainPanel);
        setTitle("Wasantha Motors");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(720, 420);
        setLocationRelativeTo(null);
        setVisible(true);


        //restrictions

        //prevent editable
        txtEtf.setEditable(false);
        totalSalary.setEditable(false);
        txtNetsalary.setEditable(false);


        //JFormatted text field configs to accept numbers only
//        NumberFormat format = NumberFormat.getNumberInstance();
//        format.setMinimumFractionDigits(2); //allow to take double value with 2 decimal places
//        txtGrossSalary = new JFormattedTextField(format);
//        txtGrossSalary.setEditable(true);
////        txtGrossSalary.setText("0");
//        txtGrossSalary.setColumns(15); //initial width of 15 cols

//        combo box
        cmbNames.setEditable(false);

        //set margin
//        btnCalGross.setMargin(new Insets(10,10,10,10));


        btnCalculate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {


                try {
//                    txtGrossSalary.commitEdit();
                    String value = txtGrossSalary.getText();

                    if (!value.isEmpty()) {

                        gSal = Double.parseDouble(txtGrossSalary.getText());

                       //calculate
                        calculate();

                    } else {
                        //
                        JOptionPane.showMessageDialog(EmployeeSalC.this, "Please Enter a valid value!");
                        return;

                    }


                } catch (Exception ex) {
                    ex.printStackTrace();
                }


            }
        });
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //clear all fields;
                clearAll();

            }
        });


        btnCalGross.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //when clicked get the data for the month and calculate gross salary for the person selected
                //display it in gross salary

                //get selected value
                String selectedName = cmbNames.getSelectedItem().toString().substring(3).toLowerCase();
//                JOptionPane.showMessageDialog(EmployeeSalC.this,selectedName);

                //get year
                String year = txtYear.getText();
                String month = cmbMonth.getSelectedItem().toString().split("-",0)[0];
//                JOptionPane.showMessageDialog(EmployeeSalC.this,year);
//                JOptionPane.showMessageDialog(EmployeeSalC.this,month);



                //if the name is selected connect to DB
                connect();

                //load data specific to name
                loadAttData(selectedName,year,month);


            }
        });
    }

    private void loadAttData(String selectedName, String year, String month) {

        System.out.println("Selected:"+selectedName);
        System.out.println("year :"+year);
        System.out.println("Month:"+month);

        //query to launch only selected months records
//table name - demo_table
        try {

//SELECT timeIN,timeOut FROM demo_table WHERE name = 'kiyas' AND date LIKE '2023-10%'
            PreparedStatement pst = conn.prepareStatement("SELECT timeIN,timeOut FROM demo_table WHERE name = ? AND date LIKE ?");
            pst.setString(1,selectedName);
            pst.setString(2,year + "-"+month+"%");

            ResultSet rs = pst.executeQuery();

            //print result to console

            while (rs.next()){
                String in = rs.getString(1);
                String out = rs.getString(2);

                System.out.println(in + "-"+out);

                //devide time to 2 parts
                String [] earlyHour = in.split(":");
                String [] lateHour = out.split(":");
//                System.out.println(Arrays.toString(earlyHour));

                //condition to calculate the gross salary
                if (earlyHour[0].equals("08")){
                    //if employee come  before 8:10
                    if(Integer.parseInt(earlyHour[1]) >= 0 && Integer.parseInt(earlyHour[1]) <= 10 ){
                        grossSalary = grossSalary + earlyCash;
                    }
                    //basic calculation for first 5 hours
//
//                    1) get total hours
                    int workedHours = Integer.parseInt(lateHour[0])-Integer.parseInt(earlyHour[0]) ;
                    System.out.println("Worked Hours:"+workedHours);
//                    2) Early leave
                    if(workedHours <= 5){
                        grossSalary = grossSalary + baseCash/5*workedHours;
                        System.out.println("Gross Salary for Early leave: "+grossSalary);
                    }
//                    2) middle time
                    else if (workedHours <= 8) {
                        grossSalary = grossSalary + baseCash/5*5 + midCash/3* (workedHours-5);
                        System.out.println("Gross Salary for Middle time leave: "+grossSalary);

                    }
//                    2) rest of hours calculate with OT
                    else {
                        //work hours should reduce 9 hours because of lunch hour
                        grossSalary = grossSalary + baseCash/5*5 + midCash/3*3 + OT*(workedHours-9);
                        System.out.println("Gross Salary with full time and OT: "+grossSalary);

                    }

                }

            }

            System.out.println("Gross Salary = "+grossSalary);
            if(grossSalary > 0){
                txtGrossSalary.setText(String.valueOf(grossSalary));
            }


//close resultset after using
rs.close();
        }catch (SQLException se){
            JOptionPane.showMessageDialog(EmployeeSalC.this, "An error occurred while fetching data from the database: " + se.getMessage());

        }

    }

    //    DBConnection
    public void connect() {
        //connect to DB
        conn = DBConn.getConnection();
    }

    public void disconnect() throws SQLException {
        //connect to DB
        conn.close();
    }

    private void clearAll() {
//        JOptionPane.showMessageDialog(EmployeeSalC.this,"Hi hi");
        txtEname.setText(null);
        txtGrossSalary.setText(null);
        totalSalary.setText(null);
        txtEtf.setText(null);
        txtLoans.setText(null);
        txtAdvance.setText(null);
        txtNetsalary.setText(null);
    }

    private void calculate(){
        totalSal = grossSalary + basicSalary;

        totalSalary.setText(String.valueOf(totalSal));

        //etf deductions
        etfDeductedBal = totalSal - ETF;
        txtEtf.setText(String.valueOf(etfDeductedBal));

        //calculate netSal
        netSal = etfDeductedBal;

        //loans or advances aquired
        if (chkLoan.isSelected()) {
            if (!txtLoans.getText().isEmpty()) {
                netSal = netSal - Double.parseDouble(txtLoans.getText());
            }else {
                JOptionPane.showMessageDialog(EmployeeSalC.this, "Please Fill Loan value!");
                return;

            }
        }
        if (chkAdvance.isSelected()) {
            if (!txtAdvance.getText().isEmpty()) {
                netSal = netSal - Double.parseDouble(txtAdvance.getText());
            }
            else {
                JOptionPane.showMessageDialog(EmployeeSalC.this, "Please Fill Advance value!");
                return;

            }
        }
        txtNetsalary.setText(String.valueOf(netSal));
    }

    public static void main(String[] args) {
//        make an instance object
        new EmployeeSalC();
    }


}
