package com.example.mystoryapp;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.BaseDirection;
import com.itextpdf.layout.properties.TextAlignment;
//import com.itextpdf.layout.property.TextAlignment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.itextpdf.io.util.StreamUtil;

public class PDFExporter {

    public static void exportBookToPDF(Activity activity, List<ViewBook.Page> pages, String bookName, String authorName) {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            try {
                Log.d("PDFExporter", "Starting PDF export for book: " + bookName);

                String fileName = bookName + "_" + System.currentTimeMillis() + ".pdf";
                File pdfFile;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    File appSpecificExternalDir = new File(activity.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "MyStoryApp");
                    if (!appSpecificExternalDir.exists()) {
                        appSpecificExternalDir.mkdirs();
                    }
                    pdfFile = new File(appSpecificExternalDir, fileName);
                } else {
                    File pdfFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    pdfFile = new File(pdfFolder, fileName);
                }

                Log.d("PDFExporter", "PDF will be saved to: " + pdfFile.getAbsolutePath());

                PdfWriter writer = new PdfWriter(new FileOutputStream(pdfFile));
                PdfDocument pdf = new PdfDocument(writer);
                Document document = new Document(pdf);

                AssetManager assetManager = activity.getAssets();
                PdfFont hebrewFont;

                try (InputStream fontStream = assetManager.open("fonts/DGL_Hollywood.ttf")) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = fontStream.read(buffer)) != -1) {
                        baos.write(buffer, 0, length);
                    }
                    byte[] fontBytes = baos.toByteArray();
                    hebrewFont = PdfFontFactory.createFont(fontBytes, PdfEncodings.IDENTITY_H);
                }

                // Add cover page
                addCoverPage(document, hebrewFont, bookName, authorName);

                // Add content pages
                int totalPages = pages.size();
                for (ViewBook.Page page : pages) {
                    addContentPage(document, pdf, hebrewFont, page, activity, totalPages);
                }

                document.close();
                Log.d("PDFExporter", "PDF created successfully");

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
                    values.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf");
                    values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

                    Uri uri = activity.getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);

                    if (uri != null) {
                        try (OutputStream os = activity.getContentResolver().openOutputStream(uri);
                             FileInputStream fis = new FileInputStream(pdfFile)) {
                            byte[] buffer = new byte[1024];
                            int length;
                            while ((length = fis.read(buffer)) > 0) {
                                os.write(buffer, 0, length);
                            }
                        }
                        pdfFile.delete();
                    }
                    Log.d("PDFExporter", "PDF saved to Downloads folder");
                }

                activity.runOnUiThread(() -> {
                    Toast.makeText(activity, "הקובץ נשמר בהצלחה במכשיר", Toast.LENGTH_LONG).show();
                });

            } catch (Exception e) {
                Log.e("PDFExporter", "Error exporting PDF: " + e.getMessage(), e);
                activity.runOnUiThread(() -> {
                    Toast.makeText(activity, "בעיה בשמירת הקובץ " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private static void addCoverPage(Document document, PdfFont hebrewFont, String bookName, String authorName) throws Exception {
        Paragraph titleParagraph = new Paragraph(new StringBuilder(bookName).reverse().toString())
                .setFont(hebrewFont)
                .setFontSize(24)
                .setTextAlignment(TextAlignment.CENTER)
                .setBaseDirection(BaseDirection.RIGHT_TO_LEFT);
        document.add(titleParagraph);

        document.add(new Paragraph("\n"));

        Paragraph authorParagraph = new Paragraph(new StringBuilder("מאת: " + authorName).reverse().toString())
                .setFont(hebrewFont)
                .setFontSize(18)
                .setTextAlignment(TextAlignment.CENTER)
                .setBaseDirection(BaseDirection.RIGHT_TO_LEFT);
        document.add(authorParagraph);

        document.add(new AreaBreak());
    }

    private static void addContentPage(Document document, PdfDocument pdf, PdfFont hebrewFont, ViewBook.Page page, Activity activity, int totalPages) throws Exception {
        Log.d("PDFExporter", "Processing page " + page.getPageNumber());

        String pageText = page.getText();

        Paragraph paragraph = new Paragraph()
                .setFont(hebrewFont)
                .setFontSize(16)
                .setTextAlignment(TextAlignment.RIGHT)
                .setBaseDirection(BaseDirection.RIGHT_TO_LEFT);

        String[] lines = pageText.split("\n");

        for (String line : lines) {
            String[] words = line.split(" ");
            for (int i = words.length - 1; i >= 0; i--) {
                String reversedWord = new StringBuilder(words[i]).reverse().toString();
                paragraph.add(reversedWord).add(" ");
            }
            document.add(paragraph);
            paragraph = new Paragraph()
                    .setFont(hebrewFont)
                    .setFontSize(16)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setBaseDirection(BaseDirection.RIGHT_TO_LEFT);
        }

        document.add(new Paragraph(" "));

        if (page.getUrl() != null && !page.getUrl().isEmpty()) {
            try {
                String cleanUrl = page.getUrl().replace("\"", "");
                Log.d("PDFExporter", "Fetching image from URL: " + cleanUrl);

                CountDownLatch latch = new CountDownLatch(1);
                final Bitmap[] bitmapHolder = new Bitmap[1];

                Glide.with(activity)
                        .asBitmap()
                        .load(cleanUrl)
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                bitmapHolder[0] = resource;
                                latch.countDown();
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {
                                latch.countDown();
                            }
                        });

                latch.await();

                if (bitmapHolder[0] != null) {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmapHolder[0].compress(Bitmap.CompressFormat.PNG, 100, stream);
                    Image image = new Image(ImageDataFactory.create(stream.toByteArray()));

                    float pageWidth = pdf.getDefaultPageSize().getWidth() - document.getLeftMargin() - document.getRightMargin();
                    image.setWidth(pageWidth);

                    document.add(image);
                }
            } catch (Exception e) {
                Log.e("PDFExporter", "Error processing image: " + e.getMessage());
            }
        }

        if (page.getPageNumber() < totalPages) {
            document.add(new AreaBreak());
        }
    }
}

