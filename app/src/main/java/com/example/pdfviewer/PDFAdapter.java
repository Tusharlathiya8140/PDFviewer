package com.example.pdfviewer;

import android.graphics.Bitmap;

import java.util.List;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class PDFAdapter extends RecyclerView.Adapter<PDFAdapter.PdfViewHolder>{

    private List<Bitmap> pdfPages;

    public PDFAdapter(List<Bitmap> pdfPages) {
        this.pdfPages = pdfPages;
    }
    @NonNull
    @Override
    public PdfViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pdf_page_item, parent, false);
        return new PdfViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PdfViewHolder holder, int position) {
        holder.imageView.setImageBitmap(pdfPages.get(position));
    }

    @Override
    public int getItemCount() {
        return pdfPages.size();
    }

    static class PdfViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        PdfViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.pdfPageImage);
        }
    }
}
