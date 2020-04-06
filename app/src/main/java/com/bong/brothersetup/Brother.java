package com.bong.brothersetup;

import android.content.Context;
import android.graphics.Bitmap;

import com.brother.ptouch.sdk.NetPrinter;
import com.brother.ptouch.sdk.Printer;
import com.brother.ptouch.sdk.PrinterInfo;
import com.brother.ptouch.sdk.LabelInfo;
import com.brother.ptouch.sdk.PrinterStatus;

import android.util.Log;

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
}
