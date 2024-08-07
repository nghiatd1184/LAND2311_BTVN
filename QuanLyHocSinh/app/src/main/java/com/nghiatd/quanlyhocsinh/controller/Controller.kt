package com.nghiatd.quanlyhocsinh.controller

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.nghiatd.quanlyhocsinh.R
import com.nghiatd.quanlyhocsinh.adapter.StudentAdapter
import com.nghiatd.quanlyhocsinh.model.Student
import java.math.RoundingMode

class Controller private constructor() {
    companion object {
        private var instance: Controller? = null
        fun getInstance(): Controller {
            if (instance == null) {
                instance = Controller()
            }
            return instance!!
        }
    }

    private var allStudentList = arrayListOf<Student>()
    private var showStudentList = arrayListOf<Student>()

    fun getStudentList(): List<Student> {
        return showStudentList
    }

    fun addStudent(
        context: Context,
        edtId: EditText,
        edtName: EditText,
        edtBirthYear: EditText,
        edtMathP: EditText,
        edtPhysP: EditText,
        edtChemP: EditText,
        edtBioP: EditText,
        edtHisP: EditText,
        edtGeoP: EditText,
        edtLiteP: EditText,
        edtEngP: EditText
    ) {
        val id: Int
        val name: String
        val birthYear: Int
        val mathP: Double
        val physP: Double
        val chemP: Double
        val bioP: Double
        val hisP: Double
        val geoP: Double
        val liteP: Double
        val engP: Double

        if (edtId.text.toString().isEmpty()) {
            dataErrorNoti(edtId, context)
            return
        } else {
            id = edtId.text.toString().toInt()
            if (allStudentList.find { it.id == id } != null) {
                Toast.makeText(context, "Mã SV đã tồn tại!", Toast.LENGTH_SHORT).show()
                edtId.requestFocus()
                return
            }
            if (edtName.text.toString().isEmpty()) {
                dataErrorNoti(edtName, context)
                return
            } else {
                name = edtName.text.toString()
                if (edtBirthYear.text.toString().isEmpty()) {
                    dataErrorNoti(edtBirthYear, context)
                    return
                } else {
                    birthYear = edtBirthYear.text.toString().toInt()
                    if (edtMathP.text.toString().isEmpty()) {
                        dataErrorNoti(edtMathP, context)
                        return
                    } else {
                        mathP = edtMathP.text.toString().toDouble()
                        if (mathP < 0 || mathP > 10) {
                            dataErrorNoti(edtMathP, context)
                            return
                        }
                        if (edtPhysP.text.toString().isEmpty()) {
                            dataErrorNoti(edtPhysP, context)
                            return
                        } else {
                            physP = edtPhysP.text.toString().toDouble()
                            if (physP < 0 || physP > 10) {
                                dataErrorNoti(edtPhysP, context)
                                return
                            }
                            if (edtChemP.text.toString().isEmpty()) {
                                dataErrorNoti(edtChemP, context)
                                return
                            } else {
                                chemP = edtChemP.text.toString().toDouble()
                                if (chemP < 0 || chemP > 10) {
                                    dataErrorNoti(edtChemP, context)
                                    return
                                }
                                if (edtBioP.text.toString().isEmpty()) {
                                    dataErrorNoti(edtBioP, context)
                                    return
                                } else {
                                    bioP = edtBioP.text.toString().toDouble()
                                    if (bioP < 0 || bioP > 10) {
                                        dataErrorNoti(edtBioP, context)
                                        return
                                    }
                                    if (edtHisP.text.toString().isEmpty()) {
                                        dataErrorNoti(edtHisP, context)
                                        return
                                    } else {
                                        hisP = edtHisP.text.toString().toDouble()
                                        if (hisP < 0 || hisP > 10) {
                                            dataErrorNoti(edtHisP, context)
                                            return
                                        }
                                        if (edtGeoP.text.toString().isEmpty()) {
                                            dataErrorNoti(edtGeoP, context)
                                            return
                                        } else {
                                            geoP = edtGeoP.text.toString().toDouble()
                                            if (geoP < 0 || geoP > 10) {
                                                dataErrorNoti(edtGeoP, context)
                                                return
                                            }
                                            if (edtLiteP.text.toString().isEmpty()) {
                                                dataErrorNoti(edtLiteP, context)
                                                return
                                            } else {
                                                liteP = edtLiteP.text.toString().toDouble()
                                                if (liteP < 0 || liteP > 10) {
                                                    dataErrorNoti(edtLiteP, context)
                                                    return
                                                }
                                                if (edtEngP.text.toString().isEmpty()) {
                                                    dataErrorNoti(edtEngP, context)
                                                    return
                                                } else {
                                                    engP = edtEngP.text.toString().toDouble()
                                                    if (engP < 0 || engP > 10) {
                                                        dataErrorNoti(edtEngP, context)
                                                        return
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        val average = (mathP + physP + chemP + bioP + hisP + geoP + liteP + engP) / 8
        val averageP = average.toBigDecimal().setScale(2, RoundingMode.UP).toDouble()
        val aP = mathP + physP + chemP
        val bP = mathP + chemP + bioP
        val cP = liteP + hisP + geoP
        val dP = mathP + liteP + engP
        val student = Student(
            id,
            name,
            birthYear,
            mathP,
            physP,
            chemP,
            bioP,
            hisP,
            geoP,
            liteP,
            engP,
            averageP,
            aP,
            bP,
            cP,
            dP
        )
        allStudentList.add(student)
        Toast.makeText(context, "Thêm học sinh thành công!", Toast.LENGTH_SHORT).show()
        edtId.setText("")
        edtName.setText("")
        edtBirthYear.setText("")
        edtMathP.setText("")
        edtPhysP.setText("")
        edtChemP.setText("")
        edtBioP.setText("")
        edtHisP.setText("")
        edtGeoP.setText("")
        edtLiteP.setText("")
        edtEngP.setText("")
        Log.d("student", student.toString())
    }

    fun showOnLongClickDialog(context: Context, position: Int, adapter: StudentAdapter) {
        var selectedItem = 0
        val items = arrayOf("Sửa Điểm", "Xóa HS")
        AlertDialog.Builder(context).apply {
            setTitle("Học Sinh: ${showStudentList[position].name}")
            setCancelable(false)
            setSingleChoiceItems(items, 0) { _, which ->
                selectedItem = which
            }
            setPositiveButton("Chọn") { _, _ ->
                when (selectedItem) {
                    0 -> showEditDialog(context, position, adapter)
                    1 -> showDeleteDialog(context, position, adapter)
                }
            }
            setNegativeButton("Hủy") { _, _ -> }
            show()
        }
    }

    private fun showDeleteDialog(context: Context, position: Int, adapter: StudentAdapter) {
        AlertDialog.Builder(context).apply {
            setTitle("Thông báo")
            setCancelable(false)
            setMessage("Bạn có chắc chắn muốn xóa?")
            setPositiveButton("Xóa") { _, _ ->
                allStudentList.remove(showStudentList[position])
                showStudentList.removeAt(position)
                adapter.notifyItemRemoved(position)
                Toast.makeText(context, "Xóa thành công!", Toast.LENGTH_SHORT).show()
            }
            setNegativeButton("Hủy") { _, _ -> }
            show()
        }
    }

    private fun showEditDialog(context: Context, position: Int, adapter: StudentAdapter) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_edit)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val tvTitle = dialog.findViewById<TextView>(R.id.tvTitle)
        val edtMathP = dialog.findViewById<EditText>(R.id.edtMathPoint)
        val edtPhysP = dialog.findViewById<EditText>(R.id.edtPhysicsPoint)
        val edtChemP = dialog.findViewById<EditText>(R.id.edtChemistryPoint)
        val edtBioP = dialog.findViewById<EditText>(R.id.edtBiologyPoint)
        val edtHisP = dialog.findViewById<EditText>(R.id.edtHistoryPoint)
        val edtGeoP = dialog.findViewById<EditText>(R.id.edtGeographyPoint)
        val edtLiteP = dialog.findViewById<EditText>(R.id.edtLiteraturePoint)
        val edtEngP = dialog.findViewById<EditText>(R.id.edtEnglishPoint)
        val btnEdit = dialog.findViewById<Button>(R.id.btnEdit)
        val btnCancel = dialog.findViewById<Button>(R.id.btnCancel)

        tvTitle.text = "Sửa điểm học sinh: ${showStudentList[position].name}"
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        btnEdit.setOnClickListener {
            editStudentPoint(
                context,
                position,
                edtMathP,
                edtPhysP,
                edtChemP,
                edtBioP,
                edtHisP,
                edtGeoP,
                edtLiteP,
                edtEngP
            )
            val dataLocation = allStudentList.find { it.id == showStudentList[position].id }
            allStudentList.indexOf(dataLocation).let {
                allStudentList[it] = showStudentList[position]
            }
            adapter.notifyItemChanged(position)
            Toast.makeText(context, "Sửa thành công!", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun editStudentPoint(
        context: Context,
        position: Int,
        edtMathP: EditText,
        edtPhysP: EditText,
        edtChemP: EditText,
        edtBioP: EditText,
        edtHisP: EditText,
        edtGeoP: EditText,
        edtLiteP: EditText,
        edtEngP: EditText
    ) {
        if (edtPhysP.text.toString().isEmpty() || edtPhysP.text.toString()
                .toDouble() < 0 || edtPhysP.text.toString().toDouble() > 10
        ) {
            dataErrorNoti(edtPhysP, context)
            return
        } else {
            showStudentList[position].mathPoint = edtMathP.text.toString().toDouble()
            if (edtPhysP.text.toString().isEmpty() || edtPhysP.text.toString()
                    .toDouble() < 0 || edtPhysP.text.toString().toDouble() > 10
            ) {
                dataErrorNoti(edtPhysP, context)
                return
            } else {
                showStudentList[position].physicsPoint = edtPhysP.text.toString().toDouble()
                if (edtChemP.text.toString().isEmpty() || edtChemP.text.toString()
                        .toDouble() < 0 || edtChemP.text.toString().toDouble() > 10
                ) {
                    dataErrorNoti(edtChemP, context)
                    return
                } else {
                    showStudentList[position].chemistryPoint = edtChemP.text.toString().toDouble()
                    if (edtBioP.text.toString().isEmpty() || edtBioP.text.toString()
                            .toDouble() < 0 || edtBioP.text.toString().toDouble() > 10
                    ) {
                        dataErrorNoti(edtBioP, context)
                        return
                    } else {
                        showStudentList[position].biologyPoint = edtBioP.text.toString().toDouble()
                        if (edtHisP.text.toString().isEmpty() || edtHisP.text.toString()
                                .toDouble() < 0 || edtHisP.text.toString().toDouble() > 10
                        ) {
                            dataErrorNoti(edtHisP, context)
                            return
                        } else {
                            showStudentList[position].historyPoint =
                                edtHisP.text.toString().toDouble()
                            if (edtGeoP.text.toString().isEmpty() || edtGeoP.text.toString()
                                    .toDouble() < 0 || edtGeoP.text.toString().toDouble() > 10
                            ) {
                                dataErrorNoti(edtGeoP, context)
                                return
                            } else {
                                showStudentList[position].geographyPoint =
                                    edtGeoP.text.toString().toDouble()
                                if (edtLiteP.text.toString().isEmpty() || edtLiteP.text.toString()
                                        .toDouble() < 0 || edtLiteP.text.toString().toDouble() > 10
                                ) {
                                    dataErrorNoti(edtLiteP, context)
                                    return
                                } else {
                                    showStudentList[position].literaturePoint =
                                        edtLiteP.text.toString().toDouble()
                                    if (edtEngP.text.toString().isEmpty() || edtEngP.text.toString()
                                            .toDouble() < 0 || edtEngP.text.toString()
                                            .toDouble() > 10
                                    ) {
                                        dataErrorNoti(edtEngP, context)
                                        return
                                    } else {
                                        showStudentList[position].englishPoint =
                                            edtEngP.text.toString().toDouble()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        showStudentList[position].let {
            it.averagePoint =
                (it.mathPoint + it.physicsPoint + it.chemistryPoint + it.biologyPoint + it.historyPoint + it.geographyPoint + it.literaturePoint + it.englishPoint) / 8
            it.aPoint = it.mathPoint + it.physicsPoint + it.chemistryPoint
            it.bPoint = it.mathPoint + it.chemistryPoint + it.biologyPoint
            it.cPoint = it.literaturePoint + it.historyPoint + it.geographyPoint
            it.dPoint = it.mathPoint + it.literaturePoint + it.englishPoint
        }
    }

    fun showFilterDialog(context: Context, adapter: StudentAdapter) {
        var selectedItem = 0
        val filterItems = arrayOf(
            "Tất cả",
            "Học sinh xuất sắc",
            "Học sinh khá",
            "Học sinh khối A",
            "Học sinh khối B",
            "Học sinh khối C",
            "Học sinh khối D"
        )
        AlertDialog.Builder(context).apply {
            setTitle("Lọc Theo:")
            setCancelable(false)
            setPositiveButton("Lọc") { _, _ ->
                when (selectedItem) {
                    0 -> allStudentFilter()
                    1 -> exStudentFilter()
                    2 -> goodStudentFilter()
                    3 -> aStudentFilter()
                    4 -> bStudentFilter()
                    5 -> cStudentFilter()
                    6 -> dStudentFilter()
                }
                adapter.notifyDataSetChanged()
            }
            setNegativeButton("Hủy") { _, _ -> }
            setSingleChoiceItems(filterItems, 0) { _, which ->
                selectedItem = which
            }
            show()
        }
    }

    fun showSortDialog(context: Context, adapter: StudentAdapter) {
        var selectedItem = 0
        val sortItems = arrayOf("Điểm TB tăng dần", "Điểm TB giảm dần", "Tên A-Z", "Tên Z-A")
        AlertDialog.Builder(context).apply {
            setTitle("Sắp xếp theo:")
            setCancelable(false)
            setPositiveButton("Sắp xếp") { _, _ ->
                when (selectedItem) {
                    0 -> {
                        allStudentList.sortBy { it.averagePoint }
                        showStudentList.sortBy { it.averagePoint }
                    }

                    1 -> {
                        allStudentList.sortByDescending { it.averagePoint }
                        showStudentList.sortByDescending { it.averagePoint }
                    }

                    2 -> {
                        allStudentList.sortBy { it.name }
                        showStudentList.sortBy { it.name }
                    }

                    3 -> {
                        allStudentList.sortByDescending { it.name }
                        showStudentList.sortByDescending { it.name }
                    }
                }
                adapter.notifyDataSetChanged()
            }
            setNegativeButton("Hủy") { _, _ -> }
            setSingleChoiceItems(sortItems, 0) { _, which ->
                selectedItem = which
            }
            show()
        }
    }

    fun allStudentFilter() {
        showStudentList.clear()
        showStudentList.addAll(allStudentList)
    }

    private fun exStudentFilter() {
        showStudentList.clear()
        showStudentList.addAll(allStudentList.filter { it.averagePoint >= 8.5 } as ArrayList<Student>)
    }

    private fun goodStudentFilter() {
        showStudentList.clear()
        showStudentList.addAll(allStudentList.filter { it.averagePoint >= 6.5 } as ArrayList<Student>)
    }

    private fun aStudentFilter() {
        showStudentList.clear()
        showStudentList.addAll(allStudentList.filter { it.aPoint >= 20 } as ArrayList<Student>)
    }

    private fun bStudentFilter() {
        showStudentList.clear()
        showStudentList.addAll(allStudentList.filter { it.bPoint >= 20 } as ArrayList<Student>)
    }

    private fun cStudentFilter() {
        showStudentList.clear()
        showStudentList.addAll(allStudentList.filter { it.cPoint >= 20 } as ArrayList<Student>)
    }

    private fun dStudentFilter() {
        showStudentList.clear()
        showStudentList.addAll(allStudentList.filter { it.dPoint >= 20 } as ArrayList<Student>)
    }

    private fun dataErrorNoti(edt: EditText, context: Context) {
        Toast.makeText(context, "Chưa nhập dữ liệu hoặc dữ liệu không đúng!", Toast.LENGTH_SHORT)
            .show()
        edt.requestFocus()
    }
}