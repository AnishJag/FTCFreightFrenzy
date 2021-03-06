package org.firstinspires.ftc.robotcontroller.external.samples.MB1242;

import android.content.Context;

import com.qualcomm.ftccommon.FtcEventLoop;
import com.qualcomm.hardware.lynx.LynxI2cDeviceSynch;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.hardware.lynx.LynxNackException;
import com.qualcomm.hardware.lynx.commands.core.LynxI2cReadMultipleBytesCommand;
import com.qualcomm.hardware.lynx.commands.core.LynxI2cReadStatusQueryCommand;
import com.qualcomm.hardware.lynx.commands.core.LynxI2cReadStatusQueryResponse;
import com.qualcomm.robotcore.eventloop.EventLoop;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpModeManagerNotifier;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.I2cDeviceSynch;
import com.qualcomm.robotcore.hardware.I2cDeviceSynchDevice;
import com.qualcomm.robotcore.hardware.I2cDeviceSynchImplOnSimple;
import com.qualcomm.robotcore.hardware.configuration.annotations.DeviceProperties;
import com.qualcomm.robotcore.hardware.configuration.annotations.I2cDeviceType;
import com.qualcomm.robotcore.util.TypeConversion;

import org.firstinspires.ftc.ftccommon.external.OnCreateEventLoop;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcontroller.external.samples.Reflection.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@I2cDeviceType
@DeviceProperties(
        name = "AsyncMB1242",
        description = "Ultrasonic distance sensor",
        xmlTag = "AsyncMB1242"
)
public class AsyncMB1242 extends I2cDeviceSynchDevice<I2cDeviceSynch> implements DistanceSensor, OpModeManagerNotifier.Notifications, Runnable {
    private final AtomicLong lastRun = new AtomicLong(0);

    private static EventLoop eventLoopStatic;

    private AtomicLong minRunDelayMs = new AtomicLong(20);

    private final AtomicBoolean running = new AtomicBoolean(true), enabled = new AtomicBoolean(false);
    private final AtomicInteger rangeMM = new AtomicInteger(0);

    private final LynxModule module;
    private final int bus;
    private final I2cAddr address;

