package com.bong.brothersetup;

import android.content.Context;
import android.graphics.Bitmap;

import com.brother.ptouch.sdk.CustomPaperInfo;
import com.brother.ptouch.sdk.NetPrinter;
import com.brother.ptouch.sdk.Printer;
import com.brother.ptouch.sdk.PrinterInfo;
import com.brother.ptouch.sdk.LabelInfo;
import com.brother.ptouch.sdk.PrinterStatus;
import com.brother.ptouch.sdk.Unit;

import android.util.Log;

import java.util.List;
import java.util.Map;

public class Brother {
    Context appContext;

    Brother(Context context) {
        Log.v("app", "Instantiating Brother Printer");
        appContext = context;
    }

    /**
     * Launch the thread to print
     */
    public void sendFile(Bitmap bitmap, Context context) {
        Log.v("app", "Prepping to send file to printer");
        // Setup the printer
        Printer printer = new Printer();
        PrinterInfo printerInfo = printer.getPrinterInfo();

        // Ensure your printer is connected to the same wi-fi used by your device
        printerInfo.ipAddress = "my-printer-ip-address";
        printerInfo.printerModel = PrinterInfo.Model.QL_820NWB;
        printerInfo.port = PrinterInfo.Port.NET;

        printerInfo.labelNameIndex = LabelInfo.QL700.W62RB.ordinal();
        printerInfo.isAutoCut = true;
        printerInfo.printMode = PrinterInfo.PrintMode.FIT_TO_PAPER;
        printerInfo.workPath = context.getCacheDir().getAbsolutePath();
        printer.setPrinterInfo(printerInfo);
        new Thread(new Runnable() {
            @Override
            public void run() {
                printBitmap(bitmap, printer);
            }
        }).start();
    }

    private void printBitmap(Bitmap bitmap, Printer printer) {
        if (printer.startCommunication()) {
            PrinterStatus status = printer.printImage(bitmap);

            // if error log the error
            if (status.errorCode != PrinterInfo.ErrorCode.ERROR_NONE) {
                Log.e("app", "Brother Printer returned an error message: " + status.errorCode.toString());
            }

            printer.endCommunication();
        }
    }

    /**
     * Launch the thread to print
     */
    public void sendFileToRJ2150(Bitmap bitmap, Context context) {
        // Specify printer
        Printer printer = new Printer();
        PrinterInfo printerInfo = printer.getPrinterInfo();
        printerInfo.printerModel = PrinterInfo.Model.RJ_2150;
        printerInfo.port = PrinterInfo.Port.BLUETOOTH;
        printerInfo.macAddress = "24-71-89-5D-5F-62";

        // Print Settings
        printerInfo.paperSize = PrinterInfo.PaperSize.CUSTOM;
        printerInfo.printMode = PrinterInfo.PrintMode.FIT_TO_PAPER;
        printer.setPrinterInfo(printerInfo);

        // Custom Paper Setting (Case: using Roll paper)
        float width = 54.0f;
        float rightMargin = 0.0f;
        float leftMargin = 0.0f;
        float topMargin = 0.0f;
        CustomPaperInfo customPaperInfo = CustomPaperInfo.newCustomRollPaper(printerInfo.printerModel,
                Unit.Mm,
                width,
                rightMargin,
                leftMargin,
                topMargin);
        List<Map<CustomPaperInfo.ErrorParameter, CustomPaperInfo.ErrorDetail>> errors = printerInfo.setCustomPaperInfo(customPaperInfo);
        if (errors.isEmpty() == false) {
            System.out.println(errors.toString());
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                printBitmap(bitmap, printer);
            }
        }).start();
    }
}
