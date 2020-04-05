package com.bong.brothersetup

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.button_first).setOnClickListener {
            if (requestPerms()) {
                openFilePicker()
            }
        }
        
    }

    override fun onActivityResult(
        requestCode: Int, resultCode: Int, resultData: Intent?) {
        if (requestCode == 1337
            && resultCode == Activity.RESULT_OK
        ) {
            resultData?.data?.also { uri ->
                printNow(uri)
            }
        } else {
            // Do nothing! for now
        }
    }

    private fun requestPerms(): Boolean {
        if (ContextCompat.checkSelfPermission(this.context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                0
            )
        }
        return true;
    }

    private fun openFilePicker(): Boolean {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            this.type = "image/*"
        }

        startActivityForResult(intent, 1337)

        return true
    }

    private fun printNow(uri: Uri): Boolean {
        // Access java class to print a label
        val printerAdapter = Brother(this.context)
        getBitmapFromUri(uri)?.also {
            printerAdapter.sendFile(it)
        }

        return true
    }


    private fun getBitmapFromUri(uri: Uri): Bitmap? {
        this.context!!.contentResolver.openFileDescriptor(uri, "r")?.apply {
            val options = BitmapFactory.Options().apply {
                // Printout works without these fields
                inJustDecodeBounds = false
                inPreferredConfig = Bitmap.Config.RGB_565
                inSampleSize = 1
            }

            return BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options)
        }
        return null
    }
}
