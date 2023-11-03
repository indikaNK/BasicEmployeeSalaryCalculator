package org.example;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

    private final double ETF = 1280;

    //meta data with default constructor
    public EmployeeSalC() {
        setContentPane(mainPanel);
        setTitle("Wasantha Motors");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(640, 420);
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
        totalSal = gSal + 16000.00;

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
