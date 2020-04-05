package com.bong.brothersetup;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;

import com.brother.ptouch.sdk.NetPrinter;
import com.brother.ptouch.sdk.Printer;
import com.brother.ptouch.sdk.PrinterInfo;
import com.brother.ptouch.sdk.LabelInfo;
import com.brother.ptouch.sdk.PrinterStatus;

import android.os.Looper;
import android.util.Log;

import java.util.logging.Handler;

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
            for (NetPrinter printer: printerList) {
                mPrinterInfo.ipAddress = printer.ipAddress;
            }

            if (mPrinterInfo.ipAddress == "") {
                Log.e("app", "Cannot find printer of this type connected to the same wifi" +
                        "Ensure your manifest contains Internet and External storage permissions.");

                return;
            }

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
