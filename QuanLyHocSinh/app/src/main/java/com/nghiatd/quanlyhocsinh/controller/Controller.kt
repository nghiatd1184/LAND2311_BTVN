package com.nghiatd.quanlyhocsinh.controller

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
    
}