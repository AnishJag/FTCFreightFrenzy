package org.firstinspires.ftc.teamcode.Auto.Red;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.Auto.Detection.ObjectDetector;
import org.firstinspires.ftc.teamcode.Base.MainBase;
import org.firstinspires.ftc.teamcode.Base.Variables;

//Red autonomous: Scores pre-loaded and parks in WH (top-left)
//Position: Front facing barcode. Left tread along INSIDE tile line. Back treads touching wall.

@Autonomous(name="RED-WH DELIVERY")
public class RedWHDeliver extends LinearOpMode{

    MainBase base = new MainBase();
    Variables var = new Variables();

    @Override
    public void runOpMode() throws InterruptedException {

        ObjectDetector detector = new ObjectDetector(this, true,true);

        base.init(hardwareMap, this);

        base.rightDT.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        base.leftDT.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        base.rightDT.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        base.leftDT.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        detector.setTelemShow(true);

        waitForStart();

        base.gyro.resetZAxisIntegrator();

        ObjectDetector.POSITIONS position = detector.getDecision();
        //ObjectDetector.POSITIONS position = ObjectDetector.POSITIONS.MIDDLE;

        //Resets bucket & claw to avoid lift collision
        base.bucket.setPosition(0.90);
        base.leftClaw.setPosition(1.0);

        switch (position) {
            case LEFT: //SCORES IN FIRST (BOTTOM) TIER

                //Positioning prior to scoring
                base.encoderDrive(0.7, 10, 10, this); //Clears back wall
                base.gyroTurn(0.5,-90,this); //Faces shipping hub
                base.encoderDrive(0.7,20,20,this);
                base.gyroTurn(0.5,0,this);

                base.liftAuto(1,false,this);
                base.encoderDrive(0.5,5,5,this);
                base.bucket.setPosition(var.BUCKET_CLOSED);
                sleep(400);
                base.encoderDrive(0.2,3,3,this);
                sleep(500);

                base.leftClaw.setPosition(var.LCLAW_CLOSED);
                sleep(600);

                //Drives backwards (away) from hub
                base.encoderDrive(0.5,-11,-11,this);

                //CLOSES bucket and claw
                base.bucket.setPosition(var.BUCKET_OPEN);
                base.leftClaw.setPosition(var.LCLAW_OPEN);

                //Brings down lift while parking
                base.liftAuto(0,false,this);

                //Placement before WH PARKING
                base.gyroTurn(0.5,73,this); //Turns towards SHARED HUB
                base.encoderDrive(1.0,60,60,this); //Drives towards SHARED HUB
                base.gyroTurn(0.5,0,this);
                base.encoderDrive(0.5,4,4,this);
                base.gyroTurn(0.5,90,this);
                base.encoderDrive(0.6,18,18,this);
                break;
            case MIDDLE: //SCORES IN SECOND (MIDDLE) TIER

                //Positioning prior to scoring
                base.encoderDrive(0.7, 10, 10, this); //Clears back wall
                base.gyroTurn(0.5,-30,this); //Faces shipping hub

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
                base.gyroTurn(0.5,65,this); //Turns towards SHARED HUB
                base.encoderDrive(0.8,53,53,this); //Drives towards SHARED HUB
                //base.encoderDrive(0.5,5,5,this);
                base.gyroTurn(0.5,90,this);
                break;
            case RIGHT: //SCORES IN THIRD (TOP) TIER

                //Positioning prior to scoring
                base.encoderDrive(0.7, 10, 10, this); //Clears back wall
                base.gyroTurn(0.5,-35,this); //Faces shipping hub

                base.liftAuto(3,false,this);
                base.encoderDrive(0.25,8,8,this);
                base.bucket.setPosition(var.BUCKET_CLOSED);
                sleep(400);
                base.encoderDrive(0.5,7.6,7.6,this);

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
                base.gyroTurn(0.5,65,this); //Turns towards SHARED HUB
                base.encoderDrive(1.0,50,50,this); //Drives towards SHARED HUB
                base.gyroTurn(0.5,90,this);
                base.encoderDrive(0.5,8,8,this);
                break;
        }
    }
}
