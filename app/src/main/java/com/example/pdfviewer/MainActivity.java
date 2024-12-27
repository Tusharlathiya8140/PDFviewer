package com.example.pdfviewer;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.widget.Button;
import androidx.annotation.Nullable;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.ParcelFileDescriptor;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private static final int PICK_PDF_FILE = 2;
    private RecyclerView pdfRecyclerView;
    private List<Bitmap> pdfPages;
    private PDFAdapter pdfAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Button btnSelectPdf = findViewById(R.id.btnSelectPdf);
        pdfRecyclerView = findViewById(R.id.pdfRecyclerView);

        pdfPages = new ArrayList<>();
        pdfAdapter = new PDFAdapter(pdfPages);
        pdfRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        pdfRecyclerView.setAdapter(pdfAdapter);

        btnSelectPdf.setOnClickListener(v -> openFileChooser());

    }
    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Select PDF"), PICK_PDF_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PDF_FILE && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            displayPdf(uri);
        }
    }

    private void displayPdf(Uri uri) {
        try {
            // Create a temporary file
            File file = createTempFileFromUri(uri);
            if (file == null) {
                Toast.makeText(this, "Failed to load PDF", Toast.LENGTH_SHORT).show();
                return;
            }

            ParcelFileDescriptor parcelFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
            PdfRenderer pdfRenderer = new PdfRenderer(parcelFileDescriptor);

            pdfPages.clear(); // Clear previous data

            for (int i = 0; i < pdfRenderer.getPageCount(); i++) {
                PdfRenderer.Page page = pdfRenderer.openPage(i);

                Bitmap bitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

                pdfPages.add(bitmap); // Add page to list
                page.close();
            }

            pdfRenderer.close();
            parcelFileDescriptor.close();

            pdfAdapter.notifyDataSetChanged(); // Update RecyclerView

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error displaying PDF", Toast.LENGTH_SHORT).show();
        }
    }

    private File createTempFileFromUri(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            File tempFile = File.createTempFile("pdf", ".pdf", getCacheDir());
            FileOutputStream outputStream = new FileOutputStream(tempFile);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            inputStream.close();
            outputStream.close();
            return tempFile;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}