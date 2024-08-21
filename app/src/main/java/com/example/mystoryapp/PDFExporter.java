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

    public static void exportBookToPDF(Activity activity, List<ViewBook.Page> pages, String bookName) {
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

                for (ViewBook.Page page : pages) {
                    Log.d("PDFExporter", "Processing page " + page.getPageNumber());

                    String pageText = page.getText();

                    // יצירת פסקה חדשה
                    Paragraph paragraph = new Paragraph()
                            .setFont(hebrewFont)
                            .setFontSize(16)
                            .setTextAlignment(TextAlignment.RIGHT)      //יישור לימין
                            .setBaseDirection(BaseDirection.RIGHT_TO_LEFT); // כיוון מימין לשמאל

                    // חילוק הטקסט לשורות (במידת הצורך)
                    String[] lines = pageText.split("\n"); // אם אין שורות, השאר את זה כפי שהוא

                    // מעבר על כל שורה בנפרד
                    for (String line : lines) {
                        String[] words = line.split(" ");  // חילוק השורה למילים
                          // לא הפיכת סדר השורות  והפיכת סדר המילים
                         for (int i = words.length - 1; i >= 0; i--) {
                        // הפיכת כל מילה בנפרד
                        String reversedWord = new StringBuilder(words[i]).reverse().toString();
                        paragraph.add(reversedWord).add(" "); // הוספת המילה ההפוכה עם רווח

                        }

                        // הוספת הפסקה למסמך
                        document.add(paragraph);

                        // יצירת פסקה חדשה עבור השורה הבאה
                        paragraph = new Paragraph()
                                .setFont(hebrewFont)
                                .setFontSize(16)
                                .setTextAlignment(TextAlignment.RIGHT)      // יישור לימין
                                .setBaseDirection(BaseDirection.RIGHT_TO_LEFT); // כיוון מימין לשמאל
                    }

                    // הוספת רווח בין הטקסט לתמונה
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

                    if (pages.indexOf(page) < pages.size() - 1) {
                        document.add(new AreaBreak());
                    }
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
                    Toast.makeText(activity, "PDF exported successfully", Toast.LENGTH_LONG).show();
                });

            } catch (Exception e) {
                Log.e("PDFExporter", "Error exporting PDF: " + e.getMessage(), e);
                activity.runOnUiThread(() -> {
                    Toast.makeText(activity, "Failed to export PDF: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }
}