    public AsyncMB1242(I2cDeviceSynch i2cDeviceSynch) {
        super(i2cDeviceSynch, true);

        deviceClient.setI2cAddress(I2cAddr.create7bit(0x70));
        registerArmingStateCallback(false);
        deviceClient.engage();

        LynxI2cDeviceSynch device = null;
        try {
            I2cDeviceSynchImplOnSimple simple = (I2cDeviceSynchImplOnSimple) i2cDeviceSynch;

            Field field = ReflectionUtils.getField(simple.getClass(), "i2cDeviceSynchSimple");
            field.setAccessible(true);

            device = (LynxI2cDeviceSynch) field.get(simple);

            //Lets also bump up the bus speed while we are here
            //Tbh it doesn't affect anything when just reading 2 bytes but why not
            device.setBusSpeed(LynxI2cDeviceSynch.BusSpeed.FAST_400K);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        LynxModule module = null;
        try {
            //Module this is being run on, sometimes the module doesn't exist??? but it seems to coincide with ESD so
            module = (LynxModule) ReflectionUtils.getField(device.getClass(), "module").get(device);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        this.module = module;

        int bus = 0;
        try {

            bus = (int) ReflectionUtils.getField(device.getClass(), "bus").get(device);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        this.bus = bus;

        I2cAddr i2cAddr = null;
        try {

            i2cAddr = (I2cAddr) ReflectionUtils.getField(device.getClass(), "i2cAddr").get(device);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        this.address = i2cAddr;
    }

    @OnCreateEventLoop
    public static void attachEventLoop(Context context, FtcEventLoop eventLoop) {
        eventLoopStatic = eventLoop;
    }

    @Override
    public double getDistance(DistanceUnit unit) {
        return unit.fromMm(rangeMM.get());
    }

    public void setMinRunDelayMs(long minRunDelayMs) {
        this.minRunDelayMs.set(minRunDelayMs);
    }

    @Override
    public Manufacturer getManufacturer() {
        return Manufacturer.Other;
    }

    @Override
    public String getDeviceName() {
        return "MB1242 sensor";
    }

    @Override
    public String getConnectionInfo() {
        return "";
    }

    @Override
    public int getVersion() {
        return 1;
    }

    @Override
    protected boolean doInitialize() {
        return true;
    }

    @Override
    public void resetDeviceConfigurationForOpMode() { }

    @Override
    public void close() {

    }

    @Override
    public void onOpModePreInit(OpMode opMode) {
        if(eventLoopStatic.getOpModeManager().getActiveOpModeName().equals("$Stop$Robot$")){
            return;
        }
        running.set(true);
        new Thread(this).start();
    }

    @Override
    public void onOpModePreStart(OpMode opMode) {

    }

    @Override
    public void onOpModePostStop(OpMode opMode) {
        if(eventLoopStatic.getOpModeManager().getActiveOpModeName().equals("$Stop$Robot$")){
            return;
        }
        running.set(false);
    }

    public long getLastRun() {
        return lastRun.get();
    }

    @Override
    public void run() {
        while(running.get()){
            if(!enabled.get())
                continue;

            final long[] timeTaken = {0};

            deviceClient.write(TypeConversion.intToByteArray(0x51)); //Send ping signal

            try {
                module.acquireI2cLockWhile(() -> {
                    LynxI2cReadMultipleBytesCommand command = new LynxI2cReadMultipleBytesCommand(module, bus, address, 2);
                    command.send(); //Schedule a read right away to read from the distance register
                    return true;
                });
            } catch (InterruptedException | RobotCoreException | LynxNackException e) {
                e.printStackTrace();
            }
            boolean end = false;
            while(!end && running.get()){
                try {
                    end = module.acquireI2cLockWhile(() -> {
                        LynxI2cReadStatusQueryCommand command = new LynxI2cReadStatusQueryCommand(module, bus, 2);
                        LynxI2cReadStatusQueryResponse response = command.sendReceive();

                        if (response.getBytes().length == 2) {
                            //Ranging has completed
                            rangeMM.set((int) DistanceUnit.MM.fromCm(TypeConversion.byteArrayToShort(response.getBytes())));
                            long now = System.currentTimeMillis();
                            timeTaken[0] = now - lastRun.get();
                            lastRun.set(System.currentTimeMillis());
                            return true;
                        } else {
                            //I2C NACK returned; ranging is still in progress
                            LynxI2cReadMultipleBytesCommand command2 = new LynxI2cReadMultipleBytesCommand(module, bus, address, 2);
                            command2.send();
                            Thread.sleep(5);
                            return false;
                        }
                    });
                } catch (InterruptedException | RobotCoreException e) {
                    e.printStackTrace();
                } catch(LynxNackException e){
                    switch (e.getNack().getNackReasonCodeAsEnum()) {
                        case I2C_MASTER_BUSY:               // TODO: REVIEW: is this ever actually returned in this situation?
                        case I2C_OPERATION_IN_PROGRESS:
                            // We used to sleep for 3ms while waiting for the result to avoid a "busy loop", but that
                            // caused a serious performance hit over what we could get otherwise, at least on the CH.
                            // Besides, we're not *truly* busy looping, we still end up waiting for the module's response
                            // and what not.

                            try { Thread.sleep(5); } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
                            continue;
                        case I2C_NO_RESULTS_PENDING:
                            // This is an internal error of some sort
                            end = true;
                            break;
                        default:
                            break;
                    }
                }
            }
            if(timeTaken[0] < minRunDelayMs.get()){
                try {
                    //Make sure we aren't running faster then 20 ms per loop
                    //Running faster could theoretically cause issues
                    Thread.sleep(minRunDelayMs.get() - timeTaken[0]);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void enable(){
        enabled.set(true);
    }

    public void disable(){
        enabled.set(false);
    }
}