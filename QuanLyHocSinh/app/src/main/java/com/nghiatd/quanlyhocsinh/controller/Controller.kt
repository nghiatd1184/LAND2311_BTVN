package com.nghiatd.quanlyhocsinh.controller

import android.content.Context
import android.util.Log
import android.widget.EditText
import android.widget.Toast
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
        val id : Int
        val name : String
        val birthYear : Int
        val mathP : Double
        val physP : Double
        val chemP : Double
        val bioP : Double
        val hisP : Double
        val geoP : Double
        val liteP : Double
        val engP : Double

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
        val student = Student(id, name, birthYear, mathP, physP, chemP, bioP, hisP, geoP, liteP, engP, averageP, aP, bP, cP, dP)
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

    fun allStudentFilter() {
        showStudentList.clear()
        showStudentList.addAll(allStudentList)
    }

    fun exStudentFilter() {
        showStudentList.clear()
        showStudentList.addAll(allStudentList.filter { it.averagePoint >= 8.5 } as ArrayList<Student>)
    }

    fun goodStudentFilter() {
        showStudentList.clear()
        showStudentList.addAll(allStudentList.filter { it.averagePoint >= 6.5 } as ArrayList<Student>)
    }

    fun aStudentFilter() {
        showStudentList.clear()
        showStudentList.addAll(allStudentList.filter { it.aPoint >= 20 } as ArrayList<Student>)
    }

    fun bStudentFilter() {
        showStudentList.clear()
        showStudentList.addAll(allStudentList.filter { it.bPoint >= 20 } as ArrayList<Student>)
    }

    fun cStudentFilter() {
        showStudentList.clear()
        showStudentList.addAll(allStudentList.filter { it.cPoint >= 20 } as ArrayList<Student>)
    }

    fun dStudentFilter() {
        showStudentList.clear()
        showStudentList.addAll(allStudentList.filter { it.dPoint >= 20 } as ArrayList<Student>)
    }

    private fun dataErrorNoti(edt: EditText, context: Context) {
        Toast.makeText(context, "Chưa nhập dữ liệu hoặc dữ liệu không đúng!", Toast.LENGTH_SHORT)
            .show()
        edt.requestFocus()
    }
}