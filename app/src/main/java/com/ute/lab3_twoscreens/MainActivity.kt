package com.ute.lab3_twoscreens

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.ute.lab3_twoscreens.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var currentStudent = Student("2415053122206", "Lâm Hưng Thiên Doanh", "24T2", "2415053122206@sv.ute.udn.vn", 3.80)

    // Contract 1: Chỉnh sửa hồ sơ
    private val editLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val updatedStudent = result.data?.getSerializableExtra("UPDATED_STUDENT") as? Student
            updatedStudent?.let {
                currentStudent = it
                bindStudentData(currentStudent)
                toast("Đã lưu thông tin mới của ${it.name}!")
            }
        }
    }

    // Contract 2: Chọn ảnh từ Gallery
    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            binding.imgAvatar.setImageURI(it)
            toast("Đã thay đổi ảnh đại diện từ Gallery!")
        }
    }

    // BT Mở Rộng 3: Chụp ảnh trực tiếp từ Camera
    private val takePhotoLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        bitmap?.let {
            binding.imgAvatar.setImageBitmap(it)
            toast("Đã gán ảnh chụp trực tiếp từ Camera!")
        }
    }

    // BT Mở Rộng 2: Xin nhiều quyền cùng lúc
    private val multiplePermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsMap: Map<String, Boolean> ->
        val cameraGranted = permissionsMap[Manifest.permission.CAMERA] ?: false
        val audioGranted = permissionsMap[Manifest.permission.RECORD_AUDIO] ?: false

        if (cameraGranted && audioGranted) {
            toast("Đã được cấp toàn bộ quyền (Camera & Audio)!")
        } else {
            toast("Camera: $cameraGranted | Audio: $audioGranted")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bindStudentData(currentStudent)

        // Nút Sửa hồ sơ
        binding.btnEditProfile.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java).apply {
                putExtra("STUDENT_DATA", currentStudent)
            }
            editLauncher.launch(intent)
        }

        // Nút Chọn ảnh Gallery
        binding.btnChangeAvatar.setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        // Nút Gọi điện
        binding.btnCallHotline.setOnClickListener {
            makePhoneCall("0905123456")
        }

        // BT Mở Rộng 1: Google Maps
        binding.btnOpenMap.setOnClickListener {
            openGoogleMapsLocation()
        }

        // BT Mở Rộng 3: Chụp ảnh
        binding.btnTakePhoto.setOnClickListener {
            takePhotoLauncher.launch(null)
        }

        // BT Mở Rộng 2: Xin nhiều quyền
        binding.btnRequestMultiplePerms.setOnClickListener {
            multiplePermissionsLauncher.launch(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO
                )
            )
        }
    }

    private fun bindStudentData(student: Student) {
        binding.tvName.text = student.name
        binding.tvDetails.text = "MSSV: ${student.id} | Lớp: ${student.className}"
        binding.tvGpaBadge.text = "GPA: ${student.gpa}"
    }

    private fun makePhoneCall(phoneNumber: String) {
        val dialIntent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phoneNumber")
        }
        try {
            startActivity(Intent.createChooser(dialIntent, "Chọn ứng dụng gọi điện"))
        } catch (e: ActivityNotFoundException) {
            toast("Không tìm thấy ứng dụng gọi điện!")
        }
    }

    private fun openGoogleMapsLocation() {
        val geoUri = Uri.parse("geo:16.0768,108.2141?q=Đại+học+Sư+phạm+Kỹ+thuật+Đà+Nẵng")
        val mapIntent = Intent(Intent.ACTION_VIEW, geoUri).apply {
            setPackage("com.google.android.apps.maps")
        }
        try {
            startActivity(mapIntent)
        } catch (e: ActivityNotFoundException) {
            val fallbackIntent = Intent(Intent.ACTION_VIEW, geoUri)
            try {
                startActivity(fallbackIntent)
            } catch (ex: ActivityNotFoundException) {
                toast("Không tìm thấy ứng dụng Bản đồ trên thiết bị!")
            }
        }
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}