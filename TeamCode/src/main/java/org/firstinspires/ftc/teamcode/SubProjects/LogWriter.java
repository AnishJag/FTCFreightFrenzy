package org.firstinspires.ftc.teamcode.SubProjects;

import android.os.Environment;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

@Disabled
@TeleOp(name = "Log Writing")
public class LogWriter extends LinearOpMode {

    private File file;
    private PrintWriter pw;


    @Override
    public void runOpMode(){

        waitForStart();

        try{
            file = new File(Environment.getExternalStorageDirectory(), "number");
            pw = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
            pw.println("Joel is a lumberjack");
            pw.close();
        }
        catch(Exception e){
            telemetry.addLine(e.getMessage());
            telemetry.update();
        }

        telemetry.addLine("Sent Message");
        telemetry.update();

        while(opModeIsActive()){

        }

    }
}