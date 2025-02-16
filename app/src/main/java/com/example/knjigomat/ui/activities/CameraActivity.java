package com.example.knjigomat.ui.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.knjigomat.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class CameraActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA = 100; // Kod zahtjeva za otvaranje kamere
    private static final int REQUEST_GALLERY = 200;  // Kod zahtjeva za otvaranje galerije
    private ImageView imageView;
    private Button btnCamera, btnGallery, btnSubmit;
    private String base64Image;  // Varijabla za pohranu Base64 kodirane slike

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        imageView = findViewById(R.id.imageView);
        btnCamera = findViewById(R.id.btnCamera);
        btnGallery = findViewById(R.id.btnGallery);
        btnSubmit = findViewById(R.id.btnSubmit);

        // Pokretanje kamere
        btnCamera.setOnClickListener(v -> openCamera());

        // Dohvaćanje slike iz galerije
        btnGallery.setOnClickListener(v -> openGallery());

        // Slanje Base64 kodirane slike vraćamo glavnoj aktivnosti
        btnSubmit.setOnClickListener(v -> {
            if (base64Image != null) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("base64Image", base64Image);
                setResult(RESULT_OK, resultIntent);
                finish(); // Zatvara CameraActivity i vraća podatke
            }
        });
    }

    // Metoda za otvaranje kamere
    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, REQUEST_CAMERA);
    }

    // Metoda za otvaranje galerije
    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, REQUEST_GALLERY);
    }

    // Obrada rezultata nakon što korisnik odabere sliku ili uslika kamerom
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            base64Image = null;  // Resetiranje Base64 varijable

            if (requestCode == REQUEST_CAMERA) {
                // Dohvaćanje slike iz kamere i njeno smanjivanje
                Bitmap imageBitmap = getResizedBitmap((Bitmap) data.getExtras().get("data"), 700);
                base64Image = convertToBase64(imageBitmap);

                // Prikaz slike pomoću Glide biblioteke
                Glide.with(getApplicationContext()).asBitmap().load(imageBitmap).into(imageView);

            } else if (requestCode == REQUEST_GALLERY) {
                try {
                    // Dohvaćanje slike iz galerije i njeno smanjivanje
                    Bitmap imageBitmap = getResizedBitmap(MediaStore.Images.Media.getBitmap(getBaseContext().getContentResolver(), data.getData()), 700);
                    base64Image = convertToBase64(imageBitmap);  // Pretvaranje u Base64
                    Glide.with(getApplicationContext()).asBitmap().load(imageBitmap).into(imageView);

                } catch (IOException e) {
                    Toast.makeText(this, this.getResources().getString(R.string.greska_dohvacanje), Toast.LENGTH_SHORT).show();
                    return;
                }
            }

        }
    }


    // Metoda za konverziju slike u Base64 format
    private String convertToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    /**
     * Metoda za smanjivanje veličine slike
     *
     * @param image   - Ulazna Bitmap slika
     * @param maxSize - Maksimalna veličina slike
     * @return - Smanjena Bitmap slika
     */
    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            // Ako je slika šira nego viša
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            // Ako je slika viša nego šira
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }
}
