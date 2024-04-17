package com.nghiatd.bt3

import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.nghiatd.bt3.adapter.FileAdapter
import com.nghiatd.bt3.databinding.ActivityMainBinding
import com.nghiatd.bt3.listener.OnFileItemClickListener
import com.nghiatd.bt3.model.File
import com.nghiatd.bt3.model.convertStringToFileModel
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets


class MainActivity : AppCompatActivity(), OnFileItemClickListener {
    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding by lazy { requireNotNull(_binding) }
    private lateinit var files: ArrayList<File>
    private val adapter: FileAdapter by lazy { FileAdapter(files, this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        files = arrayListOf()
        this.fileList().forEach {
            if (it == "profileInstalled" || it.isEmpty()){

            } else {
                files.add(readFiles(it))
            }
        }

        binding.btnSave.setOnClickListener {
            if (binding.edtTitle.text.isEmpty() || binding.edtBody.text.isEmpty()) {
                Toast.makeText(this,"Vui lòng nhập đủ Tiêu đề và nội dung", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val fileName = binding.edtTitle.text.toString().plus(".txt")

            val model = File(title = binding.edtTitle.text.toString(), body = binding.edtBody.text.toString())

            this.openFileOutput(fileName, MODE_PRIVATE).use { fos ->
                fos.write(
                    model.toString().toByteArray()
                )
            }
            binding.edtTitle.text = null
            binding.edtBody.text = null

            files.clear()
            this.fileList().forEach {
                if (it == "profileInstalled"){

                } else {
                    files.add(readFiles(it))
                }
            }
            adapter.notifyDataSetChanged()
        }

        binding.recyclerView.apply {
            layoutManager =
                LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
            adapter = this@MainActivity.adapter
        }
    }

    private fun readFiles(fileName : String) : File {
        val fis: FileInputStream = this.openFileInput(fileName)
        val inputStreamReader = InputStreamReader(fis, StandardCharsets.UTF_8)
        val stringBuilder = StringBuilder()
        val file : File
        try {
            BufferedReader(inputStreamReader).use { reader ->
                var line = reader.readLine()
                while (line != null) {
                    stringBuilder.append(line)
                    line = reader.readLine()
                }
            }
        } catch (e: IOException) {
            // Error occurred when opening raw file for reading.
        } finally {
            val contents = stringBuilder.toString()
            file = contents.convertStringToFileModel()
            fis.close()
        }
        return file
    }

    override fun onClick(fileName: String) {
        val file = readFiles(fileName)
        val title: Editable = SpannableStringBuilder(file.title)
        val body: Editable = SpannableStringBuilder(file.body)
        binding.edtTitle.text = title
        binding.edtBody.text = body
    }
}