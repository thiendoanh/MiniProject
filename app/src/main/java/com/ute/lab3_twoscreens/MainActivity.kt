package com.ute.lab3_twoscreens

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.ute.lab3_twoscreens.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var currentStudent = Student("2415053122206", "Lâm Hưng Thiên Doanh", "24T2", "2415053122206@sv.    ute.udn.vn", 3.80)

    // 1. Contract 1: Nhận dữ liệu phản hồi từ EditProfileActivity
    private val editLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
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

    // 2. Contract 2: Mở Photo Picker chọn ảnh từ Gallery
    private val galleryLauncher: ActivityResultLauncher<String> = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            binding.imgAvatar.setImageURI(it)
            toast("Đã thay đổi ảnh đại diện!")
        }
    }

    // 3. Contract 3: Xin quyền Camera thời gian chạy (Runtime Permission)
    private val cameraLauncher: ActivityResultLauncher<String> = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            toast("Đã cấp quyền Camera! Sẵn sàng chụp ảnh.")
        } else {
            toast("Bạn đã từ chối quyền Camera!")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bindStudentData(currentStudent)

        // Nút 1: Mở màn hình Sửa hồ sơ
        binding.btnEditProfile.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java).apply {
                putExtra("STUDENT_DATA", currentStudent)
            }
            editLauncher.launch(intent)
        }

        // Nút 2: Mở thư viện chọn ảnh
        binding.btnChangeAvatar.setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        // Nút 3: Gọi điện thoại
        binding.btnCallHotline.setOnClickListener {
            makePhoneCall("0905123456")
        }

        // Nút 4: Yêu cầu quyền Camera
        binding.btnRequestCamera.setOnClickListener {
            cameraLauncher.launch(Manifest.permission.CAMERA)
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

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}