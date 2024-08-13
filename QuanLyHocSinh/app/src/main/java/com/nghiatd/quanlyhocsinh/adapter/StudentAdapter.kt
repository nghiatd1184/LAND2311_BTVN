package com.nghiatd.quanlyhocsinh.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.nghiatd.quanlyhocsinh.databinding.ItemStudentBinding
import com.nghiatd.quanlyhocsinh.listener.OnStudentClickListener
import com.nghiatd.quanlyhocsinh.model.Student

class StudentAdapter(
    private val studentList: List<Student>,
    private val context: Context,
    private val listener: OnStudentClickListener
) : RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val binding = ItemStudentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StudentViewHolder(binding)
    }

    override fun getItemCount(): Int = studentList.size

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val student = studentList[position]
        holder.bind(student, position)
    }

    inner class StudentViewHolder(private val binding: ItemStudentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(student: Student, position: Int) {
            binding.apply {
                tvId.text = student.id.toString()
                tvName.text = student.name
                tvBirthYear.text = student.birthYear.toString()
                tvAveragePoint.text = student.averagePoint.toString()
                btnShow.setOnClickListener {
                    showDialog(student)
                }
                root.setOnLongClickListener {
                    listener.onLongClick(position)
                    true
                }
            }
        }
    }

    private fun showDialog(student: Student) {
        AlertDialog.Builder(context).apply {
            setTitle("Bảng Điểm")
            setCancelable(false)
            setMessage(
                "Điểm toán: ${student.mathPoint}\n" +
                        "Điểm lý: ${student.physicsPoint}\n" +
                        "Điểm hóa: ${student.chemistryPoint}\n" +
                        "Điểm sinh: ${student.biologyPoint}\n" +
                        "Điểm sử: ${student.historyPoint}\n" +
                        "Điểm địa: ${student.geographyPoint}\n" +
                        "Điểm văn: ${student.literaturePoint}\n" +
                        "Điểm anh: ${student.englishPoint}"
            )
            setNegativeButton("Đóng") { _, _ -> }
            show()
        }
    }
}
