# Image Comparison API

## 📌 Overview

This project is a Java-based API that compares two images using the OpenCV library.

It accepts images in Base64 format (via CSV input) and returns similarity results based on multiple parameters.

---

## 🚀 Features

* Compare two images
* Accept images in Base64 format
* Analyze:

  * Color similarity
  * Size similarity
  * Overall matching score
* Return final percentage match

---

## 🛠️ Technologies Used

* Java
* OpenCV
* REST API

---

## 📥 Input

* CSV file containing:

  * Image 1 (Base64 encoded)
  * Image 2 (Base64 encoded)

---

## 📤 Output

The API returns:

* Color match percentage
* Size match percentage
* Final similarity score (%)

---

## 📊 Example Response

```
{
  "colorScore": 85.5,
  "sizeScore": 90.0,
  "finalScore": 87.75
}
```

---

## ▶️ How It Works

1. Read Base64 images from CSV
2. Decode images
3. Process using OpenCV
4. Calculate similarity metrics
5. Return results as JSON

---

## 📌 Use Cases

* Image validation systems
* Duplicate image detection
* Banking / KYC verification
* Document comparison

---

## 👨‍💻 Author

Vikas Maddheshiya
