package com.ksh.cryptify.OverlayPage

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.exifinterface.media.ExifInterface
import com.ksh.cryptify.databinding.ActivityOverlayBinding

class OverlayActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOverlayBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOverlayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Intent로부터 이미지 URI와 텍스트 데이터를 가져옴
        val imageUri = intent.getStringExtra("imageUri")?.let { Uri.parse(it) }
        val texts = intent.getStringArrayListExtra("texts")
        val boundingBoxes = intent.getParcelableArrayListExtra<Rect>("boundingBoxes")

        if (imageUri != null && texts != null && boundingBoxes != null) {
            overlayTextOnImage(imageUri, texts, boundingBoxes)
        }
    }

    private fun overlayTextOnImage(imageUri: Uri, texts: List<String>, boundingBoxes: List<Rect>) {
        val imageBitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)

        // 이미지 회전 처리
        val rotatedBitmap = rotateImageIfRequired(imageUri, imageBitmap, this)

        // Bitmap을 기반으로 Canvas 생성
        val mutableBitmap = rotatedBitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(mutableBitmap)

        texts.forEachIndexed { index, text ->
            val rect = boundingBoxes[index]

            // 배경에 회색 사각형을 먼저 그림
            val backgroundPaint = Paint().apply {
                color = Color.LTGRAY  // 회색 배경색 설정
                style = Paint.Style.FILL
            }
            canvas.drawRect(rect, backgroundPaint)  // 텍스트 영역에 회색 사각형 그리기

            // 검은색 텍스트 그리기
            val textPaint = Paint().apply {
                color = Color.BLACK  // 검은색 텍스트 색상 설정
                textSize = rect.height().toFloat()  // boundingBox 높이를 글자 크기로 사용
                style = Paint.Style.FILL
            }

            // 텍스트를 그려서 원본 텍스트를 덮음
            canvas.drawText(text, rect.left.toFloat(), rect.bottom.toFloat(), textPaint)
        }

        // 회전된 이미지를 ImageView에 설정
        binding.capturedImageView.setImageBitmap(mutableBitmap)

        // ImageView에 터치 리스너 추가하여 클릭된 좌표를 감지
        binding.capturedImageView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val x = event.x
                val y = event.y

                // 클릭된 좌표가 텍스트의 boundingBox 내에 있는지 확인
                for (i in texts.indices) {
                    val rect = boundingBoxes[i]
                    if (rect.contains(x.toInt(), y.toInt())) {
                        val clickedText = texts[i]

                        // 텍스트를 클립보드에 복사
                        copyTextToClipboard(clickedText)

                        // 사용자에게 알림
                        Toast.makeText(this, "텍스트가 복사되었습니다: $clickedText", Toast.LENGTH_SHORT).show()
                        break
                    }
                }
            }
            true
        }
    }

    private fun copyTextToClipboard(text: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Copied Text", text)
        clipboard.setPrimaryClip(clip)
    }

    // 이미지 회전 처리 함수
    fun rotateImageIfRequired(imageUri: Uri, bitmap: Bitmap, context: Context): Bitmap {
        val inputStream = context.contentResolver.openInputStream(imageUri)
        val exif = ExifInterface(inputStream!!)

        val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)

        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270f)
            else -> bitmap
        }
    }

    fun rotateImage(bitmap: Bitmap, degree: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degree)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}


