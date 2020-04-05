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
    Printer mPrinter;
    Bitmap mBitmap;

    Brother(Context context) {
        Log.v("app", "Instantiating Brother Printer");
        appContext = context;
    }

    /**
     * Launch the thread to print
     */
    public void sendFile(Bitmap bitmap) {
        mBitmap = bitmap;
        SendFileThread getTread = new SendFileThread();
        getTread.start();
    }

    private class SendFileThread extends Thread {
        @Override
        public void run() {
            Log.v("app", "Prepping to send file to printer");
            // Setup the printer
            mPrinter = new Printer();
            NetPrinter[] printerList = mPrinter.getNetPrinters("QL-820NWB");
            final PrinterInfo mPrinterInfo = mPrinter.getPrinterInfo();

            // Ensure your printer is connected to the same wi-fi used by your device
            mPrinterInfo.ipAddress = "my-printer-ip-address";
            mPrinterInfo.printerModel = PrinterInfo.Model.QL_820NWB;
            mPrinterInfo.port = PrinterInfo.Port.NET;

            mPrinterInfo.labelNameIndex = LabelInfo.QL700.W62RB.ordinal();
            mPrinterInfo.isAutoCut = true;
            mPrinterInfo.printMode = PrinterInfo.PrintMode.FIT_TO_PAPER;
            mPrinterInfo.workPath = appContext.getCacheDir().getAbsolutePath();
            mPrinter.setPrinterInfo(mPrinterInfo);
            mPrinter.startCommunication();

            PrinterStatus status = mPrinter.printImage(mBitmap);

            // if error log the error
            if (status.errorCode != PrinterInfo.ErrorCode.ERROR_NONE) {
                Log.e("app", "Brother Printer returned an error message: " + status.errorCode.toString());
            }

            mPrinter.endCommunication();
        }
    }
}
