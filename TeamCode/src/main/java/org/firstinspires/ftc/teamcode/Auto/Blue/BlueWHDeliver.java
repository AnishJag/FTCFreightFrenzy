package org.firstinspires.ftc.teamcode.Auto.Blue;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.Auto.Detection.ObjectDetector;
import org.firstinspires.ftc.teamcode.Base.MainBase;
import org.firstinspires.ftc.teamcode.Base.Variables;

//Autonomous: Delivers Pre-loaded Block and Parks in Warehouse
//Position: Facing forward

@Autonomous(name="BLUE-WH DELIVERY")
public class BlueWHDeliver extends LinearOpMode{

    MainBase base = new MainBase();
    Variables var = new Variables();

    @Override
    public void runOpMode() throws InterruptedException {

        ObjectDetector detector = new ObjectDetector(this, true);

        base.init(hardwareMap, this);

        base.rightDT.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        base.leftDT.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        base.rightDT.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        base.leftDT.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        telemetry.addLine("God Speed");
        telemetry.update();

        waitForStart();

        base.gyro.resetZAxisIntegrator();

        //ObjectDetector.POSITIONS position = detector.getDecision();
        ObjectDetector.POSITIONS position = ObjectDetector.POSITIONS.MIDDLE;

        //Resets bucket & claw to avoid lift collision
        base.bucket.setPosition(var.BUCKET_OPEN);
        base.leftClaw.setPosition(1.0);

        switch (position) {
            case LEFT: //SCORES IN FIRST (BOTTOM) TIER

                //Positioning prior to scoring
                base.encoderDrive(0.7, 10, 10, this); //Clears back wall
                base.gyroTurn(0.5,30,this); //Faces shipping hub

                base.liftAuto(1,false,this);
                base.encoderDrive(0.7,8,8,this);
                base.bucket.setPosition(var.BUCKET_CLOSED);
                sleep(400);
                base.encoderDrive(0.5,6.7,6.7,this);

                base.leftClaw.setPosition(var.LCLAW_CLOSED);
                sleep(600);

                //Drives backwards (away) from hub
                base.encoderDrive(0.5,-14,-14,this);

                //CLOSES bucket and claw
                base.bucket.setPosition(var.BUCKET_OPEN);
                base.leftClaw.setPosition(var.LCLAW_OPEN);

                //Brings down lift while parking
                base.liftAuto(0,false,this);

                //Placement before WH PARKING
                base.gyroTurn(0.5,-69,this); //Turns towards SHARED HUB
                base.encoderDrive(1.0,50,50,this); //Drives towards SHARED HUB
                base.gyroTurn(0.5,-69,this);
                base.encoderDrive(0.5,5,5,this);
                base.gyroTurn(0.5,-87,this);
                break;
            case MIDDLE: //SCORES IN SECOND (MIDDLE) TIER

                //Positioning prior to scoring
                base.encoderDrive(0.7, 10, 10, this); //Clears back wall
                base.gyroTurn(0.5,30,this); //Faces shipping hub

                base.liftAuto(2,false,this);
                base.encoderDrive(0.5,8,8,this);
                base.bucket.setPosition(0.45);
                sleep(400);
                base.encoderDrive(0.3,8,7,this);

                base.leftClaw.setPosition(var.LCLAW_CLOSED);
                sleep(600);

                //Drives backwards (away) from hub
                base.encoderDrive(0.5,-15.5,-15.5,this);

                //CLOSES bucket and claw
                base.bucket.setPosition(var.BUCKET_OPEN);
                base.leftClaw.setPosition(var.LCLAW_OPEN);

                //Brings down lift while parking
                base.liftAuto(0,false,this);

                //Placement before WH PARKING
                base.gyroTurn(0.5,-65,this); //Turns towards SHARED HUB
                base.encoderDrive(0.8,55,55,this); //Drives towards SHARED HUB
                base.gyroTurn(0.5,0,this);
                base.encoderDrive(0.5,5,5,this);
                base.gyroTurn(0.5,-87,this);
                break;
            case RIGHT: //SCORES IN THIRD (TOP) TIER
                //Positioning prior to scoring
                base.encoderDrive(0.7, 10, 10, this); //Clears back wall
                base.gyroTurn(0.5,30,this); //Faces shipping hub

                base.liftAuto(3,false,this);
                base.encoderDrive(0.7,8,8,this);
                base.bucket.setPosition(var.BUCKET_CLOSED);
                sleep(400);
                base.encoderDrive(0.5,8,8,this);

                base.leftClaw.setPosition(var.LCLAW_CLOSED);
                sleep(600);

                //Drives backwards (away) from hub
                base.encoderDrive(0.5,-15.5,-15.5,this);

                //CLOSES bucket and claw
                base.bucket.setPosition(var.BUCKET_OPEN);
                base.leftClaw.setPosition(var.LCLAW_OPEN);

                //Brings down lift while parking
                base.liftAuto(0,false,this);

                //Placement before WH PARKING
                base.gyroTurn(0.5,-69,this); //Turns towards SHARED HUB
                base.encoderDrive(1.0,50,50,this); //Drives towards SHARED HUB
                base.gyroTurn(0.5,-69,this);
                base.encoderDrive(0.5,5,5,this);
                base.gyroTurn(0.5,-87,this);
                break;
        }
    }
}